package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.LogRepository
import uk.ac.aber.dcs.souschefapp.firebase.Log
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

class LogViewModel: ViewModel() {
    private val logRepository = LogRepository()

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private var logListener: ListenerRegistration? = null
    private var logListenerSelected: ListenerRegistration? = null

    private var _singleLog = MutableLiveData<Log>() // Single-view Log.
    var singleLog: LiveData<Log> = _singleLog

    private var _copyLog = MutableLiveData<Log>()
    var copyLog: LiveData<Log> = _copyLog

    private var _selectedLogs = MutableLiveData<List<Log>>() // For Shopping List
    var selectedLogs: LiveData<List<Log>> = _selectedLogs

    private var _logs = MutableLiveData<List<Log>>()
    var logs: LiveData<List<Log>> = _logs

    private fun standardDate(millis: Long): Long =
        Instant.ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

    fun createLog(userId: String?, millis: Long,  log: Log? = null){
        if (userId == null) {
            android.util.Log.e("LogViewModel", "Failed to create log due to null userId")
            return
        }

        val standardLog = log?.copy(
            logId = standardDate(millis).toString(),
            createdAt = Timestamp(Date(millis)),
            createdBy = userId
        ) ?: Log(
            logId = standardDate(millis).toString(),
            createdAt = Timestamp(Date(millis)),
            createdBy = userId
        )

        viewModelScope.launch {
            val isSuccess = logRepository.addLog(
                userId = userId,
                log = standardLog
            )
            if (isSuccess) {
                _singleLog.postValue(standardLog) // Update
                android.util.Log.d("LogViewModel", "Log successfully created!")

            } else {
                android.util.Log.e("LogViewModel", "Failed to create log.")
            }
        }
    }

    fun readLogs(userId: String?) {
        if (userId == null) return

        _isLoading.postValue(true)

        logListener?.remove() // Stop previous listener if it exists

        logListener = logRepository.listenForLogs(userId) { logs ->
            _logs.postValue(logs)
            _isLoading.postValue(false)
        }
    }

    fun readLogsByDates(userId: String?, dateList: List<LocalDate>){
        if (userId == null) return

        try {
            // Convert LocalDates to Timestamps
            val timestamps: List<Timestamp> = dateList.map { localDate ->
                val zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault())
                val instant = zonedDateTime.toInstant()
                val date = Date.from(instant)
                Timestamp(date)
            }

            // Perform the fetching of logs in the background using viewModelScope
            viewModelScope.launch {
                val logs = logRepository.fetchLogsByTimestamps(userId, timestamps)
                _selectedLogs.postValue(logs)
            }
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            println("Error fetching logs: ${e.message}")
            // Optionally, notify the user of the error
        }
    }

    fun readLogFromDate(millis: Long){
        val date = standardDate(millis)
        _singleLog.postValue(_logs.value?.find{ it.logId == date.toString() })
    }

    fun copyLog(log: Log){
        _copyLog.postValue(log)
    }

    fun stopListening(){
        logListener?.remove()
        logListener = null
    }

    fun addRecipeToLog(userId: String, recipeId: String, context: Context){
        val logId = _singleLog.value?.logId
        if (logId == null){
            Toast.makeText(context, "Log ID is missing. Cannot add recipe", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            val isSuccess = logRepository.addRecipeToLog(
                userId,
                logId,
                recipeId
            )
            val message = if (isSuccess) "Recipe added successfully!" else "Failed to add recipe."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun addRecipesToLog(userId: String?, recipes: List<Recipe>, context: Context){
        val logId = _singleLog.value?.logId
        if (logId == null){
            Toast.makeText(context, "Log ID is missing. Cannot add recipe", Toast.LENGTH_SHORT).show()
            return
        }
        if (userId == null){
            Toast.makeText(context, "User Id is missing. Cannot add recipe", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            val failed = mutableListOf<String>()

            recipes.forEach { recipe ->
                val isSuccess = logRepository.addRecipeToLog(
                    userId,
                    logId,
                    recipe.recipeId
                )
                if (!isSuccess) failed.add(recipe.name)
            }

            val message = if (failed.isEmpty()){
                "All recipes have successfully been added!"
            } else {
                "Failed to add ${failed.joinToString(", ") }}"
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        }
    }

    fun removeRecipeToLog(userId: String, millis: Long, recipeId: String, context: Context){
        viewModelScope.launch {
            val isSuccess = logRepository.removeRecipeFromLog(
                userId,
                standardDate(millis).toString(),
                recipeId
            )
            val message = if (isSuccess) "Recipe removed successfully!" else "Failed to remove recipe."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun addProductToCurrentLog(userId: String?, productId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = logRepository.addProductToLog(
                userId,
                _singleLog.value!!.logId,
                productId
            )
            val message = if (isSuccess) "Product added successfully!" else "Failed to add product."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun removeProductToLog(userId: String, millis: Long, productId: String, context: Context){
        viewModelScope.launch {
            val isSuccess = logRepository.removeProductFromLog(
                userId,
                standardDate(millis).toString(),
                productId
            )
            val message = if (isSuccess) "Product removed successfully!" else "Failed to remove product."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun updateRating(userId: String?, rating: Int){
        val currentLog: Log? = _singleLog.value
        if (userId == null) return
        if (currentLog == null) return

        viewModelScope.launch {
            logRepository.updateLogRating(userId, currentLog.logId, rating)
        }
    }

    fun updateNote(userId: String?, millis: Long, note: String, context: Context) {
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = logRepository.updateLogNote(userId, standardDate(millis).toString(), note)

            val message = if (isSuccess) "Note saved successfully!" else "Failed to save note."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteLog(userId: String?, millis: Long){
        if (userId == null) return

        val logId = standardDate(millis).toString()

        viewModelScope.launch {
            val success = logRepository.deleteLog(userId, logId)
            if (success) {
                android.util.Log.d("LogViewModel", "Log deleted successfully")
            } else {
                android.util.Log.e("LogViewModel", "Failed to delete log")
            }
        }
    }
}
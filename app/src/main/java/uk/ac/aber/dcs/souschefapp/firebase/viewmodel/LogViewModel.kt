package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.LogRepository
import uk.ac.aber.dcs.souschefapp.firebase.Log
import java.time.Instant
import java.time.ZoneOffset

class LogViewModel: ViewModel() {
    private val logRepository = LogRepository()

    private var logListener: ListenerRegistration? = null

    private var _logs = MutableLiveData<List<Log>>()
    var logs: LiveData<List<Log>> = _logs

    private fun standardDate(millis: Long): Long =
        Instant.ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

    private fun deleteLogIfNeeded(userId: String, millis: Long){
        val logId = standardDate(millis).toString()
        val log = _logs.value?.find { it.createdBy == userId }

        if (log != null && log.recipeIdList.isNullOrEmpty() && log.productIdList.isNullOrEmpty()) {
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

    fun createLog(userId: String?, millis: Long,  log: Log? = null){
        if (userId == null) {
            android.util.Log.e("LogViewModel", "Failed to create log due to null userId")
            return
        }

        val standardLog = log?.copy(
            date = standardDate(millis),
            createdBy = userId
        ) ?: Log(
            createdBy = userId,
            date = standardDate(millis)
        )

        viewModelScope.launch {
            val isSuccess = logRepository.addLog(
                userId = userId,
                logId = standardDate(millis).toString(),
                log = standardLog
            )
            if (!isSuccess) {
                android.util.Log.e("LogViewModel", "Failed to create log")
            }
        }
    }

    fun readLogs(userId: String?) {
        if (userId == null) return

        logListener?.remove() // Stop previous listener if it exists

        logListener = logRepository.listenForLogs(userId) { logs ->
            _logs.postValue(logs)
        }
    }

    fun stopListening(){
        logListener?.remove()
        logListener = null
    }

    fun addRecipeToLog(userId: String, millis: Long, recipeId: String, context: Context){
        viewModelScope.launch {
            val isSuccess = logRepository.addRecipeToLog(
                userId,
                standardDate(millis).toString(),
                recipeId
            )
            val message = if (isSuccess) "Recipe added successfully!" else "Failed to add recipe."
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
            deleteLogIfNeeded(userId, millis)
        }
    }

    fun addProductToLog(userId: String, millis: Long, productId: String, context: Context){
        viewModelScope.launch {
            val isSuccess = logRepository.addProductToLog(
                userId,
                standardDate(millis).toString(),
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
            deleteLogIfNeeded(userId, millis)
        }
    }

    fun updateRating(userId: String, millis: Long, rating: Int){
        viewModelScope.launch {
            logRepository.updateLogRating(userId, standardDate(millis).toString(), rating)
        }
    }

//    fun updateNote(userId: String, logId: String, note: String){
//        firestoreRepository.updateLogNote(
//            userId = userId,
//            logId = logId,
//            note = note
//        )
//    }

    fun updateNote(userId: String, millis: Long, note: String) {
        viewModelScope.launch {
            logRepository.updateLogNote(userId, standardDate(millis).toString(), note)
        }
    }

    fun findLog(millis: Long): Log?{
        val date = standardDate(millis)
        return _logs.value?.find{ it.date == date }
    }
}
package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.FirestoreRepository
import uk.ac.aber.dcs.souschefapp.firebase.Log
import java.time.Instant
import java.time.ZoneOffset

class LogViewModel: ViewModel() {
    private val firestoreRepository = FirestoreRepository()

    private var _logs = MutableLiveData<List<Log>>()
    var logs: LiveData<List<Log>> = _logs

    private fun standardDate(millis: Long): Long {
        val instant = Instant.ofEpochMilli(millis)
        val startOfDay = instant.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC)
        return startOfDay.toInstant().toEpochMilli()
    }

    private fun deleteLogIfNeeded(username: String, millis: Long){
        val logId = standardDate(millis).toString()
        val log = _logs.value?.find { it.createdBy == username && it.productIdList.isNotEmpty() || it.recipeIdList.isNotEmpty() }

        if (log?.recipeIdList.isNullOrEmpty() && log?.productIdList.isNullOrEmpty()){
            viewModelScope.launch {
                firestoreRepository.deleteLog(username = username, logId = logId) { success ->
                    if (success) {
                        android.util.Log.d("LogViewModel", "Log deleted successfully")
                    } else {
                        android.util.Log.e("LogViewModel", "Failed to delete log")
                    }
                }
            }
        }
    }

    fun createLog(username: String, millis: Long,  log: Log){
        val standardLog = log.copy(
            date = standardDate(millis)
        )
        viewModelScope.launch {
            val isSuccess = firestoreRepository.addLog(
                username = username,
                logId = standardDate(millis).toString(),
                log = standardLog
            )
            if (isSuccess) {
                // Optionally log success or update UI in another way
            } else {
                // Optionally log failure or update UI in another way
            }
        }
    }

    fun readLogs(username: String) {
        firestoreRepository.listenForPosts(username) { logs ->
            _logs.postValue(logs)
        }
    }

    fun addRecipeToLog(username: String, millis: Long, recipeId: String, context: Context){
        firestoreRepository.addRecipeToLog(
            username = username,
            logId = standardDate(millis).toString(),
            recipeId = recipeId
        ){  success ->
            if (success) {
                Toast.makeText(context, "Recipe added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to add recipe.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun removeRecipeToLog(username: String, millis: Long, recipeId: String, context: Context){
        firestoreRepository.removeRecipeFromLog(
            username = username,
            logId = standardDate(millis).toString(),
            recipeId = recipeId
        ){  success ->
            if (success) {
                Toast.makeText(context, "Recipe added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to add recipe.", Toast.LENGTH_SHORT).show()
            }
            deleteLogIfNeeded(username, millis)
        }
    }

    fun addProductToLog(username: String, millis: Long, productId: String, context: Context){
        firestoreRepository.addProductToLog(
            username = username,
            logId = standardDate(millis).toString(),
            productId = productId
        ){  success ->
            if (success) {
                Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to add product.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeProductToLog(username: String, millis: Long, productId: String, context: Context){
        firestoreRepository.removeProductFromLog(
            username = username,
            logId = standardDate(millis).toString(),
            productId = productId
        ){  success ->
            if (success) {
                Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to add product.", Toast.LENGTH_SHORT).show()
            }
            deleteLogIfNeeded(username, millis)
        }
    }

    fun updateRating(username: String, logId: String, rating: Int){
        firestoreRepository.updateLogRating(
            username = username,
            logId = logId,
            rating = rating
        )
    }

    fun updateNote(username: String, logId: String, note: String){
        firestoreRepository.updateLogNote(
            username = username,
            logId = logId,
            note = note
        )
    }

    fun findLog(millis: Long): Log?{
        val date = standardDate(millis)
        return _logs.value?.find{ it.date == date }
    }
}
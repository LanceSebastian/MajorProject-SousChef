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

    fun createLog(userId: String, millis: Long,  log: Log){
        val standardLog = log.copy(
            date = standardDate(millis)
        )
        viewModelScope.launch {
            val isSuccess = firestoreRepository.addLog(
                userId = userId,
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

    fun readLogs(userId: String) {
        firestoreRepository.listenForPosts(userId) { logs ->
            _logs.postValue(logs)
        }
    }

    fun addRecipeToLog(userId: String, millis: Long, recipeId: String, context: Context){
        firestoreRepository.addRecipeToLog(
            userId = userId,
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

    fun removeRecipeToLog(userId: String, millis: Long, recipeId: String, context: Context){
        firestoreRepository.removeRecipeFromLog(
            userId = userId,
            logId = standardDate(millis).toString(),
            recipeId = recipeId
        ){  success ->
            if (success) {
                Toast.makeText(context, "Recipe added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to add recipe.", Toast.LENGTH_SHORT).show()
            }
            deleteLogIfNeeded(userId, millis)
        }
    }

    fun addProductToLog(userId: String, millis: Long, productId: String, context: Context){
        firestoreRepository.addProductToLog(
            userId = userId,
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
            userId = userId,
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

    fun updateRating(userId: String, logId: String, rating: Int){
        firestoreRepository.updateLogRating(
            userId = userId,
            logId = logId,
            rating = rating
        )
    }

    fun updateNote(userId: String, logId: String, note: String){
        firestoreRepository.updateLogNote(
            userId = userId,
            logId = logId,
            note = note
        )
    }

    fun findLog(millis: Long): Log?{
        val date = standardDate(millis)
        return _logs.value?.find{ it.date == date }
    }
}
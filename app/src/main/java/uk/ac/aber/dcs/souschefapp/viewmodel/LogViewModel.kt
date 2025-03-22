package uk.ac.aber.dcs.souschefapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.database.models.Account
import uk.ac.aber.dcs.souschefapp.database.models.Log

/*      Handles Logs       */
class LogViewModel(
    application: Application
) : AndroidViewModel(application) {
    val repository: SousChefRepository = SousChefRepository(application)

    fun insertLog(log: Log){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertLog(log)
        }
    }

    fun updateLog(log: Log){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateLog(log)
        }
    }

    fun deleteLog(log: Log){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteLog(log)
        }
    }

    fun getAllLogsFromAccount(accountOwnerId: Int): LiveData<List<Log>> {
        return repository.getAllLogsFromAccount(accountOwnerId)
    }
}
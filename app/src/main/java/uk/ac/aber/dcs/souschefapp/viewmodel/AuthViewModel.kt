package uk.ac.aber.dcs.souschefapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.SousChefDatabase
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.database.models.Account

/*      Handles Account authentication (Login/Signup)       */
class AuthViewModel (
    application: Application
) : AndroidViewModel(application) {
    private val repository: SousChefRepository = SousChefRepository(application)
    private val userPreferences = UserPreferences(application)

    val isAuthenticated = userPreferences.isLoggedIn
    val userEmail = userPreferences.email
    val userName = userPreferences.username
    val userId = userPreferences.accountId

    fun login(username:String, password:String){
        viewModelScope.launch(Dispatchers.IO){
            val account = repository.login(username, password)
            if (account != null) {
                userPreferences.saveLoggedInUser(account.accountId, account.username, account.email)
            }
        }
    }

    fun register(account: Account){
        viewModelScope.launch(Dispatchers.IO){
            val success = repository.register(account)
            if (success) {
                userPreferences.saveLoggedInUser(account.accountId, account.username, account.email)
            }
        }
    }

    fun updateAccount(account: Account){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateAccount(account)
        }
    }

    fun deactivateAccount(account: Account){
        viewModelScope.launch(Dispatchers.IO){
            repository.deactivateAccount(account.accountId)
        }
    }

    fun getAccountById(accountId: Int): LiveData<Account> {
        return repository.getAccountById(accountId)
    }

    fun getAccountByEmail(email: String): LiveData<Account> {
        return repository.getAccountByEmail(email)
    }

    fun getAccountByUsername(username: String): LiveData<Account> {
        return repository.getAccountByUsername(username)
    }
}

sealed class LoginState {
    object Idle: LoginState()
    object Loading: LoginState()
    data class Success(val account: Account) : LoginState()
    data class Error(val message: String) : LoginState()
}
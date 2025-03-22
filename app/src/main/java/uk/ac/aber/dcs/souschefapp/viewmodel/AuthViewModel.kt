package uk.ac.aber.dcs.souschefapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.UserPreferences
import uk.ac.aber.dcs.souschefapp.database.models.Account

/*      Handles Account authentication (Login/Signup)       */
class AuthViewModel (
    application: Application
) : AndroidViewModel(application) {
    private val repository: SousChefRepository = SousChefRepository(application)
    private val userPreferences = UserPreferences(application)

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun insertAccount(account: Account){
        val email = account.email
        viewModelScope.launch(Dispatchers.IO){
            if (!repository.getAccountByEmail(email).equals(null)) return@launch
            repository.insertAccount(account)
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

    fun login(username:String, password:String){
        viewModelScope.launch(Dispatchers.IO){
            repository.login(username, password).observeForever { account ->
                if (account != null){
                    _loginState.value = LoginState.Success(account)

                    viewModelScope.launch(Dispatchers.IO) {
                        userPreferences.saveLoggedInUser(
                            getApplication(),
                            account.accountId,
                            account.username,
                            account.email,
                            account.password
                        )
                    }
                } else {
                    _loginState.value = LoginState.Error("Username or Password is incorrect.")
                }
            }



        }
    }
}

sealed class LoginState {
    object Idle: LoginState()
    object Loading: LoginState()
    data class Success(val account: Account) : LoginState()
    data class Error(val message: String) : LoginState()
}
package uk.ac.aber.dcs.souschefapp.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val ACCOUNTID =  intPreferencesKey("account_id")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
    }

    val isLoggedIn = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }.asLiveData()

    val accountId = dataStore.data.map { preferences ->
        preferences[ACCOUNTID]
    }.asLiveData()

    val username = dataStore.data.map { preferences ->
        preferences[USERNAME]
    }.asLiveData()

    val email = dataStore.data.map { preferences ->
        preferences[EMAIL]
    }.asLiveData()

    // Save login state and username
    suspend fun saveLoggedInUser(accountId: Int, username: String, email: String) {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[ACCOUNTID] = accountId
            prefs[USERNAME] = username
            prefs[EMAIL] = email
        }
    }

    // ✅ Logout (clear data)
    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs.remove(USERNAME)
            prefs.remove(EMAIL)
        }
    }

}
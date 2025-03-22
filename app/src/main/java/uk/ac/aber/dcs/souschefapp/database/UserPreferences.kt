package uk.ac.aber.dcs.souschefapp.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val ACCOUNTID =  intPreferencesKey("account_id")
    val USERNAME = stringPreferencesKey("username")
    val EMAIL = stringPreferencesKey("email")
    val PASSWORD = stringPreferencesKey("password")

    // Save login state and username
    suspend fun saveLoggedInUser(context: Context, accountId: Int, username: String, email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[ACCOUNTID] = accountId
            prefs[USERNAME] = username
            prefs[EMAIL] = email
            prefs[PASSWORD] = password
        }
    }

    // Get login state as Flow<Boolean>
    fun getLoginState(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LOGGED_IN] ?: false
        }
    }

    // Get stored accountId as Flow<Int>
    fun getLoggedInAccountId(context: Context): Flow<Int?> {
        return context.dataStore.data.map { prefs ->
            prefs[ACCOUNTID]
        }
    }

    // Get stored username as Flow<String?>
    fun getLoggedInUsername(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[USERNAME]
        }
    }

    // Get stored username as Flow<String?>
    fun getLoggedInEmail(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[EMAIL]
        }
    }

    // Get stored username as Flow<String?>
    fun getLoggedInPassword(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[PASSWORD]
        }
    }



    // ✅ Logout (clear data)
    suspend fun logout(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs.remove(USERNAME)
            prefs.remove(PASSWORD)
            prefs.remove(EMAIL)
        }
    }

}
// Create a new file: /app/src/main/java/com/example/kipku/data/SessionManager.kt
package org.whynot.kipku.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // Keys to store the data
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    // Function to save the login state
    suspend fun saveLoginSession(email: String) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = true
            preferences[USER_EMAIL_KEY] = email
        }
    }

    // Function to clear the login state (for logout)
    suspend fun clearLoginSession() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN_KEY)
            preferences.remove(USER_EMAIL_KEY)
        }
    }

    // A Flow to observe the login state in real-time
    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }

    // A Flow to get the logged-in user's email
    val userEmailFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY]
    }
}

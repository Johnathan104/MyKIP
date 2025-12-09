package org.whynot.kipku.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("onboarding_prefs")

class OnboardingDataStore(private val context: Context) {

    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

    val isCompleted: Flow<Boolean> = context.dataStore.data.map {
        it[ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setCompleted(value: Boolean) {
        context.dataStore.edit {
            it[ONBOARDING_COMPLETED] = value
        }
    }
}

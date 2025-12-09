package org.whynot.kipku.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 🔥 DataStore harus di luar class → singleton & tidak dibuat ulang
val Context.languageDataStore by preferencesDataStore(name = "settings")

class LanguagePreference(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }

    // Ambil bahasa dari DataStore
    val languageFlow: Flow<String> = context.languageDataStore.data
        .map { prefs -> prefs[LANGUAGE_KEY] ?: "id" }

    // Simpan bahasa ke DataStore
    suspend fun setLanguage(lang: String) {
        context.languageDataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = lang
        }
    }
}

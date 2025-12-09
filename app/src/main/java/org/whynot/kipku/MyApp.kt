package org.whynot.kipku

import android.app.Application
import org.whynot.kipku.data.LanguagePreference

class MyKIPApp : Application() {

    companion object {
        lateinit var languagePreference: LanguagePreference
            private set
    }

    override fun onCreate() {
        super.onCreate()
        languagePreference = LanguagePreference(this)
    }
}


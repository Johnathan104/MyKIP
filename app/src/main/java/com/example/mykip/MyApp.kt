package com.example.mykip

import android.app.Application
import com.example.mykip.data.LanguagePreference

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


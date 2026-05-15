package com.kashta.kala

import android.app.Application

/**
 * Application class for Kashta-Kala.
 * Initialize Firebase and other global services here.
 */
class KashtaKalaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // TODO: Initialize Firebase when google-services.json is added
        // FirebaseApp.initializeApp(this)
    }
}

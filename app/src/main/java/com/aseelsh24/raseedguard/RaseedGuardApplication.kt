package com.aseelsh24.raseedguard

import android.app.Application

class RaseedGuardApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

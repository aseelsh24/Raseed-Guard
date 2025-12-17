package com.aseelsh24.raseedguard

import android.app.Application
import com.aseelsh24.raseedguard.data.AppContainer
import com.aseelsh24.raseedguard.data.AppDataContainer

class RaseedGuardApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

package ru.veider.multitimer

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import ru.veider.multitimer.service.CountersService

class CountersApp : Application() {

    companion object {
        private var application: Application? = null
        fun getInstance() = application
    }

    override fun onCreate() {
        application = this
        val intent = Intent(applicationContext, CountersService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(applicationContext, intent)
        else
            this.startService(intent)
        super.onCreate()
    }

}
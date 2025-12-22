package ru.veider.multitimer

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.veider.multitimer.di.appModule
import ru.veider.multitimer.di.gsonModule
import ru.veider.multitimer.di.navigationModule
import ru.veider.multitimer.di.repoModule
import ru.veider.multitimer.service.CountersService

class App : Application() {

    companion object {
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                appModule,
                repoModule,
                gsonModule,
                navigationModule
            )
        }
        val intent = Intent(this, CountersService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(applicationContext, intent)
        else
            this.startService(intent)
    }
}

val app get()= App.instance!!
package ru.veider.multitimer

import android.app.Application

class CountersApp : Application() {

    companion object {
        private var application: Application? = null
        fun getInstance() = application
    }

    override fun onCreate() {
        application = this
        super.onCreate()
    }
}
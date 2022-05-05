package com.fairfareindia

import android.app.Application
import android.content.Context

class CustomerApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: CustomerApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        // initialize for any

        // Use ApplicationContext.
        // example: SharedPreferences etc...
        val context: Context = CustomerApplication.applicationContext()
    }
}
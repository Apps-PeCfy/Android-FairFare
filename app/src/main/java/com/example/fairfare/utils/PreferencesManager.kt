package com.example.fairfare.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager private constructor(context: Context) {
    private val mPref: SharedPreferences
    fun setStringValue(KEY_VALUE: String?, value: String?) {
        mPref.edit()
            .putString(KEY_VALUE, value)
            .apply()
    }

    fun getStringValue(KEY_VALUE: String?): String? {
        return mPref.getString(KEY_VALUE, "")
    }

    fun setIntegerValue(KEY_VALUE: String?, value: Int) {
        mPref.edit()
            .putInt(KEY_VALUE, value)
            .apply()
    }

    fun getIntegerValue(KEY_VALUE: String?): Int {
        return mPref.getInt(KEY_VALUE, 0)
    }

    fun setBooleanValue(KEY_VALUE: String?, value: Boolean) {
        mPref.edit().putBoolean(KEY_VALUE, value).apply()
    }

    fun getBooleanValue(KEY_VALUE: String?, defaultValue: Boolean): Boolean {
        return mPref.getBoolean(KEY_VALUE, defaultValue)
    }

    fun remove(key: String?) {
        mPref.edit()
            .remove(key)
            .apply()
    }

    fun clear(): Boolean {
        return mPref.edit()
            .clear()
            .commit()
    }

    companion object {
        private const val PREF_NAME = "FairFare"
        private var sInstance: PreferencesManager? = null

        @Synchronized
        fun initializeInstance(context: Context) {
            if (sInstance == null) {
                sInstance = PreferencesManager(context)
            }
        }

        @get:Synchronized
        val instance: PreferencesManager?
            get() {
                checkNotNull(sInstance) {
                    PreferencesManager::class.java.simpleName +
                            " is not initialized, call initializeInstance(..) method first."
                }
                return sInstance
            }
    }

    init {
        mPref = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
    }
}
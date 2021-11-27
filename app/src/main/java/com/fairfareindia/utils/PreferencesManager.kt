package com.fairfareindia.utils

import android.content.Context
import android.content.SharedPreferences
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.google.gson.Gson

class PreferencesManager private constructor(context: Context) {
    private val mPref: SharedPreferences
    private val cityList_key : String = "ALLOWED_CITY_LIST"
    private val KEY_LANGUAGE : String = "language"
    private val KEY_LANGUAGE_CHANGE : String = "language_change"

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

    fun setLanguage(language: String?) {
        mPref.edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    fun getLanguage(): String? {
        return mPref.getString(KEY_LANGUAGE, "en-gb")
    }

    fun getIntegerValue(KEY_VALUE: String?): Int {
        return mPref.getInt(KEY_VALUE, 0)
    }

    fun setLanguageChanged(value: Boolean) {
        mPref.edit().putBoolean(KEY_LANGUAGE_CHANGE, value).apply()
    }

    fun isLanguageChanged(): Boolean {
        return mPref.getBoolean(KEY_LANGUAGE_CHANGE, false)
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

    fun setCityList(cityList: List<GetAllowCityResponse.CitiesItem>) {
        val gson = Gson()
        val jsonString = gson.toJson(cityList)
        mPref.edit().putString(cityList_key, jsonString).apply()
    }

    fun getCityList(): List<GetAllowCityResponse.CitiesItem> {
        var cityList: List<GetAllowCityResponse.CitiesItem> = ArrayList()
        val gson = Gson()
        val jsonString = mPref.getString(cityList_key, "")
        if(jsonString != null && jsonString.isNotEmpty()){
            cityList = gson.fromJson(jsonString, Array<GetAllowCityResponse.CitiesItem>::class.java).asList()
        }
        return cityList
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
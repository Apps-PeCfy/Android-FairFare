package com.fairfareindia.utils

import android.content.Context
import com.fairfareindia.ui.home.pojo.GeneralSettingModel
import com.fairfareindia.ui.otp.pojo.VerifyOTPResponsePojo

open class SessionManager(context: Context) {
    private var mContext : Context = context
    private var userModel:  VerifyOTPResponsePojo.User ?= null
    private var generalSettingModel:  GeneralSettingModel ?= null
    var isSplashDisplayed = false


    open fun getUserModel():  VerifyOTPResponsePojo.User? {
        if (userModel == null) {
            PreferencesManager.initializeInstance(context = mContext)
            userModel = PreferencesManager.instance?.getUserModel()
        }
        return userModel
    }

    open fun setUserModel(generalSettingModel: VerifyOTPResponsePojo.User) {
        this.userModel = userModel
    }

    open fun getGeneralSettingModel():  GeneralSettingModel? {
        if (userModel == null) {
            PreferencesManager.initializeInstance(context = mContext)
            generalSettingModel = PreferencesManager.instance?.getGeneralSettingModel()
        }
        return generalSettingModel
    }

    open fun setGeneralSettingModel(generalSettingModel: GeneralSettingModel) {
        this.generalSettingModel = generalSettingModel
    }



    companion object {
        private var sInstance: SessionManager? = null

        @Synchronized
        fun getInstance(context: Context) : SessionManager {
            if (sInstance == null) {
                sInstance = SessionManager(context)
            }
            return sInstance as SessionManager
        }
    }


}
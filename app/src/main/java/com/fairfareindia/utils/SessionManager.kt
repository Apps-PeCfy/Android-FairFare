package com.fairfareindia.utils

import android.content.Context
import com.fairfareindia.ui.otp.pojo.VerifyOTPResponsePojo

open class SessionManager(context: Context) {
    private var mContext : Context = context
    private var userModel:  VerifyOTPResponsePojo.User ?= null


    open fun getUserModel():  VerifyOTPResponsePojo.User? {
        if (userModel == null) {
            PreferencesManager.initializeInstance(context = mContext)
            userModel = PreferencesManager.instance?.getUserModel()
        }
        return userModel
    }

    open fun setUserModel(userModel:  VerifyOTPResponsePojo.User) {
        this.userModel = userModel
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
package com.fairfareindia.ui.Login

interface ILoginPresenter {
    fun login(
        phoneNo: String?,
        type: String?,
        deviceType: String?,
        loginType: String?,
        countryCode: String?,
        name: String?,
        email: String?,
        token: String?
    )

    fun socialLogin(
        deviceType: String?,
        loginType: String?,
        name: String?,
        providerId: String?,
        token: String?,
        email: String?,
        deviceID: String?,
        device_token: String?
    )
}
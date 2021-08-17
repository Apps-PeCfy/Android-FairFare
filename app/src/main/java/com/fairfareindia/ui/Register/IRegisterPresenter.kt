package com.fairfareindia.ui.Register

interface IRegisterPresenter {
    fun register(
        phoneNo: String?,
        type: String?,
        deviceType: String?,
        loginType: String?,
        countryCode: String?,
        name: String?,
        email: String?,
        token: String?
    )
}
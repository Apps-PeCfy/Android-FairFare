package com.fairfareindia.ui.otp

interface IOtpPresenter {
    fun verifyOtp(
        phoneNo: String?,
        type: String?,
        deviceType: String?,
        loginType: String?,
        countryCode: String?,
        name: String?,
        email: String?,
        gender: String?,
        otp: String?,
        deviceId: String?,
        device_token: String?,
        register_Latitude: String?,
        register_Longitude: String?
    )


    fun verifyOtpWithReferel(
        phoneNo: String?,
        type: String?,
        deviceType: String?,
        loginType: String?,
        countryCode: String?,
        name: String?,
        email: String?,
        gender: String?,
        otp: String?,
        deviceId: String?,
        device_token: String?,
        register_Latitude: String?,
        register_Longitude: String?,
        refer_code: String?
    )

    fun resendOtp(
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
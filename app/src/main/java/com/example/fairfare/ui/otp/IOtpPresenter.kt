package com.example.fairfare.ui.otp

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
        deviceId: String?
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
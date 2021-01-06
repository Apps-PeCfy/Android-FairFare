package com.example.fairfare.ui.otp

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.otp.pojo.VerifyOTPResponsePojo

interface IOtpView : IBaseView {
    fun otpSuccess(verifyOTPResponsePojo: VerifyOTPResponsePojo?)
    fun reSendOTPSuccess(loginResponsepojo: LoginResponsepojo?)
    fun validationError(validationResponse: ValidationResponse?)
}
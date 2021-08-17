package com.fairfareindia.ui.otp

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.otp.pojo.VerifyOTPResponsePojo

interface IOtpView : IBaseView {
    fun otpSuccess(verifyOTPResponsePojo: VerifyOTPResponsePojo?)
    fun reSendOTPSuccess(loginResponsepojo: LoginResponsepojo?)
    fun validationError(validationResponse: ValidationResponse?)
}
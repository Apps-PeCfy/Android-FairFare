package com.example.fairfare.ui.Login

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.Login.pojo.ValidationResponse

interface ILoginView : IBaseView {
    fun onLoginSUccess(loginResponsepojo: LoginResponsepojo?)
    fun validationError(validationResponse: ValidationResponse?)
    fun socialLoginSuccess(loginResponsepojo: LoginResponsepojo?)
}
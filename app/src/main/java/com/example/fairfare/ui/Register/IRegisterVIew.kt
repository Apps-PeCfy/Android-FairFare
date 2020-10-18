package com.example.fairfare.ui.Register

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.Login.pojo.ValidationResponse

interface IRegisterVIew : IBaseView {
    fun onLoginSUccess(loginResponsepojo: LoginResponsepojo?)
    fun validationError(validationResponse: ValidationResponse?)
}
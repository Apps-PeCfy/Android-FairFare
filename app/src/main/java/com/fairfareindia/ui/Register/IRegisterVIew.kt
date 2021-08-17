package com.fairfareindia.ui.Register

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.Login.pojo.ValidationResponse

interface IRegisterVIew : IBaseView {
    fun onLoginSUccess(loginResponsepojo: LoginResponsepojo?)
    fun validationError(validationResponse: ValidationResponse?)
}
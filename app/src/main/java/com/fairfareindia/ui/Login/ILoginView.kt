package com.fairfareindia.ui.Login

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.Login.pojo.ValidationResponse

interface ILoginView : IBaseView {
    fun onLoginSUccess(loginResponsepojo: LoginResponsepojo?)
    fun validationError(validationResponse: ValidationResponse?)
    fun socialLoginSuccess(loginResponsepojo: LoginResponsepojo?)
}
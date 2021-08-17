package com.fairfareindia.ui.Login

import com.fairfareindia.networking.ApiClient.client
import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginPresenterImplementer(var view: ILoginView) : ILoginPresenter {
    override fun login(
        phoneNo: String?,
        type: String?,
        deviceType: String?,
        loginType: String?,
        countryCode: String?,
        name: String?,
        email: String?,
        token: String?
    ) {
        view.showWait()
        val call = client.login(
            phoneNo, type, deviceType, loginType,
            countryCode, name, email, token
        )
        call!!.enqueue(object : Callback<LoginResponsepojo?> {
            override fun onResponse(
                call: Call<LoginResponsepojo?>,
                response: Response<LoginResponsepojo?>
            ) {
                view.removeWait()
                if (response.code() == 200) {
                    view.onLoginSUccess(response.body())
                } else if (response.code() == 400||response.code()==422) {
                    view.removeWait()
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        view.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                }
            }

            override fun onFailure(
                call: Call<LoginResponsepojo?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun socialLogin(
        deviceType: String?,
        loginType: String?,
        name: String?,
        providerId: String?,
        token: String?,
        email: String?,
        deviceID: String?,
        device_token: String?
    ) {
        view.showWait()
        val call = client.sociallogin(
            deviceType, loginType,
            name, providerId, token, email,deviceID,device_token
        )
        call!!.enqueue(object : Callback<LoginResponsepojo?> {
            override fun onResponse(
                call: Call<LoginResponsepojo?>,
                response: Response<LoginResponsepojo?>
            ) {
                view.removeWait()
                if (response.code() == 200) {
                    view.socialLoginSuccess(response.body())
                }
                else if (response.code() == 400||response.code()==422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        view.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                }
            }

            override fun onFailure(
                call: Call<LoginResponsepojo?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

}
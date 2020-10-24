package com.example.fairfare.ui.otp

import com.example.fairfare.networking.ApiClient.client
import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.otp.pojo.VerifyOTPResponsePojo
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OtpPresenterImplmenter(var view: IOtpView) : IOtpPresenter {
    override fun verifyOtp(
        phoneNo: String?,
        type: String?,
        deviceType: String?,
        loginType: String?,
        countryCode: String?,
        name: String?,
        email: String?,
        gender: String?,
        otp: String?
    ) {
        view.showWait()
        val call = client.verifyOtp(
            phoneNo, type, deviceType, loginType,
            countryCode, name, email, gender, otp
        )
        call!!.enqueue(object : Callback<VerifyOTPResponsePojo?> {
            override fun onResponse(call: Call<VerifyOTPResponsePojo?>, response: Response<VerifyOTPResponsePojo?>) {
                view.removeWait()
                if (response.code() == 200) {
                    view.otpSuccess(response.body())
                } else if (response.code() == 400||response.code()==422) {
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
                call: Call<VerifyOTPResponsePojo?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun resendOtp(
        phoneNo: String?,
        type: String?,
        deviceType: String?,
        loginType: String?,
        countryCode: String?,
        name: String?,
        email: String?,
        token: String?
    ) {
        val call = client.login(
            phoneNo, type, deviceType, loginType,
            countryCode, name, email, token
        )
        view.showWait()
        call!!.enqueue(object : Callback<LoginResponsepojo?> {
            override fun onResponse(
                call: Call<LoginResponsepojo?>,
                response: Response<LoginResponsepojo?>
            ) {
                view.removeWait()
                if (response.code() == 200) {
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
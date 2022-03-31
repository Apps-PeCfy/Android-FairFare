package com.fairfareindia.ui.drawer.servicepartners

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ServicePartnersImplementer(private val view: IServicePartnersView) : IServicePartnersPresenter {
    
    override fun getServicePartners(token: String?) {
        view.showWait()
        val call = ApiClient.client.getServicePartners(
            "Bearer $token")
        call!!.enqueue(object : Callback<ServicePartnerModel?> {
            override fun onResponse(
                call: Call<ServicePartnerModel?>,
                response: Response<ServicePartnerModel?>
            ) {

                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getServicePartnersSuccess(response.body())
                    }
                } else if (response.code() == 422) {
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

                }else{
                    view.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<ServicePartnerModel?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }


}
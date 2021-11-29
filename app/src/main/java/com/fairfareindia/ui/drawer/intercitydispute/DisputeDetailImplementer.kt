package com.fairfareindia.ui.drawer.intercitydispute

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DisputeDetailImplementer(private val view: IDisputeDetailView) : IDisputeDetailPresenter {

    override fun getDisputeDetail(token: String?, disputeID: String?) {
        view.showWait()
        val call = ApiClient.client.getInterCityDisputeDetail("Bearer $token", disputeID)
        call!!.enqueue(object : Callback<DisputeDetailModel?> {
            override fun onResponse(
                call: Call<DisputeDetailModel?>,
                response: Response<DisputeDetailModel?>
            ) {

                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getDisputeDetailSuccess(response.body())
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
                call: Call<DisputeDetailModel?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun fileComplaint(token: String?, id: String?) {
        view.showWait()
        val call = ApiClient.client.saveComplaint("Bearer $token", id)
        call!!.enqueue(object : Callback<DeleteDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<DeleteDisputResponsePOJO?>,
                response: Response<DeleteDisputResponsePOJO?>
            ) {

                view.removeWait()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.fileComplaintSuccess(response.body())
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

                } else {
                    view.onFailure("Internal Server Error")

                }
            }

            override fun onFailure(
                call: Call<DeleteDisputResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

}
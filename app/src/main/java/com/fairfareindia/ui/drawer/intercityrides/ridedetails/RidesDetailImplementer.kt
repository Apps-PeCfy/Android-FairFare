package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RidesDetailImplementer(private val view: IRideDetailView) : IRidesDetailPresenter {

    override fun getRideDetail(token: String?, rideID: String?) {
        view.showWait()
        val call = ApiClient.client.getRideDetails("Bearer $token", rideID)
        call!!.enqueue(object : Callback<RideDetailModel?> {
            override fun onResponse(
                call: Call<RideDetailModel?>,
                response: Response<RideDetailModel?>
            ) {

                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getRideDetailSuccess(response.body())
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
                call: Call<RideDetailModel?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }


}
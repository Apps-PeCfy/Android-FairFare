package com.fairfareindia.ui.drawer.intercityrides

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RidesImplementer(private val view: IRidesView) : IRidesPresenter {

    override fun getRide(token: String?,cnt:Int?,currentLat: String?,currentLong: String?) {


        view.showWait()
        val call = ApiClient.client.getMyRides(
             token,cnt,currentLat,currentLong)
        call!!.enqueue(object : Callback<GetRideResponsePOJO?> {
            override fun onResponse(
                call: Call<GetRideResponsePOJO?>,
                response: Response<GetRideResponsePOJO?>
            ) {

                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getRidesSuccess(response.body())
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
                call: Call<GetRideResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun cancelRide(token: String?, rideID: String?, status: String?) {
        view.showWait()
        val call = ApiClient.client.cancelRide("Bearer $token", rideID, status)
        call!!.enqueue(object : Callback<GetRideResponsePOJO?> {
            override fun onResponse(
                call: Call<GetRideResponsePOJO?>,
                response: Response<GetRideResponsePOJO?>
            ) {

                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getCancelRideSuccess(response.body())
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
                call: Call<GetRideResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }


}
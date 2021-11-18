package com.fairfareindia.ui.intercitytrackride

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class InterCityTrackRideImplementer(private val trackRideView: IIntercityTrackRideView) :
    IInterCityTrackRidePresenter {


    
    override fun getRideDetails(token: String?, ride_id: String?) {
        trackRideView.showWait()
        val call = ApiClient.client.getRideDetails(
            "Bearer $token",
            ride_id
        )
        call!!.enqueue(object : Callback<RideDetailModel?> {
            override fun onResponse(
                call: Call<RideDetailModel?>,
                response: Response<RideDetailModel?>
            ) {
                trackRideView.removeWait()

                if (response.code() == 200) {
                    trackRideView.removeWait()

                    trackRideView.getRideDetails(response.body())
                } else if (response.code() == 400 || response.code() == 422) {
                    trackRideView.removeWait()

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        trackRideView.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                } else {
                    trackRideView.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<RideDetailModel?>,
                t: Throwable
            ) {
                trackRideView.removeWait()
                trackRideView.onFailure(t.message)
            }
        })
    }



}

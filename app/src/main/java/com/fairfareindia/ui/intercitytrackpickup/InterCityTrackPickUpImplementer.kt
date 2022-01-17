package com.fairfareindia.ui.intercitytrackpickup

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.intercityrides.GetRideResponsePOJO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class InterCityTrackPickUpImplementer(private val trackPickUpView: IIntercityTrackPickUpView) :
    IInterCityTrackPickUpPresenter {


    
    override fun getRideDetails(token: String?, ride_id: String?) {
        trackPickUpView.showWait()
        val call = ApiClient.client.getRideDetails(
            "Bearer $token",
            ride_id
        )
        call!!.enqueue(object : Callback<RideDetailModel?> {
            override fun onResponse(
                call: Call<RideDetailModel?>,
                response: Response<RideDetailModel?>
            ) {
                trackPickUpView.removeWait()

                if (response.code() == 200) {
                    trackPickUpView.removeWait()

                    trackPickUpView.getRideDetails(response.body())
                } else if (response.code() == 400 || response.code() == 422) {
                    trackPickUpView.removeWait()

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        trackPickUpView.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                } else {
                    trackPickUpView.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<RideDetailModel?>,
                t: Throwable
            ) {
                trackPickUpView.removeWait()
                trackPickUpView.onFailure(t.message)
            }
        })
    }

    override fun getDriverLocation(token: String?, ride_id: String?) {

        val call = ApiClient.client.getDriverLocation(
            "Bearer $token",
            ride_id
        )
        call!!.enqueue(object : Callback<DriverLocationModel?> {
            override fun onResponse(
                call: Call<DriverLocationModel?>,
                response: Response<DriverLocationModel?>
            ) {
                trackPickUpView.removeWait()

                if (response.code() == 200) {
                    trackPickUpView.removeWait()

                    trackPickUpView.getDriverLocation(response.body())
                } else if (response.code() == 400 || response.code() == 422) {
                    trackPickUpView.removeWait()

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        trackPickUpView.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                } else {
                    trackPickUpView.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<DriverLocationModel?>,
                t: Throwable
            ) {
                trackPickUpView.removeWait()
                trackPickUpView.onFailure(t.message)
            }
        })
    }

    override fun cancelRide(token: String?, rideID: String?, status: String?) {
        trackPickUpView.showWait()
        val call = ApiClient.client.cancelRide("Bearer $token", rideID, status)
        call!!.enqueue(object : Callback<GetRideResponsePOJO?> {
            override fun onResponse(
                call: Call<GetRideResponsePOJO?>,
                response: Response<GetRideResponsePOJO?>
            ) {

                trackPickUpView.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        trackPickUpView.removeWait()
                        trackPickUpView.getCancelRideSuccess(response.body())
                    }
                } else if (response.code() == 422) {
                    trackPickUpView.removeWait()
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        trackPickUpView.validationError(pojo)
                    } catch (exception: IOException) {
                    }

                }else{
                    trackPickUpView.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<GetRideResponsePOJO?>,
                t: Throwable
            ) {
                trackPickUpView.removeWait()
                trackPickUpView.onFailure(t.message)
            }
        })
    }

}

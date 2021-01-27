package com.example.fairfare.ui.viewride

import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ViewRideImplementer(private val view: IViesRideView) : IViewRidePresenter {
    override fun schduleRide(
        token: String?,
        vehicle_rate_card_id: String?,
        luggage_quantity: String?,
        schedule_date: String?,
        origin_place_id: String?,
        destination_place_id: String?,
        overview_polyline: String?,
        distance: String?,
        duration: String?,
        city_id: String?,
        airport_rate_card_id: String?,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        sourceAdddress:String?,
        destinationAddress:String?
    )
    {
        view.showWait()
        val call = ApiClient.client.schduleRide(
            "Bearer $token",
            vehicle_rate_card_id,
            luggage_quantity,
            schedule_date,
            origin_place_id,
            destination_place_id,
            overview_polyline,
            distance,
            duration,
            city_id,
            airport_rate_card_id,sLat,sLong,dLat,dLong,sourceAdddress,destinationAddress
        )
        call!!.enqueue(object : Callback<ScheduleRideResponsePOJO?> {
            override fun onResponse(
                call: Call<ScheduleRideResponsePOJO?>,
                response: Response<ScheduleRideResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.schduleRideSuccess(response.body())
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

                }
            }

            override fun onFailure(
                call: Call<ScheduleRideResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }
}

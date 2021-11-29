package com.fairfareindia.ui.intercityviewride

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class InterCityViewRideImplementer(private val viewRideView: IIntercityViewRideView) :
    IInterCityViewRidePresenter {

    override fun bookingRequest(
        token: String?,
        type: String?,
        from_city_id: String?,
        to_city_id: String?,
        origin_address: String?,
        destination_address: String?,
        origin_latitude: String?,
        origin_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        shedule_date: String?,
        way_flag: String?,
        intercity_ratecard_id: String?,
        shedule_type: String?,
        luggage_quantity: String?,
        luggage_charges: String?,
        distance: String?,
        travel_time: String?,
        travel_time_second: String?,
        amount: String?,
        transaction_id: String?,
        method: String?,
        payment_status: String?,
        gateway_tye: String?
    ) {
        viewRideView.showWait()
        val call = ApiClient.client.bookingRequest(
            "Bearer $token",
            type,
            from_city_id,
            to_city_id,
            origin_address,
            destination_address,
            origin_latitude,
            origin_longitude,
            destination_latitude,
            destination_longitude,
            shedule_date,
            way_flag,
            intercity_ratecard_id,
            shedule_type,
            luggage_quantity,
            luggage_charges,
            distance,
            travel_time,
            travel_time_second,
            amount,
            transaction_id,
            method,
            payment_status,
            gateway_tye
        )
        call!!.enqueue(object : Callback<BookingRequestModel?> {
            override fun onResponse(
                call: Call<BookingRequestModel?>,
                response: Response<BookingRequestModel?>
            ) {
                viewRideView.removeWait()

                if (response.code() == 200) {
                    viewRideView.removeWait()

                    viewRideView.bookingRequestSuccess(response.body())
                } else if (response.code() == 400 || response.code() == 422) {
                    viewRideView.removeWait()

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        viewRideView.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                } else {
                    viewRideView.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<BookingRequestModel?>,
                t: Throwable
            ) {
                viewRideView.removeWait()
                viewRideView.onFailure(t.message)
            }
        })
    }

    override fun getViewRideDetails(
        token: String?,
        intercity_rate_card_id: String?,
        total_dist: String?,
        luggage_quantity: String?
    ) {
        viewRideView.showWait()
        val call = ApiClient.client.getViewRideDetails(
            "Bearer $token",
            intercity_rate_card_id,
            total_dist,
            luggage_quantity
        )
        call!!.enqueue(object : Callback<ViewRideModel?> {
            override fun onResponse(
                call: Call<ViewRideModel?>,
                response: Response<ViewRideModel?>
            ) {
                viewRideView.removeWait()

                if (response.code() == 200) {
                    viewRideView.removeWait()

                    viewRideView.getViewRideDetails(response.body())
                } else if (response.code() == 400 || response.code() == 422) {
                    viewRideView.removeWait()

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        viewRideView.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                } else {
                    viewRideView.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<ViewRideModel?>,
                t: Throwable
            ) {
                viewRideView.removeWait()
                viewRideView.onFailure(t.message)
            }
        })
    }

}

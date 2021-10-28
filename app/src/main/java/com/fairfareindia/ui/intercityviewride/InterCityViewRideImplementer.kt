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
        driverId: String?,
        permitType: String?,
        originAddress: String?,
        destinationAddress: String?,
        originLatitude: String?,
        originLongitude: String?,
        destinationLatitude: String?,
        destinationLongitude: String?,
        sheduleDate: String?,
        wayFlag: String?,
        vehicle_rate_card_id: String?,
        intercity_ratecard_id: String?,
        status: String?
    ) {
        viewRideView.showWait()
        val call = ApiClient.client.bookingRequest(
            "Bearer $token",
            driverId,
            permitType,
            originAddress,
            destinationAddress,
            originLatitude,
            originLongitude,
            destinationLatitude,
            destinationLongitude,
            sheduleDate,
            wayFlag,
            vehicle_rate_card_id,
            intercity_ratecard_id,
            status
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

}

package com.fairfareindia.ui.intercityviewride

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.utils.Constants
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
        gateway_type: String?,
        firstRideTotal: String?,
        secondRideTotal: String?,
        secondRidePercentageToPay: String?,
        amountToCollect: String?,
        tolls: ArrayList<RideDetailModel.Tolls>
    ) {

        var jsonArray = JSONArray()

        try {


            for (i in tolls!!.indices) {
                val jsonObjectMain = JSONObject()
                jsonObjectMain.put("latitude", tolls[i].latitude)
                jsonObjectMain.put("longitude", tolls[i].longitude)
                jsonObjectMain.put("name", tolls[i].name)
                jsonObjectMain.put("road", tolls[i].road)
                jsonObjectMain.put("state", tolls[i].state)
                jsonObjectMain.put("country", tolls[i].country)
                jsonObjectMain.put("type", tolls[i].type)
                jsonObjectMain.put("currency", tolls[i].currency)
                jsonObjectMain.put("charges", tolls[i].charges)
                jsonArray.put(jsonObjectMain)
            }



        } catch (e: JSONException) {
            e.printStackTrace()
        }


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
            gateway_type,
            firstRideTotal,
            secondRideTotal,
            secondRidePercentageToPay,
            amountToCollect,
            jsonArray
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

    override fun localBookingRequest(
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
        vehicle_rate_card_id: String?,
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
        gateway_type: String?
    ) {
        viewRideView.showWait()
        val call = ApiClient.client.localBookingRequest(
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
            vehicle_rate_card_id,
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
            gateway_type
        )
        call!!.enqueue(object : Callback<BookingRequestModel?> {
            override fun onResponse(
                call: Call<BookingRequestModel?>,
                response: Response<BookingRequestModel?>
            ) {
                viewRideView.removeWait()

                if (response.code() == 200) {
                    viewRideView.removeWait()

                    viewRideView.localBookingRequestSuccess(response.body())
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
        permit_type: String?,
        rate_card_id: String?,
        distance: String?,
        luggage: String?,
        origin_place_id: String?,
        destination_place_id: String?,
        origin_latitude: String?,
        origin_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        way_flag: String?,
        travel_time: String?,
        travel_time_second: String?
    ) {
        viewRideView.showWait()

        var call: Call<ViewRideModel?>? = null

        if (permit_type == Constants.TYPE_INTERCITY) {
            call = ApiClient.client.getViewIntercityRideDetails(
                "Bearer $token",
                permit_type,
                rate_card_id,
                distance,
                luggage,
                origin_place_id,
                destination_place_id,
                origin_latitude,
                origin_longitude,
                destination_latitude,
                destination_longitude,
                travel_time,
                travel_time_second
            )
        }else{
            call =  ApiClient.client.getViewLocalRideDetails(
                "Bearer $token",
                permit_type,
                rate_card_id,
                distance,
                luggage,
                origin_place_id,
                destination_place_id,
                origin_latitude,
                origin_longitude,
                destination_latitude,
                destination_longitude,
                way_flag,
                travel_time,
                travel_time_second
            )
        }

        call?.enqueue(object : Callback<ViewRideModel?> {
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

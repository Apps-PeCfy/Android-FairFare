package com.example.fairfare.ui.viewride

import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.compareride.pojo.CompareRideResponsePOJO
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList

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
        sourceAdddress: String?,
        destinationAddress: String?,
        tolls: ArrayList<CompareRideResponsePOJO.VehiclesItem>
    )
    {
        view.showWait()

        var jsonProductObj: JSONObject? =null
        var jsonArray = JSONArray()

        try {
            jsonProductObj = JSONObject()

            jsonProductObj.accumulate("vehicle_rate_card_id", vehicle_rate_card_id)
            jsonProductObj.accumulate("luggage_quantity", luggage_quantity)
            jsonProductObj.accumulate("schedule_date", schedule_date)
            jsonProductObj.accumulate("origin_place_id", origin_place_id)
            jsonProductObj.accumulate("destination_place_id", destination_place_id)
            jsonProductObj.accumulate("overview_polyline", overview_polyline)
            jsonProductObj.accumulate("distance", distance)
            jsonProductObj.accumulate("duration", duration)
            jsonProductObj.accumulate("city_id", city_id)
            jsonProductObj.accumulate("airport_rate_card_id", airport_rate_card_id)
            jsonProductObj.accumulate("origin_place_lat", sLat)
            jsonProductObj.accumulate("origin_place_long", sLong)
            jsonProductObj.accumulate("destination_place_lat", dLat)
            jsonProductObj.accumulate("destination_place_long", dLong)
            jsonProductObj.accumulate("origin_full_address", sourceAdddress)
            jsonProductObj.accumulate("destination_full_address", destinationAddress)



            for (i in tolls[0].tolls!!.indices) {
                val jsonObjectMain = JSONObject()
                jsonObjectMain.accumulate("latitude", tolls[0].tolls!!.get(i).latitude)
                jsonObjectMain.accumulate("longitude", tolls[0].tolls!!.get(i).longitude)
                jsonObjectMain.accumulate("name", tolls[0].tolls!!.get(i).name)
                jsonObjectMain.accumulate("road", tolls[0].tolls!!.get(i).road)
                jsonObjectMain.accumulate("state", tolls[0].tolls!!.get(i).state)
                jsonObjectMain.accumulate("country", tolls[0].tolls!!.get(i).country)
                jsonObjectMain.accumulate("type", tolls[0].tolls!!.get(i).type)
                jsonObjectMain.accumulate("currency", tolls[0].tolls!!.get(i).currency)
                jsonObjectMain.accumulate("charges", tolls[0].tolls!!.get(i).charges)
                jsonArray.put(jsonObjectMain)
            }
            jsonProductObj.accumulate("tolls", jsonArray)


        } catch (e: JSONException) {
            e.printStackTrace()
        }


        val call = ApiClient.client.schduleRidejObj(
            "Bearer $token",
            jsonProductObj.toString())
        call!!.enqueue(object : Callback<ScheduleRideResponsePOJO?> {
            override fun onResponse(
                call: Call<ScheduleRideResponsePOJO?>,
                response: Response<ScheduleRideResponsePOJO?>
            ) {
                view.removeWait()
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

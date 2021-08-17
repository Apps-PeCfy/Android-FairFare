package com.fairfareindia.ui.endrides

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.endrides.pojo.ResponseEnd
import com.google.android.gms.common.api.ApiException
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.GeocodingResult
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class EndRideImplementer(private val view: IEndRideView) : IEndRidePresenter {

    override fun endRide(
        token: String?,
        rideId: String?,
        distance: Double?,
        duration: String?,
        arrWaitTime: ArrayList<HashMap<String, String>>,
        endLat: String?,
        endLon: String?,
        endAddress: String?,
        originLat: String?,
        originLon: String?,
        originAddress: String?,
        nightChargeDistance: String?,
        tollsJSONArrayFromTollGuru: JSONArray
    ) {


        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
            .build()
        var results = arrayOfNulls<GeocodingResult>(0)
        try {
            results = GeocodingApi.newRequest(context)
                .latlng(
                    com.google.maps.model.LatLng(
                        originLat!!.toDouble(),
                        originLon!!.toDouble()
                    )
                )
                .await()
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val context1 = GeoApiContext.Builder()
            .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
            .build()
        var resultsendDest = arrayOfNulls<GeocodingResult>(0)
        try {
            resultsendDest = GeocodingApi.newRequest(context1)
                .latlng(
                    com.google.maps.model.LatLng(
                        endLat!!.toDouble(),
                        endLon!!.toDouble()
                    )
                )
                .await()
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        var jsonMainObj: JSONObject? = null
        var jsonArray = JSONArray()

        try {
            jsonMainObj = JSONObject()
            jsonMainObj.accumulate("km_of_night", nightChargeDistance)
            jsonMainObj.accumulate("distance", distance)
            jsonMainObj.accumulate("duration", duration)
            jsonMainObj.accumulate("ride_id", rideId)
            jsonMainObj.accumulate("origin_place_id", results[0]!!.placeId)
            jsonMainObj.accumulate("origin_place_lat", originLat)
            jsonMainObj.accumulate("origin_place_long", originLon)
            jsonMainObj.accumulate("origin_full_address", originAddress)

            jsonMainObj.accumulate("destination_place_id", resultsendDest[0]!!.placeId)
            jsonMainObj.accumulate("destination_place_lat", endLat)
            jsonMainObj.accumulate("destination_place_long", endLon)
            jsonMainObj.accumulate("destination_full_address", endAddress)
            jsonMainObj.accumulate("tolls", tollsJSONArrayFromTollGuru)



            for (i in arrWaitTime.indices) {
                val jsonProductObj = JSONObject()


                if (arrWaitTime[i].get("waiting_time")!!.isNotEmpty()) {
                    jsonProductObj.accumulate("waiting_time", arrWaitTime[i].get("waiting_time"))
                } else {
                    jsonProductObj.accumulate("waiting_time", "-")
                }

                if (arrWaitTime[i].get("full_address")!!.isNotEmpty()) {
                    jsonProductObj.accumulate("full_address", arrWaitTime[i].get("full_address"))
                } else {
                    jsonProductObj.accumulate("full_address", "-")
                }

                if (arrWaitTime[i].get("wait_at")!!.isNotEmpty()) {
                    jsonProductObj.accumulate("wait_at", arrWaitTime[i].get("wait_at"))
                } else {
                    jsonProductObj.accumulate("wait_at", "-")
                }

                if (arrWaitTime[i].get("lat")!!.isNotEmpty()) {
                    jsonProductObj.accumulate("lat", arrWaitTime[i].get("lat"))
                } else {
                    jsonProductObj.accumulate("lat", "-")
                }

                if (arrWaitTime[i].get("long")!!.isNotEmpty()) {
                    jsonProductObj.accumulate("long", arrWaitTime[i].get("long"))
                } else {
                    jsonProductObj.accumulate("long", "-")
                }


                jsonArray.put(jsonProductObj)
            }




            jsonMainObj.accumulate("ride_waitings", jsonArray)


        } catch (e: JSONException) {
            e.printStackTrace()
        }






        view.showWait()
        val call = ApiClient.client.enndRide("Bearer " + token, jsonMainObj.toString())
        call!!.enqueue(object : Callback<ResponseEnd?> {
            override fun onResponse(
                call: Call<ResponseEnd?>,
                response: Response<ResponseEnd?>
            ) {
                if (response.code() == 200) {
                    view.removeWait()
                    view.endRideSuccess(response.body())
                } else if (response.code() == 422 || response.code() == 401) {
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
                call: Call<ResponseEnd?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }


}
package com.example.fairfare.ui.endrides

import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.endrides.pojo.ResponseEnd
import com.google.gson.GsonBuilder
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
        distance: String?,
        duration: String?,
        arrWaitTime: ArrayList<HashMap<String, String>>
    ) {




        var jsonMainObj: JSONObject? =null
        var jsonArray = JSONArray()

        try {
            jsonMainObj = JSONObject()
            jsonMainObj.accumulate("distance", distance)
            jsonMainObj.accumulate("duration", duration)
            jsonMainObj.accumulate("ride_id", rideId)



            for (i in arrWaitTime.indices) {
                val jsonProductObj = JSONObject()


                if(arrWaitTime[i].get("waiting_time")!!.isNotEmpty()){
                    jsonProductObj.accumulate("waiting_time", arrWaitTime[i].get("waiting_time"))
                }else{
                    jsonProductObj.accumulate("waiting_time", "-")
                }

                if(arrWaitTime[i].get("full_address")!!.isNotEmpty()){
                    jsonProductObj.accumulate("full_address", arrWaitTime[i].get("full_address"))
                }else{
                    jsonProductObj.accumulate("full_address", "-")
                }

                if(arrWaitTime[i].get("wait_at")!!.isNotEmpty()){
                    jsonProductObj.accumulate("wait_at", arrWaitTime[i].get("wait_at"))
                }else{
                    jsonProductObj.accumulate("wait_at", "-")
                }

                if(arrWaitTime[i].get("lat")!!.isNotEmpty()){
                    jsonProductObj.accumulate("lat", arrWaitTime[i].get("lat"))
                }else{
                    jsonProductObj.accumulate("lat", "-")
                }

                if(arrWaitTime[i].get("long")!!.isNotEmpty()){
                    jsonProductObj.accumulate("long", arrWaitTime[i].get("long"))
                }else{
                    jsonProductObj.accumulate("long", "-")
                }


                jsonArray.put(jsonProductObj)
            }




            jsonMainObj.accumulate("ride_waitings", jsonArray)


        } catch (e: JSONException) {
            e.printStackTrace()
        }






         view.showWait()
         val call = ApiClient.client.enndRide("Bearer "+token, jsonMainObj.toString())
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

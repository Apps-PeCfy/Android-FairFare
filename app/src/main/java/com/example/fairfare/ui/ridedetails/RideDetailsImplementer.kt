package com.example.fairfare.ui.ridedetails

import android.widget.Toast
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

class RideDetailsImplementer(private val view: IRideDetaisView) : IRidePresenter {


    override fun startRide(
        token: String?,
        id: String?,
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
        driver_name: String?,
        vehicle_no: String?,
        badge_no: String?,
        start_meter_reading: String?,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        imageList: ArrayList<ImageModel>?,
        sourceAddress:String?,
        destinationAddress: String?
    )
    {
        if(imageList != null && imageList.size> 0){

            calmultipartdata(token,id,vehicle_rate_card_id,luggage_quantity,schedule_date,
                origin_place_id, destination_place_id,overview_polyline,distance,
                duration,city_id,airport_rate_card_id, driver_name,vehicle_no,badge_no,
                start_meter_reading,sLat,sLong,dLat,dLong,imageList,sourceAddress,destinationAddress)


        }else{
            view.showWait()
            val call = ApiClient.client.startRide(
                "Bearer $token",
                id,
                vehicle_rate_card_id,
                luggage_quantity,
                schedule_date,
                origin_place_id,
                destination_place_id,
                overview_polyline,
                distance,
                duration,
                city_id,
                airport_rate_card_id,
                driver_name, vehicle_no,
                badge_no,
                start_meter_reading,
                sLat,
                sLong,
                dLat,

                dLong,sourceAddress,destinationAddress

            )
            call!!.enqueue(object : Callback<ScheduleRideResponsePOJO?> {
                override fun onResponse(
                    call: Call<ScheduleRideResponsePOJO?>,
                    response: Response<ScheduleRideResponsePOJO?>
                ) {

                    view.removeWait()

                    if (response.code() == 200) {
                        if (response.body() != null) {
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

                    }else{

                        view.onFailure(response.message())


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

    private fun calmultipartdata(
        token: String?,
        id: String?,
        vehicleRateCardId: String?,
        luggageQuantity: String?,
        scheduleDate: String?,
        originPlaceId: String?,
        destinationPlaceId: String?,
        overviewPolyline: String?,
        distance: String?,
        duration: String?,
        cityId: String?,
        airportRateCardId: String?,
        driverName: String?,
        vehicleNo: String?,
        badgeNo: String?,
        startMeterReading: String?,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        imageList: ArrayList<ImageModel>?,
        sourceAddress: String?,
        destAddress: String?
    ) {
        var body: MultipartBody.Part? = null
        val imagesMultipart = arrayOfNulls<MultipartBody.Part>(

            imageList!!.size

        )

        var requestFile: RequestBody
        for (pos in imageList!!.indices) {

            /* val file = File(imageList[pos].image!!)
             requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
             body =
                 MultipartBody.Part.createFormData("vehicle_detail_images[]", imageList[pos].image!!, requestFile)*/


            val file = File(imageList[pos].image!!)
            val surveyBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            imagesMultipart[pos] = MultipartBody.Part.createFormData("vehicle_detail_images[]", imageList[pos].image!!, surveyBody)
        }


        val map = HashMap<String?, String?>()

        map["luggage_quantity"] = luggageQuantity!!
        map["origin_place_id"] = originPlaceId!!
        map["destination_place_id"] = destinationPlaceId!!
        map["overview_polyline"] = overviewPolyline!!
        map["duration"] = duration!!
        map["driver_name"] = driverName!!
        map["vehicle_no"] = vehicleNo!!
        map["badge_no"] = badgeNo!!
        map["start_meter_reading"] = startMeterReading!!
        map["origin_place_lat"] = sLat!!
        map["origin_place_long"] = sLong!!
        map["destination_place_lat"] = dLat!!
        map["destination_place_long"] = dLong!!
        map["schedule_date"] = scheduleDate!!
        map["origin_full_address"] = sourceAddress!!
        map["destination_full_address"] = destAddress!!

        val map1 = HashMap<String?, Int?>()
        map1["vehicle_rate_card_id"] = vehicleRateCardId!!.toInt()
        map1["city_id"] = cityId!!.toInt()


        val map2 = HashMap<String?, Float?>()
        map2["distance"] = distance!!.toFloat()




        /*  val today = Date()
          val formatviewRide = SimpleDateFormat("YYYY-MM-dd HH:MM:ss")
          Log.d("wnfrd",formatviewRide.format(today).toString())
  */

        view.showWait()
        val call = ApiClient.client.uploadstartRide(
            "Bearer $token",
            imagesMultipart,
            map, map1, map2
        )
        call!!.enqueue(object : Callback<ScheduleRideResponsePOJO?> {
            override fun onResponse(
                call: Call<ScheduleRideResponsePOJO?>,
                response: Response<ScheduleRideResponsePOJO?>
            ) {
                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
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

                }else{
                    view.onFailure(response.message())

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

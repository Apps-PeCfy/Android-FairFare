package com.fairfareindia.ui.ridedetails

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
        vehicleImageList: ArrayList<String>?,
        meterImageList: ArrayList<String>?,
        driverImageList: ArrayList<String>?,
        badgeImageList: ArrayList<String>?,
        sourceAddress:String?,
        destinationAddress: String?,
        nightallow: String?,
        tolls: ArrayList<CompareRideResponsePOJO.TollsItem>)
    {
        if((vehicleImageList != null && vehicleImageList.size> 0) || (meterImageList != null && meterImageList.size> 0)
            || (driverImageList != null && driverImageList.size> 0) || (badgeImageList != null && badgeImageList.size> 0)){
            calmultipartdata(token,id,vehicle_rate_card_id,luggage_quantity,schedule_date,
                origin_place_id, destination_place_id,overview_polyline,distance,
                duration,city_id,airport_rate_card_id, driver_name,vehicle_no,badge_no,
                start_meter_reading,sLat,sLong,dLat,dLong,vehicleImageList, meterImageList, driverImageList, badgeImageList,sourceAddress,destinationAddress,

                nightallow,tolls
            )

        }else{
            view.showWait()




            var jsonProductObj: JSONObject? =null
            var jsonArray = JSONArray()

            try {


                jsonProductObj = JSONObject()

                jsonProductObj.accumulate("ride_id", id)
                jsonProductObj.accumulate("luggage_quantity", luggage_quantity)
                jsonProductObj.accumulate("airport_rate_card_id", airport_rate_card_id)
                jsonProductObj.accumulate("driver_name", driver_name)
                jsonProductObj.accumulate("vehicle_no", vehicle_no)
                jsonProductObj.accumulate("badge_no", badge_no)
                jsonProductObj.accumulate("start_meter_reading", start_meter_reading)
                jsonProductObj.accumulate("origin_full_address", sourceAddress)
                jsonProductObj.accumulate("destination_full_address", destinationAddress)
                jsonProductObj.accumulate("night_allow", nightallow)
                jsonProductObj.accumulate("vehicle_rate_card_id", vehicle_rate_card_id)
                jsonProductObj.accumulate("schedule_date", schedule_date)
                jsonProductObj.accumulate("origin_place_id", origin_place_id)
                jsonProductObj.accumulate("origin_place_lat", sLat)
                jsonProductObj.accumulate("origin_place_long", sLong)
                jsonProductObj.accumulate("destination_place_id", destination_place_id)
                jsonProductObj.accumulate("destination_place_lat", dLat)
                jsonProductObj.accumulate("destination_place_long", dLong)
                jsonProductObj.accumulate("distance", distance)
                jsonProductObj.accumulate("duration", duration)
                jsonProductObj.accumulate("city_id", city_id)
                jsonProductObj.accumulate("overview_polyline", overview_polyline)






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

                jsonProductObj.accumulate("tolls", jsonArray)



            } catch (e: JSONException) {
                e.printStackTrace()
            }




            val call = ApiClient.client.startRidear(
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

                    }else if(response.code() == 406){

                        val gson = GsonBuilder().create()
                        var pojo: ValidationResponse? = ValidationResponse()
                        try {
                            pojo = gson.fromJson(
                                response.errorBody()!!.string(),
                                ValidationResponse::class.java
                            )
                            view.procedPopUp(pojo)
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



    override fun startRideMyRide(
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
        vehicleImageList: ArrayList<String>?,
        meterImageList: ArrayList<String>?,
        driverImageList: ArrayList<String>?,
        badgeImageList: ArrayList<String>?,
        sourceAddress:String?,
        destinationAddress: String?,
        nightallow: String?,
        tolls: ArrayList<GetRideResponsePOJO.TollsItem>)
    {
        if((vehicleImageList != null && vehicleImageList.size> 0) || (meterImageList != null && meterImageList.size> 0)
            || (driverImageList != null && driverImageList.size> 0) || (badgeImageList != null && badgeImageList.size> 0)){
            calmultipartdataMyRide(token,id,vehicle_rate_card_id,luggage_quantity,schedule_date,
                origin_place_id, destination_place_id,overview_polyline,distance,
                duration,city_id,airport_rate_card_id, driver_name,vehicle_no,badge_no,
                start_meter_reading,sLat,sLong,dLat,dLong,vehicleImageList,meterImageList,driverImageList,badgeImageList,sourceAddress,destinationAddress,
                nightallow,tolls
            )

        }else{
            view.showWait()




            var jsonProductObj: JSONObject? =null
            var jsonArray = JSONArray()

            try {


                jsonProductObj = JSONObject()

                jsonProductObj.accumulate("ride_id", id)
                jsonProductObj.accumulate("luggage_quantity", luggage_quantity)
                jsonProductObj.accumulate("airport_rate_card_id", airport_rate_card_id)
                jsonProductObj.accumulate("driver_name", driver_name)
                jsonProductObj.accumulate("vehicle_no", vehicle_no)
                jsonProductObj.accumulate("badge_no", badge_no)
                jsonProductObj.accumulate("start_meter_reading", start_meter_reading)
                jsonProductObj.accumulate("origin_full_address", sourceAddress)
                jsonProductObj.accumulate("destination_full_address", destinationAddress)
                jsonProductObj.accumulate("night_allow", nightallow)
                jsonProductObj.accumulate("vehicle_rate_card_id", vehicle_rate_card_id)
                jsonProductObj.accumulate("schedule_date", schedule_date)
                jsonProductObj.accumulate("origin_place_id", origin_place_id)
                jsonProductObj.accumulate("origin_place_lat", sLat)
                jsonProductObj.accumulate("origin_place_long", sLong)
                jsonProductObj.accumulate("destination_place_id", destination_place_id)
                jsonProductObj.accumulate("destination_place_lat", dLat)
                jsonProductObj.accumulate("destination_place_long", dLong)
                jsonProductObj.accumulate("distance", distance)
                jsonProductObj.accumulate("duration", duration)
                jsonProductObj.accumulate("city_id", city_id)
                jsonProductObj.accumulate("overview_polyline", overview_polyline)






                for (i in tolls!!.indices) {
                    val jsonObjectMain = JSONObject()
                    jsonObjectMain.accumulate("latitude", tolls!!.get(i).latitude)
                    jsonObjectMain.accumulate("longitude", tolls!!.get(i).longitude)
                    jsonObjectMain.accumulate("name",tolls!!.get(i).name)
                    jsonObjectMain.accumulate("road", tolls!!.get(i).road)
                    jsonObjectMain.accumulate("state", tolls!!.get(i).state)
                    jsonObjectMain.accumulate("country", tolls!!.get(i).country)
                    jsonObjectMain.accumulate("type", tolls!!.get(i).type)
                    jsonObjectMain.accumulate("currency", tolls!!.get(i).currency)
                    jsonObjectMain.accumulate("charges",tolls!!.get(i).charges)
                    jsonArray.put(jsonObjectMain)
                }
                jsonProductObj.accumulate("tolls", jsonArray)


            } catch (e: JSONException) {
                e.printStackTrace()
            }




            val call = ApiClient.client.startRidear(
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

                    }else if(response.code() == 406){

                        val gson = GsonBuilder().create()
                        var pojo: ValidationResponse? = ValidationResponse()
                        try {
                            pojo = gson.fromJson(
                                response.errorBody()!!.string(),
                                ValidationResponse::class.java
                            )
                            view.procedPopUp(pojo)
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
        vehicleImageList: ArrayList<String>?,
        meterImageList: ArrayList<String>?,
        driverImageList: ArrayList<String>?,
        badgeImageList: ArrayList<String>?,
        sourceAddress: String?,
        destAddress: String?,
        nightallow: String?,
        tolls: ArrayList<CompareRideResponsePOJO.TollsItem>)

    {
        val multipartSize : Int = vehicleImageList!!.size + meterImageList!!.size + driverImageList!!.size + badgeImageList!!.size
        val imagesMultipart = arrayOfNulls<MultipartBody.Part>(
            multipartSize
        )

        var currentMultiPartPosition : Int = 0

        for (pos in vehicleImageList!!.indices) {
            /* val file = File(imageList[pos].image!!)
             requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
             body =
                 MultipartBody.Part.createFormData("vehicle_detail_images[]", imageList[pos].image!!, requestFile)*/

            val file = File(vehicleImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("vehicle_no_image_files[]", vehicleImageList[pos], surveyBody)
            currentMultiPartPosition++
        }

        for (pos in meterImageList!!.indices) {
            val file = File(meterImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("start_meter_image_files[]", meterImageList[pos], surveyBody)
            currentMultiPartPosition++
        }

        for (pos in driverImageList!!.indices) {
            val file = File(driverImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("driver_image_files[]", driverImageList[pos], surveyBody)
            currentMultiPartPosition++
        }

        for (pos in badgeImageList!!.indices) {
            val file = File(badgeImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("badge_no_image_files[]", badgeImageList[pos], surveyBody)
            currentMultiPartPosition++
        }


        val map3 = HashMap<String?, String?>()

        for (i in tolls.indices) {
            var lattitudekey : String = "tolls." + i + ".latitude"
            map3[lattitudekey] = tolls[i].latitude

            var longitudekey : String = "tolls." + i + ".longitude"
            map3[longitudekey] = tolls[i].longitude

            var namekey : String = "tolls." + i + ".name"
            map3[namekey] = tolls[i].name

            var roadkey : String = "tolls." + i + ".road"
            map3[roadkey] = tolls[i].road

            var statekey : String = "tolls." + i + ".state"
            map3[statekey] = tolls[i].state

            var countrykey : String = "tolls." + i + ".country"
            map3[countrykey] = tolls[i].country

            var typekey : String = "tolls." + i + ".type"
            map3[typekey] = tolls[i].type

            var currencykey : String = "tolls." + i + ".currency"
            map3[currencykey] = tolls[i].currency

            var chargeskey : String = "tolls." + i + ".charges"
            map3[chargeskey] = tolls[i].charges.toString()
        }

        val map = HashMap<String?, String?>()

        map["night_allow"] = nightallow!!
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
            map, map1, map2,map3
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

                }else if(response.code() == 406){

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        view.procedPopUp(pojo)
                    } catch (exception: IOException) {
                    }

                }

                else{
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

    private fun calmultipartdataMyRide(
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
        vehicleImageList: ArrayList<String>?,
        meterImageList: ArrayList<String>?,
        driverImageList: ArrayList<String>?,
        badgeImageList: ArrayList<String>?,
        sourceAddress: String?,
        destAddress: String?,
        nightallow: String?,
        tolls: ArrayList<GetRideResponsePOJO.TollsItem>)

    {
        val multipartSize : Int = vehicleImageList!!.size + meterImageList!!.size + driverImageList!!.size + badgeImageList!!.size
        val imagesMultipart = arrayOfNulls<MultipartBody.Part>(
            multipartSize
        )

        var currentMultiPartPosition : Int = 0

        for (pos in vehicleImageList!!.indices) {
            /* val file = File(imageList[pos].image!!)
             requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
             body =
                 MultipartBody.Part.createFormData("vehicle_detail_images[]", imageList[pos].image!!, requestFile)*/

            val file = File(vehicleImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("vehicle_no_image_files[]", vehicleImageList[pos], surveyBody)
            currentMultiPartPosition++
        }

        for (pos in meterImageList!!.indices) {
            val file = File(meterImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("start_meter_image_files[]", meterImageList[pos], surveyBody)
            currentMultiPartPosition++
        }

        for (pos in driverImageList!!.indices) {
            val file = File(driverImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("driver_image_files[]", driverImageList[pos], surveyBody)
            currentMultiPartPosition++
        }

        for (pos in badgeImageList!!.indices) {
            val file = File(badgeImageList[pos])
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[currentMultiPartPosition] = MultipartBody.Part.createFormData("badge_no_image_files[]", badgeImageList[pos], surveyBody)
            currentMultiPartPosition++
        }


        val map3 = HashMap<String?, String?>()

        for (i in tolls!!.indices) {
            var lattitudekey : String = "tolls." + i + ".latitude"
            map3[lattitudekey] = tolls!!.get(i).latitude

            var longitudekey : String = "tolls." + i + ".longitude"
            map3[longitudekey] = tolls!!.get(i).longitude

            var namekey : String = "tolls." + i + ".name"
            map3[namekey] = tolls!!.get(i).name

            var roadkey : String = "tolls." + i + ".road"
            map3[roadkey] = tolls!!.get(i).road

            var statekey : String = "tolls." + i + ".state"
            map3[statekey] = tolls!!.get(i).state

            var countrykey : String = "tolls." + i + ".country"
            map3[countrykey] =tolls!!.get(i).country

            var typekey : String = "tolls." + i + ".type"
            map3[typekey] = tolls!!.get(i).type

            var currencykey : String = "tolls." + i + ".currency"
            map3[currencykey] = tolls!!.get(i).currency

            var chargeskey : String = "tolls." + i + ".charges"
            map3[chargeskey] =tolls!!.get(i).charges.toString()
        }



        val map = HashMap<String?, String?>()

        map["night_allow"] = nightallow!!
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
            map, map1, map2,map3
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

                }else if(response.code() == 406){

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        view.procedPopUp(pojo)
                    } catch (exception: IOException) {
                    }

                }

                else{
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
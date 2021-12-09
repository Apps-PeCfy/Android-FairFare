package com.fairfareindia.utils

import android.annotation.SuppressLint
import android.content.Context
import com.fairfareindia.R
import com.fairfareindia.ui.home.pojo.GeneralSettingModel
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AppUtils {


    companion object {
        fun convertDateGMTToLocal(sourceDate: String?): String? {

            var outputText: String? = null
            val formatter = SimpleDateFormat("dd MM yyyy HH:mm:ss")
            //formatter.timeZone = TimeZone.getTimeZone("Etc/UTC")
            val outputFormat = SimpleDateFormat("dd MMM, hh:mm a")
            try {
                val date = formatter.parse(sourceDate)
                outputText = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return outputText
        }

        @SuppressLint("SimpleDateFormat")
        fun changeDateFormat(sourceDate: String?, oldFormat: String?, newFormat: String?): String? {
            var outputText: String = ""
            if (sourceDate != null && oldFormat != null && newFormat != null){
                val formatter = SimpleDateFormat(oldFormat, Locale.US)
                val outputFormat = SimpleDateFormat(newFormat, Locale.US)
                try {
                    val date = formatter.parse(sourceDate)
                    outputText = outputFormat.format(date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            return outputText
        }

        @SuppressLint("SimpleDateFormat")
        fun dateToRequiredFormat(sourceDate: Date?, newFormat: String?): String? {
            var outputText: String = ""
            if (sourceDate != null && newFormat != null){
                val outputFormat = SimpleDateFormat(newFormat)
                try {
                    outputText = outputFormat.format(sourceDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            return outputText
        }

        @JvmStatic
        fun getDate(date: String?, format: String?): Date? {
            try {
                if (!date.isNullOrEmpty() && !format.isNullOrEmpty()) {
                    return SimpleDateFormat(format, Locale.US).parse(date)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }

        fun getPlaceID(context: Context, latitude: String?, longitude: String?): String {
            var placeID :String = ""
            val context = GeoApiContext.Builder()
                .apiKey(context.resources.getString(R.string.google_maps_key))
                .build()
            var results = arrayOfNulls<GeocodingResult>(0)
            try {
                results = GeocodingApi.newRequest(context)
                    .latlng(
                        com.google.maps.model.LatLng(
                            latitude?.toDouble()!!,
                            longitude?.toDouble()!!
                        )
                    ).await()

                placeID = results[0]?.placeId!!
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return placeID
        }

        fun getValueOfKeyFromGeneralSettings(context: Context, key: String?): String? {
            var keyValue : String ?= null

            var model : GeneralSettingModel ?= SessionManager.getInstance(context).getGeneralSettingModel()

            if (model != null && !model.data.isNullOrEmpty()){
                for (settingModel in model.data!!){
                    if (settingModel.key == key){
                        keyValue = settingModel.value
                        return keyValue
                    }
                }
            }

           return keyValue
        }
    }

}
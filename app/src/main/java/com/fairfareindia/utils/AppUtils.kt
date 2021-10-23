package com.fairfareindia.utils

import android.annotation.SuppressLint
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
                val formatter = SimpleDateFormat(oldFormat)
                val outputFormat = SimpleDateFormat(newFormat)
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
    }

}
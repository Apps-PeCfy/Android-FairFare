package com.fairfareindia.utils

import java.text.ParseException
import java.text.SimpleDateFormat


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
    }

}
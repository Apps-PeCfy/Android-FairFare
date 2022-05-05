/*
 * Author : Chetan Patil.
 * Module : Networking module
 * Version : V 1.0
 * Sprint : III
 * Date of Development : 13/02/2019 11:25:00 AM
 * Date of Modified : 13/02/2019 03:30:00 PM
 * Comments : Connectivity interceptor class
 * Output : This class provides the internet error while calling the api
 */
package com.fairfareindia.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.fairfareindia.CustomerApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor : Interceptor {

    private val applicationContext = CustomerApplication.applicationContext()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable())
            throw IOException("No network available, please check your WiFi or Data connection")

        val builder = chain.request().newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Accept", "application/json")
        return chain.proceed(builder.build())
    }

    private fun isInternetAvailable(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo
                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val n = cm.activeNetwork
                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)
                    return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
        }
        return false
    }
}
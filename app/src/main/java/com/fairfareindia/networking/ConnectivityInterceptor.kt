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

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Accept", "application/json")
        return chain.proceed(builder.build())










    }
}
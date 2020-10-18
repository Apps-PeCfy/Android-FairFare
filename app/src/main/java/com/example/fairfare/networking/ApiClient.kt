package com.example.fairfare.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {


    private var retrofit: Retrofit? = null
    @JvmStatic
    val client: NetworkService
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("https://dev.fairfareindia.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(NetworkService::class.java)
        }
}
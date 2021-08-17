package com.fairfareindia.networking

import com.fairfareindia.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {


    private var retrofit: Retrofit? = null
    @JvmStatic
    val client: NetworkService
        get() {
            if (retrofit == null) {


                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY

                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(ConnectivityInterceptor())
                httpClient.connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)

                httpClient.addInterceptor(logging)







                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.API_URL)
                    .client(httpClient.build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(NetworkService::class.java)
        }



    private var retrofitPlaces: Retrofit? = null
    @JvmStatic
    val clientPlaces: NetworkService
        get() {
            if (retrofitPlaces == null) {
                retrofitPlaces = Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofitPlaces!!.create(NetworkService::class.java)
        }

    private var retrofitTollGuru: Retrofit? = null
    @JvmStatic
    val clientTollGuru: NetworkService
        get() {
            if (retrofitTollGuru == null) {
                retrofitTollGuru = Retrofit.Builder()
                    .baseUrl("https://dev.TollGuru.com/v1/calc/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofitTollGuru!!.create(NetworkService::class.java)
        }

}
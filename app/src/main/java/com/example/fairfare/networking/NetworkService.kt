package com.example.fairfare.networking

import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.compareride.pojo.CompareRideResponsePOJO
import com.example.fairfare.ui.home.pojo.DeleteSaveDataResponsePOJO
import com.example.fairfare.ui.home.pojo.GetSaveLocationResponsePOJO
import com.example.fairfare.ui.home.pojo.SaveLocationResponsePojo
import com.example.fairfare.ui.otp.pojo.VerifyOTPResponsePojo
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {


    @FormUrlEncoded
    @POST("sendOtp")
    fun login(
        @Field("phone_no") phoneno: String?,
        @Field("type") type: String?,
        @Field("device_type") device_type: String?,
        @Field("login_type") login_type: String?,
        @Field("country_phone_code") logcountry_phone_codein_type: String?,
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("tokan") tokan: String?
    ): Call<LoginResponsepojo?>?

    @FormUrlEncoded
    @POST("socialLogin")
    fun sociallogin(
        @Field("device_type") device_type: String?,
        @Field("login_type") login_type: String?,
        @Field("name") name: String?,
        @Field("provider_id") provider_id: String?,
        @Field("token") token: String?,
        @Field("email") email: String?
    ): Call<LoginResponsepojo?>?

    @FormUrlEncoded
    @POST("verifyOtp")
    fun verifyOtp(
        @Field("phone_no") phoneno: String?,
        @Field("type") type: String?,
        @Field("device_type") device_type: String?,
        @Field("login_type") login_type: String?,
        @Field("country_phone_code") logcountry_phone_codein_type: String?,
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("gender") gender: String?,
        @Field("otp") otp: String?
    ): Call<VerifyOTPResponsePojo?>?

    @FormUrlEncoded
    @POST("saveLocation")
    fun SaveLocation(
        @Header("Authorization") header: String?,
        @Field("place_id") place_id: String?,
        @Field("city") city: String?,
        @Field("state") state: String?,
        @Field("country") country: String?,
        @Field("full_address") full_address: String?
    ): Call<SaveLocationResponsePojo?>?

    @FormUrlEncoded
    @POST("recentLocation")
    fun SaveRecentLocation(
        @Header("Authorization") header: String?,
        @Field("place_id") place_id: String?,
        @Field("city") city: String?,
        @Field("state") state: String?,
        @Field("country") country: String?,
        @Field("full_address") full_address: String?
    ): Call<SaveLocationResponsePojo?>?

    @GET("getSaveLocation")
    fun getSavedLocation(@Header("Authorization") header: String?): Call<GetSaveLocationResponsePOJO?>?

    @GET("getRecentLocation")
    fun getRecentLocation(@Header("Authorization") header: String?): Call<GetSaveLocationResponsePOJO?>?

    @FormUrlEncoded
    @POST("deleteSavedLocation")
    fun deleteRecentLocation(
        @Header("Authorization") header: String?,
        @Field("location_id") location_id: String?
    ): Call<DeleteSaveDataResponsePOJO?>?

    @FormUrlEncoded
    @POST("compareRide")
    fun getCompareRide(
        @Header("Authorization") header: String?,
        @Field("distance") distance: String?,
        @Field("city_id") city_id: String?,
        @Field("origin_place_id") origin_place_id: String?,
        @Field("destination_place_id") destination_place_id: String?,
        @Field("luggage") luggage: String?,
        @Field("airport") airport: String?
    ): Call<CompareRideResponsePOJO?>?
}
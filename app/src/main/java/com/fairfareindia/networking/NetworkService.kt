package com.fairfareindia.networking

//created by kiran.p

import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.disputs.pojo.DisputesReasonResponsePOJO
import com.fairfareindia.ui.disputs.pojo.SaveDisputResponsePOJO
import com.fairfareindia.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.fairfareindia.ui.drawer.faq.pojo.FAQResponsePOJO
import com.fairfareindia.ui.drawer.myaccount.pojo.UpdateProfileResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.disputDetail.pojo.DisputDetailResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.drawer.myrides.ridedetails.RideDetailsResponsePOJO
import com.fairfareindia.ui.drawer.privacypolicy.ContentResponsePOJO
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardResponsePOJO
import com.fairfareindia.ui.drawer.setting.pojo.SettingResponsePojo
import com.fairfareindia.ui.home.pojo.DeleteSaveDataResponsePOJO
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.home.pojo.GetSaveLocationResponsePOJO
import com.fairfareindia.ui.home.pojo.SaveLocationResponsePojo
import com.fairfareindia.ui.otp.pojo.VerifyOTPResponsePojo
import com.fairfareindia.ui.endrides.pojo.ResponseEnd
import com.fairfareindia.ui.trackRide.NearByPlacesPOJO.NearByResponse
import com.fairfareindia.ui.trackRide.currentFare.CurrentFareeResponse
import com.fairfareindia.ui.trackRide.distMatrixPOJP.DistanceMatrixResponse
import com.fairfareindia.ui.trackRide.snaptoRoad.SnapTORoadResponse
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import java.util.*


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
        @Field("email") email: String?,
        @Field("device_id") deviceId: String?,
        @Field("device_token") device_token: String?
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
        @Field("otp") otp: String?,
        @Field("device_id") deviceId: String?,
        @Field("device_token") device_token: String?,
        @Field("reg_latitude") register_Latitude: String?,
        @Field("reg_longitude") register_Longitude: String?
    ): Call<VerifyOTPResponsePojo?>?

    @FormUrlEncoded
    @POST("saveLocation")
    fun SaveLocation(
        @Header("Authorization") header: String?,
        @Field("place_id") place_id: String?,
        @Field("city") city: String?,
        @Field("state") state: String?,
        @Field("full_address") full_address: String?,
        @Field("country") country: String?
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
        @Field("airport") airport: String?,
        @Field("schedule_datetime") schedule_datetime: String?,
        @Field("current_place_id") current_place_id: String?,
        @Field("duration") duration: String?
    ): Call<CompareRideResponsePOJO?>?


    @FormUrlEncoded
    @POST("scheduleRide")
    fun schduleRide(
        @Header("Authorization") header: String?,
        @Field("vehicle_rate_card_id") vehicle_rate_card_id: String?,
        @Field("luggage_quantity") luggage_quantity: String?,
        @Field("schedule_date") schedule_date: String?,
        @Field("origin_place_id") origin_place_id: String?,
        @Field("destination_place_id") destination_place_id: String?,
        @Field("overview_polyline") overview_polyline: String?,
        @Field("distance") distance: String?,
        @Field("duration") duration: String?,
        @Field("city_id") city_id: String?,
        @Field("airport_rate_card_id") airport_rate_card_id: String?,
        @Field("origin_place_lat") origin_place_lat: String?,
        @Field("origin_place_long") origin_place_long: String?,
        @Field("destination_place_lat") destination_place_lat: String?,
        @Field("destination_place_long") destination_place_long: String?,
        @Field("origin_full_address") origin_full_address: String?,
        @Field("destination_full_address") destination_full_address: String?


    ): Call<ScheduleRideResponsePOJO?>?


    @FormUrlEncoded
    @POST("startRide")
    fun startRide(
        @Header("Authorization") header: String?,
        @Field("ride_id") id: String?,
        @Field("vehicle_rate_card_id") vehicle_rate_card_id: String?,
        @Field("luggage_quantity") luggage_quantity: String?,
        @Field("schedule_date") schedule_date: String?,
        @Field("origin_place_id") origin_place_id: String?,
        @Field("destination_place_id") destination_place_id: String?,
        @Field("overview_polyline") overview_polyline: String?,
        @Field("distance") distance: String?,
        @Field("duration") duration: String?,
        @Field("city_id") city_id: String?,
        @Field("airport_rate_card_id") airport_rate_card_id: String?,
        @Field("driver_name") driver_name: String?,
        @Field("vehicle_no") vehicle_no: String?,
        @Field("badge_no") badge_no: String?,
        @Field("start_meter_reading") start_meter_reading: String?,
        @Field("origin_place_lat") origin_place_lat: String?,
        @Field("origin_place_long") origin_place_long: String?,
        @Field("destination_place_lat") destination_place_lat: String?,
        @Field("destination_place_long") destination_place_long: String?,
        @Field("origin_full_address") origin_full_address: String?,
        @Field("destination_full_address") destination_full_address: String?,
        @Field("night_allow") night_allow: String?,
        @Body body: String?
    ): Call<ScheduleRideResponsePOJO?>?


    @Multipart
    @POST("startRide")
    fun uploadstartRide(
        @Header("Authorization") header: String?,
        @Part file: Array<MultipartBody.Part?>,
        @PartMap map: HashMap<String?, String?>?,
        @PartMap map1: HashMap<String?, Int?>,
        @PartMap map2: HashMap<String?, Float?>,
        @PartMap map3: HashMap<String?, String?>

    ): Call<ScheduleRideResponsePOJO?>?


    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
    fun getNearbyPlaces(
        @Query("location") location: String?,
        @Query("radius") radius: Int
    ): Call<NearByResponse?>?

    @GET("https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
    fun distanceMatrix(
        @Query("origins") origins: String?,
        @Query("destinations") destinations: String
    ): Call<DistanceMatrixResponse?>?


    @GET("https://roads.googleapis.com/v1/snapToRoads?interpolate=true&key=AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
    fun getsnaPTOROAD(
        @Query("path") location: String?
    ): Call<SnapTORoadResponse?>?


    @POST("currentFare")
    fun getCurrentFare(
        @Header("Authorization") header: String?,
        @Query("ride_id") ride_id: Int,
        @Query("distance") distance: String?,
        @Query("waiting_time") wating_time: String?
    ): Call<CurrentFareeResponse?>?

    @POST("currentFare")
    fun getCurrentFareWithoutID(
        @Header("Authorization") header: String?,
        @Query("distance") distance: String?,
        @Query("vehicle_rate_card_id") vehicle_rate_card_id: String?,
        @Query("airport_rate_card_id") airport_rate_card_id: String?,
        @Query("luggage_quantity") luggage_quantity: String?
    ): Call<CurrentFareeResponse?>?


    @POST("endRide")
    fun getEndRide(
        @Header("Authorization") header: String?,
        @Query("ride_id") ride_id: JSONObject?


    ): Call<ResponseEnd>?


    @POST("example/api/order")
    fun postOrder(@Query("data") jsonArray: JSONArray?): Call<JSONArray?>?

    @Headers("Content-Type: application/json")
    @POST("endRide")
    fun enndRide(
        @Header("Authorization") header: String?,
        @Body body: String?
    ): Call<ResponseEnd?>?



    @Headers("Content-Type: application/json")
    @POST("startRide")
    fun startRidear(
        @Header("Authorization") header: String?,
        @Body body: String?
    ): Call<ScheduleRideResponsePOJO?>?


  @Headers("Content-Type: application/json")
    @POST("scheduleRide")
    fun schduleRidejObj(
        @Header("Authorization") header: String?,
        @Body body: String?
    ): Call<ScheduleRideResponsePOJO?>?




    @Headers("Content-Type: application/json")
    @POST("log")
    fun logRide(
        @Header("Authorization") header: String?,
        @Body body: String?
    ): Call<DeleteDisputResponsePOJO?>?


    @GET("rides")
    fun getMyRides(
        @Header("Authorization") header: String?,
        @Query("page") pageCount: Int?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?
    ): Call<GetRideResponsePOJO?>?

    @GET("getDisputeReasons")
    fun getDisputeReasons(@Header("Authorization") header: String?): Call<DisputesReasonResponsePOJO?>?


    @POST("updateProfile")
    fun updateProfile(
        @Header("Authorization") header: String?,
        @Query("name") name: String?,
        @Query("gender") gender: String?,
        @Query("date_of_birth") date_of_birth: String?,
        @Query("location") location: String?,
        @Query("profession") profession: String?,
        @Query("email") email: String?
    ): Call<UpdateProfileResponsePOJO?>?

    @POST("updateProfile")
    fun updateProfileWithOutEmail(
        @Header("Authorization") header: String?,
        @Query("name") name: String?,
        @Query("gender") gender: String?,
        @Query("date_of_birth") date_of_birth: String?,
        @Query("location") location: String?,
        @Query("profession") profession: String?
    ): Call<UpdateProfileResponsePOJO?>?

    @Headers("Content-Type: application/json")
    @POST("saveDispute")
    fun saveDispute(
        @Header("Authorization") header: String?,
        @Query("ride_id") ride_id: String?,
        @Query("type") type: String?,
        @Query("dispute_reason_id[]") reasionID: ArrayList<Int>?,
        @Query("start_meter_reading") start_meter_reading: String?,
        @Query("end_meter_reading") end_meter_reading: String?,
        @Query("actual_meter_charges") actual_meter_charges: String?,
        @Query("comment") comment: String?
    ): Call<SaveDisputResponsePOJO?>?

    /**
     * iLoma Team :- Mohasin 8 Jan
     */

    @Multipart
    @POST("saveDispute")
    fun multipartSaveDispute(
        @Header("Authorization") header: String?,
        @Part file: Array<MultipartBody.Part?>,
        @PartMap map: HashMap<String?, String?>?,
        @Query("dispute_reason_id[]") reasionID: ArrayList<Int>?

    ): Call<SaveDisputResponsePOJO?>?


    @Headers("Content-Type: application/json")
    @POST("saveDispute")
    fun addToWishList(
        @Header("Authorization") header: String?,
        @Body body: Map<String?, String?>?
    ): Call<SaveDisputResponsePOJO?>?


    @GET("getDispute")
    fun getMyDispute(@Header("Authorization") header: String?): Call<GetDisputResponsePOJO?>?

    @GET("getDispute")
    fun getComplaint(
        @Header("Authorization") header: String?,
        @Query("type") ride_id: String?
    ): Call<GetDisputResponsePOJO?>?


    @POST("deleteDispute")
    fun deleteDisput(
        @Header("Authorization") header: String?,
        @Query("dispute_id") ride_id: String?
    ): Call<DeleteDisputResponsePOJO?>?

    @POST("saveComplaint")
    fun saveComplaint(
        @Header("Authorization") header: String?,
        @Query("dispute_id") ride_id: String?
    ): Call<DeleteDisputResponsePOJO?>?


    @POST("updateLocation")
    fun updateLocation(
        @Header("Authorization") header: String?,
        @Query("category") category: String?,
        @Query("location_id") location_id: String?
    ): Call<UpdateProfileResponsePOJO?>?


    @GET("detailDispute")
    fun getDisputeDetail(
        @Header("Authorization") header: String?,
        @Query("dispute_id") dispute_id: String?
    ): Call<DisputDetailResponsePOJO?>?

    @POST("detailRide")
    fun getDetailRide(
        @Header("Authorization") header: String?,
        @Query("ride_id") dispute_id: String?
    ): Call<RideDetailsResponsePOJO?>?


    @POST("saveContactUs")
    fun saveContactUs(
        @Header("Authorization") header: String?,
        @Query("message") message: String?
    ): Call<ContactUsResponsePojo?>?

    @POST("signOut")
    fun signOut(
        @Header("Authorization") header: String?,
        @Query("device_id") deviceid: String?,
        @Query("device_type") devicetype: String?
    ): Call<ContactUsResponsePojo?>?


    @POST("rideReview")
    fun setRideReview(
        @Header("Authorization") header: String?,
        @Query("ride_id") rideid: String?,
        @Query("stars") stars: String?,
        @Query("reviews") reviews: String?
    ): Call<ContactUsResponsePojo?>?


    @GET("faqs")
    fun getFaqs(@Header("Authorization") header: String?): Call<FAQResponsePOJO?>?


    @GET("pageContents")
    fun pageContents(@Query("page_name") page_name: String?): Call<ContentResponsePOJO?>?


    /*  @GET("rateCards")
      fun rateCards(@Header("Authorization") header: String?): Call<RateCardResponsePOJO?>?
  */


    @GET("rateCards")
    fun rateCards(
        @Header("Authorization") header: String?,
        @Query("city_id") city_id: String?
    ): Call<RateCardResponsePOJO?>?


    @GET("getUserSetting")
    fun getUserSetting(@Header("Authorization") header: String?): Call<SettingResponsePojo?>?


    @POST("updateUserSetting")
    fun updateUserSetting(
        @Header("Authorization") header: String?,
        @Query("user_setting_id") user_setting_id: Int,
        @Query("language") language: String?,
        @Query("city") city: String?,
        @Query("currency") currency: String?,
        @Query("measurement_unit") measurement_unit: String?,
        @Query("time_format") time_format: String?
    ): Call<ContactUsResponsePojo?>?


  /*  @GET("getAllowCities")
    fun getAllowCities(@HeaderMap header: HashMap<String, String>): Call<GetAllowCityResponse?>?
*/

    @GET("getAllowCities")
    fun getAllowCities(
        @Header("Authorization") header: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?
    ): Call<GetAllowCityResponse?>?


    @Headers("Content-Type: application/json")
    @POST("route/upload")
    fun uploadCSVTollGuru(
        @Header("x-api-key") header: String?,
        @Body file: RequestBody?
    ): Call<ResponseBody?>?

}
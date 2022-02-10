package com.fairfareindia.networking

//created by kiran.p

import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.disputs.pojo.DisputesReasonResponsePOJO
import com.fairfareindia.ui.disputs.pojo.SaveDisputResponsePOJO
import com.fairfareindia.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.fairfareindia.ui.drawer.faq.pojo.FAQResponsePOJO
import com.fairfareindia.ui.drawer.intercitydispute.DisputeDetailModel
import com.fairfareindia.ui.drawer.myaccount.pojo.UpdateProfileResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import com.fairfareindia.ui.drawer.intercityrides.GetRideResponsePOJO
import com.fairfareindia.ui.drawer.notifications.NotificationModel
import com.fairfareindia.ui.drawer.privacypolicy.ContentResponsePOJO
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardModel
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardResponsePOJO
import com.fairfareindia.ui.drawer.setting.pojo.SettingResponsePojo
import com.fairfareindia.ui.home.pojo.DeleteSaveDataResponsePOJO
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.home.pojo.GetSaveLocationResponsePOJO
import com.fairfareindia.ui.home.pojo.SaveLocationResponsePojo
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel
import com.fairfareindia.ui.intercitytrackpickup.DriverLocationModel
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.intercitytrackride.NearByPlacesPOJO.NearByResponse
import com.fairfareindia.ui.intercityviewride.BookingRequestModel
import com.fairfareindia.ui.intercityviewride.RazorPayModel
import com.fairfareindia.ui.intercityviewride.ViewRideModel
import com.fairfareindia.ui.otp.pojo.VerifyOTPResponsePojo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
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
    @POST("compareRideLocalV1")
    fun getNewLocalCompareRide(
        @Header("Authorization") header: String?,
        @Field("distance") distance: String?,
        @Field("travel_time") estTime: String?,
        @Field("travel_time_second") travel_time_second: String?,
        @Field("permit_type") permitType: String?,
        @Field("city_id") city_id: String?,
        @Field("origin_place_id") fromPlaceID: String?,
        @Field("destination_place_id") toPlaceID: String?,
        @Field("luggage") luggage: String?,
        @Field("way_flag") airport: String?,
        @Field("schedule_datetime") schedule_datetime: String?,
        @Field("origin_latitude") origin_latitude: String?,
        @Field("origin_longitude") origin_longitude: String?,
        @Field("destination_latitude") destination_latitude: String?,
        @Field("destination_longitude") destination_longitude: String?
    ): Call<InterCityCompareRideModel?>?


    @FormUrlEncoded
    @POST("compareRideIntercity")
    fun getIntercityCompareRide(
        @Header("Authorization") header: String?,
        @Field("distance") distance: String?,
        @Field("travel_time") estTime: String?,
        @Field("permit_type") permitType: String?,
        @Field("from_city_id") fromCityID: String?,
        @Field("to_city_id") toCityID: String?,
        @Field("origin_place_id") fromPlaceID: String?,
        @Field("destination_place_id") toPlaceID: String?,
        @Field("luggage") luggage: String?,
        @Field("way_flag") airport: String?,
        @Field("schedule_datetime") schedule_datetime: String?,
        @Field("origin_latitude") origin_latitude: String?,
        @Field("origin_longitude") origin_longitude: String?,
        @Field("destination_latitude") destination_latitude: String?,
        @Field("destination_longitude") destination_longitude: String?
    ): Call<InterCityCompareRideModel?>?


    @FormUrlEncoded
    @POST("bookingRequest")
    fun bookingRequest(
        @Header("Authorization") header: String?,
        @Field("permit_type") type: String?,
        @Field("from_city_id") from_city_id: String?,
        @Field("to_city_id") to_city_id: String?,
        @Field("origin_address") origin_address: String?,
        @Field("destination_address") destination_address: String?,
        @Field("origin_latitude") origin_latitude: String?,
        @Field("origin_longitude") origin_longitude: String?,
        @Field("destination_latitude") destination_latitude: String?,
        @Field("destination_longitude") destination_longitude: String?,
        @Field("schedule_date") schedule_date: String?,
        @Field("way_flag") way_flag: String?,
        @Field("intercity_rate_card_id") intercity_rate_card_id: String?,
        @Field("schedule_type") shedule_type: String?,
        @Field("luggage_quantity") luggage_quantity: String?,
        @Field("luggage_charges") luggage_charges: String?,
        @Field("distance") distance: String?,
        @Field("travel_time") travel_time: String?,
        @Field("travel_time_second") travel_time_second: String?,
        @Field("amount") amount: String?,
        @Field("transaction_id") transaction_id: String?,
        @Field("method") method: String?,
        @Field("payment_status") payment_status: String?,
        @Field("gateway_type") gateway_type: String?,
        @Field("firstRideTotal") firstRideTotal: String?,
        @Field("secondRideTotal") secondRideTotal: String?,
        @Field("secondRidePercentageToPay") secondRidePercentageToPay: String?,
        @Field("amountToCollect") amountToCollect: String?,
        @Field("rp_order_id") rp_order_id: String?,
        @Field("rp_payment_id") rp_payment_id: String?,
        @Field("tolls") jsonArray: JSONArray?


    ): Call<BookingRequestModel?>?

    @FormUrlEncoded
    @POST("localBookingRequest")
    fun localBookingRequest(
        @Header("Authorization") header: String?,
        @Field("permit_type") type: String?,
        @Field("from_city_id") from_city_id: String?,
        @Field("to_city_id") to_city_id: String?,
        @Field("origin_address") origin_address: String?,
        @Field("destination_address") destination_address: String?,
        @Field("origin_latitude") origin_latitude: String?,
        @Field("origin_longitude") origin_longitude: String?,
        @Field("destination_latitude") destination_latitude: String?,
        @Field("destination_longitude") destination_longitude: String?,
        @Field("schedule_date") schedule_date: String?,
        @Field("way_flag") way_flag: String?,
        @Field("vehicle_rate_card_id") vehicle_rate_card_id: String?,
        @Field("schedule_type") shedule_type: String?,
        @Field("luggage_quantity") luggage_quantity: String?,
        @Field("luggage_charges") luggage_charges: String?,
        @Field("distance") distance: String?,
        @Field("travel_time") travel_time: String?,
        @Field("travel_time_second") travel_time_second: String?,
        @Field("amount") amount: String?,
        @Field("transaction_id") transaction_id: String?,
        @Field("method") method: String?,
        @Field("payment_status") payment_status: String?,
        @Field("gateway_type") gateway_type: String?,
        @Field("rp_order_id") rp_order_id: String?,
        @Field("rp_payment_id") rp_payment_id: String?,
        @Field("tolls") jsonArray: JSONArray?
    ): Call<BookingRequestModel?>?

    @FormUrlEncoded
    @POST("getRazorpayOrderId")
    fun getRazorPayOrderId(
        @Header("Authorization") header: String?,
        @Field("union_id") union_id: String?,
        @Field("amount") amount: String?
    ): Call<RazorPayModel?>?



    @FormUrlEncoded
    @POST("v1/getViewIntercityRideDetails")
    fun getViewIntercityRideDetails(
        @Header("Authorization") header: String?,
        @Header("permit_type") permit_type: String?,
        @Field("rate_card_id") rate_card_id: String?,
        @Field("distance") distance: String?,
        @Field("luggage") luggage: String?,
        @Field("origin_place_id") origin_place_id: String?,
        @Field("destination_place_id") destination_place_id: String?,
        @Field("origin_latitude") origin_latitude: String?,
        @Field("origin_longitude") origin_longitude: String?,
        @Field("destination_latitude") destination_latitude: String?,
        @Field("destination_longitude") destination_longitude: String?,
        @Field("travel_time") travel_time: String?,
        @Field("travel_time_second") travel_time_second: String?
    ): Call<ViewRideModel?>?

    @FormUrlEncoded
    @POST("v1/getViewLocalRideDetails")
    fun getViewLocalRideDetails(
        @Header("Authorization") header: String?,
        @Header("permit_type") permit_type: String?,
        @Field("rate_card_id") rate_card_id: String?,
        @Field("distance") distance: String?,
        @Field("luggage") luggage: String?,
        @Field("origin_place_id") origin_place_id: String?,
        @Field("destination_place_id") destination_place_id: String?,
        @Field("origin_latitude") origin_latitude: String?,
        @Field("origin_longitude") origin_longitude: String?,
        @Field("destination_latitude") destination_latitude: String?,
        @Field("destination_longitude") destination_longitude: String?,
        @Field("way_flag") way_flag: String?,
        @Field("travel_time") travel_time: String?,
        @Field("travel_time_second") travel_time_second: String?,
        @Field("schedule_datetime") schedule_datetime: String?
    ): Call<ViewRideModel?>?


    @GET("ridesList")
    fun getMyRides(
        @Header("Authorization") header: String?,
        @Query("page") pageCount: Int?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?
    ): Call<GetRideResponsePOJO?>?

    @GET("getNotificationList")
    fun getNotificationList(
        @Header("Authorization") header: String?,
        @Query("page") pageCount: Int?
    ): Call<NotificationModel?>?


    @POST("cancelRide")
    fun cancelRide(
        @Header("Authorization") header: String?,
        @Query("ride_id") rideID: String?,
        @Query("status") status: String?
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


    @POST("rideDetailsNew")
    fun getRideDetails(
        @Header("Authorization") header: String?,
        @Query("ride_id") dispute_id: String?
    ): Call<RideDetailModel?>?

    @GET("detailDispute")
    fun getInterCityDisputeDetail(
        @Header("Authorization") header: String?,
        @Query("dispute_id") dispute_id: String?
    ): Call<DisputeDetailModel?>?

    @POST("storePayment")
    fun updatePaymentStatus(
        @Header("Authorization") header: String?,
        @Query("ride_id") ride_id: String?,
        @Query("method") method: String?,
        @Query("amount") amount: String?,
        @Query("payment_status") payment_status: String?,
        @Query("gateway_type") gateway_type: String?,
        @Query("transaction_id") transaction_id: String?,
        @Query("rp_order_id") rp_order_id: String?,
        @Query("rp_payment_id") rp_payment_id: String?
    ): Call<RideDetailModel?>?


    @POST("getDriverLocation")
    fun getDriverLocation(
        @Header("Authorization") header: String?,
        @Query("ride_id") ride_id: String?
    ): Call<DriverLocationModel?>?


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

    @POST("getNewRateCardDetails")
    fun getNewRateCardDetails(
        @Header("Authorization") header: String?,
        @Query("permit_type") permit_type: String?,
        @Query("from_city_id") from_city_id: String?,
        @Query("to_city_id") to_city_id: String?
    ): Call<RateCardModel?>?

    /*  @GET("getAllowCities")
      fun getAllowCities(@HeaderMap header: HashMap<String, String>): Call<GetAllowCityResponse?>?
  */

    @GET("getAllowCities")
    fun getAllowCities(
        @Header("Authorization") header: String?,
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?
    ): Call<GetAllowCityResponse?>?


    @GET("getToCities")
    fun getToInterCities(
        @Header("Authorization") header: String?,
        @Query("from_city_id") fromCityID: String?
    ): Call<GetAllowCityResponse?>?

    @GET("getFromCities")
    fun getFromInterCities(
        @Header("Authorization") header: String?
    ): Call<GetAllowCityResponse?>?


    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
    fun getNearbyPlaces(
        @Query("location") location: String?,
        @Query("radius") radius: Int
    ): Call<NearByResponse?>?

}
package com.fairfareindia.ui.intercityviewride

import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.google.gson.annotations.SerializedName
import org.json.JSONArray


interface IInterCityViewRidePresenter {

    fun bookingRequest(
        token: String?,
        type: String?,
        from_city_id: String?,
        to_city_id: String?,
        origin_address: String?,
        destination_address: String?,
        origin_latitude: String?,
        origin_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        shedule_date: String?,
        way_flag: String?,
        intercity_ratecard_id: String?,
        shedule_type: String?,
        luggage_quantity: String?,
        luggage_charges: String?,
        distance: String?,
        travel_time: String?,
        travel_time_second: String?,
        amount: String?,
        transaction_id: String?,
        method: String?,
        payment_status: String?,
        gateway_type: String?,
        firstRideTotal: String?,
        secondRideTotal: String?,
        secondRidePercentageToPay: String?,
        amountToCollect: String?,
        tolls: ArrayList<RideDetailModel.Tolls>
    )


    fun localBookingRequest(
        token: String?,
        type: String?,
        from_city_id: String?,
        to_city_id: String?,
        origin_address: String?,
        destination_address: String?,
        origin_latitude: String?,
        origin_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        shedule_date: String?,
        way_flag: String?,
        vehicle_rate_card_id: String?,
        shedule_type: String?,
        luggage_quantity: String?,
        luggage_charges: String?,
        distance: String?,
        travel_time: String?,
        travel_time_second: String?,
        amount: String?,
        transaction_id: String?,
        method: String?,
        payment_status: String?,
        gateway_type: String?
    )



    fun getViewRideDetails(
        token: String?,
        permit_type: String?,
        rate_card_id: String?,
        distance: String?,
        luggage: String?,
        origin_place_id: String?,
        destination_place_id: String?,
        origin_latitude: String?,
        origin_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        way_flag: String?,
        travel_time: String?,
        travel_time_second: String?
    )


}
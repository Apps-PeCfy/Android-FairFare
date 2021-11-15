package com.fairfareindia.ui.intercityviewride

import retrofit2.http.Field
import retrofit2.http.Header


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
        transaction_id: String?
    )

    fun getViewRideDetails(
        token: String?,
        intercity_rate_card_id: String?,
        total_dist: String?,
        luggage_quantity: String?
    )


}
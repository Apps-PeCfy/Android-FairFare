package com.fairfareindia.ui.intercity

import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import java.util.ArrayList

interface IInterCityPresenter {
    fun scheduleRide(
        token: String?,
        vehicle_rate_card_id: String?,
        luggage_quantity: String?,
        schedule_date: String?,
        origin_place_id: String?,
        destination_place_id: String?,
        overview_polyline: String?,
        distance: String?,
        duration: String?,
        city_id: String?,
        airport_rate_card_id: String?,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        sorceAddress: String?,
        destinationAddress: String?,
        tolls: ArrayList<CompareRideResponsePOJO.TollsItem>)

    fun getCity(
        token: String?,
        lat: String?,
        long: String?)
}
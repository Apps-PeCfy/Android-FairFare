package com.example.fairfare.ui.ridedetails

import com.example.fairfare.ui.compareride.pojo.CompareRideResponsePOJO
import com.example.fairfare.ui.drawer.myrides.pojo.GetRideResponsePOJO
import java.util.ArrayList

interface IRidePresenter {

    fun startRide(

        token: String?,
        id: String?,
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
        driver_name: String?,
        vehicle_no: String?,
        badge_no: String?,
        start_meter_reading: String?,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        vehicleImageList: ArrayList<String>?,
        meterImageList: ArrayList<String>?,
        driverImageList: ArrayList<String>?,
        badgeImageList: ArrayList<String>?,
        sourceAddress: String?,
        destinationAddress: String?,
        night_allow: String?,
        tolls: ArrayList<CompareRideResponsePOJO.VehiclesItem>)



    fun startRideMyRide(

        token: String?,
        id: String?,
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
        driver_name: String?,
        vehicle_no: String?,
        badge_no: String?,
        start_meter_reading: String?,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        vehicleImageList: ArrayList<String>?,
        meterImageList: ArrayList<String>?,
        driverImageList: ArrayList<String>?,
        badgeImageList: ArrayList<String>?,
        sourceAddress: String?,
        destinationAddress: String?,
        night_allow: String?,
        tolls: ArrayList<GetRideResponsePOJO.TollsItem>)
}
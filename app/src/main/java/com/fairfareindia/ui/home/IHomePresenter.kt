package com.fairfareindia.ui.home

interface IHomePresenter {

    fun getCompareRideLocalNew(
        token: String?,
        distance: String?,
        travel_time: String?,
        travel_time_second: String?,
        permit_type: String?,
        city_id: String?,
        origin_place_id: String?,
        destination_place_id: String?,
        luggage: String?,
        way_flag: String?,
        schedule_datetime: String?,
        origin_latitude: String?,
        origin_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?)

}
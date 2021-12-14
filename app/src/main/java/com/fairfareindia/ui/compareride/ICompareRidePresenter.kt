package com.fairfareindia.ui.compareride

interface ICompareRidePresenter {
    fun getCompareRideData(
        token: String?,
        distance: String?,
        placeid: String?,
        sPlacesID: String?,
        dPlaceID: String?,
        baggs: String?,
        airport: String?,
        fdate: String?,
        currentPlaceID: String?,
        legDuration: String?,
        originLatitude: String?,
        originLongitude: String?,
        destinationLatitude: String?,
        destinationLongitude: String?

    )

    fun getCompareRideLocalNew(
        token: String?,
        distance: String?,
        travel_time: String?,
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
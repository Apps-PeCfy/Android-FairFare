package com.fairfareindia.ui.intercity


interface IInterCityPresenter {
    fun getCompareRideData(
        token: String?,
        distance: String?,
        estTime: String?,
        permitType: String?,
        fromCityID: String?,
        toCityID: String?,
        fromPlaceID: String?,
        toPlaceID: String?,
        luggage: String?,
        wayFlag: String?,
        date: String?,
        originLatitude: String?,
        originLongitude: String?,
        destinationLatitude: String?,
        destinationLongitude: String?
    )


    fun getCity(
        token: String?,
        lat: String?,
        long: String?)

    fun getFromInterCities(token: String?)

    fun getToInterCities(token: String?, fromCityID: String?)

}
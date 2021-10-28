package com.fairfareindia.ui.intercity


interface IInterCityPresenter {
    fun getCompareRideData(
        token: String?,
        distance: String?,
        estTime: String?,
        fromCityID: String?,
        toCityID: String?,
        fromPlaceID: String?,
        toPlaceID: String?,
        luggage: String?,
        airport: String?,
        date: String?)

    fun getCity(
        token: String?,
        lat: String?,
        long: String?)

    fun getFromInterCities(token: String?)

    fun getToInterCities(token: String?, fromCityID: String?)

}
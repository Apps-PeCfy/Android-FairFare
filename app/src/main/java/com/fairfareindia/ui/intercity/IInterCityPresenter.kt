package com.fairfareindia.ui.intercity


interface IInterCityPresenter {
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
        legDuration: String?

    )

    fun getCity(
        token: String?,
        lat: String?,
        long: String?)

}
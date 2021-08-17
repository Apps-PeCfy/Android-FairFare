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
        legDuration: String?

    )
}
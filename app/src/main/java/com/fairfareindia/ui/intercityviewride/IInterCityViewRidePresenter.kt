package com.fairfareindia.ui.intercityviewride


interface IInterCityViewRidePresenter {

    fun bookingRequest(
        token: String?,
        driverId: String?,
        permitType: String?,
        originAddress: String?,
        destinationAddress: String?,
        originLatitude: String?,
        originLongitude: String?,
        destinationLatitude: String?,
        destinationLongitude: String?,
        sheduleDate: String?,
        wayFlag: String?,
        vehicle_rate_card_id: String?,
        intercity_ratecard_id: String?,
        status: String?
    )

}
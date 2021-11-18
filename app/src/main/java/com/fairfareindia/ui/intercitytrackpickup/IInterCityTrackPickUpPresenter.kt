package com.fairfareindia.ui.intercitytrackpickup


interface IInterCityTrackPickUpPresenter {

    fun getRideDetails(token: String?, ride_id: String?)

    fun getDriverLocation(token: String?, ride_id: String?)

    fun cancelRide( token: String?, rideID:String?, status: String?)
}
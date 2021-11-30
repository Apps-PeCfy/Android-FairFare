package com.fairfareindia.ui.intercitytrackride


interface IInterCityTrackRidePresenter {

    fun getRideDetails(token: String?, ride_id: String?)
    fun getNearByPlaces(latitude: String?, longitude: String?)

}
package com.fairfareindia.ui.drawer.intercityrides

interface IRidesPresenter {

    fun getRide( token: String?,cnt:Int?,currentLat: String?,currentLong: String?)
    fun cancelRide( token: String?, rideID:String?, status: String?)

}
package com.fairfareindia.ui.drawer.intercityrides.ridedetails

interface IRidesDetailPresenter {

    fun getRideDetail( token: String?, rideID:String?)
    fun updatePaymentStatus( token: String?, rideID:String?, method:String?, amount:String?, payment_status:String?, gateway_type:String?, transaction_id:String?)
}
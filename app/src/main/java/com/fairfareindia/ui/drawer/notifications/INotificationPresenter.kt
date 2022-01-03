package com.fairfareindia.ui.drawer.notifications

interface INotificationPresenter {

    fun getNotificationList( token: String?, page:Int?)

    fun getRideDetail( token: String?, rideID:String?)
}
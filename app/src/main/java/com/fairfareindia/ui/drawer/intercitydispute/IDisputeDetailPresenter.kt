package com.fairfareindia.ui.drawer.intercitydispute

interface IDisputeDetailPresenter {

    fun getDisputeDetail( token: String?, disputeID:String?)

    fun fileComplaint( token: String?,id: String?)
}
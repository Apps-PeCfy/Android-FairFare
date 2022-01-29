package com.fairfareindia.ui.drawer.ratecard

interface IRateCardsPresenter {

    fun getRateCards(
        token: String?,
        permit_type: String?,
        fromCityID: String?,
        toCityID: String?)

    fun getCity(
        token: String?,
        lat: String?,
        long: String?)

    fun getFromInterCities(token: String?)

    fun getToInterCities(token: String?, fromCityID: String?)

}
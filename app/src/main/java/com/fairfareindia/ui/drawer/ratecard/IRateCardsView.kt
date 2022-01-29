package com.fairfareindia.ui.drawer.ratecard

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardModel
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse

interface IRateCardsView : IBaseView {

    fun getRateCards(model: RateCardModel?)
    fun getCitySuccess(getAllowCityResponse: GetAllowCityResponse?)
    fun getFromInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?)
    fun getToInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?)
    fun validationError(validationResponse: ValidationResponse?)
}
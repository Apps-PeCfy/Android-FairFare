package com.fairfareindia.ui.intercity

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel

interface IIntercityView : IBaseView {
    fun compareRideSuccess(info: InterCityCompareRideModel?)
    fun getCitySuccess(getAllowCityResponse: GetAllowCityResponse?)
    fun getFromInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?)
    fun getToInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?)
    fun validationError(validationResponse: ValidationResponse?)

}
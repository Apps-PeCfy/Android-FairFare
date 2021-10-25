package com.fairfareindia.ui.intercity

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IIntercityView : IBaseView {
    fun compareRideSuccess(info: CompareRideResponsePOJO?)
    fun getCitySuccess(getAllowCityResponse: GetAllowCityResponse?)
    fun validationError(validationResponse: ValidationResponse?)

}
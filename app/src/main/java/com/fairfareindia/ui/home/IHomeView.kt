package com.fairfareindia.ui.home

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel

interface IHomeView : IBaseView {
    fun onNewCompareRideLocalSuccess(info: InterCityCompareRideModel?)
    fun validationError(validationResponse: ValidationResponse?)

}
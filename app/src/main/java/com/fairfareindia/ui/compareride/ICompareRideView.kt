package com.fairfareindia.ui.compareride

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel

interface ICompareRideView : IBaseView {
    fun onSuccess(info: CompareRideResponsePOJO?)
    fun onNewCompareRideLocalSuccess(info: InterCityCompareRideModel?)
    fun validationError(validationResponse: ValidationResponse?)

}
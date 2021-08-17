package com.fairfareindia.ui.compareride

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO

interface ICompareRideView : IBaseView {
    fun onSuccess(info: CompareRideResponsePOJO?)
    fun validationError(validationResponse: ValidationResponse?)

}
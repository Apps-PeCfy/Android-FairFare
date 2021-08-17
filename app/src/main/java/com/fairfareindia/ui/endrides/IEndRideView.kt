package com.fairfareindia.ui.endrides

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.endrides.pojo.ResponseEnd

interface IEndRideView : IBaseView {
    fun endRideSuccess(endRideResponsePOJO: ResponseEnd?)
    fun validationError(validationResponse: ValidationResponse?)
}
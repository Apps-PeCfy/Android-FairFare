package com.example.fairfare.ui.endrides

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.endrides.pojo.ResponseEnd

interface IEndRideView : IBaseView {
    fun endRideSuccess(endRideResponsePOJO: ResponseEnd?)
    fun validationError(validationResponse: ValidationResponse?)
}
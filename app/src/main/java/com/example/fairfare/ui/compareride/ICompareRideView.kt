package com.example.fairfare.ui.compareride

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.compareride.pojo.CompareRideResponsePOJO

interface ICompareRideView : IBaseView {
    fun onSuccess(info: CompareRideResponsePOJO?)
    fun validationError(validationResponse: ValidationResponse?)

}
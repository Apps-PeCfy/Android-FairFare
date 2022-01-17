package com.fairfareindia.ui.drawer.intercityrides

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse

interface IRidesView : IBaseView {

    fun validationError(validationResponse: ValidationResponse?)

    fun getRidesSuccess(getRideResponsePOJO: GetRideResponsePOJO?)

    fun getCancelRideSuccess(getRideResponsePOJO: GetRideResponsePOJO?)
}
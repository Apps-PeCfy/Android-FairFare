package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel

interface IRideDetailView : IBaseView {

    fun validationError(validationResponse: ValidationResponse?)

    fun getRideDetailSuccess(model: RideDetailModel?)

}
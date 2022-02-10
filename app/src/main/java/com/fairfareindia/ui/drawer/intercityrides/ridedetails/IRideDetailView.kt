package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.intercityviewride.RazorPayModel

interface IRideDetailView : IBaseView {

    fun validationError(validationResponse: ValidationResponse?)

    fun getRideDetailSuccess(model: RideDetailModel?)
    fun updatePaymentStatusSuccess(model: RideDetailModel?)
    fun getRazorPayIdSuccess(model: RazorPayModel?)

}
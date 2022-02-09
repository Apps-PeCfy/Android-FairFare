package com.fairfareindia.ui.intercityviewride

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse


interface IIntercityViewRideView : IBaseView {
    fun bookingRequestSuccess(model: BookingRequestModel?)
    fun localBookingRequestSuccess(model: BookingRequestModel?)
    fun getViewRideDetails(model: ViewRideModel?)
    fun getRazorPayIdSuccess(model: RazorPayModel?)
    fun validationError(validationResponse: ValidationResponse?)

}
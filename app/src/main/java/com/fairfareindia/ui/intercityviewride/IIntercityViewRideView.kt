package com.fairfareindia.ui.intercityviewride

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IIntercityViewRideView : IBaseView {
    fun bookingRequestSuccess(model: BookingRequestModel?)
    fun localBookingRequestSuccess(model: BookingRequestModel?)
    fun getViewRideDetails(model: ViewRideModel?)
    fun validationError(validationResponse: ValidationResponse?)

}
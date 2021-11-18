package com.fairfareindia.ui.intercitytrackpickup

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO

interface IIntercityTrackPickUpView : IBaseView {
    fun getRideDetails(model: RideDetailModel?)
    fun getDriverLocation(model: DriverLocationModel?)
    fun getCancelRideSuccess(getRideResponsePOJO: GetRideResponsePOJO?)
    fun validationError(validationResponse: ValidationResponse?)

}
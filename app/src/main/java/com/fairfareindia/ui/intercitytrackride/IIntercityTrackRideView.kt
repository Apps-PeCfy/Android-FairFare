package com.fairfareindia.ui.intercitytrackride

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel

interface IIntercityTrackRideView : IBaseView {
    fun getRideDetails(model: RideDetailModel?)
    fun validationError(validationResponse: ValidationResponse?)

}
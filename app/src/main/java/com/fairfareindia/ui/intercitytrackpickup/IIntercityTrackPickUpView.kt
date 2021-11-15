package com.fairfareindia.ui.intercitytrackpickup

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse

interface IIntercityTrackPickUpView : IBaseView {
    fun getRideDetails(model: RideDetailModel?)
    fun validationError(validationResponse: ValidationResponse?)

}
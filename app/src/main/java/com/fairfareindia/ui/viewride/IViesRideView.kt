package com.fairfareindia.ui.viewride

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IViesRideView : IBaseView {
    fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?)

    fun validationError(validationResponse: ValidationResponse?)

}
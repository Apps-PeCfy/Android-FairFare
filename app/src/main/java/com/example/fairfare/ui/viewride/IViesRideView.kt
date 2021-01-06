package com.example.fairfare.ui.viewride

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IViesRideView : IBaseView {
    fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?)

    fun validationError(validationResponse: ValidationResponse?)

}
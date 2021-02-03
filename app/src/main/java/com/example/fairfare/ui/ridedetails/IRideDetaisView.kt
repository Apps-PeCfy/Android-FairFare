package com.example.fairfare.ui.ridedetails

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IRideDetaisView  : IBaseView {

    fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?)

    fun procedPopUp(msg: ValidationResponse?)

    fun validationError(validationResponse: ValidationResponse?)



}
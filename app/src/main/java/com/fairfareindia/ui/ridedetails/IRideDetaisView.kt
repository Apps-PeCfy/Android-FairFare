package com.fairfareindia.ui.ridedetails

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IRideDetaisView  : IBaseView {

    fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?)

    fun procedPopUp(msg: ValidationResponse?)

    fun validationError(validationResponse: ValidationResponse?)



}
package com.fairfareindia.ui.drawer.myrides

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IMyRidesView : IBaseView {


    fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?)

    fun validationError(validationResponse: ValidationResponse?)

    fun getRidesSuccess(getRideResponsePOJO: GetRideResponsePOJO?)
}
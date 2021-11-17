package com.fairfareindia.ui.drawer.intercityrides

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IRidesView : IBaseView {


    fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?)

    fun validationError(validationResponse: ValidationResponse?)

    fun getRidesSuccess(getRideResponsePOJO: GetRideResponsePOJO?)

    fun getCancelRideSuccess(getRideResponsePOJO: GetRideResponsePOJO?)
}
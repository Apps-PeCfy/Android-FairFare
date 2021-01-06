package com.example.fairfare.ui.drawer.myrides

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO

interface IMyRidesView : IBaseView {


    fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?)

    fun validationError(validationResponse: ValidationResponse?)

    fun getRidesSuccess(getRideResponsePOJO: GetRideResponsePOJO?)
}
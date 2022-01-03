package com.fairfareindia.ui.drawer.notifications

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel

interface INotificationView : IBaseView {

    fun getNotificationListSuccess(model: NotificationModel?)

    fun getRideDetailSuccess(model: RideDetailModel?)

    fun validationError(validationResponse: ValidationResponse?)

}
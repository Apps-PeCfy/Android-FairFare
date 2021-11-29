package com.fairfareindia.ui.drawer.intercitydispute

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO

interface IDisputeDetailView : IBaseView {

    fun validationError(validationResponse: ValidationResponse?)
    fun getDisputeDetailSuccess(model: DisputeDetailModel?)
    fun fileComplaintSuccess(model: DeleteDisputResponsePOJO?)
}
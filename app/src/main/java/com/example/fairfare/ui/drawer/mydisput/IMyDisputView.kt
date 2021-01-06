package com.example.fairfare.ui.drawer.mydisput

import com.example.fairfare.base.IBaseView
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.example.fairfare.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import com.example.fairfare.ui.drawer.myrides.pojo.GetRideResponsePOJO

interface IMyDisputView : IBaseView {

    fun validationError(validationResponse: ValidationResponse?)

    fun getDisputSuccess(getDisputResponsePOJO: GetDisputResponsePOJO?)

    fun deleteDisputSuccess(deleteDisputResponsePOJO: DeleteDisputResponsePOJO?)

    fun filecomplaintSuccess(deleteDisputResponsePOJO: DeleteDisputResponsePOJO?)
}
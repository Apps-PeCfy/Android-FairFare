package com.fairfareindia.ui.drawer.mydisput

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.pojo.GetDisputResponsePOJO

interface IMyDisputView : IBaseView {

    fun validationError(validationResponse: ValidationResponse?)

    fun getDisputSuccess(getDisputResponsePOJO: GetDisputResponsePOJO?)

    fun deleteDisputSuccess(deleteDisputResponsePOJO: DeleteDisputResponsePOJO?)

    fun filecomplaintSuccess(deleteDisputResponsePOJO: DeleteDisputResponsePOJO?)
}
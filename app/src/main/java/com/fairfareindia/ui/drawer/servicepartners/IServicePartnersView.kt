package com.fairfareindia.ui.drawer.servicepartners

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse

interface IServicePartnersView : IBaseView {

    fun validationError(validationResponse: ValidationResponse?)

    fun getServicePartnersSuccess(model: ServicePartnerModel?)

}
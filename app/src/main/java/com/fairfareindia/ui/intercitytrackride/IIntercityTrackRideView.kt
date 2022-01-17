package com.fairfareindia.ui.intercitytrackride

import com.fairfareindia.base.IBaseView
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.intercitytrackride.NearByPlacesPOJO.NearByResponse

interface IIntercityTrackRideView : IBaseView {
    fun getRideDetails(model: RideDetailModel?)
    fun getNearByPlaces(model: NearByResponse?)
    fun validationError(validationResponse: ValidationResponse?)

}
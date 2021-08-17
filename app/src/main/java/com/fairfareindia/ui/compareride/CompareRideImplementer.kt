package com.fairfareindia.ui.compareride

import com.fairfareindia.networking.ApiClient.client
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CompareRideImplementer(private val view: ICompareRideView) : ICompareRidePresenter {
    var compareRideResponsePOJO: CompareRideResponsePOJO? = null
    override fun getCompareRideData(
        token: String?,
        distance: String?,
        placeid: String?,
        sPlacesID: String?,
        dPlaceID: String?,
        baggs: String?,
        airport: String?,
        formatedDate: String?,
        currentPlaceID: String?,
        legDuration: String?
    )
    {
        view.showWait()
        val call = client.getCompareRide(
            "Bearer $token",
            distance,
            placeid,
            sPlacesID, dPlaceID, baggs, airport, formatedDate,currentPlaceID,legDuration
        )
        call!!.enqueue(object : Callback<CompareRideResponsePOJO?> {
            override fun onResponse(call: Call<CompareRideResponsePOJO?>, response: Response<CompareRideResponsePOJO?>)
            {
                view.removeWait()

                if (response.code() == 200) {
                    view.removeWait()

                    compareRideResponsePOJO = response.body()
                    view.onSuccess(response.body())
                }else if (response.code() == 400 || response.code() ==422) {
                    view.removeWait()

                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        view.validationError(pojo)
                    } catch (exception: IOException) {
                    }
                }else{
                    view.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<CompareRideResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

}
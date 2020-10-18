package com.example.fairfare.ui.compareride

import com.example.fairfare.networking.ApiClient.client
import com.example.fairfare.ui.compareride.pojo.CompareRideResponsePOJO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompareRideImplementer(private val view: ICompareRideView) : ICompareRidePresenter {
    var compareRideResponsePOJO: CompareRideResponsePOJO? = null
    override fun getCompareRideData(
        token: String?,
        distance: String?,
        placeid: String?,
        sPlacesID: String?,
        dPlaceID: String?,
        baggs: String?,
        airport: String?
    ) {
        view.showWait()
        val call = client.getCompareRide(
            "Bearer $token",
            distance,
            "2707",
            sPlacesID, dPlaceID, baggs, airport
        )
        call!!.enqueue(object : Callback<CompareRideResponsePOJO?> {
            override fun onResponse(
                call: Call<CompareRideResponsePOJO?>,
                response: Response<CompareRideResponsePOJO?>
            ) {
                if (response.body() != null) {
                    compareRideResponsePOJO = response.body()
                    view.onSuccess(response.body())
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
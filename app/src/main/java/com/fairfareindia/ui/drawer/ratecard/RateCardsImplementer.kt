package com.fairfareindia.ui.drawer.ratecard

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardModel
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RateCardsImplementer(private val view: IRateCardsView) : IRateCardsPresenter {
    override fun getRateCards(
        token: String?,
        permit_type: String?,
        fromCityID: String?,
        toCityID: String?
    ) {

        view.showWait()

        val call = ApiClient.client.getNewRateCardDetails("Bearer $token", permit_type, fromCityID, toCityID)
        call!!.enqueue(object : Callback<RateCardModel?> {
            override fun onResponse(
                call: Call<RateCardModel?>,
                response: Response<RateCardModel?>
            ) {
                view.removeWait()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getRateCards(response.body())
                    }
                } else if (response.code() == 422) {
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

                }
            }

            override fun onFailure(
                call: Call<RateCardModel?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })

    }


    override fun getCity(token: String?, lat: String?, long: String?) {
        view.showWait()

        val call = ApiClient.client.getAllowCities("Bearer $token", lat, long)
        call!!.enqueue(object : Callback<GetAllowCityResponse?> {
            override fun onResponse(
                call: Call<GetAllowCityResponse?>,
                response: Response<GetAllowCityResponse?>
            ) {
                view.removeWait()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getCitySuccess(response.body())
                    }
                } else if (response.code() == 422) {
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

                }
            }

            override fun onFailure(
                call: Call<GetAllowCityResponse?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun getFromInterCities(token: String?) {
        view.showWait()

        val call = ApiClient.client.getFromInterCities("Bearer $token")
        call!!.enqueue(object : Callback<GetAllowCityResponse?> {
            override fun onResponse(
                call: Call<GetAllowCityResponse?>,
                response: Response<GetAllowCityResponse?>
            ) {
                view.removeWait()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getFromInterCitiesSuccess(response.body())
                    }
                } else if (response.code() == 422) {
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

                }
            }

            override fun onFailure(
                call: Call<GetAllowCityResponse?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun getToInterCities(token: String?, fromCityID: String?) {
        view.showWait()

        val call = ApiClient.client.getToInterCities("Bearer $token", fromCityID)
        call!!.enqueue(object : Callback<GetAllowCityResponse?> {
            override fun onResponse(
                call: Call<GetAllowCityResponse?>,
                response: Response<GetAllowCityResponse?>
            ) {
                view.removeWait()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getToInterCitiesSuccess(response.body())
                    }
                } else if (response.code() == 422) {
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

                }
            }

            override fun onFailure(
                call: Call<GetAllowCityResponse?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

}
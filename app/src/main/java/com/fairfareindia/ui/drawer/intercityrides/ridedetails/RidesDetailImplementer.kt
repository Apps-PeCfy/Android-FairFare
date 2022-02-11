package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.intercityviewride.RazorPayModel
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import java.io.IOException

class RidesDetailImplementer(private val view: IRideDetailView) : IRidesDetailPresenter {

    override fun getRideDetail(token: String?, rideID: String?) {
        view.showWait()
        val call = ApiClient.client.getRideDetails("Bearer $token", rideID)
        call!!.enqueue(object : Callback<RideDetailModel?> {
            override fun onResponse(
                call: Call<RideDetailModel?>,
                response: Response<RideDetailModel?>
            ) {

                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getRideDetailSuccess(response.body())
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

                }else{
                    view.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<RideDetailModel?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun updatePaymentStatus(
        token: String?,
        rideID: String?,
        method: String?,
        amount: String?,
        payment_status: String?,
        gateway_type: String?,
        transaction_id: String?,
        rp_order_id: String?,
        rp_payment_id: String?,
        razorpay_key: String?,
        razorpay_secret_key: String?
    ) {


        view.showWait()
        val call = ApiClient.client.updatePaymentStatus("Bearer $token", rideID, method, amount, payment_status, gateway_type, transaction_id, rp_order_id, rp_payment_id, razorpay_key, razorpay_secret_key)
        call!!.enqueue(object : Callback<RideDetailModel?> {
            override fun onResponse(
                call: Call<RideDetailModel?>,
                response: Response<RideDetailModel?>
            ) {

                view.removeWait()

                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.updatePaymentStatusSuccess(response.body())
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

                }else{
                    view.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<RideDetailModel?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun getRazorPayOrderID(token: String?, union_id: String?, amount: String?) {
        view.showWait()
        val call = ApiClient.client.getRazorPayOrderId(
            "Bearer $token",
            union_id,
            amount
        )
        call!!.enqueue(object : Callback<RazorPayModel?> {
            override fun onResponse(
                call: Call<RazorPayModel?>,
                response: Response<RazorPayModel?>
            ) {
                view.removeWait()

                if (response.code() == 200) {
                    view.removeWait()

                    view.getRazorPayIdSuccess(response.body())
                } else if (response.code() == 400 || response.code() == 422) {
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
                } else {
                    view.onFailure(response.message())
                }
            }

            override fun onFailure(
                call: Call<RazorPayModel?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }


}
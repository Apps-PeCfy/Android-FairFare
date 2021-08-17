package com.fairfareindia.ui.drawer.mydisput

import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyDisputImplementer(private val view: IMyDisputView) : IMyDisputPresenter {


    override fun getMyDisput(token: String?) {


        view.showWait()
        val call = ApiClient.client.getMyDispute("Bearer $token")
        call!!.enqueue(object : Callback<GetDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<GetDisputResponsePOJO?>,
                response: Response<GetDisputResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getDisputSuccess(response.body())
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
                    view.onFailure("Internal Server Error")

                }
            }

            override fun onFailure(
                call: Call<GetDisputResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun deleteDisput(token: String?, id: String?) {

        view.showWait()
        val call = ApiClient.client.deleteDisput("Bearer $token", id)
        call!!.enqueue(object : Callback<DeleteDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<DeleteDisputResponsePOJO?>,
                response: Response<DeleteDisputResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.deleteDisputSuccess(response.body())
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
                    view.onFailure("Internal Server Error")

                }
            }

            override fun onFailure(
                call: Call<DeleteDisputResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

    override fun fileComplaint(token: String?, id: String?) {
        view.showWait()
        val call = ApiClient.client.saveComplaint("Bearer $token", id)
        call!!.enqueue(object : Callback<DeleteDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<DeleteDisputResponsePOJO?>,
                response: Response<DeleteDisputResponsePOJO?>
            ) {

                view.removeWait()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.filecomplaintSuccess(response.body())
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

                } else {
                    view.onFailure("Internal Server Error")

                }
            }

            override fun onFailure(
                call: Call<DeleteDisputResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }

}
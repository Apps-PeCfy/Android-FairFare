package com.fairfareindia.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

open class APIManager private constructor() {
    var listener: APIManagerInterface? = null
    var requestQueue: RequestQueue? = null
    var jsonString: String? = null




    interface APIManagerInterface {
        fun onSuccess(resultObj: Any?)
        fun onError(error: String?)
    }


    fun postAPI(url: String?, params: JSONObject?, classType: Class<*>?, context: Context?, listener: APIManagerInterface?) {
        Log.e("LASTAPI", url!!)
        if (ConnectionManager.Connection(context!!)) {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context)
            }
            val mJsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, url, params,
                Response.Listener { response -> //  Log.d("TAG", "ON RESPONSE " + response);
                    try {
                        val gson = Gson()
                        val jsonString = response.toString()

                        val model: Any? = gson.fromJson(jsonString,classType)
                        listener?.onSuccess(model)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        if (listener != null) {
                            try {
                                listener.onError(e.toString())
                            } catch (e1: JSONException) {
                                e1.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        if (listener != null) {
                            try {
                                listener.onError(e.toString())
                            } catch (e1: JSONException) {
                                e1.printStackTrace()
                            }
                        }
                    }
                }, Response.ErrorListener { error -> listener?.let { parseVolleyError(context, error, it) } }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json; charset=utf-8"
                    return params
                }
            }
            requestQueue!!.add(mJsonObjectRequest)
            mJsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                Constants.REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        } else {
            listener?.onError("Please check your internet connection")
        }
    }



    fun getAPI(url: String?, params: JSONObject?, classType: Class<*>?, context: Context?, listener: APIManagerInterface?) {
        Log.e("LASTAPI", url!!)
        if (ConnectionManager.Connection(context!!)) {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context)
            }

            val mJsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, url, params,
                Response.Listener { response -> //     Log.d("TAG", "RESPONSE  = " + response);
                    try {
                        val gson = Gson()
                        val jsonString = response.toString()

                        val model: Any? = gson.fromJson(jsonString,classType)
                        listener?.onSuccess(model)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        if (listener != null) {
                            try {
                                listener.onError(e.toString())
                            } catch (e1: JSONException) {
                                e1.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        if (listener != null) {
                            try {
                                listener.onError(e.toString())
                            } catch (e1: JSONException) {
                                e1.printStackTrace()
                            }
                        }
                    }
                }, Response.ErrorListener { error -> listener?.let { parseVolleyError(context, error, it) } }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json; charset=utf-8"

                    return params
                }
            }
            requestQueue!!.add(mJsonObjectRequest)
            mJsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                Constants.REQUEST_TIMEOUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        } else {
            if (listener != null && context != null) {
                listener.onError("Please check your internet connection")
            }
        }
    }

    fun parseVolleyError(context: Context?, error: VolleyError, listener: APIManagerInterface) {
        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
            showUnauthorzedAccess(context)
        } else {
            listener.onError(error.message)
        }
    }

    fun showUnauthorzedAccess(context: Context?) {
        val builder = AlertDialog.Builder(context!!)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
           // AppPreference.getInstance(context)?.logoutUser()
        }


        //builder.setTitle(R.string.str_unauthorized_access);
        builder.setMessage("Session Expired!")
        builder.setCancelable(false)
        builder.show()
    }

    companion object {
        private var sInstance: APIManager? = null

        @Synchronized
        fun getInstance(context: Context) : APIManager {
            if (sInstance == null) {
                sInstance = APIManager()
            }
            return sInstance as APIManager
        }
    }


}
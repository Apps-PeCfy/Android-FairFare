package com.fairfareindia.ui.drawer.covid19

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.privacypolicy.ContentResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Covid : Fragment() {

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    @JvmField
    @BindView(R.id.webView)
    var webView: WebView? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_content_page, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        callApi()

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_home_lang, menu!!)
        super.onCreateOptionsMenu(menu!!, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "LOCALITY"
                )
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(intent)
            }
        }
        return true
    }


    private fun callApi() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog


        val call = ApiClient.client.pageContents( "Covid-19")
        call!!.enqueue(object : Callback<ContentResponsePOJO?> {
            override fun onResponse(
                call: Call<ContentResponsePOJO?>,
                response: Response<ContentResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        webView!!.settings.javaScriptEnabled = true
                        webView!!.loadDataWithBaseURL(
                            null,
                            response.body()!!.pageContent!!.content!!,
                            "text/html",
                            "utf-8",
                            null
                        )

                    }
                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(activity, pojo.message, Toast.LENGTH_LONG).show()


                    } catch (exception: IOException) {
                    }

                }
            }

            override fun onFailure(
                call: Call<ContentResponsePOJO?>, t: Throwable
            ) {
                progressDialog.dismiss()
                Toast.makeText(activity, t.stackTrace.toString(), Toast.LENGTH_LONG).show()
                Log.d("response", t.stackTrace.toString())
            }
        })
    }


    private fun initView() {
        val spinnerLang: Spinner = activity!!.findViewById(R.id.spinnerLang)
        spinnerLang.visibility = View.GONE


        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "Covid-19"
        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }


}
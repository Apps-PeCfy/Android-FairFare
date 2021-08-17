package com.fairfareindia.ui.drawer.setting

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.fairfareindia.ui.drawer.setting.pojo.SettingResponsePojo
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Setting : Fragment(), AdapterView.OnItemSelectedListener {

    var token: String? = null
    var preferencesManager: PreferencesManager? = null
    var spncity = 0
    var spinnerCity: List<String?>? = null
    var langSpinner: List<String?>? = null
    var currencySpinner: List<String?>? = null
    var unitSpinner: List<String?>? = null
    var timeFormatSpinner: List<String?>? = null
   var settingpojo: SettingResponsePojo? = null

    @JvmField
    @BindView(R.id.spinner_lang)
    var spinner_lang: Spinner? = null

    @JvmField
    @BindView(R.id.spinner_city)
    var spinner_city: Spinner? = null

    @JvmField
    @BindView(R.id.spinner_currency)
    var spinner_currency: Spinner? = null

    @JvmField
    @BindView(R.id.spinner_unit)
    var spinner_unit: Spinner? = null

    @JvmField
    @BindView(R.id.spinner_timeformat)
    var spinner_timeformat: Spinner? = null

    @JvmField
    @BindView(R.id.btnSave)
    var btnSave: Button? = null

    var sharedpreferences: SharedPreferences? = null


    // var langSpinner = arrayOf<String?>("English")
    var citySpinner = arrayOf<String?>("Mumbai", "Pune")
    // var currencySpinner = arrayOf<String?>("INR")
    //  var unitSpinner = arrayOf<String?>("Kilometers(KM)")
    //   var timeFormatSpinner = arrayOf<String?>("12")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        getUserSettings()
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


    private fun getUserSettings() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.getUserSetting("Bearer $token")!!.enqueue(object :
            Callback<SettingResponsePojo?> {
            override fun onResponse(
                call: Call<SettingResponsePojo?>,
                response: Response<SettingResponsePojo?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    settingpojo = response.body()

                    initspinner(response.body())


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

                } else {
                    Toast.makeText(
                        activity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<SettingResponsePojo?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })


    }

    private fun initspinner(body: SettingResponsePojo?) {
        spinnerCity = ArrayList()
        langSpinner = ArrayList()
        currencySpinner = ArrayList()
        unitSpinner = ArrayList()
        timeFormatSpinner = ArrayList()
        (spinnerCity as ArrayList<String?>).add(body!!.userSetting!!.city)
        (langSpinner as ArrayList<String?>).add(body!!.userSetting!!.language)
        (currencySpinner as ArrayList<String?>).add(body!!.userSetting!!.currency)
        (unitSpinner as ArrayList<String?>).add(body!!.userSetting!!.measurementUnit)
        (timeFormatSpinner as ArrayList<String?>).add(body!!.userSetting!!.timeFormat)


        val spinnerLuggage: ArrayAdapter<*> = ArrayAdapter<Any?>(
            activity!!.applicationContext, R.layout.simple_setting_spinner,
            langSpinner!!
        )
        spinnerLuggage.setDropDownViewResource(R.layout.simple_setting_spinner)
        spinner_lang?.adapter = spinnerLuggage
        spinner_lang?.setSelection(0)

        val spinnercity: ArrayAdapter<*> = ArrayAdapter<Any?>(
            activity!!.applicationContext,
            R.layout.simple_setting_spinner,
            citySpinner
        )
        spinnercity.setDropDownViewResource(R.layout.simple_setting_spinner)
        spinner_city?.adapter = spinnercity
        if (body!!.userSetting!!.city.equals("Mumbai")) {
            spinner_city?.setSelection(0)

        } else {
            spinner_city?.setSelection(1)

        }
        spinner_city!!.setOnItemSelectedListener(this)


        val spinnercurrency: ArrayAdapter<*> = ArrayAdapter<Any?>(
            activity!!.applicationContext,
            R.layout.simple_setting_spinner,
            currencySpinner!!
        )
        spinnercurrency.setDropDownViewResource(R.layout.simple_setting_spinner)
        spinner_currency?.adapter = spinnercurrency
        spinner_currency?.setSelection(0)

        val spinnerunit: ArrayAdapter<*> = ArrayAdapter<Any?>(
            activity!!.applicationContext,
            R.layout.simple_setting_spinner,
            unitSpinner!!
        )
        spinnerunit.setDropDownViewResource(R.layout.simple_setting_spinner)
        spinner_unit?.adapter = spinnerunit
        spinner_unit?.setSelection(0)

        val spinnerTimefprmat: ArrayAdapter<*> = ArrayAdapter<Any?>(
            activity!!.applicationContext,
            R.layout.simple_setting_spinner,
            timeFormatSpinner!!
        )
        spinnerTimefprmat.setDropDownViewResource(R.layout.simple_setting_spinner)
        spinner_timeformat?.adapter = spinnerTimefprmat
        spinner_timeformat?.setSelection(0)


    }

    private fun initView() {
        val spinnerLang: Spinner = activity!!.findViewById(R.id.spinnerLang)
        spinnerLang.visibility = View.GONE


        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "Settings"
        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

    }

    @OnClick(R.id.btnSave)
    fun btnSave() {

        if (ProjectUtilities.checkInternetAvailable(activity)) {

            var city: String? = null

            if (spncity == 0) {
                city = "Mumbai"
            } else {
                city = "Pune"
            }


            val progressDialog = ProgressDialog(activity)
            progressDialog.setCancelable(false) // set cancelable to false
            progressDialog.setMessage("Please Wait") // set message
            progressDialog.show() // show progress dialog

            ApiClient.client.updateUserSetting(
                "Bearer $token", settingpojo!!.userSetting!!.id,
                settingpojo!!.userSetting!!.language,
                city,
                settingpojo!!.userSetting!!.currency,
                settingpojo!!.userSetting!!.measurementUnit,
                settingpojo!!.userSetting!!.timeFormat
            )!!.enqueue(object :
                Callback<ContactUsResponsePojo?> {
                override fun onResponse(
                    call: Call<ContactUsResponsePojo?>,
                    response: Response<ContactUsResponsePojo?>
                ) {
                    progressDialog.dismiss()
                    if (response.code() == 200) {
                        Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_LONG)
                            .show()

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

                    } else {
                        Toast.makeText(
                            activity,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ContactUsResponsePojo?>,
                    t: Throwable
                ) {
                    progressDialog.dismiss()
                    Log.d("response", t.stackTrace.toString())
                }
            })

        }else{
            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )
        }



    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!!.id == R.id.spinner_city) {
            spncity = position
        }

    }
}
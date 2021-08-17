/*
 * Author : Kiran Poman
 * Module :  user profile Module
 * Version : V 1.0
 * Sprint : VIII
 * Date of Development : 20/03/2019 04.00.00 PM
 * Date of Modified : 19/04/2019 06.30.00 PM
 * Comments :  user profile screen
 * Output :  View User profile activity
 */
package com.fairfareindia.ui.drawer.myaccount

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myaccount.pojo.UpdateProfileResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.fairfareindia.utils.RoundedImageView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyAccountFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    AdapterView.OnItemSelectedListener {
    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null

    @JvmField
    @BindView(R.id.userName)
    var userName: TextView? = null

    @JvmField
    @BindView(R.id.tvUserNumber)
    var tvUserNumber: TextView? = null

    @JvmField
    @BindView(R.id.tvUserEmailID)
    var tvUserEmailID: EditText? = null

    @JvmField
    @BindView(R.id.tvUserGender)
    var tvUserGender: Spinner? = null

    @JvmField
    @BindView(R.id.tvUserDOB)
    var tvUserDOB: TextView? = null

    @JvmField
    @BindView(R.id.tvUserLocation)
    var tvUserLocation: TextView? = null


    @JvmField
    @BindView(R.id.btnSave)
    var btnSave: Button? = null

    @JvmField
    @BindView(R.id.tvUserPosotion)
    var tvUserPosotion: EditText? = null


    @JvmField
    @BindView(R.id.iv_user)
    var iv_user: RoundedImageView? = null


    var mday = 0
    var mmonth = 0
    var myear = 0
    var profession: String? = null
    var calendar: Calendar? = null

    var luggageSpinner = arrayOf<String?>(
        "Male", "Female", "Other"
    )

    var spngen = 0
    var token: String? = null
    var DOBsendTxt: String? = null
    var gender: String? = null
    var strMonth: String? = null
    var stDay: String? = null


    var formatviewRide = SimpleDateFormat("yyyy-mm-dd")

    var formatRide = SimpleDateFormat("dd-mm-yyyy")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_account, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        userName!!.text =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_NAME)
        tvUserNumber!!.text =
            "+91 " + preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PHONENO)

        if (preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_EMAIL)!!
                .isNotEmpty()
        ) {
            tvUserEmailID!!.isClickable = false
            tvUserEmailID!!.isEnabled = false
            tvUserEmailID!!.setText(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_EMAIL))

        } else {
            tvUserEmailID!!.isClickable = true
            tvUserEmailID!!.isEnabled = true

            tvUserEmailID!!.setText(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_EMAIL))

        }

        if ((preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_GENDER)).equals("Male")) {
            spngen = 0
        } else if ((preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_GENDER)).equals(
                "Female"
            )
        ) {
            spngen = 1
        } else {
            spngen = 2
        }


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(activity!!, R.layout.simple_item_spinner, luggageSpinner)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tvUserGender?.adapter = spinnerLuggage
        tvUserGender?.setSelection(spngen)
        tvUserGender!!.setOnItemSelectedListener(this)



        Glide.with(this@MyAccountFragment)
            .load(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK))
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            ).into(iv_user!!)







        if (!(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFESTION))!!.isEmpty()) {
            tvUserPosotion!!.setText((preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFESTION)).toString())

        }

        if (!(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_LOCATION))!!.isEmpty()) {
            tvUserLocation!!.setText((preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_LOCATION)).toString())

        }


        if (!(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_DOB))!!.isEmpty()) {

            val formaredDate =
                formatRide.format(formatviewRide.parse(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_DOB)))


            tvUserDOB!!.text = formaredDate
        }




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


    private fun initView() {

        val spinnerLang: Spinner = activity!!.findViewById(R.id.spinnerLang)
        spinnerLang.visibility = View.GONE

        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "My Account"

        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }

    override fun onResume() {
        super.onResume()
    }


    @OnClick(R.id.tvUserDOB)
    fun dob() {

        calendar = Calendar.getInstance()
        calendar!!.add(java.util.Calendar.YEAR, -18)
        myear = calendar!!.get(Calendar.YEAR)
        mmonth = calendar!!.get(Calendar.MONTH)
        mday = calendar!!.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog =
            DatePickerDialog(activity!!, this@MyAccountFragment, myear, mmonth, mday)
        datePickerDialog.getDatePicker().setMaxDate(calendar!!.timeInMillis)
        datePickerDialog.show()


    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myear = year
        mmonth = month + 1
        mday = dayOfMonth
        Log.d("respoasseC", "$myear-$mmonth-$mday")

        if ((mmonth.toString().length == 1)) {
            strMonth = ("0" + mmonth)
        } else {
            strMonth = mmonth.toString()
        }

        if ((mday.toString().length == 1)) {
            stDay = ("0" + mday)
        } else {
            stDay = mday.toString()
        }


        tvUserDOB!!.text = "$stDay-" + strMonth + "-" + myear
        //  DOBsendTxt = "$myear-" + strMonth + "-" + stDay
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        spngen = position
    }


    @OnClick(R.id.btnSave)
    fun btnSave() {

        if(ProjectUtilities.checkInternetAvailable(context)) {

            if (spngen == 0) {
                gender = "Male"
            } else if (spngen == 1) {
                gender = "Female"
            } else {
                gender = "Other"
            }


            Log.d(
                "wedsdsdede", tvUserNumber!!.text.toString()
                        + "  " + tvUserEmailID!!.text
                        + "   " + gender
                        + "   " + tvUserDOB!!.text
                        + "   " + tvUserPosotion!!.text
                        + "   " + tvUserLocation!!.text
            )



            var formaredDate = ""

            if (tvUserDOB!!.text.isNotEmpty()) {


                val formatviewRide = SimpleDateFormat("dd-mm-yyyy")

                val formatRide = SimpleDateFormat("yyyy-mm-dd")

                 formaredDate = formatRide.format(formatviewRide.parse(tvUserDOB!!.text.toString()))

            }
                if (tvUserEmailID!!.isEnabled) {
                    callAPIWithEmail(formaredDate)

                } else {
                    callAPIWithOutEmail(formaredDate)
                }



        }else{
            ProjectUtilities.showToast(context,"No internet connection")
        }


    }

    private fun callAPIWithOutEmail(formaredDate: String) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.updateProfileWithOutEmail(
            "Bearer $token",
            userName!!.text.toString(),
            gender, formaredDate,
            tvUserLocation!!.text.toString(),
            tvUserPosotion!!.text.toString()
        )!!.enqueue(object :
            Callback<UpdateProfileResponsePOJO?> {
            override fun onResponse(
                call: Call<UpdateProfileResponsePOJO?>,
                response: Response<UpdateProfileResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {


                    Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()


                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_NAME,
                        response.body()!!.user!!.name
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_EMAIL,
                        response.body()!!.user!!.email
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_PHONENO,
                        response.body()!!.user!!.phoneNo
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_GENDER,
                        response.body()!!.user!!.gender
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_PROFESTION,
                        response.body()!!.user!!.profession
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_DOB,
                        response.body()!!.user!!.dateOfBirth
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK,
                        response.body()!!.user!!.profilePic
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_LOCATION,
                        response.body()!!.user!!.location
                    )


                    if ((response!!.body()!!.user!!.gender).equals("Male")) {
                        spngen = 0
                    } else if ((response!!.body()!!.user!!.gender).equals("Female")) {
                        spngen = 1
                    } else {
                        spngen = 2
                    }

                    Glide.with(this@MyAccountFragment)
                        .load(response.body()!!.user!!.profilePic)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .dontTransform()
                        ).into(iv_user!!)


                    userName!!.text = response.body()!!.user!!.name
                    tvUserNumber!!.text = "+91 " + response.body()!!.user!!.phoneNo
                    tvUserEmailID!!.setText(response.body()!!.user!!.email)


                    if(response.body()!!.user!!.dateOfBirth!!.isEmpty()){

                        tvUserDOB!!.text = response.body()!!.user!!.dateOfBirth

                    }else{
                        val formatviewRide = SimpleDateFormat("yyyy-mm-dd")

                        val formatRide = SimpleDateFormat("dd-mm-yyyy")
                        val formaredDate = formatRide.format(
                            formatviewRide.parse(
                                response.body()!!.user!!.dateOfBirth
                            )
                        )


                        tvUserDOB!!.text = formaredDate

                    }
                   tvUserPosotion!!.setText(response.body()!!.user!!.profession.toString())
                    tvUserLocation!!.setText(response.body()!!.user!!.location.toString())


                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(
                                activity,
                                pojo.errors!!.get(0).message,
                                Toast.LENGTH_LONG
                            )
                            .show()


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
                call: Call<UpdateProfileResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })


    }

    private fun callAPIWithEmail(formaredDate: String) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.updateProfile(
            "Bearer $token",
            userName!!.text.toString(),
            gender, formaredDate,
            tvUserLocation!!.text.toString(),
            tvUserPosotion!!.text.toString(),
            tvUserEmailID!!.text.toString()
        )!!.enqueue(object :
            Callback<UpdateProfileResponsePOJO?> {
            override fun onResponse(
                call: Call<UpdateProfileResponsePOJO?>,
                response: Response<UpdateProfileResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {


                    Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()


                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_NAME,
                        response.body()!!.user!!.name
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_EMAIL,
                        response.body()!!.user!!.email
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_PHONENO,
                        response.body()!!.user!!.phoneNo
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_GENDER,
                        response.body()!!.user!!.gender
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_PROFESTION,
                        response.body()!!.user!!.profession
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_DOB,
                        response.body()!!.user!!.dateOfBirth
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK,
                        response.body()!!.user!!.profilePic
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_LOGIN_LOCATION,
                        response.body()!!.user!!.location
                    )


                    if ((response!!.body()!!.user!!.gender).equals("Male")) {
                        spngen = 0
                    } else if ((response!!.body()!!.user!!.gender).equals("Female")) {
                        spngen = 1
                    } else {
                        spngen = 2
                    }

                    Glide.with(this@MyAccountFragment)
                        .load(response.body()!!.user!!.profilePic)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .dontTransform()
                        ).into(iv_user!!)


                    userName!!.text = response.body()!!.user!!.name
                    tvUserNumber!!.text = "+91 " + response.body()!!.user!!.phoneNo
                    tvUserEmailID!!.setText(response.body()!!.user!!.email)
                    val formatviewRide = SimpleDateFormat("yyyy-mm-dd")

                    val formatRide = SimpleDateFormat("dd-mm-yyyy")
                    val formaredDate = formatRide.format(
                        formatviewRide.parse(
                            response.body()!!.user!!.dateOfBirth
                        )
                    )


                    tvUserDOB!!.text = formaredDate
                    tvUserPosotion!!.setText(response.body()!!.user!!.profession.toString())
                    tvUserLocation!!.setText(response.body()!!.user!!.location.toString())


                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(
                                activity,
                                pojo.errors!!.get(0).message,
                                Toast.LENGTH_LONG
                            )
                            .show()


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
                call: Call<UpdateProfileResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })

    }


}
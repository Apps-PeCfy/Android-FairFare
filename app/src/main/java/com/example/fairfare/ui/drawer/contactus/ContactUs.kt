package com.example.fairfare.ui.drawer.contactus

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.fairfare.R
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class ContactUs : Fragment() {

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null


    @JvmField
    @BindView(R.id.btnSubmitContactUs)
    var btnSubmitContactUs: Button? = null

 @JvmField
    @BindView(R.id.editReview)
    var editReview: EditText? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_contact_us, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

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
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "Contact Us"

        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }


    @OnClick(R.id.btnSubmitContactUs)
    fun btnSubmitContactUs() {


        if ((editReview!!.text.toString()).length < 1) {
            Toast.makeText(activity, "Please enter message", Toast.LENGTH_LONG).show()

        } else {


        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog


        val call = ApiClient.client.saveContactUs("Bearer $token", editReview!!.text.toString())
        call!!.enqueue(object : Callback<ContactUsResponsePojo?> {
            override fun onResponse(
                call: Call<ContactUsResponsePojo?>,
                response: Response<ContactUsResponsePojo?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        //Toast.makeText(activity, response!!.body()!!.message, Toast.LENGTH_LONG).show()


                        val toastDurationInMilliSeconds = 20000
                       val mToastToShow = Toast.makeText(activity, response!!.body()!!.message, Toast.LENGTH_LONG)
                        val toastCountDown: CountDownTimer
                        toastCountDown = object :
                            CountDownTimer(
                                toastDurationInMilliSeconds.toLong(),
                                1 /*Tick duration*/
                            ) {
                            override fun onTick(millisUntilFinished: Long) {
                                mToastToShow.show()
                            }

                            override fun onFinish() {
                                mToastToShow.cancel()
                            }
                        }

                        mToastToShow.show()
                        toastCountDown.start()

                    }
                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(activity, pojo.errors!!.get(0).message, Toast.LENGTH_LONG).show()


                    } catch (exception: IOException) {
                    }

                }
            }

            override fun onFailure(
                call: Call<ContactUsResponsePojo?>, t: Throwable
            ) {
                progressDialog.dismiss()
                Toast.makeText(activity, t.stackTrace.toString(), Toast.LENGTH_LONG).show()
                Log.d("response", t.stackTrace.toString())
            }
        })

    }


    }
}
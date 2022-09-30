package com.fairfareindia.ui.drawer.contactus

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class ContactUs : Fragment() {

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var contactUsArray = arrayOf<String?>("Feedback", "Enquiry", "Technical Issues")
    var subjectStr: String? = ""


    @JvmField
    @BindView(R.id.btnSubmitContactUs)
    var btnSubmitContactUs: Button? = null

    @JvmField
    @BindView(R.id.spnContactUsType)
    var spnContactUsType: Spinner? = null


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
        PreferencesManager.initializeInstance(requireActivity().applicationContext)
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
        val spinnerLang: Spinner = requireActivity().findViewById(R.id.spinnerLang)
        spinnerLang.visibility = View.GONE


        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_home)
        toolbar.title = getString(R.string.drawer_contactus)

        sharedpreferences = requireActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE)


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireActivity(), R.layout.simple_item_spinner, contactUsArray)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnContactUsType?.adapter = spinnerLuggage
        spnContactUsType?.setSelection(0)
        spnContactUsType!!.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                subjectStr = contactUsArray[position]
                Log.d("sdewdwwwd",subjectStr!!)
              }

            override fun onNothingSelected(p0: AdapterView<*>?) {
               }
        }

    }






    @OnClick(R.id.btnSubmitContactUs)
    fun btnSubmitContactUs() {

        if (ProjectUtilities.checkInternetAvailable(activity)) {

            if ((editReview!!.text.toString()).length < 1) {
                Toast.makeText(activity, getString(R.string.err_enter_message), Toast.LENGTH_LONG).show()

            } else {


                val progressDialog = ProgressDialog(activity)
                progressDialog.setCancelable(false) // set cancelable to false
                progressDialog.setMessage(getString(R.string.str_please_wait)) // set message
                progressDialog.show() // show progress dialog


                val call =
                    ApiClient.client.saveContactUs("Bearer $token", editReview!!.text.toString(),subjectStr,"Open")
                call!!.enqueue(object : Callback<ContactUsResponsePojo?> {
                    override fun onResponse(
                        call: Call<ContactUsResponsePojo?>,
                        response: Response<ContactUsResponsePojo?>
                    ) {
                        progressDialog.dismiss()
                        editReview!!.setText("")
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                //Toast.makeText(activity, response!!.body()!!.message, Toast.LENGTH_LONG).show()


                                val toastDurationInMilliSeconds = 4000
                                val mToastToShow = Toast.makeText(
                                    activity,
                                    response!!.body()!!.message,
                                    Toast.LENGTH_LONG
                                )
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
                                        sharedpreferences!!.edit().clear().commit()
                                        val intent = Intent(activity, HomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                    }
                                }
                                mToastToShow.setGravity(Gravity.CENTER, 0, 0);
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
                                Toast.makeText(
                                    activity,
                                    pojo.errors!!.get(0).message,
                                    Toast.LENGTH_LONG
                                ).show()


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

        }else{

            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )
        }


    }


}
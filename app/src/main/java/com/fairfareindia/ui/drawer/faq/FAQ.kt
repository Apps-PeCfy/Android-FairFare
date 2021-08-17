package com.fairfareindia.ui.drawer.faq

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.faq.pojo.FAQResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList

class FAQ : Fragment() {
    var token: String? = null
    var preferencesManager: PreferencesManager? = null
    private var getFaqList: List<FAQResponsePOJO.FaqsItem> = ArrayList()
    var faqAdapter: FAQAdapter? = null
    var sharedpreferences: SharedPreferences? = null



    @JvmField
    @BindView(R.id.recycler_faqs)
    var recycler_faqs: RecyclerView? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_faqs, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)


        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.getFaqs("Bearer $token")!!.enqueue(object :
            Callback<FAQResponsePOJO?> {
            override fun onResponse(
                call: Call<FAQResponsePOJO?>,
                response: Response<FAQResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    getFaqList = response.body()!!.faqs!!
                    if (getFaqList.isNotEmpty()) {
                        faqAdapter = FAQAdapter(activity, getFaqList)
                        recycler_faqs!!.layoutManager = LinearLayoutManager(activity)
                        recycler_faqs!!.adapter = faqAdapter

                        faqAdapter!!.notifyDataSetChanged()
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

                }else {
                    Toast.makeText(
                        activity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<FAQResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })


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
        toolbar.title = "FAQs"

        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }
}
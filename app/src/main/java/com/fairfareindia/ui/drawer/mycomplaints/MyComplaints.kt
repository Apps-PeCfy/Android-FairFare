package com.fairfareindia.ui.drawer.mycomplaints

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.mycomplaints.complaintDetails.ComplaintsDetailsActivity
import com.fairfareindia.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList

class MyComplaints : Fragment(),MyComplaintsAdapter.IMyComplaintClickListener {


    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var myComplaintsAdapter: MyComplaintsAdapter? = null
    var token: String? = null
    private var getComplaintListList: List<GetDisputResponsePOJO.DataItem> = ArrayList()


    @JvmField
    @BindView(R.id.recycler_view_myRides)
    var recycler_view_myRides: RecyclerView? = null

    @JvmField
    @BindView(R.id.ivImg)
    var ivImg: ImageView? = null

    @JvmField
    @BindView(R.id.tvEmptyTxt)
    var tvEmptyTxt: TextView? = null

    @JvmField
    @BindView(R.id.rlEmpty)
    var rlEmpty: RelativeLayout? = null

    @JvmField
    @BindView(R.id.rl_sort)
    var rl_sort: RelativeLayout? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_rides, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        rl_sort!!.visibility = View.GONE

        initView()
        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)


        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.getComplaint("Bearer $token", "Complaint")!!.enqueue(object :
            Callback<GetDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<GetDisputResponsePOJO?>,
                response: Response<GetDisputResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    getComplaintListList = response.body()!!.data!!
                    if (getComplaintListList.size > 0) {
                        myComplaintsAdapter = MyComplaintsAdapter(activity, getComplaintListList)
                        recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
                        recycler_view_myRides!!.adapter = myComplaintsAdapter
                        myComplaintsAdapter!!.setClickListener(this@MyComplaints)

                        myComplaintsAdapter!!.notifyDataSetChanged()
                    }else{
                        rlEmpty!!.visibility=View.VISIBLE
                        ivImg!!.setBackgroundResource(R.drawable.empty_complaint)
                        tvEmptyTxt!!.text="You have not filed any Complaints yet."

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
                call: Call<GetDisputResponsePOJO?>,
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
        toolbar.title = "My Complaints"

        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun detailDisputClick(id: Int) {

        if (ProjectUtilities.checkInternetAvailable(activity)) {

            val intent = Intent(activity, ComplaintsDetailsActivity::class.java)
            intent.putExtra("Id", id.toString())
            startActivity(intent)
        }else{

            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )
        }

    }

}
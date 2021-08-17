package com.fairfareindia.ui.drawer.mylocation

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
import com.fairfareindia.ui.drawer.myaccount.pojo.UpdateProfileResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.home.pojo.DeleteSaveDataResponsePOJO
import com.fairfareindia.ui.home.pojo.GetSaveLocationResponsePOJO
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class MyLocation : Fragment(), MyLocationAdapter.IClickListener {


    var myLocationAdapter: MyLocationAdapter? = null
    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    private var savedLocationList: List<GetSaveLocationResponsePOJO.DataItem> = ArrayList()
    var token: String? = null


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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_rides, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()


        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance

        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)


        getLocation()










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


    private fun getLocation() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.getSavedLocation("Bearer $token")!!.enqueue(object :
            Callback<GetSaveLocationResponsePOJO?> {
            override fun onResponse(
                call: Call<GetSaveLocationResponsePOJO?>,
                response: Response<GetSaveLocationResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    savedLocationList = response.body()!!.data!!
                    if (savedLocationList.size > 0) {
                        myLocationAdapter = MyLocationAdapter(activity, savedLocationList)
                        recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
                        recycler_view_myRides!!.adapter = myLocationAdapter
                        myLocationAdapter!!.setClickListener(this@MyLocation)
                        myLocationAdapter!!.notifyDataSetChanged()
                    } else {
                        rlEmpty!!.visibility = View.VISIBLE
                        ivImg!!.setBackgroundResource(R.drawable.empty_location)
                        tvEmptyTxt!!.text = "You have not Saved any Locations yet!"

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

                } else {
                    Toast.makeText(
                        activity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<GetSaveLocationResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })


    }


    private fun initView() {

        val spinnerLang: Spinner = activity!!.findViewById(R.id.spinnerLang)
        spinnerLang.visibility = View.GONE

        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "My Locations"

        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun favClick(deletedid: Int) {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog


        val tokenLogin =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        ApiClient.client.deleteRecentLocation("Bearer $tokenLogin", deletedid.toString())!!
            .enqueue(object : Callback<DeleteSaveDataResponsePOJO?> {
                override fun onResponse(
                    call: Call<DeleteSaveDataResponsePOJO?>,
                    response: Response<DeleteSaveDataResponsePOJO?>
                ) {
                    progressDialog.dismiss()
                    if (response.code() == 200) {

                           ApiClient.client.getSavedLocation("Bearer $token")!!.enqueue(object :
                               Callback<GetSaveLocationResponsePOJO?> {
                               override fun onResponse(
                                   call: Call<GetSaveLocationResponsePOJO?>,
                                   response: Response<GetSaveLocationResponsePOJO?>
                               ) {
                                   if (response.code() == 200) {

                                       savedLocationList = response.body()!!.data!!
                                       if (savedLocationList.size > 0) {
                                           myLocationAdapter =
                                               MyLocationAdapter(activity, savedLocationList)
                                           recycler_view_myRides!!.layoutManager =
                                               LinearLayoutManager(activity)
                                           recycler_view_myRides!!.adapter = myLocationAdapter
                                           myLocationAdapter!!.setClickListener(this@MyLocation)
                                           myLocationAdapter!!.notifyDataSetChanged()
                                       }else{

                                           myLocationAdapter =
                                               MyLocationAdapter(activity, savedLocationList)
                                           recycler_view_myRides!!.layoutManager =
                                               LinearLayoutManager(activity)
                                           recycler_view_myRides!!.adapter = myLocationAdapter
                                           myLocationAdapter!!.notifyDataSetChanged()
                                           rlEmpty!!.visibility=View.VISIBLE
                                           ivImg!!.setBackgroundResource(R.drawable.empty_location)
                                           tvEmptyTxt!!.text="You have not Saved any Locations yet!"
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
                                   call: Call<GetSaveLocationResponsePOJO?>,
                                   t: Throwable
                               ) {
                                   Log.d("response", t.stackTrace.toString())
                               }
                           })
                    } else {
                        Toast.makeText(
                            activity,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<DeleteSaveDataResponsePOJO?>,
                    t: Throwable
                ) {
                    progressDialog.dismiss()
                    Log.d("response", t.stackTrace.toString())
                }
            })

    }

    override fun editClick(id: Int, str: String?) {


        ApiClient.client.updateLocation("Bearer $token", str, id.toString())!!.enqueue(object :
            Callback<UpdateProfileResponsePOJO?> {
            override fun onResponse(
                call: Call<UpdateProfileResponsePOJO?>,
                response: Response<UpdateProfileResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    getLocation()

                   // Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_LONG).show()

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
                call: Call<UpdateProfileResponsePOJO?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
            }
        })


    }

}
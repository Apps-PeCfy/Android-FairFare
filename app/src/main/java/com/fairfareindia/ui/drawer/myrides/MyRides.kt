package com.fairfareindia.ui.drawer.myrides

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.fairfareindia.R
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.drawer.myrides.ridedetails.MyRideDetailsActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.ridedetails.RideDetailsActivity
import com.fairfareindia.ui.ridereview.RideReviewActivity
import com.fairfareindia.ui.service.GPSTracker
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import java.io.IOException
import java.util.*
import java.util.stream.Collectors

class MyRides : Fragment(), IMyRidesView, MyTripsAdapter.IClickListener {

    private var iMyRidesPresenter: IMyRidesPresenter? = null

    var myTripsAdapter: MyTripsAdapter? = null
    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var currentLat: String? = null
    var currentLong: String? = null
    var CurrentpageCount: Int = 1
    var totalPageCount: Int = 1

    var statusFilter = arrayOf<String?>()

    var mylist = ArrayList<String?>()


    var loading = true

    var mLayoutManager: LinearLayoutManager? = null

    var getCurrentCity: String? = null
    private var myRideList: List<GetRideResponsePOJO.DataItem> = ArrayList()
    private var myRideListFilter: List<GetRideResponsePOJO.DataItem> = ArrayList()


    @JvmField
    @BindView(R.id.recycler_view_myRides)
    var recycler_view_myRides: RecyclerView? = null


    @JvmField
    @BindView(R.id.ivImg)
    var ivImg: ImageView? = null

    @JvmField
    @BindView(R.id.rl_sort)
    var rl_sort: RelativeLayout? = null

    @JvmField
    @BindView(R.id.tvEmptyTxt)
    var tvEmptyTxt: TextView? = null

    @JvmField
    @BindView(R.id.tv_filter)
    var tv_filter: TextView? = null

    @JvmField
    @BindView(R.id.rlEmpty)
    var rlEmpty: RelativeLayout? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_rides, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)


        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        currentLat = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CLat)
        currentLong = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CLong)
        initView()


        iMyRidesPresenter = MyRidesImplementer(this)
        iMyRidesPresenter!!.getRide("Bearer " + token, CurrentpageCount, currentLat, currentLong)



        mLayoutManager = LinearLayoutManager(activity)
        recycler_view_myRides!!.setLayoutManager(mLayoutManager)


        recycler_view_myRides!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                loading = true


                if (dy > 0) { //check for scroll down
                    if (loading) {
                        CurrentpageCount = CurrentpageCount + 1
                        loading = false

                        Log.d("onScrolled", dx.toString())

                    }


                }
            }
        })


        return rootView
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {

        val spinnerLang: Spinner = activity!!.findViewById(R.id.spinnerLang)
        spinnerLang.visibility = View.GONE


        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "My Rides"


        val gps = GPSTracker(activity!!)
        if (gps.canGetLocation()) {

            val geocoder = Geocoder(activity!!, Locale.getDefault())
            try {
                val addresses =
                    geocoder.getFromLocation(
                        gps!!.latitude!!.toDouble(),
                        gps!!.longitude!!.toDouble(),
                        1
                    )
                if (addresses != null && addresses.size>0) {
                    val returnedAddress = addresses[0]
                    val strReturnedAddress =
                        StringBuilder()
                    for (j in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j))
                    }
                    getCurrentCity = returnedAddress.subAdminArea

                }else{
                    getCurrentCity = ""
                }
            } catch (e: IOException) {
            }

        } else {
            gps.showSettingsAlert()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_home_lang, menu!!)
        super.onCreateOptionsMenu(menu!!, inflater)
    }

    @OnClick(R.id.tv_filter)
    fun filter() {
        showAlertDialog()
    }


    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(activity!!)
        alertDialog.setItems(statusFilter, object : DialogInterface.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    which ->
                        adapterchang(statusFilter[which])

                }
            }
        })

        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        alert.show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun adapterchang(sdata: String?) {


        if (sdata.equals("Clear Filter")) {

            val first = "Filter By Status: "
            tv_filter!!.setText(first)




            myTripsAdapter = MyTripsAdapter(activity, myRideList, getCurrentCity)
            recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
            recycler_view_myRides!!.adapter = myTripsAdapter
            myTripsAdapter!!.setClickListener(this@MyRides)
            myTripsAdapter!!.notifyDataSetChanged()
        } else {

            val first = "Filter By Status: "
            val next = "<font color='#F15E38'>" + sdata + "</font> "
            tv_filter!!.setText(Html.fromHtml(first + next.toUpperCase()))



            for (a in myRideList) {
                if (a.status.equals(sdata)) {
                    Log.d("wcxvzqss", a.dateTime!!)
                    myRideListFilter = myRideList.stream()
                        .filter({ article -> article.status!!.contains(sdata!!) })
                        .collect(Collectors.toList())
                }
            }


            myTripsAdapter = MyTripsAdapter(activity, myRideListFilter, getCurrentCity)
            recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
            recycler_view_myRides!!.adapter = myTripsAdapter
            myTripsAdapter!!.setClickListener(this@MyRides)
            myTripsAdapter!!.notifyDataSetChanged()
        }


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


    override fun onResume() {
        super.onResume()
    }

    override fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?) {
    }

    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            activity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getRidesSuccess(getRideResponsePOJO: GetRideResponsePOJO?) {


        myRideList = getRideResponsePOJO!!.data!!
        CurrentpageCount = getRideResponsePOJO.currentPage
        totalPageCount = getRideResponsePOJO.lastPage



        if (myRideList.size > 0) {
            rl_sort!!.visibility = View.VISIBLE


            for (i in myRideList!!.indices) {
                if (mylist.contains(myRideList.get(i).status)) {

                } else {
                    mylist.add(myRideList.get(i).status)
                }
            }
            mylist.add("Clear Filter")

            statusFilter = mylist.toTypedArray()

            myTripsAdapter = MyTripsAdapter(activity, myRideList, getCurrentCity)
            recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
            recycler_view_myRides!!.adapter = myTripsAdapter
            myTripsAdapter!!.setClickListener(this@MyRides)
            myTripsAdapter!!.notifyDataSetChanged()
        } else {
            rl_sort!!.visibility = View.GONE
            rlEmpty!!.visibility = View.VISIBLE
            ivImg!!.setBackgroundResource(R.drawable.empty_ride)
            tvEmptyTxt!!.text = "You have not taken any Rides yet."

        }


    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(activity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(activity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun startRide(
        id: Int,
        airport: String?,
        vahicalRadeCardID: Int,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        originalAddress: String?,
        destinationAddress: String?,
        str: String?,
        compareRideList: ArrayList<GetRideResponsePOJO.TollsItem>?
    ) {

        val intent = Intent(activity, RideDetailsActivity::class.java)
        intent.putExtra("MyRides_vehicle_rate_card_id", vahicalRadeCardID.toString())
        intent.putExtra("MyRides_airport_ratr_card_id", airport)
        intent.putExtra("MyRides_RideID", id.toString())
        intent.putExtra("MyRidessLat", sLat)
        intent.putExtra("MyRidessLong", sLong)
        intent.putExtra("MyRidesdLat", dLat)
        intent.putExtra("MyRidesdLong", dLong)
        intent.putExtra("MyRidesoriginalAddress", originalAddress)
        intent.putExtra("MyRidesdestinationAddress", destinationAddress)
        intent.putExtra("MyRide", str)
        intent.putExtra("compareRideList", compareRideList)

        startActivity(intent)


    }

    override fun rateRide(id: Int) {

        if (ProjectUtilities.checkInternetAvailable(activity)) {
            val intent = Intent(activity, RideReviewActivity::class.java)
            intent.putExtra("MyRides_id", id.toString())
            intent.putExtra("MyRides", "DrawerMyRides")
            startActivity(intent)
        }else{
            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )
        }

    }

    override fun rideDetails(id: Int) {
        if (ProjectUtilities.checkInternetAvailable(activity)) {
            val intent = Intent(activity, MyRideDetailsActivity::class.java)
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
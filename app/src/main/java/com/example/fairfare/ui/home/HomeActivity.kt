

package com.example.fairfare.ui.home

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.icu.util.Calendar
import android.location.*
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fairfare.R
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.LoginActivity
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.compareride.CompareRideActivity
import com.example.fairfare.ui.compareride.CompareRideImplementer
import com.example.fairfare.ui.compareride.ICompareRidePresenter
import com.example.fairfare.ui.compareride.ICompareRideView
import com.example.fairfare.ui.compareride.pojo.CompareRideResponsePOJO
import com.example.fairfare.ui.drawer.adapter.DrawerAdapter
import com.example.fairfare.ui.drawer.contactus.ContactUs
import com.example.fairfare.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.example.fairfare.ui.drawer.covid19.Covid
import com.example.fairfare.ui.drawer.faq.FAQ
import com.example.fairfare.ui.drawer.myaccount.MyAccountFragment
import com.example.fairfare.ui.drawer.mycomplaints.MyComplaints
import com.example.fairfare.ui.drawer.mydisput.MyDisput
import com.example.fairfare.ui.drawer.mylocation.MyLocation
import com.example.fairfare.ui.drawer.myrides.MyRides
import com.example.fairfare.ui.drawer.pojo.DrawerPojo
import com.example.fairfare.ui.drawer.privacypolicy.ContentPage
import com.example.fairfare.ui.drawer.ratecard.RateCard
import com.example.fairfare.ui.drawer.setting.Setting
import com.example.fairfare.ui.home.pojo.GetAllowCityResponse
import com.example.fairfare.ui.placeDirection.DirectionsJSONParser
import com.example.fairfare.utils.*
import com.example.fairfare.utils.ProjectUtilities.showProgressDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity(), OnMapReadyCallback, OnDateSetListener,
    OnTimeSetListener, OnItemSelectedListener, AdapterView.OnItemClickListener, ICompareRideView,
    LocationListener {
    protected var locationManager: LocationManager? = null

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    private var mPolyline: Polyline? = null
    var sourecemarker: Marker? = null
    var destmarker: Marker? = null
    var spinnerLuggagetxt: String? = null
    var city: String? = null
    var setDate: String? = null
    var placesClient: PlacesClient? = null

    var appSignatureHelper: AppSignatureHelper? = null

    var timeSpinner = arrayOf<String?>("Now", "Later")

    // var cityspinner = Array<String?>
    var cityspinner = ArrayList<String>()
    var luggageSpinner = arrayOf<String?>(
        "Luggage", "1 Luggage", "2 Luggage", "3 Luggage", "4 Luggage", "5 Luggage"
    )

    @JvmField
    @BindView(R.id.drawer_layout)
    var mDrawerLayout: DrawerLayout? = null

    @JvmField
    @BindView(R.id.btnCompareRide)
    var btnCompareRide: Button? = null

    @JvmField
    @BindView(R.id.add)
    var add: Button? = null

    @JvmField
    @BindView(R.id.rlRideScheduled)
    var rlRideScheduled: RelativeLayout? = null

    @JvmField
    @BindView(R.id.reestimateDateandTime)
    var reestimateDateandTime: RelativeLayout? = null

    @JvmField
    @BindView(R.id.spinner_time)
    var spinner_time: Spinner? = null

    @JvmField
    @BindView(R.id.spinnerLang)
    var spinnerLang: Spinner? = null

    @JvmField
    @BindView(R.id.spinner_Luggage)
    var spinner_Luggage: Spinner? = null

    @JvmField
    @BindView(R.id.toolbar_home)
    var toolbar: Toolbar? = null


    @JvmField
    @BindView(R.id.tv_myDropUpLocation)
    var myDropUpLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_RideScheduled)
    var tv_RideScheduled: TextView? = null


    @JvmField
    @BindView(R.id.llhideview)
    var llhideview: LinearLayout? = null

    @JvmField
    @BindView(R.id.homeMain)
    var homeMain: RelativeLayout? = null


    @JvmField
    @BindView(R.id.tvhideShow)
    var tvhideShow: RelativeLayout? = null

    @JvmField
    @BindView(R.id.lvDrawer)
    var lvDrawer: ListView? = null

    @JvmField
    @BindView(R.id.tv_myCurrentLocation)
    var myCurrentLocation: TextView? = null

    @JvmField
    @BindView(R.id.tvEstTime)
    var tvEstTime: TextView? = null

    @JvmField
    @BindView(R.id.logoLayout)
    var logoLayout: RoundedImageView? = null

    @JvmField
    @BindView(R.id.tvUserName)
    var tvUserName: TextView? = null

    @JvmField
    @BindView(R.id.tvEmailAddress)
    var tvEmailAddress: TextView? = null

    @JvmField
    @BindView(R.id.tvUserLocation)
    var tvUserLocation: TextView? = null

    @JvmField
    @BindView(R.id.tvProfession)
    var tvProfession: TextView? = null

    @JvmField
    @BindView(R.id.btnLogout)
    var btnLogout: TextView? = null

    @JvmField
    @BindView(R.id.mainRelativeLayout)
    var mainRelativeLayout: RelativeLayout? = null


    @JvmField
    @BindView(R.id.tvEstDistance)
    var tvEstDistance: TextView? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var mMap: GoogleMap? = null
    var extras: Bundle? = null
    var streetAddress: String? = null
    var sharedpreferences: SharedPreferences? = null
    var SourceLat: String? = null
    var SourceLong: String? = null
    var DestinationLat: String? = null
    var DestinationLong: String? = null
    var spinnertxt: String? = null
    var city_Name: String? = null
    var formaredDate: String? = null
    var formaredDateLater: String? = null
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0
    var second = 0
    var AMorPM = 0
    var myday = 0
    var myMonth = 0
    var myYear = 0
    var myHour = 0
    var myMinute = 0
    var calendar: Calendar? = null
    var estTime: String? = null
    var estDistance: String? = null
    var estDistanceInMeter = 0
    var hideshow: String? = null
    var spnrbag = 0
    var cityID = ""
    var spnrtime = 0

    private var drawerPojoArrayList: ArrayList<DrawerPojo>? = null
    private var drawerAdapter: DrawerAdapter? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    var preferencesManager: PreferencesManager? = null

    private var cityPojoList: List<GetAllowCityResponse.CitiesItem> = ArrayList()


    private var iCompareRidePresenter: ICompareRidePresenter? = null
    var token: String? = null
    var deviceID: String? = null
    var PortAir: String? = null
    var airportYesOrNO: String? = null
    var replacedistance: String? = null
    var replacebags: String? = null
    var sourcePlaceID: String? = null
    var DestinationPlaceID: String? = null
    var CurrentPlaceID: String? = null
    var tvDateandTime: String? = null
    var dateToStr: String? = null


    var stDay: String? = null
    var strMonth: String? = null
    var strsecond: String? = null
    var strhr: String? = null
    var strMinute: String? = null
    var callOnLocation: String? = null

    var loc: Location? = null

    var progressDialogstart: ProgressDialog? = null


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        progressDialogstart = ProgressDialog(this@HomeActivity)
        progressDialogstart!!.setCancelable(false) // set cancelable to false
        progressDialogstart!!.setMessage("Please Wait") // set message
        progressDialogstart!!.show() // show progress dialog


        spinnerLang!!.visibility = View.VISIBLE
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)


       /* appSignatureHelper = AppSignatureHelper(this)
        appSignatureHelper!!.getAppSignatures()*/



        callOnLocation = "first"
        val action = intent.action


        Places.initialize(this, resources.getString(R.string.google_maps_key))
        placesClient = Places.createClient(this@HomeActivity)
        PreferencesManager.initializeInstance(this@HomeActivity)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)



        iCompareRidePresenter = CompareRideImplementer(this)






        initView()
        // Initialize drawer list
        setListData()

        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        val intent = intent
        extras = intent.extras
        if (extras != null) {
            spnrbag = extras!!.getInt("spnbg")
            spnrtime = extras!!.getInt("spnTime")
            tvDateandTime = extras!!.getString("TvDateTime")
            city = extras!!.getString("getcity")
            formaredDateLater = extras!!.getString("formaredDateLater")
        } else {
            spnrbag = 0
            spnrtime = 0
        }

        if (spnrtime == 0) {
            rlRideScheduled!!.visibility = View.GONE
        } else {
            rlRideScheduled!!.visibility = View.VISIBLE
        }

/*
        val gps = GPSTracker(this@HomeActivity)
        if (gps.canGetLocation()) {
             currentLatitude = gps.latitude
             currentLongitude = gps.longitude
        } else {
            gps.showSettingsAlert()
        }*/


        hideshow = "show"
        getcurrentDate()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        val NowLater: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, timeSpinner)
        NowLater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_time!!.adapter = NowLater
        spinner_time!!.setSelection(spnrtime)
        spinner_time!!.onItemSelectedListener = this


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, luggageSpinner)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_Luggage?.adapter = spinnerLuggage
        spinner_Luggage?.setSelection(spnrbag)
        spinner_Luggage!!.setOnItemSelectedListener(this)


        if (action == "schduleRideSuccess") {
            spinnerLang!!.visibility = View.GONE
            setFragment(MyRides())
        } else if (action == "RegisterDisput") {
            spinnerLang!!.visibility = View.GONE
            setFragment(MyDisput())
        } else if (action == "filecomplaintSuccess") {
            spinnerLang!!.visibility = View.GONE
            setFragment(MyComplaints())
        }



        SourceLat = sharedpreferences!!.getString("SourceLat", "")
        SourceLong = sharedpreferences!!.getString("SourceLong", "")
        DestinationLat = sharedpreferences!!.getString("DestinationLat", "")
        DestinationLong = sharedpreferences!!.getString("DestinationLong", "")


        if(SourceLat!!.isNotEmpty()&&DestinationLat!!.isNotEmpty()){
            progressDialogstart!!.dismiss()
        }


    }


    private fun getCity() {

        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Accept"] = "application/json"
        headers["Authorization"] = "Bearer $token"



        ApiClient.client.getAllowCities(headers)!!.enqueue(object :
            Callback<GetAllowCityResponse?> {
            override fun onResponse(
                call: Call<GetAllowCityResponse?>,
                response: Response<GetAllowCityResponse?>
            ) {
                if (response.code() == 200) {
                    cityPojoList = response!!.body()!!.cities

                    preferencesManager?.setCityList(cityPojoList)

                    setCitySpinner()




                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(this@HomeActivity, pojo.message, Toast.LENGTH_LONG).show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<GetAllowCityResponse?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
            }
        })


    }

    private fun setCitySpinner() {
        for (cityModel : GetAllowCityResponse.CitiesItem in cityPojoList) {
            cityspinner.add(cityModel.name)

        }


        val SpnLang: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this@HomeActivity,
            R.layout.simple_city_txt_spinner,
            cityspinner as List<Any?>
        )
        SpnLang.setDropDownViewResource(R.layout.simple_city_spinner)
        spinnerLang!!.adapter = SpnLang

        if (extras == null) {

            if (cityspinner.contains(city)) {

                for (i in cityspinner!!.indices) {
                    if (city.equals(cityspinner[i])) {
                        spinnerLang!!.setSelection(i)
                    }

                }
            }else{
                cityspinner.add(0, "Choose City")
                Toast.makeText(
                    this@HomeActivity,
                    "Sorry, we dont serve location in " + city + " city yet.We will notify you as soon as we launch. Kindly choose Active city from the drop down",
                    Toast.LENGTH_LONG
                ).show()

            }

        } else {

            if (cityspinner.contains(city)) {
                for (i in cityspinner!!.indices) {
                    if (city.equals(cityspinner[i])) {
                        spinnerLang!!.setSelection(i)
                    }

                }
            }else {

                cityspinner.add(0, "Choose City")

                Toast.makeText(
                    this@HomeActivity,
                    "Sorry, we dont serve location in " + city + " city yet.We will notify you as soon as we launch. Kindly choose Active city from the drop down",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
        spinnerLang!!.setOnItemSelectedListener(this@HomeActivity)
    }


    private fun setFragment(myRides: Fragment) {


        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.add(R.id.container_framelayout, myRides)
        fragmentTransaction!!.commit()
    }


    private fun setListData() {
        drawerPojoArrayList = ArrayList<DrawerPojo>()
        drawerPojoArrayList!!.add(
            DrawerPojo(
                1,
                getString(R.string.drawer_covid),
                R.drawable.ic_nav_covid
            )
        )



       /* drawerPojoArrayList!!.add(
            DrawerPojo(
                2,
                getString(R.string.drawer_mylocation),
                R.drawable.ic_nav_mylocation
            )
        )
*/
        drawerPojoArrayList!!.add(
            DrawerPojo(
                2,
                getString(R.string.drawer_myrides),
                R.drawable.ic_nav_mytrips
            )
        )

        drawerPojoArrayList!!.add(
            DrawerPojo(
                3,
                getString(R.string.drawer_mydisput),
                R.drawable.ic_nav_mydisput
            )
        )

        drawerPojoArrayList!!.add(
            DrawerPojo(
                4,
                getString(R.string.drawer_mycomplents),
                R.drawable.ic_nav_mycomplaint
            )
        )

        drawerPojoArrayList!!.add(
            DrawerPojo(
                5,
                getString(R.string.drawer_ratecard),
                R.drawable.ic_nav_ratecard
            )
        )

        drawerPojoArrayList!!.add(
            DrawerPojo(
                6,
                getString(R.string.drawer_faq),
                R.drawable.ic_nav_helpandsupport
            )
        )


        drawerPojoArrayList!!.add(
            DrawerPojo(
                7,
                getString(R.string.drawer_contactus),
                R.drawable.ic_nav_helpandsupport
            )
        )


        drawerPojoArrayList!!.add(
            DrawerPojo(
                8,
                getString(R.string.drawer_privacypolicy),
                R.drawable.ic_nav_privacypolicy
            )
        )
        drawerPojoArrayList!!.add(
            DrawerPojo(
                9,
                getString(R.string.drawer_setting),
                R.drawable.ic_nav_setting
            )
        )


        drawerAdapter = DrawerAdapter(this, drawerPojoArrayList)
        lvDrawer!!.adapter = drawerAdapter

    }

    @SuppressLint("WrongConstant")
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(Covid())

            }

            1 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(MyRides())
            }


            2 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(MyDisput())
            }


            3 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(MyComplaints())
            }


            4 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(RateCard())
            }


            5 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(FAQ())
            }


            6 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(ContactUs())
            }


            7 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(ContentPage())
            }


            8 -> {
                spinnerLang!!.visibility = View.GONE
                homeMain!!.visibility = View.GONE
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                mDrawerLayout!!.closeDrawer(Gravity.START)

                replaceFragment(Setting())
            }


            else -> {
            }
        }
    }


    fun replaceFragment(fragment: Fragment?) {

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.container_framelayout, fragment!!)
        fragmentTransaction!!.commit()
    }


    private fun getcurrentDate() {
        val today = Date()
        val format = SimpleDateFormat("dd MMM, hh:mm a")
        val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")



        dateToStr = format.format(today)!!.replace("am", "AM").replace("pm", "PM")


        setDate = dateToStr

        formaredDate = formatviewRide.format(today).toString()


        if (extras != null) {
            if (spnrtime == 1) {
                tv_RideScheduled?.text = tvDateandTime
            } else {
                tv_RideScheduled?.text = dateToStr

            }

        } else {
            tv_RideScheduled?.text = dateToStr

        }


    }

    private fun initView() {
        toolbar!!.title = "Get Fair Fare"
        toolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        lvDrawer!!.onItemClickListener = this
        drawerToggle = object : ActionBarDrawerToggle(
            this, mDrawerLayout, toolbar,
            R.string.drawer_open, R.string.drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }
        mDrawerLayout?.addDrawerListener(drawerToggle as ActionBarDrawerToggle)!!
        drawerToggle?.setDrawerIndicatorEnabled(true)
        drawerToggle?.setDrawerIndicatorEnabled(false)
        drawerToggle?.setHomeAsUpIndicator(R.drawable.ic_action_menu)
        drawerToggle?.syncState()
        drawerToggle?.setToolbarNavigationClickListener(View.OnClickListener {
            if (mDrawerLayout!!.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout!!.closeDrawer(Gravity.LEFT)
            } else {
                tvUserName!!.text =
                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_NAME)
                tvEmailAddress!!.text =
                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_EMAIL)
                tvUserLocation!!.text =
                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_LOCATION)

                tvProfession!!.text =
                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFESTION)



                Glide.with(this@HomeActivity)
                    .load(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK))
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(logoLayout!!)

                mDrawerLayout!!.openDrawer(Gravity.LEFT)
            }
        })
    }

    @OnClick(R.id.logoLayout)
    fun myAccount(){

        spinnerLang!!.visibility = View.GONE
        homeMain!!.visibility = View.GONE
        mDrawerLayout!!.closeDrawer(Gravity.LEFT)
        mDrawerLayout!!.closeDrawer(Gravity.START)

        replaceFragment(MyAccountFragment())



    }

    @OnClick(R.id.btnLogout)
    fun logOut() {
        deviceID = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_DEVICEID)
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)


        val progressDialogLogout = ProgressDialog(this@HomeActivity)
        progressDialogLogout.setCancelable(false) // set cancelable to false
        progressDialogLogout.setMessage("Please Wait") // set message
        progressDialogLogout.show() // show progress dialog

        ApiClient.client.signOut("Bearer $token", deviceID, "Android")!!
            .enqueue(object : Callback<ContactUsResponsePojo?> {
                override fun onResponse(
                    call: Call<ContactUsResponsePojo?>,
                    response: Response<ContactUsResponsePojo?>
                ) {
                    progressDialogLogout.dismiss()
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            preferencesManager!!.clear()
                            Toast.makeText(
                                this@HomeActivity,
                                response!!.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                    } else if (response.code() == 422) {
                        val gson = GsonBuilder().create()
                        var pojo: ValidationResponse? = ValidationResponse()
                        try {
                            pojo = gson.fromJson(
                                response.errorBody()!!.string(),
                                ValidationResponse::class.java
                            )
                            Toast.makeText(this@HomeActivity, pojo.message, Toast.LENGTH_LONG)
                                .show()


                        } catch (exception: IOException) {
                        }

                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Internal Server Error",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(
                    call: Call<ContactUsResponsePojo?>, t: Throwable
                ) {
                    progressDialogLogout.dismiss()
                    Toast.makeText(this@HomeActivity, t.stackTrace.toString(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("response", t.stackTrace.toString())
                }
            })

    }


    @OnClick(R.id.tv_RideScheduled)
    fun RideScheduled() {

        calendar = Calendar.getInstance()
        year = calendar!!.get(Calendar.YEAR)
        month = calendar!!.get(Calendar.MONTH)
        day = calendar!!.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this@HomeActivity,
            this@HomeActivity, year, month, day
        )
        datePickerDialog.datePicker.minDate = Date().time
        datePickerDialog.show()
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {


        myYear = year
        myday = dayOfMonth
        myMonth = month + 1
        val c = Calendar.getInstance()
        if (spinner_time?.selectedItem.toString().equals("Later", ignoreCase = true)) {
            c.add(Calendar.MINUTE,16)
        }
        hour = c.get(Calendar.HOUR_OF_DAY)
        minute = c.get(Calendar.MINUTE)
        second = c.get(Calendar.SECOND)
        AMorPM = c.get(Calendar.AM_PM)

        val timePickerDialog = TimePickerDialog(
            this@HomeActivity,
            this@HomeActivity, hour, minute, DateFormat.is24HourFormat(this)
        )

        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, Tminute: Int) {

        myHour = hourOfDay
        myMinute = Tminute

        var dateTime = myday.toString() + "-" + myMonth + "-" + myYear + " " + myHour + ":" + myMinute
        val selectedDateTime= SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateTime)
        val minValidDateTime = SimpleDateFormat("dd MM yyyy HH:mm:ss").parse(minTimeToShedule())

        if (selectedDateTime.before(minValidDateTime)){

            tv_RideScheduled?.text = AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM").replace("pm", "PM")

            Toast.makeText(this, "Scheduled time should be greater than 15 minutes from current time.", Toast.LENGTH_LONG).show()

            return

        }else{
            val AMorPM: String

        if (myHour >= 12) {
            AMorPM = "PM"
            if (myHour == 12) {
            } else {
                myHour = myHour - 12
            }
        } else {
            if (myHour == 0) {
                myHour = myHour + 12
            }
            AMorPM = "AM"
        }
        var dmonth: String? = null
        val monthParse = SimpleDateFormat("MM")
        val monthDisplay = SimpleDateFormat("MMM")
        try {
            dmonth = monthDisplay.format(monthParse.parse(myMonth.toString()))
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val displaDate = SimpleDateFormat("HH")
        val olddisplaDate = SimpleDateFormat("hh")


        var formatedhr = displaDate.format(olddisplaDate.parse((myHour.toString())))
        var formatedminute = displaDate.format(olddisplaDate.parse((myMinute.toString())))






            if ((myMonth.toString().length == 1)) {
                strMonth = ("0" + myMonth)
            } else {
                strMonth = myMonth.toString()
            }

            if ((myday.toString().length == 1)) {
                stDay = ("0" + myday)
            } else {
                stDay = myday.toString()
            }


            if ((second.toString().length == 1)) {
                strsecond = ("0" + second)
            } else {
                strsecond = second.toString()
            }

            if ((myHour.toString().length == 1)) {
                strhr = ("0" + myHour)
            } else {
                strhr = myHour.toString()
            }

            if ((myMinute.toString().length == 1)) {
                strMinute = ("0" + myMinute)
            } else {
                strMinute = myMinute.toString()
            }


            var dayhour: String? = null

            if (hourOfDay.toString().length == 1) {
                dayhour = ("0" + hourOfDay)
            } else {
                dayhour = hourOfDay.toString()
            }


            formaredDateLater =
                (myYear.toString() + "-" + strMonth + "-" + stDay + " " + dayhour + ":" + strMinute + ":" + strsecond)
            tv_RideScheduled!!.text = "$myday $dmonth, $strhr:$strMinute $AMorPM"


        }
    }

    private fun minTimeToShedule(): String {
        val ONE_MINUTE_IN_MILLIS: Long = 60000 //millisecs
        val date = Calendar.getInstance()
        val t = date.timeInMillis
        val afterAddingTenMins = Date(t + 15 * ONE_MINUTE_IN_MILLIS)

        val minDateTime = SimpleDateFormat("dd MM yyyy HH:mm:ss").format(afterAddingTenMins)
        return minDateTime;

    }


    @OnClick(R.id.tvhideShow)
    fun hideshow() {
        if (hideshow.equals("show")) {
            hideshow = "hide"
            llhideview?.visibility = View.GONE
        } else {
            hideshow = "show"
            llhideview?.visibility = View.VISIBLE
        }
    }


    @OnClick(R.id.tv_myCurrentLocation)
    fun myCurrentLocations() {
        val intent = Intent(applicationContext, PickUpDropActivity::class.java)
        if (SourceLat!!.isEmpty()) {
            intent.putExtra("Toolbar_Title", "Pick-Up")
            intent.putExtra("currentLatitude", currentLatitude)
            intent.putExtra("currentLongitude", currentLongitude)
            intent.putExtra("spinnerbag", spnrbag)
            intent.putExtra("spinnerTime", spnrtime)
            intent.putExtra("City", city_Name)
            intent.putExtra("formaredDateLater", formaredDateLater)
            intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


        } else {
            intent.putExtra("Toolbar_Title", "Pick-Up")
            intent.putExtra("currentLatitude", SourceLat!!.toDouble())
            intent.putExtra("currentLongitude", SourceLong!!.toDouble())
            intent.putExtra("spinnerbag", spnrbag)
            intent.putExtra("spinnerTime", spnrtime)
            intent.putExtra("City", city_Name)
            intent.putExtra("formaredDateLater", formaredDateLater)
            intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


        }
        startActivity(intent)
    }

    override fun onDestroy() {
        // sharedpreferences!!.edit().clear().commit()

        //ILOMADEV
        try {
            EventBus.getDefault().unregister(this)
        }catch (ex : Exception){

        }

        super.onDestroy()
    }

    @OnClick(R.id.tv_myDropUpLocation)
    fun myDropUpLocation() {
        val intent = Intent(applicationContext, PickUpDropActivity::class.java)
        if (DestinationLat!!.isEmpty()) {
            intent.putExtra("Toolbar_Title", "Drop-off")
            intent.putExtra("currentLatitude", currentLatitude)
            intent.putExtra("currentLongitude", currentLongitude)
            intent.putExtra("spinnerbag", spnrbag)
            intent.putExtra("spinnerTime", spnrtime)
            intent.putExtra("City", city_Name)
            intent.putExtra("formaredDateLater", formaredDateLater)

            intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


        } else {
            intent.putExtra("Toolbar_Title", "Drop-off")
            intent.putExtra("currentLatitude", DestinationLat!!.toDouble())
            intent.putExtra("currentLongitude", DestinationLong!!.toDouble())
            intent.putExtra("spinnerbag", spnrbag)
            intent.putExtra("spinnerTime", spnrtime)
            intent.putExtra("City", city_Name)
            intent.putExtra("formaredDateLater", formaredDateLater)

            intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


        }
        startActivity(intent)
    }

    private fun setStatusBarGradiant(activity: HomeActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    override fun onBackPressed() {
        finish()
        //super.onBackPressed();
    }


    override fun onMapReady(googleMap: GoogleMap) {
        //googleMap.clear()
        mMap = googleMap
        // mMap!!.getUiSettings().setAllGesturesEnabled(false)
        //  mMap!!.getUiSettings().setScrollGesturesEnabled(false)


    }


    @OnClick(R.id.btnCompareRide)
    fun btnCompareClick(){
        if (isValid()){
            btnCompare()
        }
    }

    private fun isValid(): Boolean {
        if (spinner_time!!.selectedItem.toString().equals("Later", ignoreCase = true)) {
            var dateTime :String?
            if (year >0){

                dateTime =  year.toString()+ " " + tv_RideScheduled?.text.toString().replace("AM", "am").replace("PM", "pm")
            }else{
                dateTime =  Calendar.getInstance().get(Calendar.YEAR).toString()+ " " + tv_RideScheduled?.text.toString().replace("AM", "am").replace("PM", "pm")
            }

            val selectedDateTime= SimpleDateFormat("yyyy dd MMM, hh:mm a").parse(dateTime)
            val minValidDateTime = SimpleDateFormat("dd MM yyyy HH:mm:ss").parse(minTimeToShedule())

            if(tv_RideScheduled?.text.toString().equals(AppUtils.convertDateGMTToLocal(minTimeToShedule()), ignoreCase = true)){
                return true
            } else if (selectedDateTime.before(minValidDateTime)){

                tv_RideScheduled?.text = AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM").replace("pm", "PM")

                Toast.makeText(this, "Scheduled time should be greater than 15 minutes from current time.", Toast.LENGTH_LONG).show()

                return false

            }else{
                return true
            }
        }
        return true
    }

    fun btnCompare() {


        sharedpreferences!!.edit().putString("SourceAddress", myCurrentLocation!!.text.toString())
            .commit()
        sharedpreferences!!.edit()
            .putString("DestinationAddress", myDropUpLocation!!.text.toString()).commit()





        if (spnrtime == 1) {
            formaredDate = formaredDateLater
        } else {
            formaredDate = setDate
        }


        if (formaredDateLater != null) {

            val simpleDateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            var date1: Date? = null
            var date2: Date? = null
            val today = Date()
            var formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            var dateq: Date? = null
            try {
                dateq = formatter.parse(formaredDateLater)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            formatter = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

            try {
                date1 = simpleDateFormat.parse(simpleDateFormat.format(today))
                date2 = simpleDateFormat.parse(formatter.format(dateq))
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            var different: Long? = null
            different = date2!!.time - date1!!.time



            if (estDistanceInMeter>499) {



                if (PortAir.equals("AIRPORT", ignoreCase = true)) {
                    airportYesOrNO = "Yes"
                } else {
                    airportYesOrNO = "NO"
                }
                PortAir =
                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT)
                replacedistance = estDistance!!.replace(" km", "")


                if ((spinnerLuggagetxt == "1 Luggage")) {
                    replacebags = spinnerLuggagetxt!!.replace(" Luggage", "")

                } else if ((spinnerLuggagetxt == "Luggage")) {
                    replacebags = "0"

                } else {
                    replacebags = spinnerLuggagetxt!!.replace(" Luggage", "")


                }


                val context = GeoApiContext.Builder()
                    .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                    .build()
                var results = arrayOfNulls<GeocodingResult>(0)
                try {
                    results = GeocodingApi.newRequest(context)
                        .latlng(
                            com.google.maps.model.LatLng(
                                SourceLat!!.toDouble(),
                                SourceLong!!.toDouble()
                            )
                        )
                        .await()
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                val context1 = GeoApiContext.Builder()
                    .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                    .build()
                var results1 = arrayOfNulls<GeocodingResult>(0)
                try {
                    results1 = GeocodingApi.newRequest(context1)
                        .latlng(
                            com.google.maps.model.LatLng(
                                DestinationLat!!.toDouble(),
                                DestinationLong!!.toDouble()
                            )
                        )
                        .await()
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                val contextCurrentPlaceID = GeoApiContext.Builder()
                    .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                    .build()
                var PlaceIDCurrent = arrayOfNulls<GeocodingResult>(0)
                try {
                    PlaceIDCurrent = GeocodingApi.newRequest(contextCurrentPlaceID)
                        .latlng(
                            com.google.maps.model.LatLng(
                                currentLatitude!!.toDouble(),
                                currentLongitude!!.toDouble()
                            )
                        )
                        .await()
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                sourcePlaceID = results[0]!!.placeId
                DestinationPlaceID = results1[0]!!.placeId
                CurrentPlaceID = PlaceIDCurrent[0]!!.placeId



                iCompareRidePresenter!!.getCompareRideData(
                    token,
                    replacedistance,
                    cityID,
                    sourcePlaceID,
                    DestinationPlaceID,
                    replacebags,
                    airportYesOrNO,
                    formaredDate,
                    CurrentPlaceID!!
                )


            } else {
                Toast.makeText(
                    this,
                    "Pick-UP and Drop-Off Location should not be same.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            if (estDistanceInMeter>499) {


                if (PortAir.equals("AIRPORT", ignoreCase = true)) {
                    airportYesOrNO = "Yes"
                } else {
                    airportYesOrNO = "NO"
                }
                PortAir =
                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT)
                replacedistance = estDistance!!.replace(" km", "")

                if ((spinnerLuggagetxt == "1 Luggage")) {
                    replacebags = spinnerLuggagetxt!!.replace(" Luggage", "")

                } else if ((spinnerLuggagetxt == "Luggage")) {
                    replacebags = "0"

                } else {
                    replacebags = spinnerLuggagetxt!!.replace(" Luggage", "")

                }


                val context = GeoApiContext.Builder()
                    .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                    .build()
                var results = arrayOfNulls<GeocodingResult>(0)
                try {
                    results = GeocodingApi.newRequest(context)
                        .latlng(
                            com.google.maps.model.LatLng(
                                SourceLat!!.toDouble(),
                                SourceLong!!.toDouble()
                            )
                        )
                        .await()
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                val context1 = GeoApiContext.Builder()
                    .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                    .build()
                var results1 = arrayOfNulls<GeocodingResult>(0)
                try {
                    results1 = GeocodingApi.newRequest(context1)
                        .latlng(
                            com.google.maps.model.LatLng(
                                DestinationLat!!.toDouble(),
                                DestinationLong!!.toDouble()
                            )
                        )
                        .await()
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                val contextCurrentPlaceID = GeoApiContext.Builder()
                    .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                    .build()
                var PlaceIDCurrent = arrayOfNulls<GeocodingResult>(0)
                try {
                    PlaceIDCurrent = GeocodingApi.newRequest(contextCurrentPlaceID)
                        .latlng(
                            com.google.maps.model.LatLng(
                                currentLatitude!!.toDouble(),
                                currentLongitude!!.toDouble()
                            )
                        )
                        .await()
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                sourcePlaceID = results[0]!!.placeId
                DestinationPlaceID = results1[0]!!.placeId
                CurrentPlaceID = PlaceIDCurrent[0]!!.placeId



                iCompareRidePresenter!!.getCompareRideData(
                    token,
                    replacedistance,
                    cityID,
                    sourcePlaceID,
                    DestinationPlaceID,
                    replacebags,
                    airportYesOrNO,
                    formaredDate,
                    CurrentPlaceID!!
                )


            } else {
                Toast.makeText(
                    this,
                    "Pick-UP and Drop-Off Location should not be same.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    }

    private fun drawRoute() {
        val mOrigin =
            LatLng(SourceLat!!.toDouble(), SourceLong!!.toDouble())
        val mDestination =
            LatLng(
                DestinationLat!!.toDouble(),
                DestinationLong!!.toDouble()
            )
        val url = getDirectionsUrl(mOrigin, mDestination)
        val downloadTask =
            DownloadTask()
        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(
        mOrigin: LatLng,
        mDestination: LatLng
    ): String {
        val str_origin = "origin=" + mOrigin.latitude + "," + mOrigin.longitude
        val str_dest =
            "destination=" + mDestination.latitude + "," + mDestination.longitude

        // Key
        val key = "key=" + getString(R.string.google_maps_key)

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$key"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }



    private inner class DownloadTask :
        AsyncTask<String, Void, String>() {
        // Downloading data in non-ui thread
        protected override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
                Log.d("DownloadTask", "DownloadTask : $data")
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask =
                ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
        }
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection!!.inputStream
            val br =
                BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception on download", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    private inner class ParserTask :
        AsyncTask<String, Int, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        protected override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? =
                null
            try {
                jObject = JSONObject(jsonData[0])
                val parser =
                    DirectionsJSONParser()
                val array = jObject.getJSONArray("routes")
                val routes1 = array.getJSONObject(0)
                val legs = routes1.getJSONArray("legs")
                val steps = legs.getJSONObject(0)
                val distance = steps.getJSONObject("distance")
                val duration = steps.getJSONObject("duration")

                estTime = duration.getString("text")
                estDistance = distance.getString("value")
                estDistanceInMeter = distance.getString("value").toInt()
                estDistance = DecimalFormat("####.##").format((estDistance!!.toDouble() / 1000)) + " km"


                /* if((distance.getString("text")).contains("mi")){
                     estDistance = distance.getString("text")
                     estDistance = estDistance!!.replace(" mi", "")
                     var estD =  estDistance!!.toDouble()
                     estD = estD * (1.60934)
                 }else{
                     estDistance = distance.getString("text")
                 }
 */
                // Starts parsing data
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng?>? = null
            var lineOptions: PolylineOptions? = null

            if (result != null) {
                for (i in result!!.indices) {
                    points = ArrayList()
                    lineOptions = PolylineOptions()

                    // Fetching i-th route
                    val path =
                        result[i]

                    // Fetching all the points in i-th route
                    for (j in path.indices) {
                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position =
                            LatLng(lat, lng)
                        points.add(position)
                    }
                    tvEstDistance!!.text = "Est.Distance $estDistance"
                    tvEstTime!!.text = "Est.Time $estTime"
                    if (!estDistance!!.isEmpty()) {
                        btnCompareRide!!.visibility = View.VISIBLE
                    }

                    lineOptions.addAll(points)
                    lineOptions.width(15f)
                    //  lineOptions.color(Color.GREEN);
                    lineOptions.color(
                        this@HomeActivity.resources.getColor(R.color.gradientstartcolor)
                    )
                }

                if (lineOptions != null) {
                    if (mPolyline != null) {
                        mPolyline!!.remove()
                    }
                    mPolyline = mMap!!.addPolyline(lineOptions)
                } else {
                    Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                    .show()
            }


        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!!.id == R.id.spinner_Luggage) {
            spnrbag = position
            spinnerLuggagetxt = spinner_Luggage!!.selectedItem.toString()
        } else if (parent!!.id == R.id.spinnerLang) {
            if (city_Name.equals("Choose City")) {

            }else{

                if (cityspinner.contains("Choose City")) {
                    if(position>0){
                        cityID = cityPojoList!!.get(position-1).id.toString()
                        city_Name = cityPojoList!!.get(position-1).name
                        preferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_CITY_ID,
                            cityID
                        )
                    }else{
                        preferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_CITY_ID,
                            cityPojoList!!.get(position).id.toString()
                        )
                    }



                }else{
                    cityID = cityPojoList!!.get(position).id.toString()
                    city_Name = cityPojoList!!.get(position).name
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_CITY_ID,
                        cityID
                    )

                }
            }





        } else {
            spnrtime = position
            spinnertxt = spinner_time!!.selectedItem.toString()
            if (spinnertxt == "Later") {

                /**
                 * Mohsin to display 15 min Later time.
                 */
                dateToStr = AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM").replace("pm", "PM")


                if (extras != null) {
                    if (spnrtime == 1) {

                      //  tv_RideScheduled?.text = tvDateandTime
                        tv_RideScheduled?.text = dateToStr

                    } else {
                        tv_RideScheduled?.text = dateToStr

                    }

                } else {
                    tv_RideScheduled?.text = dateToStr

                }



                rlRideScheduled!!.visibility = View.VISIBLE
            } else {
                getcurrentDate()
                rlRideScheduled!!.visibility = View.GONE
            }
        }

    }

    override fun onSuccess(info: CompareRideResponsePOJO?) {
        // Toast.makeText(this, "onSuccess", Toast.LENGTH_LONG).show()

        val distance = tvEstDistance!!.text.toString()
        val estTine = tvEstTime!!.text.toString()
        val intent = Intent(applicationContext, CompareRideActivity::class.java)
        intent.putExtra("SourceLat", SourceLat)
        intent.putExtra("SourceLong", SourceLong)
        intent.putExtra("DestinationLat", DestinationLat)
        intent.putExtra("DestinationLong", DestinationLong)
        intent.putExtra("Distance", estDistance)
        intent.putExtra("CITY_ID", cityID)
        intent.putExtra("CITY_NAME", city_Name)
        intent.putExtra("EstTime", estTime)
        intent.putExtra("Liggage", spinnerLuggagetxt)
        intent.putExtra("TimeSpinner", spinnertxt)
        intent.putExtra("Airport", extras!!.getString("keyAirport"))
        intent.putExtra("SourceAddress", myCurrentLocation!!.text.toString())
        intent.putExtra("DestinationAddress", myDropUpLocation!!.text.toString())
        intent.putExtra("currentDate", tv_RideScheduled!!.text.toString())
        intent.putExtra("currentFormatedDate", formaredDate)
        intent.putExtra("currentPlaceId", CurrentPlaceID)
        intent.putExtra("MyPOJOClass", info)


        startActivity(intent)
    }

    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            this@HomeActivity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()


    }

    override fun showWait() {
        showProgressDialog(this@HomeActivity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {

        Toast.makeText(this@HomeActivity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location: Location) {

        Log.d("sdsdsdswnwe", "onLocationChanged")

        currentLatitude = location!!.latitude
        currentLongitude = location!!.longitude


        if (callOnLocation.equals("first")) {
            if ( currentLatitude != 0.0 && currentLatitude != null) {

                getLocationReady()
                cityPojoList = preferencesManager!!.getCityList()
                if (cityPojoList != null && cityPojoList.size>0){
                    setCitySpinner()
                }else{
                    getCity()
                }

                progressDialogstart!!.dismiss()
                mainRelativeLayout!!.visibility = View.VISIBLE


            } else {
                Toast.makeText(
                    this,
                    "Please wait we are featching your current location",
                    Toast.LENGTH_LONG
                ).show()
            }

        }


    }

    private fun updateCameraBearing(mMap: GoogleMap?, bering: Float) {
        if (mMap == null) return
        val camPos = CameraPosition
            .builder(
                mMap.getCameraPosition() // current Camera
            )
            .bearing(bering)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }

    private fun getLocationReady() {

        if (currentLatitude == 0.0) {
            Toast.makeText(this, "Unable to find CURRENT LOCATION", Toast.LENGTH_LONG).show()
        } else {

            callOnLocation = "second"

            Log.d("mMarkerPointsSize", "$SourceLat       $SourceLong")
            Log.d("mMarkerPointsSizeDestin", "$DestinationLat    $DestinationLong")


            /*   mMap!!.setOnMapClickListener { latLng ->
                   val markerOptions = MarkerOptions()
                   markerOptions.position(latLng)
                   if (extras != null) {
                       var street: String? = null
                       if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
                           sharedpreferences!!.edit()
                               .putString("SourceLat", latLng.latitude.toString()).commit()
                           sharedpreferences!!.edit()
                               .putString("SourceLong", latLng.longitude.toString()).commit()
                           SourceLat = latLng.latitude.toString()
                           SourceLong = latLng.longitude.toString()
                           val geocoder =
                               Geocoder(this@HomeActivity, Locale.getDefault())
                           try {
                               val addresses =
                                   geocoder.getFromLocation(
                                       SourceLat!!.toDouble(),
                                       SourceLong!!.toDouble(),
                                       1
                                   )
                               if (addresses != null) {
                                   val returnedAddress = addresses[0]
                                   val strReturnedAddress =
                                       StringBuilder()
                                   for (j in 0..returnedAddress.maxAddressLineIndex) {
                                       strReturnedAddress.append(returnedAddress.getAddressLine(j))
                                   }
                                   street = strReturnedAddress.toString()
                                   city = returnedAddress.subAdminArea
                               }
                           } catch (e: IOException) {
                           }
                           sharedpreferences!!.edit().putString("fromLocation", street).commit()
                           myCurrentLocation!!.text = street
                           sourecemarker!!.remove()
                           markerOptions.title(street)
                           mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                           mMap!!.animateCamera(
                               CameraUpdateFactory.newLatLngZoom(
                                   latLng,
                                   15.0f
                               )
                           )
                           sourecemarker = mMap!!.addMarker(
                               MarkerOptions()
                                   .position(LatLng(latLng.latitude, latLng.longitude))
                                   .draggable(false)
                                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                           )
                       } else {
                           sharedpreferences!!.edit()
                               .putString("DestinationLat", latLng.latitude.toString()).commit()
                           sharedpreferences!!.edit()
                               .putString("DestinationLong", latLng.longitude.toString()).commit()
                           DestinationLat = latLng.latitude.toString()
                           DestinationLong = latLng.longitude.toString()
                           val geocoder =
                               Geocoder(this@HomeActivity, Locale.getDefault())
                           try {
                               val addresses =
                                   geocoder.getFromLocation(
                                       DestinationLat!!.toDouble(),
                                       DestinationLong!!.toDouble(),
                                       1
                                   )
                               if (addresses != null) {
                                   val returnedAddress = addresses[0]
                                   val strReturnedAddress =
                                       StringBuilder()
                                   for (j in 0..returnedAddress.maxAddressLineIndex) {
                                       strReturnedAddress.append(returnedAddress.getAddressLine(j))
                                   }
                                   street = strReturnedAddress.toString()
                               }
                           } catch (e: IOException) {
                           }
                           sharedpreferences!!.edit().putString("destiNationLocation", street).commit()
                           myDropUpLocation!!.text = street
                           destmarker!!.remove()
                           markerOptions.title(street)
                           mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                           mMap!!.animateCamera(
                               CameraUpdateFactory.newLatLngZoom(
                                   latLng,
                                   15.0f
                               )
                           )
                           destmarker = mMap!!.addMarker(
                               MarkerOptions().position(
                                       LatLng(
                                           latLng.latitude,
                                           latLng.longitude
                                       )
                                   ).draggable(false)
                                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                           )
                       }
                   } else {
                       var streetAddress: String? = null
                       sharedpreferences!!.edit().putString("SourceLat", latLng.latitude.toString())
                           .commit()
                       sharedpreferences!!.edit().putString("SourceLong", latLng.longitude.toString())
                           .commit()
                       SourceLat = latLng.latitude.toString()
                       SourceLong = latLng.longitude.toString()
                       val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
                       try {
                           val addresses =
                               geocoder.getFromLocation(
                                   SourceLat!!.toDouble(),
                                   SourceLong!!.toDouble(),
                                   1
                               )
                           if (addresses != null) {
                               val returnedAddress = addresses[0]
                               val strReturnedAddress =
                                   StringBuilder()
                               for (j in 0..returnedAddress.maxAddressLineIndex) {
                                   strReturnedAddress.append(returnedAddress.getAddressLine(j))
                               }
                               streetAddress = strReturnedAddress.toString()
                               city = returnedAddress.subAdminArea
                           }
                       } catch (e: IOException) {
                       }
                       sharedpreferences!!.edit().putString("fromLocation", streetAddress).commit()
                       myCurrentLocation!!.text = streetAddress
                       sourecemarker!!.remove()
                       markerOptions.title(streetAddress)
                       mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                       mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
                       sourecemarker = mMap!!.addMarker(
                           MarkerOptions().position(
                               LatLng(
                                   latLng.latitude,
                                   latLng.longitude
                               )
                           ).draggable(false)
                               .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                       )
                   }
                   if (!SourceLat!!.isEmpty() && !DestinationLat!!.isEmpty()) {
                       drawRoute()
                   }
               }
   */

            if (extras != null) {
                if (!SourceLat!!.isEmpty()) {

                    val geocoder =
                        Geocoder(this@HomeActivity, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(
                            SourceLat!!.toDouble(),
                            SourceLong!!.toDouble(),
                            1
                        )

                        //Kiran Code
                       // streetAddress = if (addresses!!.size > 0 && addresses != null) {

                        //Mohsin Code

                        streetAddress = if (addresses != null && addresses!!.size > 0) {
                            addresses[0].getAddressLine(0)
                        } else {
                            ""
                        }
                    } catch (e: IOException) {
                    }

                    myCurrentLocation!!.text = sharedpreferences!!.getString("fromLocation", "")
                    if (mMap != null){
                        mMap!!.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    SourceLat!!.toDouble(),
                                    SourceLong!!.toDouble()
                                ), 15.0f
                            )
                        )
                        sourecemarker = mMap!!.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    SourceLat!!.toDouble(),
                                    SourceLong!!.toDouble()
                                )
                            ).draggable(false)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                        )
                    }

                }
                if (!DestinationLat!!.isEmpty()) {
                    val returnedAddress: Address? = null
                    val geocoder =
                        Geocoder(this@HomeActivity, Locale.getDefault())
                    try {
                        val addresses =
                            geocoder.getFromLocation(
                                DestinationLat!!.toDouble(),
                                DestinationLong!!.toDouble(),
                                1
                            )

                        streetAddress = if (addresses != null && addresses!!.size > 0 ) {
                            addresses[0].getAddressLine(0)
                        } else {
                            ""
                        }
                    } catch (e: IOException) {
                    }

                    myDropUpLocation!!.text =
                        sharedpreferences!!.getString("destiNationLocation", "")
                    destmarker = mMap!!.addMarker(
                        MarkerOptions().position(
                                LatLng(
                                    DestinationLat!!.toDouble(),
                                    DestinationLong!!.toDouble()
                                )
                            ).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_grey))
                    )
                }
                if (!SourceLat!!.isEmpty() && !DestinationLat!!.isEmpty()) {
                    reestimateDateandTime!!.visibility = View.VISIBLE
                    drawRoute()
                }
            } else {
                val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1)

                    if (addresses != null && addresses!!.size > 0) {
                        streetAddress = addresses[0].getAddressLine(0)
                        city = addresses[0].subAdminArea
                    } else {
                        streetAddress = ""
                    }
                } catch (e: IOException) {
                }


                if (SourceLat!!.isEmpty()) {
                    sharedpreferences!!.edit().putString("SourceLat", currentLatitude.toString())
                        .commit()
                    sharedpreferences!!.edit().putString("SourceLong", currentLongitude.toString())
                        .commit()
                }

                sharedpreferences!!.edit().putString("fromLocation", streetAddress).commit()
                myCurrentLocation!!.text = streetAddress

                Log.d("sdewddwasMar", currentLatitude.toString())
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            currentLatitude,
                            currentLongitude
                        ), 15.0f
                    )
                )
                sourecemarker = mMap?.addMarker(
                    MarkerOptions().position(LatLng(currentLatitude, currentLongitude))
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                )
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }


    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {

    }


}
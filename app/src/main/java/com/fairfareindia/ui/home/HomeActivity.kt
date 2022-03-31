package com.fairfareindia.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.*
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.*
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.BuildConfig
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityHomeBinding
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.adapter.DrawerAdapter
import com.fairfareindia.ui.drawer.contactus.ContactUs
import com.fairfareindia.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.fairfareindia.ui.drawer.covid19.Covid
import com.fairfareindia.ui.drawer.faq.FAQ
import com.fairfareindia.ui.drawer.intercityrides.GetRideResponsePOJO
import com.fairfareindia.ui.drawer.intercityrides.RidesFragment
import com.fairfareindia.ui.drawer.intercityrides.ridedetails.IntercityRideDetailsActivity
import com.fairfareindia.ui.drawer.myaccount.MyAccountFragment
import com.fairfareindia.ui.drawer.mycomplaints.MyComplaints
import com.fairfareindia.ui.drawer.mydisput.MyDisput
import com.fairfareindia.ui.drawer.notifications.NotificationsFragment
import com.fairfareindia.ui.drawer.pojo.DrawerPojo
import com.fairfareindia.ui.drawer.privacypolicy.ContentPage
import com.fairfareindia.ui.drawer.privacypolicy.TermsOfUse
import com.fairfareindia.ui.drawer.ratecard.RateCardsFragment
import com.fairfareindia.ui.drawer.servicepartners.ServicePartnersFragment
import com.fairfareindia.ui.drawer.setting.Setting
import com.fairfareindia.ui.home.pojo.GeneralSettingModel
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.home.pojo.PickUpLocationModel
import com.fairfareindia.ui.intercity.InterCityActivity
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel
import com.fairfareindia.ui.intercitytrackpickup.TrackPickUpActivity
import com.fairfareindia.ui.intercitytrackride.InterCityTrackRideActivity
import com.fairfareindia.ui.localcompareride.LocalCompareRideActivity
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.ui.splashscreen.PermissionActivity
import com.fairfareindia.utils.*
import com.fairfareindia.utils.ProjectUtilities.showProgressDialog
import com.google.android.gms.location.LocationSettingsStates
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
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
    OnTimeSetListener, AdapterView.OnItemClickListener, IHomeView {

    protected var myLocationManager: MyLocationManager? = MyLocationManager(this)

    lateinit var binding: ActivityHomeBinding
    private var context: Context = this

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentUserLatitude = 0.0
    var currentUserLongitude = 0.0
    private var mPolyline: Polyline? = null
    var sourecemarker: Marker? = null
    var destmarker: Marker? = null
    var spinnerLuggagetxt: String? = null
    var city: String? = null
    var onePlusEmptyCity: String? = null
    var setDate: String? = null
    var keyAirport: String? = null
    var placesClient: PlacesClient? = null
    var isFirstTimeLocationShowed: Boolean? = false
    var getAllowCityResponse: GetAllowCityResponse? = null
    private var rideModel: GetRideResponsePOJO.DataItem? = null

    var doubleBackPressed: Boolean? = false
    private var mToastToShow: Toast? = null


    var appSignatureHelper: AppSignatureHelper? = null

    private var timeSpinner: Array<String>? = null
    private var luggageSpinner: Array<String>? = null

    var cityspinner = ArrayList<String>()


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
    var legDuration: String? = null
    var estDistance: String? = null
    var estDistanceInMeter = 0
    var hideshow: String? = null
    var spnrbag = 0
    var cityID = ""
    var spnrtime = 0
    var flgstreetaddress = true

    private var drawerPojoArrayList: ArrayList<DrawerPojo>? = null
    private var drawerAdapter: DrawerAdapter? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    var preferencesManager: PreferencesManager? = null

    private var cityPojoList: List<GetAllowCityResponse.CitiesItem> = ArrayList()


    private var iCompareRidePresenter: IHomePresenter? = null
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
    var scheduleType: String? = null


    var stDay: String? = null
    var strMonth: String? = null
    var strsecond: String? = null
    var strhr: String? = null
    var strMinute: String? = null
    var callOnLocation: String? = null
    var actionNotify: String? = null


    var progressDialogstart: ProgressDialog? = null

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        setStatusBarGradiant(this)

        // generateSSHKey(this)
        progressDialogstart = ProgressDialog(context)
        progressDialogstart?.setCancelable(false) // set cancelable to false
        progressDialogstart?.setMessage(getString(R.string.str_please_wait)) // set message
        progressDialogstart?.show() // show progress dialog


        binding.spinnerLang.visibility = View.VISIBLE

        initLocationUpdates()


        //  appSignatureHelper = AppSignatureHelper(this)
        //  appSignatureHelper!!.getAppSignatures()


        callOnLocation = "first"
        val action = intent.action


        Places.initialize(this, resources.getString(R.string.google_maps_key))
        placesClient = Places.createClient(this@HomeActivity)
        PreferencesManager.initializeInstance(this@HomeActivity)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)



        iCompareRidePresenter = HomeImplementer(this)






        initView()
        // Initialize drawer list
        setListData()

        getActiveRidesAPI()

        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        val intent = intent
        extras = intent.extras
        if (extras != null) {
            spnrbag = extras!!.getInt("spnbg")
            spnrtime = extras!!.getInt("spnTime")
            tvDateandTime = extras!!.getString("TvDateTime")
            formaredDateLater = extras!!.getString("formaredDateLater")
            actionNotify = extras!!.getString("notifyAction")

        } else {
            spnrbag = 0
            spnrtime = 0
        }

        if (spnrtime == 0) {
            binding.rlRideScheduled.visibility = View.GONE
        } else {
            binding.rlRideScheduled.visibility = View.VISIBLE
        }




        hideshow = "show"
        getCurrentDate()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

      /*  val NowLater: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, timeSpinner)
        NowLater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTime.adapter = NowLater
        binding.spinnerTime.setSelection(spnrtime)
        binding.spinnerTime.onItemSelectedListener = this


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, luggageSpinner)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLuggage.adapter = spinnerLuggage
        binding.spinnerLuggage.setSelection(spnrbag)
        binding.spinnerLuggage.setOnItemSelectedListener(this)*/



        if (action == "schduleRideSuccess") {
            binding.spinnerLang.visibility = View.GONE
            setFragment(RidesFragment())
        } else if (action == "RegisterDisput") {
            binding.spinnerLang.visibility = View.GONE
            setFragment(MyDisput())
        } else if (action == "filecomplaintSuccess") {
            binding.spinnerLang.visibility = View.GONE
            setFragment(MyComplaints())
        } else if (action == "MyRides") {
            binding.spinnerLang.visibility = View.GONE
            setFragment(RidesFragment())
        }



        SourceLat = sharedpreferences!!.getString(
            "SourceLat", "" +
                    ""
        )
        SourceLong = sharedpreferences!!.getString("SourceLong", "")
        DestinationLat = sharedpreferences!!.getString("DestinationLat", "")
        DestinationLong = sharedpreferences!!.getString("DestinationLong", "")


        if (SourceLat!!.isNotEmpty() && DestinationLat!!.isNotEmpty()) {
            progressDialogstart?.dismiss()
        }

        EventBus.getDefault().register(this)

        setSpinners()
        setListeners()
    }

    private fun setSpinners() {
        timeSpinner = arrayOf<String>(
            context.resources.getString(R.string.str_now),
            context.resources.getString(R.string.str_later)
        )
        luggageSpinner = arrayOf<String>(
            getString(R.string.str_luggage),
            getString(R.string.str_luggage_1),
            getString(R.string.str_luggage_2),
            getString(R.string.str_luggage_3),
            getString(R.string.str_luggage_4),
            getString(R.string.str_luggage_5)
        )

        val NowLater: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, timeSpinner!!)
        NowLater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTime.adapter = NowLater


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, luggageSpinner!!)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLuggage.adapter = spinnerLuggage

    }

    private fun setData() {
        binding.apply {
            if (rideModel != null) {
                llActiveRide.visibility = View.VISIBLE
                llhideview.visibility = View.GONE
                tvhideShow.visibility = View.GONE
                btnIntercity.visibility = View.GONE


                txtVehicleName.text = rideModel?.vehicleName
                txtVehicleNumber.text = rideModel?.vehicleNo


                txtSourceAddress.text = (rideModel?.originFullAddress)
                txtDestinationAddress.text = (rideModel?.destinationFullAddress)
                txtDriverName.text = rideModel?.driver?.name

                if (rideModel?.status == Constants.BOOKING_SCHEDULED){
                    txtStatusMessage.text = getString(R.string.str_ride_accepted)
                    txtRideMessage.text = getString(R.string.msg_ride_scheduled)
                    txtRideMessage.visibility = View.VISIBLE
                }else if (rideModel?.status == Constants.BOOKING_ARRIVING){
                    txtStatusMessage.text = getString(R.string.msg_track_arriving)
                    txtRideMessage.text = getString(R.string.msg_ride_arriving)
                    txtRideMessage.visibility = View.VISIBLE
                }else if (rideModel?.status == Constants.BOOKING_ARRIVED){
                    txtStatusMessage.text = getString(R.string.msg_track_arrived)
                    txtRideMessage.text = getString(R.string.msg_ride_arrived)
                    txtRideMessage.visibility = View.VISIBLE
                }else if (rideModel?.status == Constants.BOOKING_ACTIVE){
                    txtStatusMessage.text = getString(R.string.msg_track_active)
                    txtRideMessage.visibility = View.GONE
                }

                Glide.with(context)
                    .load(rideModel?.vehicleImageUrl)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(imgVehicle)

            } else {
                llActiveRide.visibility = View.GONE
                llhideview.visibility = View.VISIBLE
                tvhideShow.visibility = View.VISIBLE
                btnIntercity.visibility = View.VISIBLE
            }
        }

    }

    private fun initView() {
        binding.apply {
            toolbarHome.title = getString(R.string.title_home)
            toolbarHome.setTitleTextColor(Color.WHITE)
            setSupportActionBar(toolbarHome)
            lvDrawer.onItemClickListener = this@HomeActivity
            drawerToggle = object : ActionBarDrawerToggle(
                this@HomeActivity, drawerLayout, toolbarHome,
                R.string.drawer_open, R.string.drawer_close
            ) {
                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                }
            }
            drawerLayout.addDrawerListener(drawerToggle as ActionBarDrawerToggle)!!
            drawerToggle?.isDrawerIndicatorEnabled = true
            drawerToggle?.isDrawerIndicatorEnabled = false
            drawerToggle?.setHomeAsUpIndicator(R.drawable.ic_action_menu)
            drawerToggle?.syncState()
            drawerToggle?.toolbarNavigationClickListener = View.OnClickListener {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT)
                } else {


                    tvEmailAddress.text =
                        getString(R.string.drawer_available_reward_point) + " " + preferencesManager!!.getStringValue(
                            Constants.SHARED_PREFERENCE_USER_REWARD
                        )

                    tvUserName.text =
                        preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_NAME)

                    tvUserLocation.text =
                        preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_LOCATION)

                    tvProfession.text =
                        preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFESTION)



                    Glide.with(this@HomeActivity)
                        .load(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK))
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .dontTransform()
                        ).into(logoLayout!!)

                    drawerLayout.openDrawer(Gravity.LEFT)
                }
            }
        }

    }

    private fun setListeners() {
        binding.apply {
            btnTrackRide?.setOnClickListener {
                if (rideModel?.status == Constants.BOOKING_SCHEDULED || rideModel?.status == Constants.BOOKING_ARRIVING || rideModel?.status == Constants.BOOKING_ARRIVED) {
                    val intent = Intent(this@HomeActivity, TrackPickUpActivity::class.java)
                    intent.putExtra("ride_id", rideModel?.id.toString())
                    startActivity(intent)
                } else if (rideModel?.status == Constants.BOOKING_ACTIVE) {
                    val intent = Intent(this@HomeActivity, InterCityTrackRideActivity::class.java)
                    intent.putExtra("ride_id", rideModel?.id.toString())
                    intent.putExtra("vehicle_type", rideModel?.vehicleType)
                    startActivity(intent)
                } else if (rideModel?.status == Constants.BOOKING_COMPLETED) {
                    val intent = Intent(this@HomeActivity, IntercityRideDetailsActivity::class.java)
                    intent.putExtra("ride_id", rideModel?.id.toString())
                    startActivity(intent)
                }
            }

            btnIntercity.setOnClickListener {
                startActivity(
                    Intent(context, InterCityActivity::class.java)
                        .putExtra("current_latitude", currentLatitude)
                        .putExtra("current_longitude", currentLongitude)
                )
            }

            crdPuneMetro.setOnClickListener {
                val FB_DYNAMIC_LINK_URL = "https://punemobileapp.page.link/androidapp"
                val browserIntent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        FB_DYNAMIC_LINK_URL
                    )
                )
                startActivity(browserIntent)
            }

            btnCompareRide.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    if (isValid()) {
                        btnCompare()
                    }
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            logoLayout.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {


                    spinnerLang.visibility = View.GONE
                    homeMain.visibility = View.GONE
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    drawerLayout.closeDrawer(Gravity.START)

                    replaceFragment(MyAccountFragment())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }

            btnLogout.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {
                    callLogoutAPI()
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }

            tvRideScheduled.setOnClickListener {
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

            tvhideShow.setOnClickListener {
                if (hideshow.equals("show")) {
                    hideshow = "hide"
                    llhideview.visibility = View.GONE
                } else {
                    hideshow = "show"
                    llhideview.visibility = View.VISIBLE
                }
            }

            tvMyCurrentLocation.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    val intent = Intent(applicationContext, PickUpDropActivity::class.java)
                    if (SourceLat!!.isEmpty()) {
                        intent.putExtra("Toolbar_Title", "Pick-Up")
                        intent.putExtra("currentLatitude", currentLatitude)
                        intent.putExtra("currentLongitude", currentLongitude)
                        intent.putExtra("spinnerbag", spnrbag)
                        intent.putExtra("spinnerTime", spnrtime)
                        intent.putExtra("City", city_Name)
                        intent.putExtra("formaredDateLater", formaredDateLater)
                        intent.putExtra("spinnerTimeDate", tvRideScheduled.text)


                        intent.putExtra("currentUserLatitude", currentUserLatitude)
                        intent.putExtra("currentUserLongitude", currentUserLongitude)
                        intent.putExtra("currentUserAddress", streetAddress)


                    } else {
                        intent.putExtra("Toolbar_Title", "Pick-Up")
                        intent.putExtra("currentLatitude", SourceLat!!.toDouble())
                        intent.putExtra("currentLongitude", SourceLong!!.toDouble())
                        intent.putExtra("spinnerbag", spnrbag)
                        intent.putExtra("spinnerTime", spnrtime)
                        intent.putExtra("City", city_Name)
                        intent.putExtra("formaredDateLater", formaredDateLater)
                        intent.putExtra("spinnerTimeDate", tvRideScheduled.text)


                        intent.putExtra("currentUserLatitude", currentUserLatitude)
                        intent.putExtra("currentUserLongitude", currentUserLongitude)
                        intent.putExtra("currentUserAddress", streetAddress)


                    }
                    startActivity(intent)
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }

            tvMyDropUpLocation.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    val intent = Intent(applicationContext, PickUpDropActivity::class.java)
                    if (DestinationLat!!.isEmpty()) {
                        intent.putExtra("Toolbar_Title", "Drop-off")
                        intent.putExtra("currentLatitude", currentLatitude)
                        intent.putExtra("currentLongitude", currentLongitude)
                        intent.putExtra("spinnerbag", spnrbag)
                        intent.putExtra("spinnerTime", spnrtime)
                        intent.putExtra("City", city_Name)
                        intent.putExtra("formaredDateLater", formaredDateLater)
                        intent.putExtra("spinnerTimeDate", tvRideScheduled!!.text)


                        intent.putExtra("currentUserLatitude", currentUserLatitude)
                        intent.putExtra("currentUserLongitude", currentUserLongitude)
                        intent.putExtra("currentUserAddress", streetAddress)


                    } else {
                        intent.putExtra("Toolbar_Title", "Drop-off")
                        intent.putExtra("currentLatitude", DestinationLat!!.toDouble())
                        intent.putExtra("currentLongitude", DestinationLong!!.toDouble())
                        intent.putExtra("spinnerbag", spnrbag)
                        intent.putExtra("spinnerTime", spnrtime)
                        intent.putExtra("City", city_Name)
                        intent.putExtra("formaredDateLater", formaredDateLater)
                        intent.putExtra("spinnerTimeDate", tvRideScheduled.text)


                        intent.putExtra("currentUserLatitude", currentUserLatitude)
                        intent.putExtra("currentUserLongitude", currentUserLongitude)
                        intent.putExtra("currentUserAddress", streetAddress)


                    }
                    startActivity(intent)
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }

            spinnerLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!city_Name.equals("Choose City")) {
                        if (cityspinner.contains("Choose City")) {
                            if (position > 0) {
                                cityID = cityPojoList[position - 1].id.toString()
                                city_Name = cityPojoList[position - 1].name
                                preferencesManager!!.setStringValue(
                                    Constants.SHARED_PREFERENCE_CITY_ID,
                                    cityID
                                )
                            } else {

                                cityID = ""
                                preferencesManager!!.setStringValue(
                                    Constants.SHARED_PREFERENCE_CITY_ID,
                                    cityPojoList[position].id.toString()
                                )
                            }


                        } else {
                            if (position < cityPojoList.size) {
                                cityID = cityPojoList[position].id.toString()
                                city_Name = cityPojoList[position].name
                                preferencesManager?.setStringValue(
                                    Constants.SHARED_PREFERENCE_CITY_ID,
                                    cityID
                                )
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            spinnerLuggage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    spnrbag = position
                    replacebags = position.toString()
                    spinnerLuggagetxt = binding.spinnerLuggage.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }



            spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    spnrtime = position
                    spinnertxt = binding.spinnerTime.selectedItem.toString()
                    if (timeSpinner?.get(position)?.equals(getString(R.string.str_now))!!) {
                        getCurrentDate()
                        binding.rlRideScheduled.visibility = View.GONE
                        scheduleType = "Now"

                    } else {
                        /**
                         * Mohsin to display 15 min Later time.
                         */
                        scheduleType = "Later"
                        dateToStr =
                            AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM")
                                .replace("pm", "PM")


                        if (extras != null) {
                            if (spnrtime == 1) {
                                binding.tvRideScheduled.text = dateToStr

                            } else {
                                binding.tvRideScheduled.text = dateToStr

                            }

                        } else {
                            binding.tvRideScheduled.text = dateToStr

                        }



                        binding.rlRideScheduled.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }




    private fun initLocationUpdates() {
        myLocationManager?.getMyCurrentLocationChange(object :
            MyLocationManager.LocationManagerTrackInterface {
            override fun onMyLocationChange(
                currentLocation: MutableList<Location>?,
                lastLocation: Location?
            ) {
                if (lastLocation != null) {

                    Log.d("sdsdsdswnwe", "onLocationChanged")

                    currentLatitude = lastLocation!!.latitude
                    currentLongitude = lastLocation!!.longitude

                    currentUserLatitude = lastLocation!!.latitude
                    currentUserLongitude = lastLocation!!.longitude


                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_CLat,
                        currentLatitude.toString()
                    )
                    preferencesManager!!.setStringValue(
                        Constants.SHARED_PREFERENCE_CLong,
                        currentLongitude.toString()
                    )

                    //Stop Location Updates
                    if (isFirstTimeLocationShowed!! && currentLatitude != null && currentLatitude != 0.0) {
                        myLocationManager?.stopLocationUpdates()
                    }

                    if (callOnLocation.equals("first")) {
                        if (currentLatitude != null && currentLatitude != 0.0) {

                            isFirstTimeLocationShowed = true


                            mapAndLocationReady()
                            getCity()

                            progressDialogstart?.dismiss()
                            binding.mainRelativeLayout.visibility = View.VISIBLE


                        }

                    }

                }
            }

        })
    }

    private fun mapAndLocationReady() {

        if (CommonAppPermission.hasLocationPermission(this) && ProjectUtilities.isGPSEnabled(this)) {
            if (callOnLocation.equals("first") && mMap != null && currentLatitude != null && currentLatitude != 0.0) {
                getLocationReady()
            }
        } else {
            startActivity(
                Intent(this, PermissionActivity::class.java).putExtra(
                    "isFromSplash",
                    true
                )
            )
            finish()
        }
    }

    private fun getCurrentCityName() {
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
            streetAddress = getAddressFromLocation()
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPickUpLocationModel(model: PickUpLocationModel) {
        if (model != null) {
            keyAirport = model.keyAirport
            if (model.isSource!!) {
                SourceLat = model.latitude.toString()
                SourceLong = model.longitude.toString()
                binding.tvMyCurrentLocation.text = model.address

            } else {
                DestinationLat = model.latitude.toString()
                DestinationLong = model.longitude.toString()
                binding.tvMyDropUpLocation.text = model.address
            }
            drawRouteOnAddressSelection()
        }

    }

    private fun drawRouteOnAddressSelection() {
        if (!SourceLat!!.isEmpty()) {

            if (mMap != null) {
                if (sourecemarker != null) {
                    sourecemarker?.remove()
                }
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
            if (destmarker != null) {
                destmarker?.remove()
            }
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
            binding.reestimateDateandTime.visibility = View.VISIBLE
            drawRoute()
        }
    }


    private fun getCity() {


        val cLat = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CLat)
        val cLong = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CLong)


        ApiClient.client.getAllowCities("Bearer $token", cLat, cLong)!!.enqueue(object :
            Callback<GetAllowCityResponse?> {
            override fun onResponse(
                call: Call<GetAllowCityResponse?>,
                response: Response<GetAllowCityResponse?>
            ) {
                if (response.code() == 200) {
                    getAllowCityResponse = response.body()
                    city = response.body()!!.getallowCityName()

                    onePlusEmptyCity = response.body()!!.getcurrentAddress()

                    sharedpreferences!!.edit().putString("fromLocation", onePlusEmptyCity).commit()
                    binding.tvMyCurrentLocation.text = onePlusEmptyCity




                    cityPojoList = response!!.body()!!.cities

                    preferencesManager?.setCityList(cityPojoList)

                    setCitySpinner()


                } else if (response.code() == 401) {
                    preferencesManager?.clear()
                    val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
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
                        getString(R.string.err_internal_server_error),
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
        if (city == null) {
            getCurrentCityName()
        }

        for (cityModel: GetAllowCityResponse.CitiesItem in cityPojoList) {
            cityspinner.add(cityModel.name)

        }


        val SpnLang: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this@HomeActivity,
            R.layout.simple_city_txt_spinner,
            cityspinner as List<Any?>
        )
        SpnLang.setDropDownViewResource(R.layout.simple_city_spinner)
        binding.spinnerLang.adapter = SpnLang

        if (extras == null) {

            if (city.equals("thane", ignoreCase = true)) {
                city = "Mumbai"
            }

            if (cityspinner.contains(city)) {

                for (i in cityspinner!!.indices) {
                    if (city.equals(cityspinner[i])) {
                        binding.spinnerLang.setSelection(i)
                    }

                }
            } else {
                cityspinner.add(0, "Choose City")


                val toastDurationInMilliSeconds = 10000
                mToastToShow = Toast.makeText(
                    this@HomeActivity,
                    getString(R.string.msg_not_served_city) + " " + city + " " + getString(R.string.msg_not_serve_location_two),
                    Toast.LENGTH_LONG
                )
                val toastCountDown: CountDownTimer
                toastCountDown = object :
                    CountDownTimer(toastDurationInMilliSeconds.toLong(), 1000 /*Tick duration*/) {
                    override fun onTick(millisUntilFinished: Long) {
                        mToastToShow!!.show()
                    }

                    override fun onFinish() {
                        mToastToShow!!.cancel()
                    }
                }
                mToastToShow!!.show()
                toastCountDown.start()


            }

        } else {
            if (city.equals("thane", ignoreCase = true)) {
                city = "Mumbai"
            }


            if (cityspinner.contains(city)) {
                for (i in cityspinner!!.indices) {
                    if (city.equals(cityspinner[i])) {
                        binding.spinnerLang.setSelection(i)
                    }

                }
            } else {

                cityspinner.add(0, "Choose City")


                val toastDurationInMilliSeconds = 10000
                mToastToShow = Toast.makeText(
                    this@HomeActivity,
                    getString(R.string.msg_not_served_city) + " " + city + " " + getString(R.string.msg_not_serve_location_two),
                    Toast.LENGTH_LONG
                )
                val toastCountDown: CountDownTimer
                toastCountDown = object :
                    CountDownTimer(toastDurationInMilliSeconds.toLong(), 1000 /*Tick duration*/) {
                    override fun onTick(millisUntilFinished: Long) {
                        mToastToShow!!.show()
                    }

                    override fun onFinish() {
                        mToastToShow!!.cancel()
                    }
                }
                mToastToShow!!.show()
                toastCountDown.start()


            }
        }
    }


    private fun setFragment(myRides: Fragment) {


        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.add(R.id.container_framelayout, myRides)
        fragmentTransaction!!.commit()
    }


    private fun setListData() {
        binding.apply {
            tvEmailAddress.text =
                getString(R.string.drawer_available_reward_point) + " " + preferencesManager?.getStringValue(
                    Constants.SHARED_PREFERENCE_USER_REWARD
                )

            tvUserName.text =
                preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_NAME)

            tvUserLocation.text =
                preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_LOCATION)

            tvProfession.text =
                preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFESTION)



            Glide.with(this@HomeActivity)
                .load(preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK))
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                ).into(logoLayout!!)
        }




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
                11,
                getString(R.string.drawer_notifications),
                R.drawable.ic_nav_ratecard
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
                R.drawable.ic_nav_faqs
            )
        )


        drawerPojoArrayList!!.add(
            DrawerPojo(
                7,
                getString(R.string.drawer_contactus),
                R.drawable.ic_nav_contactus
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
                getString(R.string.drawer_term_of_use),
                R.drawable.ic_nav_privacypolicy
            )
        )

        drawerPojoArrayList!!.add(
            DrawerPojo(
                10,
                getString(R.string.drawer_service_partners),
                R.drawable.ic_nav_privacypolicy
            )
        )



        drawerPojoArrayList!!.add(
            DrawerPojo(
                11,
                getString(R.string.drawer_setting),
                R.drawable.ic_nav_setting
            )
        )


        drawerAdapter = DrawerAdapter(this, drawerPojoArrayList)
        binding.lvDrawer.adapter = drawerAdapter

    }

    @SuppressLint("WrongConstant")
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }

                    replaceFragment(Covid())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }

            }

            1 -> {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }
                    replaceFragment(RidesFragment())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            2 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }
                    replaceFragment(MyDisput())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            3 -> {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }

                    replaceFragment(MyComplaints())

                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }

            4 -> {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }
                    replaceFragment(NotificationsFragment())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            5 -> {
                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }
                    replaceFragment(RateCardsFragment())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            6 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }

                    replaceFragment(FAQ())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }

            }


            7 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    if (preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_EMAIL)!!
                            .isNotEmpty()
                    ) {
                        binding.apply {
                            spinnerLang.visibility = View.GONE
                            homeMain.visibility = View.GONE
                            drawerLayout.closeDrawer(Gravity.LEFT)
                            drawerLayout.closeDrawer(Gravity.START)
                        }

                        replaceFragment(ContactUs())

                    } else {

                        val alertDialog =
                            AlertDialog.Builder(this@HomeActivity, R.style.alertDialog)
                        alertDialog.setTitle(getString(R.string.str_fair_fare_india))
                        alertDialog.setMessage(getString(R.string.msg_dialog_email_id_required))
                        alertDialog.setCancelable(false)
                        alertDialog.setPositiveButton(getString(R.string.str_no)) { dialog, which ->
                            dialog.cancel()
                        }
                        alertDialog.setNegativeButton(getString(R.string.str_yes)) { dialog, which ->
                            binding.apply {
                                spinnerLang.visibility = View.GONE
                                homeMain.visibility = View.GONE
                                drawerLayout.closeDrawer(Gravity.LEFT)
                                drawerLayout.closeDrawer(Gravity.START)
                            }

                            replaceFragment(MyAccountFragment())


                        }
                        alertDialog.show()


                    }

                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            8 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }

                    replaceFragment(ContentPage())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )

                }
            }


            9 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }
                    replaceFragment(TermsOfUse())

                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )

                }
            }

            10 -> {


                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {
                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }

                    replaceFragment(ServicePartnersFragment())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )

                }


            }


            11 -> {


                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {
                    binding.apply {
                        spinnerLang.visibility = View.GONE
                        homeMain.visibility = View.GONE
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        drawerLayout.closeDrawer(Gravity.START)
                    }

                    replaceFragment(Setting())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )

                }


            }

            else -> {
            }
        }
    }


    private fun replaceFragment(fragment: Fragment?) {

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.container_framelayout, fragment!!)
        fragmentTransaction!!.commit()
    }


    private fun getCurrentDate() {
        val today = Date()
        val format = SimpleDateFormat("dd MMM, hh:mm a")
        val formatviewRide = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")



        dateToStr = format.format(today)!!.replace("am", "AM").replace("pm", "PM")


        setDate = formatviewRide.format(today).toString()

        formaredDate = formatviewRide.format(today).toString()


        if (extras != null) {
            if (spnrtime == 1) {
                binding.tvRideScheduled.text = tvDateandTime
            } else {
                binding.tvRideScheduled.text = dateToStr

            }

        } else {
            binding.tvRideScheduled.text = dateToStr

        }


    }






    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {


        myYear = year
        myday = dayOfMonth
        myMonth = month + 1
        val c = Calendar.getInstance()
        if (binding.spinnerTime.selectedItem.toString().equals(getString(R.string.str_later), ignoreCase = true)) {
            c.add(Calendar.MINUTE, 16)
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

        var dateTime =
            myday.toString() + "-" + myMonth + "-" + myYear + " " + myHour + ":" + myMinute
        val selectedDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateTime)
        val minValidDateTime = SimpleDateFormat("dd MM yyyy HH:mm:ss").parse(minTimeToShedule())

        if (selectedDateTime.before(minValidDateTime)) {

            binding.tvRideScheduled.text =
                AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM")
                    .replace("pm", "PM")

            Toast.makeText(
                this,
                getString(R.string.err_schedule_min_time),
                Toast.LENGTH_LONG
            ).show()

            return

        } else {
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
            binding.tvRideScheduled.text = "$myday $dmonth, $strhr:$strMinute $AMorPM"


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







    override fun onDestroy() {
        try {
            EventBus.getDefault().unregister(this)
        } catch (ex: Exception) {

        }

        super.onDestroy()
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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapAndLocationReady()
    }


    private fun isValid(): Boolean {
        binding.apply {
            if (binding.spinnerTime.selectedItem.toString().equals(getString(R.string.str_later), ignoreCase = true)) {
                var dateTime: String?
                if (year > 0) {

                    dateTime =
                        year.toString() + " " + tvRideScheduled.text.toString()
                            .replace("AM", "am")
                            .replace("PM", "pm")
                } else {
                    dateTime = Calendar.getInstance().get(Calendar.YEAR)
                        .toString() + " " + tvRideScheduled.text.toString().replace("AM", "am")
                        .replace("PM", "pm")
                }

                val selectedDateTime = SimpleDateFormat("yyyy dd MMM, hh:mm a").parse(dateTime)
                val minValidDateTime =
                    SimpleDateFormat("dd MM yyyy HH:mm:ss").parse(minTimeToShedule())

                if (tvRideScheduled.text.toString()
                        .equals(
                            AppUtils.convertDateGMTToLocal(minTimeToShedule()),
                            ignoreCase = true
                        )
                ) {
                    return true
                } else if (selectedDateTime.before(minValidDateTime)) {

                    tvRideScheduled.text =
                        AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM")
                            .replace("pm", "PM")

                    Toast.makeText(
                        context,
                        getString(R.string.err_schedule_min_time),
                        Toast.LENGTH_LONG
                    ).show()

                    return false

                } else {
                    return true
                }
            }

            return true
        }


    }

    private fun btnCompare() {

        sharedpreferences!!.edit()
            .putString("SourceAddress", binding.tvMyCurrentLocation.text.toString())
            .commit()
        sharedpreferences!!.edit()
            .putString("DestinationAddress", binding.tvMyDropUpLocation.text.toString()).commit()


        if (spnrtime == 1) {
            formaredDate = formaredDateLater
        } else {
            formaredDate = setDate
        }

        PortAir =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT)

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



            if (estDistanceInMeter > 499) {


                if (PortAir.equals("AIRPORT", ignoreCase = true)) {
                    airportYesOrNO = "Yes"
                } else {
                    airportYesOrNO = "NO"
                }

                replacedistance = estDistance!!.replace(" km", "")





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


                iCompareRidePresenter?.getCompareRideLocalNew(
                    token,
                    replacedistance,
                    estTime,
                    legDuration,
                    Constants.TYPE_LOCAL,
                    cityID,
                    sourcePlaceID,
                    DestinationPlaceID,
                    replacebags,
                    Constants.ONE_WAY_FLAG,
                    formaredDate,
                    SourceLat,
                    SourceLong,
                    DestinationLat,
                    DestinationLong
                )


            } else {
                Toast.makeText(
                    this,
                    getString(R.string.err_pick_drop_location_same),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            if (estDistanceInMeter > 499) {


                if (PortAir.equals("AIRPORT", ignoreCase = true)) {
                    airportYesOrNO = "Yes"
                } else {
                    airportYesOrNO = "NO"
                }
                replacedistance = estDistance!!.replace(" km", "")




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
                        ).await()
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
                        ).await()
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
                        ).await()
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


                iCompareRidePresenter?.getCompareRideLocalNew(
                    token,
                    replacedistance,
                    estTime,
                    legDuration,
                    Constants.TYPE_LOCAL,
                    cityID,
                    sourcePlaceID,
                    DestinationPlaceID,
                    replacebags,
                    Constants.ONE_WAY_FLAG,
                    formaredDate,
                    SourceLat,
                    SourceLong,
                    DestinationLat,
                    DestinationLong
                )


            } else {
                Toast.makeText(
                    this,
                    getString(R.string.err_pick_drop_location_same),
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


                legDuration = duration.getString("value")

                estTime = duration.getString("text")
                estDistance = distance.getString("value")
                estDistanceInMeter = distance.getString("value").toInt()
                estDistance =
                    DecimalFormat("####.#").format((estDistance!!.toDouble() / 1000)) + " km"

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
                    binding.tvEstDistance.text =
                        getString(R.string.str_est_distance) + " $estDistance"
                    binding.tvEstTime.text = getString(R.string.str_est_time) + " $estTime"
                    if (!estDistance!!.isEmpty()) {
                        binding.btnCompareRide.visibility = View.VISIBLE
                    }

                    lineOptions.addAll(points)
                    lineOptions.width(15f)
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
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_no_route_found),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_no_route_found),
                    Toast.LENGTH_LONG
                )
                    .show()
            }


        }
    }






    override fun onNewCompareRideLocalSuccess(info: InterCityCompareRideModel?) {
        val intent = Intent(applicationContext, LocalCompareRideActivity::class.java)
        intent.putExtra("SourceLat", SourceLat)
        intent.putExtra("SourceLong", SourceLong)
        intent.putExtra("DestinationLat", DestinationLat)
        intent.putExtra("DestinationLong", DestinationLong)
        intent.putExtra("Distance", estDistance)
        intent.putExtra("CITY_ID", cityID)
        intent.putExtra("CITY_NAME", city_Name)
        intent.putExtra("EstTime", estTime)
        intent.putExtra("Luggage", spinnerLuggagetxt)
        intent.putExtra("TimeSpinner", spinnertxt)
        intent.putExtra("Airport", keyAirport)
        intent.putExtra("SourceAddress", binding.tvMyCurrentLocation.text.toString())
        intent.putExtra("DestinationAddress", binding.tvMyDropUpLocation.text.toString())
        intent.putExtra("currentDate", binding.tvRideScheduled.text.toString())
        intent.putExtra("currentFormattedDate", formaredDate)
        intent.putExtra("currentPlaceId", CurrentPlaceID)
        intent.putExtra("time_in_seconds", legDuration)
        intent.putExtra("schedule_type", scheduleType)
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

    private fun getLocationReady() {

        if (currentLatitude == 0.0) {
            Toast.makeText(this, "Unable to find CURRENT LOCATION", Toast.LENGTH_LONG).show()
        } else {

            callOnLocation = "second"

            Log.d("mMarkerPointsSize", "$SourceLat       $SourceLong")
            Log.d("mMarkerPointsSizeDestin", "$DestinationLat    $DestinationLong")



            if (extras != null) {

                drawRouteOnAddressSelection()


            } else {
                streetAddress = getAddressFromLocation()

                if (SourceLat!!.isEmpty()) {
                    //ILOMADEV
                    SourceLat = currentLatitude.toString()
                    SourceLong = currentLongitude.toString()

                    sharedpreferences!!.edit()
                        .putString("SourceLat", currentLatitude.toString())
                        .commit()
                    sharedpreferences!!.edit()
                        .putString("SourceLong", currentLongitude.toString())
                        .commit()
                }

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

    private fun getAddressFromLocation(): String? {
        var address: String = ""
        val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1)

            if (addresses != null && addresses!!.size > 0) {
                val obj = addresses[0]
                address = obj.getAddressLine(0)
                var countryName = obj?.countryName
                if (obj?.countryName != null && obj.countryName.equals(
                        "United States",
                        ignoreCase = true
                    )
                ) {
                    obj.countryName = "USA"
                }
                if (!countryName.equals("")) {
                    address =
                        address.replace(", $countryName", "").replace("- $countryName", "")
                }

                if (obj != null && obj.postalCode != null && !obj.postalCode.equals("")) {
                    address = address.replace(" " + obj.postalCode, "")
                }

            } else {
                address = ""
            }
        } catch (e: IOException) {
            Toast.makeText(
                this,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }

        return address
    }

    override fun onBackPressed() {
        if (doubleBackPressed!!) {
            finishAffinity()
        } else {
            doubleBackPressed = true
            Toast.makeText(
                this,
                getString(R.string.msg_back_pressed),
                Toast.LENGTH_SHORT
            ).show()
        }
        Handler().postDelayed({ doubleBackPressed = false }, 3000)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val states: LocationSettingsStates = LocationSettingsStates.fromIntent(data)
        when (requestCode) {
            1000 ->
                if (resultCode == Activity.RESULT_OK) {
                    initLocationUpdates()
                    mapAndLocationReady()
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    startActivity(
                        Intent(
                            this,
                            PermissionActivity::class.java
                        ).putExtra("isFromSplash", true)
                    )
                    finish()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Constants.SHOULD_RELOAD) {
            Constants.SHOULD_RELOAD = false
            getActiveRidesAPI()
        }
        setup()
        generalSettingAPI()
        if (!CommonAppPermission.hasLocationPermission(this) || !ProjectUtilities.isGPSEnabled(this)) {
            startActivity(
                Intent(this, PermissionActivity::class.java).putExtra(
                    "isFromSplash",
                    true
                )
            )
            finish()
        }
    }

    private fun generalSettingAPI() {
        var url = BuildConfig.API_URL + Constants.API_GENERAL_SETTINGS

        var params: JSONObject = JSONObject()
        params.put("token", token)
        APIManager.getInstance(this).getAPI(
            url,
            params,
            GeneralSettingModel::class.java,
            this,
            object : APIManager.APIManagerInterface {
                override fun onSuccess(resultObj: Any?, jsonObject: JSONObject) {
                    var model: GeneralSettingModel = resultObj as GeneralSettingModel
                    preferencesManager?.setGeneralSettingModel(model)
                    SessionManager.getInstance(this@HomeActivity).setGeneralSettingModel(model)
                }

                override fun onError(error: String?) {
                    Toast.makeText(this@HomeActivity, error, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getActiveRidesAPI() {
        var url = BuildConfig.API_URL + Constants.API_GET_ACTIVE_RIDES

        var params: JSONObject = JSONObject()
        params.put("token", token)
        APIManager.getInstance(this).getAPI(
            url,
            params,
            GetRideResponsePOJO::class.java,
            this,
            object : APIManager.APIManagerInterface {
                override fun onSuccess(resultObj: Any?, jsonObject: JSONObject) {
                    var model: GetRideResponsePOJO = resultObj as GetRideResponsePOJO
                    if (!model.data.isNullOrEmpty()) {
                        rideModel = model.data!![0]
                    } else {
                        rideModel = null
                    }

                    setData()
                }

                override fun onError(error: String?) {
                    Toast.makeText(this@HomeActivity, error, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun callLogoutAPI() {
        deviceID =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_DEVICEID)
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)


        val progressDialogLogout = ProgressDialog(this@HomeActivity)
        progressDialogLogout.setCancelable(false) // set cancelable to false
        progressDialogLogout.setMessage(getString(R.string.str_please_wait)) // set message
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
                            val intent =
                                Intent(this@HomeActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                    } else if (response.code() == 401) {
                        preferencesManager?.clear()
                        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else if (response.code() == 422) {
                        val gson = GsonBuilder().create()
                        var pojo: ValidationResponse? = ValidationResponse()
                        try {
                            pojo = gson.fromJson(
                                response.errorBody()!!.string(),
                                ValidationResponse::class.java
                            )
                            Toast.makeText(
                                this@HomeActivity,
                                pojo.errors!!.get(0).message,
                                Toast.LENGTH_LONG
                            ).show()


                        } catch (exception: IOException) {
                        }

                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            getString(R.string.err_internal_server_error),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(
                    call: Call<ContactUsResponsePojo?>, t: Throwable
                ) {
                    progressDialogLogout.dismiss()
                    Toast.makeText(
                        this@HomeActivity,
                        t.stackTrace.toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    Log.d("response", t.stackTrace.toString())
                }
            })
    }


    /**
     * BROADCAST RECEIVER FOR UNREAD MESSAGE INDICATION
     */
    private fun setup() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mCountReceiver,
            IntentFilter("ride_status_changed")
        )
    }

    private val mCountReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == "ride_status_changed") {
                getActiveRidesAPI()
                val newIntent = Intent("refresh_ride_list")
                LocalBroadcastManager.getInstance(context).sendBroadcast(newIntent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCountReceiver)
    }


}
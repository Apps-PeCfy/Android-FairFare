package com.fairfareindia.ui.home

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
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.CompareRideActivity
import com.fairfareindia.ui.compareride.CompareRideImplementer
import com.fairfareindia.ui.compareride.ICompareRidePresenter
import com.fairfareindia.ui.compareride.ICompareRideView
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.drawer.adapter.DrawerAdapter
import com.fairfareindia.ui.drawer.contactus.ContactUs
import com.fairfareindia.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.fairfareindia.ui.drawer.covid19.Covid
import com.fairfareindia.ui.drawer.faq.FAQ
import com.fairfareindia.ui.drawer.myaccount.MyAccountFragment
import com.fairfareindia.ui.drawer.mycomplaints.MyComplaints
import com.fairfareindia.ui.drawer.mydisput.MyDisput
import com.fairfareindia.ui.drawer.myrides.MyRides
import com.fairfareindia.ui.drawer.pojo.DrawerPojo
import com.fairfareindia.ui.drawer.privacypolicy.ContentPage
import com.fairfareindia.ui.drawer.privacypolicy.TermsOfUse
import com.fairfareindia.ui.drawer.ratecard.RateCard
import com.fairfareindia.ui.drawer.setting.Setting
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.home.pojo.PickUpLocationModel
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.utils.*
import com.fairfareindia.utils.ProjectUtilities.showProgressDialog
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
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity(), OnMapReadyCallback, OnDateSetListener,
    OnTimeSetListener, OnItemSelectedListener, AdapterView.OnItemClickListener, ICompareRideView,
    LocationListener {
    protected var locationManager: LocationManager? = null

    protected var myLocationManager: MyLocationManager? = MyLocationManager(this)

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

    var doubleBackPressed: Boolean? = false
    private var mToastToShow: Toast? = null


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
    var addressType: String? = null


    var stDay: String? = null
    var strMonth: String? = null
    var strsecond: String? = null
    var strhr: String? = null
    var strMinute: String? = null
    var callOnLocation: String? = null
    var actionNotify: String? = null

    var loc: Location? = null

    var progressDialogstart: ProgressDialog? = null

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ButterKnife.bind(this)
        setStatusBarGradiant(this)

        // generateSSHKey(this)
        progressDialogstart = ProgressDialog(this@HomeActivity)
        progressDialogstart!!.setCancelable(false) // set cancelable to false
        progressDialogstart!!.setMessage("Please Wait") // set message
        progressDialogstart!!.show() // show progress dialog


        spinnerLang!!.visibility = View.VISIBLE

        if (Constants.IS_OLD_PICK_UP_CODE) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
        } else {
            // ILOMADEV :- New method for location update
            initLocationUpdates()
        }


        //  appSignatureHelper = AppSignatureHelper(this)
        //  appSignatureHelper!!.getAppSignatures()


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
            formaredDateLater = extras!!.getString("formaredDateLater")
            actionNotify = extras!!.getString("notifyAction")

        } else {
            spnrbag = 0
            spnrtime = 0
        }

        if (spnrtime == 0) {
            rlRideScheduled!!.visibility = View.GONE
        } else {
            rlRideScheduled!!.visibility = View.VISIBLE
        }




        hideshow = "show"
        getcurrentDate()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val NowLater: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, timeSpinner)
        NowLater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_time!!.adapter = NowLater
        spinner_time!!.setSelection(spnrtime)
        spinner_time!!.onItemSelectedListener = this


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, luggageSpinner)
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
        } else if (action == "MyRides") {
            spinnerLang!!.visibility = View.GONE
            setFragment(MyRides())
        }



        SourceLat = sharedpreferences!!.getString(
            "SourceLat", "" +
                    ""
        )
        SourceLong = sharedpreferences!!.getString("SourceLong", "")
        DestinationLat = sharedpreferences!!.getString("DestinationLat", "")
        DestinationLong = sharedpreferences!!.getString("DestinationLong", "")


        if (SourceLat!!.isNotEmpty() && DestinationLat!!.isNotEmpty()) {
            progressDialogstart!!.dismiss()
        }

        EventBus.getDefault().register(this)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSSHKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.getEncoder().encode(md.digest()))
                Log.d("AppLog", "key:$hashKey=")
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
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
                            /*   cityPojoList = preferencesManager!!.getCityList()
                               if (cityPojoList != null && cityPojoList.size > 0) {
                                   setCitySpinner()


                               } else {
                                   getCity()
                               }*/

                            progressDialogstart!!.dismiss()
                            mainRelativeLayout!!.visibility = View.VISIBLE


                        }

                    }

                }
            }

        })
    }

    private fun mapAndLocationReady() {

        if (ProjectUtilities.checkPermission(this@HomeActivity)) {
            if (callOnLocation.equals("first") && mMap != null && currentLatitude != null && currentLatitude != 0.0) {
                getLocationReady()
            }
        } else {
            ProjectUtilities.showToast(this@HomeActivity, getString(R.string.internet_error))
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
//            SourceLat = sharedpreferences!!.getString("SourceLat", "")
//            SourceLong = sharedpreferences!!.getString("SourceLong", "")
                SourceLat = model.latitude.toString()
                SourceLong = model.longitude.toString()
                myCurrentLocation!!.text = model.address

            } else {
                //  DestinationLat = sharedpreferences!!.getString("DestinationLat", "")
                //  DestinationLong = sharedpreferences!!.getString("DestinationLong", "")
                DestinationLat = model.latitude.toString()
                DestinationLong = model.longitude.toString()
                myDropUpLocation!!.text = model.address

                /*   if(model.addressType!!.length==0){
                       myDropUpLocation!!.text = model.address

                   }else{

                       myDropUpLocation!!.text = model.addressType

                   }*/
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
            reestimateDateandTime!!.visibility = View.VISIBLE
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
                    myCurrentLocation!!.text = onePlusEmptyCity




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
        spinnerLang!!.adapter = SpnLang

        if (extras == null) {

            if (city.equals("thane", ignoreCase = true)) {
                city = "Mumbai"
            }

            if (cityspinner.contains(city)) {

                for (i in cityspinner!!.indices) {
                    if (city.equals(cityspinner[i])) {
                        spinnerLang!!.setSelection(i)
                    }

                }
            } else {
                cityspinner.add(0, "Choose City")


                val toastDurationInMilliSeconds = 10000
                mToastToShow = Toast.makeText(
                    this@HomeActivity,
                    "Sorry, we don’t serve locations within " + city + " & its Subarban areas yet. We will notify you as soon as we launch our services. Kindly choose other city from the drop down where our services are active.",
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
                        spinnerLang!!.setSelection(i)
                    }

                }
            } else {

                cityspinner.add(0, "Choose City")


                val toastDurationInMilliSeconds = 10000
                mToastToShow = Toast.makeText(
                    this@HomeActivity,
                    "Sorry, we don’t serve locations within " + city + " & its Subarban areas yet. We will notify you as soon as we launch our services. Kindly choose other city from the drop down where our services are active.",
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
        spinnerLang!!.setOnItemSelectedListener(this@HomeActivity)
    }


    private fun setFragment(myRides: Fragment) {


        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.add(R.id.container_framelayout, myRides)
        fragmentTransaction!!.commit()
    }


    private fun setListData() {

        tvEmailAddress!!.text =
            "Available Reward Points " + preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_USER_REWARD)

        tvUserName!!.text =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_NAME)

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

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)
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

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)
                    replaceFragment(MyRides())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            2 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)
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

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)

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

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)
                    replaceFragment(RateCard())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }
            }


            5 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)

                    replaceFragment(FAQ())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )
                }

            }


            6 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    if (preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_EMAIL)!!
                            .isNotEmpty()
                    ) {
                        spinnerLang!!.visibility = View.GONE
                        homeMain!!.visibility = View.GONE
                        mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                        mDrawerLayout!!.closeDrawer(Gravity.START)

                        replaceFragment(ContactUs())

                    } else {

                        val alertDialog =
                            AlertDialog.Builder(this@HomeActivity, R.style.alertDialog)
                        alertDialog.setTitle("FairFareIndia")
                        alertDialog.setMessage("Email id is required. Please edit it in My Accounts.")
                        alertDialog.setCancelable(false)
                        alertDialog.setPositiveButton("NO") { dialog, which ->
                            dialog.cancel()
                        }
                        alertDialog.setNegativeButton("YES") { dialog, which ->
                            spinnerLang!!.visibility = View.GONE
                            homeMain!!.visibility = View.GONE
                            mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                            mDrawerLayout!!.closeDrawer(Gravity.START)

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


            7 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)

                    replaceFragment(ContentPage())
                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )

                }
            }


            8 -> {

                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)
                    replaceFragment(TermsOfUse())

                } else {
                    ProjectUtilities.showToast(
                        this@HomeActivity,
                        getString(R.string.internet_error)
                    )

                }
            }


            9 -> {


                if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {
                    spinnerLang!!.visibility = View.GONE
                    homeMain!!.visibility = View.GONE
                    mDrawerLayout!!.closeDrawer(Gravity.LEFT)
                    mDrawerLayout!!.closeDrawer(Gravity.START)

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


                tvEmailAddress!!.text =
                    "Available Reward Points " + preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_USER_REWARD)

                tvUserName!!.text =
                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_NAME)

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

    @SuppressLint("WrongConstant")
    @OnClick(R.id.logoLayout)
    fun myAccount() {


        if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {


            spinnerLang!!.visibility = View.GONE
            homeMain!!.visibility = View.GONE
            mDrawerLayout!!.closeDrawer(Gravity.LEFT)
            mDrawerLayout!!.closeDrawer(Gravity.START)

            replaceFragment(MyAccountFragment())
        } else {
            ProjectUtilities.showToast(
                this@HomeActivity,
                getString(R.string.internet_error)
            )
        }


    }

    @OnClick(R.id.btnLogout)
    fun logOut() {

        if (ProjectUtilities.checkInternetAvailable(this@HomeActivity)) {

            deviceID =
                preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_DEVICEID)
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
                                val intent =
                                    Intent(this@HomeActivity, LoginActivity::class.java)
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
                                "Internal Server Error",
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

        } else {
            ProjectUtilities.showToast(
                this@HomeActivity,
                getString(R.string.internet_error)
            )
        }

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

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {


        myYear = year
        myday = dayOfMonth
        myMonth = month + 1
        val c = Calendar.getInstance()
        if (spinner_time?.selectedItem.toString().equals("Later", ignoreCase = true)) {
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

            tv_RideScheduled?.text =
                AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM")
                    .replace("pm", "PM")

            Toast.makeText(
                this,
                "Scheduled time should be greater than 15 minutes from current time.",
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
                intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


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
                intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


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

    override fun onDestroy() {
        // sharedpreferences!!.edit().clear().commit()

        //ILOMADEV
        try {
            EventBus.getDefault().unregister(this)
        } catch (ex: Exception) {

        }

        super.onDestroy()
    }

    @OnClick(R.id.tv_myDropUpLocation)
    fun myDropUpLocation() {

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
                intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


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
                intent.putExtra("spinnerTimeDate", tv_RideScheduled!!.text)


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
        //googleMap.clear()
        mMap = googleMap
        mapAndLocationReady()
        // mMap!!.getUiSettings().setAllGesturesEnabled(false)
        //  mMap!!.getUiSettings().setScrollGesturesEnabled(false)


    }


    @OnClick(R.id.btnCompareRide)
    fun btnCompareClick() {
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

    private fun isValid(): Boolean {
        if (spinner_time!!.selectedItem.toString().equals("Later", ignoreCase = true)) {
            var dateTime: String?
            if (year > 0) {

                dateTime =
                    year.toString() + " " + tv_RideScheduled?.text.toString()
                        .replace("AM", "am")
                        .replace("PM", "pm")
            } else {
                dateTime = Calendar.getInstance().get(Calendar.YEAR)
                    .toString() + " " + tv_RideScheduled?.text.toString().replace("AM", "am")
                    .replace("PM", "pm")
            }

            val selectedDateTime = SimpleDateFormat("yyyy dd MMM, hh:mm a").parse(dateTime)
            val minValidDateTime =
                SimpleDateFormat("dd MM yyyy HH:mm:ss").parse(minTimeToShedule())

            if (tv_RideScheduled?.text.toString()
                    .equals(
                        AppUtils.convertDateGMTToLocal(minTimeToShedule()),
                        ignoreCase = true
                    )
            ) {
                return true
            } else if (selectedDateTime.before(minValidDateTime)) {

                tv_RideScheduled?.text =
                    AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM")
                        .replace("pm", "PM")

                Toast.makeText(
                    this,
                    "Scheduled time should be greater than 15 minutes from current time.",
                    Toast.LENGTH_LONG
                ).show()

                return false

            } else {
                return true
            }
        }
        return true
    }

    fun btnCompare() {


        sharedpreferences!!.edit()
            .putString("SourceAddress", myCurrentLocation!!.text.toString())
            .commit()
        sharedpreferences!!.edit()
            .putString("DestinationAddress", myDropUpLocation!!.text.toString()).commit()





        if (spnrtime == 1) {
            formaredDate = formaredDateLater
        } else {
            formaredDate = setDate
        }

        //ILOMADEV :- Take value before comparing

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
//                PortAir =
//                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT)
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
                    CurrentPlaceID!!, legDuration
                )


            } else {
                Toast.makeText(
                    this,
                    "Pick-UP and Drop-Off Location should not be same.",
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
//                PortAir =
//                    preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT)
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



                iCompareRidePresenter!!.getCompareRideData(
                    token,
                    replacedistance,
                    cityID,
                    sourcePlaceID,
                    DestinationPlaceID,
                    replacebags,
                    airportYesOrNO,
                    formaredDate,
                    CurrentPlaceID!!, legDuration
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


                legDuration = duration.getString("value")

                estTime = duration.getString("text")
                estDistance = distance.getString("value")
                estDistanceInMeter = distance.getString("value").toInt()
                estDistance =
                    DecimalFormat("####.#").format((estDistance!!.toDouble() / 1000)) + " km"


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
            city_Name = cityPojoList!!.get(position).name
            if (city_Name.equals("Choose City")) {

            } else {

                if (cityspinner.contains("Choose City")) {
                    if (position > 0) {
                        cityID = cityPojoList!!.get(position - 1).id.toString()
                        city_Name = cityPojoList!!.get(position - 1).name
                        preferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_CITY_ID,
                            cityID
                        )
                    } else {

                        cityID = ""
                        preferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_CITY_ID,
                            cityPojoList!!.get(position).id.toString()
                        )
                    }


                } else {
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
                dateToStr =
                    AppUtils.convertDateGMTToLocal(minTimeToShedule())!!.replace("am", "AM")
                        .replace("pm", "PM")


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
        // intent.putExtra("Airport", extras!!.getString("keyAirport"))

        // ILOMADEV

        intent.putExtra("Airport", keyAirport)
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
            if (currentLatitude != 0.0 && currentLatitude != null) {

                mapAndLocationReady()
                cityPojoList = preferencesManager!!.getCityList()
                if (cityPojoList != null && cityPojoList.size > 0) {
                    setCitySpinner()


                } else {
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
//            Toast.makeText(this, "On Start Location Ready", Toast.LENGTH_LONG).show()


            callOnLocation = "second"

            Log.d("mMarkerPointsSize", "$SourceLat       $SourceLong")
            Log.d("mMarkerPointsSizeDestin", "$DestinationLat    $DestinationLong")



            if (extras != null) {

                //ILOMADEV :- 30 Jan 2021 :- Created Method for this code

                drawRouteOnAddressSelection()


            } else {
                streetAddress = getAddressFromLocation()

                /* if (streetAddress!!.isEmpty() && flgstreetaddress) {

                     Toast.makeText(this@HomeActivity,""+currentLatitude+"    "+currentLongitude,Toast.LENGTH_LONG).show()

                     streetAddress = getAddressFromLocation()
                     flgstreetaddress = false

                 }
 */
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

                // sharedpreferences!!.edit().putString("fromLocation", streetAddress).commit()
                // myCurrentLocation!!.text = streetAddress

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

                //   city = obj.subAdminArea
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
                "Press once again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
        Handler().postDelayed({ doubleBackPressed = false }, 3000)
    }


}
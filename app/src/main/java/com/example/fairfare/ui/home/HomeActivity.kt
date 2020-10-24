package com.example.fairfare.ui.home

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.location.Location
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
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.fairfare.R
import com.example.fairfare.base.BaseLocationClass
import com.example.fairfare.ui.compareride.CompareRideActivity
import com.example.fairfare.ui.placeDirection.DirectionsJSONParser
import com.example.fairfare.ui.service.GPSTracker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class HomeActivity : BaseLocationClass(), OnMapReadyCallback, OnDateSetListener,
    OnTimeSetListener,OnItemSelectedListener {
    var currentLatitude = 0.0
    var currentLongitude = 0.0
    private var mPolyline: Polyline? = null
    var sourecemarker: Marker? = null
    var destmarker: Marker? = null
    var spinnerLuggagetxt: String? = null
    var placesClient: PlacesClient? = null



    var timeSpinner = arrayOf<String?>("Now", "Later")
    var luggageSpinner = arrayOf<String?>(
        "1 Bag", "2 Bags", "3 Bags", "4 Bags", "5 Bags", "6 Bags", "7 Bags", "8 Bags",
        "9 Bags", "10 Bags"
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
    @BindView(R.id.tvhideShow)
    var tvhideShow: RelativeLayout? = null


    @JvmField
    @BindView(R.id.tv_myCurrentLocation)
    var myCurrentLocation: TextView? = null

    @JvmField
    @BindView(R.id.tvEstTime)
    var tvEstTime: TextView? = null

    @JvmField
    @BindView(R.id.tvEstDistance)
    var tvEstDistance: TextView? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private val fragmentManager: FragmentManager? = null
    private val fragmentTransaction: FragmentTransaction? = null
    private var mMap: GoogleMap? = null
    var extras: Bundle? = null
    var streetAddress: String? = null
    var sharedpreferences: SharedPreferences? = null
    var SourceLat: String? = null
    var SourceLong: String? = null
    var DestinationLat: String? = null
    var DestinationLong: String? = null
    var spinnertxt: String?=null
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0
    var AMorPM = 0
    var myday = 0
    var myMonth = 0
    var myYear = 0
    var myHour = 0
    var myMinute = 0
    var calendar: Calendar? = null
    var estTime: String? = null
    var estDistance: String? = null
    var hideshow: String? = null
    var spnrbag = 0
    var spnrtime = 0


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)

        Places.initialize(this, resources.getString(R.string.google_maps_key))
        placesClient = Places.createClient(this@HomeActivity)


        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        val intent = intent
        extras = intent.extras
        if (extras != null) {
            spnrbag = extras!!.getInt("spnbg")
            spnrtime = extras!!.getInt("spnTime")
        } else {
            spnrbag = 0
            spnrtime = 0
        }

        if (spnrtime == 0) {
            rlRideScheduled!!.visibility = View.GONE
        } else {
            rlRideScheduled!!.visibility = View.VISIBLE
        }


        val gps = GPSTracker(this@HomeActivity)
        if (gps.canGetLocation()) {
            currentLatitude = gps.latitude
            currentLongitude = gps.longitude
        } else {
            gps.showSettingsAlert()
        }


        hideshow = "show"
        getcurrentDate()
        initView()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        val NowLater: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, timeSpinner)
        NowLater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_time?.adapter = NowLater
        spinner_time!!.adapter = NowLater
        spinner_time!!.setSelection(spnrtime)
        spinner_time!!.onItemSelectedListener = this


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, luggageSpinner)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_Luggage?.adapter = spinnerLuggage
        spinner_Luggage?.setSelection(spnrbag)
        spinner_Luggage!!.setOnItemSelectedListener(this)

            }

    override fun onLocationChanged(location: Location) {
        super.onLocationChanged(location)
    }

    private fun getcurrentDate() {
        val today = Date()
        val format = SimpleDateFormat("dd MMM, hh:mm a")
        val dateToStr = format.format(today)
        tv_RideScheduled?.text = dateToStr
    }

    private fun initView() {

        toolbar?.title = ""
        toolbar?.setTitleTextColor(resources.getColor(android.R.color.white))
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
                mDrawerLayout!!.openDrawer(Gravity.LEFT)
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


    @OnClick(R.id.tvhideShow)
    fun  hideshow(){
        if(hideshow.equals("show")){
            hideshow = "hide"
            llhideview?.visibility = View.GONE
        }else{
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

        } else {
            intent.putExtra("Toolbar_Title", "Pick-Up")
            intent.putExtra("currentLatitude", SourceLat!!.toDouble())
            intent.putExtra("currentLongitude", SourceLong!!.toDouble())
            intent.putExtra("spinnerbag", spnrbag)
            intent.putExtra("spinnerTime", spnrtime)

        }
        startActivity(intent)
    }

    override fun onDestroy() {
       // sharedpreferences!!.edit().clear().commit()
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

        } else {
            intent.putExtra("Toolbar_Title", "Drop-off")
            intent.putExtra("currentLatitude", DestinationLat!!.toDouble())
            intent.putExtra("currentLongitude", DestinationLong!!.toDouble())
            intent.putExtra("spinnerbag", spnrbag)
            intent.putExtra("spinnerTime", spnrtime)

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
        //super.onBackPressed();
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (currentLatitude == 0.0) {
            Toast.makeText(this, "Unable to find CURRENT LOCATION", Toast.LENGTH_LONG).show()
        } else {
            SourceLat = sharedpreferences!!.getString("SourceLat", "")
            SourceLong = sharedpreferences!!.getString("SourceLong", "")
            DestinationLat = sharedpreferences!!.getString("DestinationLat", "")
            DestinationLong = sharedpreferences!!.getString("DestinationLong", "")
            Log.d("mMarkerPointsSize", "$SourceLat       $SourceLong")
            Log.d("mMarkerPointsSizeDestin", "$DestinationLat    $DestinationLong")





            googleMap.setOnMapClickListener { latLng ->
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                if (extras != null) {
                    var street: String? = null
                    if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
                        sharedpreferences!!.edit().putString("SourceLat", latLng.latitude.toString()).commit()
                        sharedpreferences!!.edit().putString("SourceLong", latLng.longitude.toString()).commit()
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
                            }
                        } catch (e: IOException) {
                        }
                        sharedpreferences!!.edit().putString("fromLocation",street).commit()

                        myCurrentLocation!!.text = street
                        sourecemarker!!.remove()
                        markerOptions.title(street)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                13.0f
                            )
                        )
                        sourecemarker = mMap!!.addMarker(
                            MarkerOptions().position(
                                    LatLng(
                                        latLng.latitude,
                                        latLng.longitude
                                    )
                                )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                        )
                    }
                    else
                    {
                        sharedpreferences!!.edit().putString("DestinationLat", latLng.latitude.toString()).commit()
                        sharedpreferences!!.edit().putString("DestinationLong", latLng.longitude.toString()).commit()
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

                        sharedpreferences!!.edit().putString("destiNationLocation",street).commit()

                        myDropUpLocation!!.text = street
                        destmarker!!.remove()
                        markerOptions.title(street)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                13.0f
                            )
                        )
                        destmarker = mMap!!.addMarker(
                            MarkerOptions().position(
                                    LatLng(
                                        latLng.latitude,
                                        latLng.longitude
                                    )
                                )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                        )
                    }
                }

                else {
                    var streetAddress: String? = null
                    sharedpreferences!!.edit().putString("SourceLat", latLng.latitude.toString()).commit()
                    sharedpreferences!!.edit().putString("SourceLong", latLng.longitude.toString()).commit()
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
                            streetAddress = strReturnedAddress.toString()
                        }
                    } catch (e: IOException) {
                    }
                    sharedpreferences!!.edit().putString("fromLocation",streetAddress).commit()
                    myCurrentLocation!!.text = streetAddress
                    sourecemarker!!.remove()
                    markerOptions.title(streetAddress)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f))
                    sourecemarker = mMap!!.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                latLng.latitude,
                                latLng.longitude
                            )
                        ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                    )
                }



                if (!SourceLat!!.isEmpty() && !DestinationLat!!.isEmpty()) {
                    drawRoute()
                }
            }


            if (extras != null) {
                if (!SourceLat!!.isEmpty()) {

                    val geocoder =
                        Geocoder(this@HomeActivity, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(SourceLat!!.toDouble(), SourceLong!!.toDouble(), 1)
                        streetAddress = if (addresses!!.size > 0 && addresses != null) {
                            addresses[0].getAddressLine(0)
                        } else { "" }
                    } catch (e: IOException) {
                    }

                    myCurrentLocation!!.text = sharedpreferences!!.getString("fromLocation", "")
                    mMap!!.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(SourceLat!!.toDouble(), SourceLong!!.toDouble()), 13.0f))
                    sourecemarker = mMap!!.addMarker(
                        MarkerOptions().position(LatLng(SourceLat!!.toDouble(), SourceLong!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)))
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
                        streetAddress = if (addresses!!.size > 0 && addresses != null) {
                            addresses[0].getAddressLine(0)
                        } else {
                            ""
                        }
                    } catch (e: IOException) {
                    }

                        myDropUpLocation!!.text = sharedpreferences!!.getString("destiNationLocation", "")


                    destmarker = mMap!!.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                DestinationLat!!.toDouble(),
                                DestinationLong!!.toDouble()
                            )
                        ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                    )
                }
                if (!SourceLat!!.isEmpty() && !DestinationLat!!.isEmpty()) {
                    reestimateDateandTime!!.visibility = View.VISIBLE
                    drawRoute()
                }
            } else {
                val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
                try {
                    val addresses =
                        geocoder.getFromLocation(
                            currentLatitude,
                            currentLongitude, 1
                        )
                    streetAddress = if (addresses!!.size > 0 && addresses != null) {
                        addresses[0].getAddressLine(0)
                    } else {
                        ""
                    }
                } catch (e: IOException) {
                }


               if (SourceLat!!.isEmpty()) {
                    sharedpreferences!!.edit().putString("SourceLat", currentLatitude.toString()).commit()
                    sharedpreferences!!.edit().putString("SourceLong", currentLongitude.toString()).commit()
                }

                sharedpreferences!!.edit().putString("fromLocation",streetAddress).commit()
                myCurrentLocation?.text = streetAddress
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLatitude, currentLongitude), 13.0f))
                sourecemarker = mMap!!.addMarker(MarkerOptions().position(LatLng(currentLatitude, currentLongitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                )
            }
        }
    }

    @OnClick(R.id.btnCompareRide)
    fun btnCompare() {
        if (estDistance!!.contains("km")) {
            val distance = tvEstDistance!!.text.toString()
            val estTine = tvEstTime!!.text.toString()
            val intent = Intent(applicationContext, CompareRideActivity::class.java)
            intent.putExtra("SourceLat", SourceLat)
            intent.putExtra("SourceLong", SourceLong)
            intent.putExtra("DestinationLat", DestinationLat)
            intent.putExtra("DestinationLong", DestinationLong)
            intent.putExtra("Distance", estDistance)
            intent.putExtra("EstTime", estTime)
            intent.putExtra("Liggage", spinnerLuggagetxt)
            intent.putExtra("TimeSpinner", spinnertxt)
            intent.putExtra("Airport", extras!!.getString("keyAirport"))
            intent.putExtra("SourceAddress", myCurrentLocation!!.text.toString())
            intent.putExtra("DestinationAddress", myDropUpLocation!!.text.toString())
            intent.putExtra("currentDate", tv_RideScheduled!!.text.toString())

            startActivity(intent)
        } else {
            Toast.makeText(this, "Pick-UP and Drop-Off Location should not be same.", Toast.LENGTH_LONG).show()
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

    override fun onDateSet(
        view: DatePicker,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        myYear = year
        myday = dayOfMonth
        myMonth = month + 1
        val c = Calendar.getInstance()
        hour = c[Calendar.HOUR]
        minute = c[Calendar.MINUTE]
        AMorPM = c[Calendar.AM_PM]
        val timePickerDialog = TimePickerDialog(
            this@HomeActivity,
            this@HomeActivity, hour, minute, DateFormat.is24HourFormat(this)
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
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
        tv_RideScheduled!!.text = "$myday $dmonth, $myHour:$myMinute $AMorPM"
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
                estDistance = distance.getString("text")
                estTime = duration.getString("text")
                Log.d(
                    "Distance",
                    distance.getString("text") + "   " + duration.getString("text")
                )


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

            // Traversing through all the routes
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
                if(!estDistance!!.isEmpty()) {
                    btnCompareRide!!.visibility = View.VISIBLE
                }

                lineOptions.addAll(points)
                lineOptions.width(8f)
                //  lineOptions.color(Color.GREEN);
                lineOptions.color(
                    this@HomeActivity.resources.getColor(R.color.gradientstartcolor)
                )
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline!!.remove()
                }
                mPolyline = mMap!!.addPolyline(lineOptions)
            } else Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!!.id == R.id.spinner_Luggage) {
            spnrbag = position
            spinnerLuggagetxt = spinner_Luggage!!.selectedItem.toString()
        } else {
            spnrtime = position
            spinnertxt = spinner_time!!.selectedItem.toString()
            if (spinnertxt == "Later") {
                rlRideScheduled!!.visibility = View.VISIBLE
            } else {
                rlRideScheduled!!.visibility = View.GONE
            }
        }

    }
}
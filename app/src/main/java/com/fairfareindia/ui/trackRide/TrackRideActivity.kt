package com.fairfareindia.ui.trackRide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.*
import android.graphics.Color
import android.hardware.GeomagneticField
import android.icu.math.BigDecimal
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.service.BackgroundLocationService
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.fairfareindia.ui.endrides.EndRidesActivity
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.ui.trackRide.NearByPlacesPOJO.NearByResponse
import com.fairfareindia.ui.trackRide.currentFare.CurrentFareeResponse
import com.fairfareindia.ui.trackRide.distMatrixPOJP.DistanceMatrixResponse
import com.fairfareindia.ui.trackRide.snaptoRoad.SnapTORoadResponse
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TrackRideActivity : BaseLocationClass(), OnMapReadyCallback, LocationListener {
    lateinit var info: ScheduleRideResponsePOJO
    protected var locationManager: LocationManager? = null
    var waypoints = ""

    //For Background Service
    private lateinit var mService: BackgroundLocationService
    private var mBound: Boolean = false

    var hideshow: String? = "show"
    var nightChargesFrom: String? = null
    var nightChargesTo: String? = null

    var sAddress: String? = null
    var dAddress: String? = null
    var trackBoard: String? = ""
    var mMap: GoogleMap? = null
    var sourecemarker: Marker? = null

    var today: Date? = null
    var different = 0.0
    var actulDis = 0.0

    var actualTravelDistance = ArrayList<Double>()
    var googlePathListForTolls = ArrayList<com.fairfareindia.ui.trackRide.NearByPlacesPOJO.Location>()
    var tollsJSONArrayFromTollGuru : JSONArray = JSONArray()
    var actualTravelDistanceForNightCharges = ArrayList<Double>()


    var locationChangelatitude = 0.0
    var locationChangelongitude = 0.0
    var mPolyline: Polyline? = null
    var updatedPolyline: Polyline? = null
    var mGreyPolyline: Polyline? = null
    var prevLatLng: LatLng? = null

    private var myMarker: Marker? = null
    private var isMapZoomed: Boolean? = false
    private var isWaiting: Boolean? = false
    private var lastCompassBering: Float? = null


    @JvmField
    @BindView(R.id.tvhideShow)
    var tvhideShow: RelativeLayout? = null

    @JvmField
    @BindView(R.id.rlhideview)
    var rlhideview: LinearLayout? = null

    @JvmField
    @BindView(R.id.tvEstDistance)
    var tvEstDistance: TextView? = null

    @JvmField
    @BindView(R.id.tvEstTime)
    var tvEstTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_myCurrentLocation)
    var tv_myCurrentLocation: TextView? = null


    @JvmField
    @BindView(R.id.tv_myDropUpLocation)
    var tv_myDropUpLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_carType)
    var tv_carType: TextView? = null

    @JvmField
    @BindView(R.id.tv_ShowTrackBoard)
    var tv_ShowTrackBoard: TextView? = null

    @JvmField
    @BindView(R.id.tv_refresh)
    var tv_refresh: TextView? = null

    @JvmField
    @BindView(R.id.tv_atm)
    var tv_atm: TextView? = null

    @JvmField
    @BindView(R.id.tv_hotel)
    var tv_hotel: TextView? = null

    @JvmField
    @BindView(R.id.museum)
    var museum: TextView? = null

    @JvmField
    @BindView(R.id.hospital)
    var hospital: TextView? = null

    @JvmField
    @BindView(R.id.tv_currentFare)
    var tv_currentFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_close)
    var tv_close: TextView? = null

    @JvmField
    @BindView(R.id.tv_estimatedDistance)
    var tv_estimatedDistance: TextView? = null

    @JvmField
    @BindView(R.id.tv_travelledDistance)
    var tv_travelledDistance: TextView? = null

    @JvmField
    @BindView(R.id.rlData)
    var rlData: RelativeLayout? = null

    @JvmField
    @BindView(R.id.llshowData)
    var llshowData: LinearLayout? = null


    @JvmField
    @BindView(R.id.progressBarTime)
    var progressBarTime: ProgressBar? = null

    @JvmField
    @BindView(R.id.progressBarDistance)
    var progressBarDistance: ProgressBar? = null

    @JvmField
    @BindView(R.id.btnEndRide)
    var btnEndRide: Button? = null

    @JvmField
    @BindView(R.id.tv_distance)
    var tv_distance: TextView? = null

    @JvmField
    @BindView(R.id.tv_travelTime)
    var tv_travelTime: TextView? = null


    @JvmField
    @BindView(R.id.tv_currentwaitTime)
    var tv_currentwaitTime: TextView? = null

    @JvmField
    @BindView(R.id.tvTravelTime)
    var tvTravelTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_currentSpeed)
    var tv_currentSpeed: TextView? = null

    @JvmField
    @BindView(R.id.ivAtm)
    var ivAtm: ImageView? = null

    @JvmField
    @BindView(R.id.ivMuseum)
    var ivMuseum: ImageView? = null

    @JvmField
    @BindView(R.id.ivHospital)
    var ivHospital: ImageView? = null

    @JvmField
    @BindView(R.id.ivHotel)
    var ivHotel: ImageView? = null

    @JvmField
    @BindView(R.id.llAtm)
    var llAtm: LinearLayout? = null

    @JvmField
    @BindView(R.id.llMuseum)
    var llMuseum: LinearLayout? = null

    @JvmField
    @BindView(R.id.llHospital)
    var llHospital: LinearLayout? = null

    @JvmField
    @BindView(R.id.llHotel)
    var llHotel: LinearLayout? = null


    @JvmField
    @BindView(R.id.toolbar_trackRide)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.iv_vehical)
    var iv_vehical: ImageView? = null
    var preferencesManager: PreferencesManager? = null
    var token: String? = null
    var estCurrentDistance: String? = null
    var estCurrentDistanceInKm: String? = null
    var estCurrentDuration: String? = null
    var estCurrentDurationInMin: String? = null
    var travelledDistance: Double? = null
    var distanceBetweenCurrent: Double? = null
    var kmphSpeed: Double? = null
    var cLocation: Location? = null
    var count: Int = 0
    var travellTime: Int = 0
    var timer = Timer()
    var sharedpreferences: SharedPreferences? = null


    var waitTime: Date? = null
    var arrWaitTime: ArrayList<HashMap<String, String>> = ArrayList()
    var waitLocation: String? = ""
    var waitLat: String? = ""
    var waitLong: String? = ""
    var waitAt: String? = ""
    var waitTimeForCurrentFare: String? = "0"
    var strDistCal: String? = "0"
    var actualTime: String? = "0"
    var actualDistance: String? = "0"
    var actualDistanceForNightCharg: String? = "0"


    var waitStartLocation: String? = ""
    var waitStartLocationNew: String? = ""
    var waitStartLat: String? = ""
    var waitStartLong: String? = ""
    var vehicleName: String? = ""

    var destinationLat:String=""
    var destinationLong:String=""
    var actualRemainingDistanceInMeter: Int = 0


    var passjObject: JSONObject? = null


    var City_ID: String? = null
    var sorceAddress: String? = null
    var destinationAddress: String? = null
    var streetAddress: String? = null
    var deststreetAddress: String? = null
    var isFirstTimeSetUpDone: Boolean = false

    var markerPoints: ArrayList<LatLng?>? = null
    var globalmarkerPoints: ArrayList<LatLng?>? = null
    var OriginM: LatLng? = null
    val handler = Handler()
    lateinit var destLocation: Location
    lateinit var startLocation: Location
    var geoField: GeomagneticField? = null
    private var cityPojoList: List<GetAllowCityResponse.CitiesItem> = ArrayList()


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_ride)
        init()
    }

    private fun init() {

        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        PreferencesManager.initializeInstance(this@TrackRideActivity)
        //     locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //     locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        //     locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)

        markerPoints = ArrayList()
        globalmarkerPoints = ArrayList()




        getcurrentDate()




        preferencesManager = PreferencesManager.instance

        cityPojoList = preferencesManager!!.getCityList()
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        sorceAddress = sharedpreferences!!.getString("SourceAddress", "")
        destinationAddress = sharedpreferences!!.getString("DestinationAddress", "")


        sAddress = intent.getStringExtra("SAddress")
        City_ID = intent.getStringExtra("City_ID")
        dAddress = intent.getStringExtra("DAddress")
        tv_carType!!.text = intent.getStringExtra("ImageName")
        vehicleName = intent.getStringExtra("VehicleName")
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)





        Glide.with(this@TrackRideActivity)
            .load(intent.getStringExtra("ImageUrl"))
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            ).into(iv_vehical!!)


        info = intent.getSerializableExtra("ResponsePOJOScheduleRide") as ScheduleRideResponsePOJO

        destLocation = Location("")
        destLocation.latitude = (info.ride!!.estimatedTrackRide!!.destinationPlaceLat)!!.toDouble()
        destLocation.longitude =
            (info.ride!!.estimatedTrackRide!!.destinationPlaceLong)!!.toDouble()

        startLocation = Location("")
        startLocation.latitude = (info.ride!!.estimatedTrackRide!!.originPlaceLat)!!.toDouble()
        startLocation.longitude = (info.ride!!.estimatedTrackRide!!.originPlaceLong)!!.toDouble()


        if ((intent.getStringExtra("MyRidesLat")) != null) {

            streetAddress = getAddressFromLatLng(
                (intent.getStringExtra("MyRidesLat"))!!.toDouble(),
                (intent.getStringExtra("MyRidesLong"))!!.toDouble()
            )
            deststreetAddress = getAddressFromLatLng(
                (intent.getStringExtra("MyRidesDLat"))!!.toDouble(),
                (intent.getStringExtra("MyRidesDLong"))!!.toDouble()
            )


            tv_myCurrentLocation!!.text = intent.getStringExtra("MyRidesoriginalAddress")
            tv_myDropUpLocation!!.text = intent.getStringExtra("MyRidesdestinationAddress")


        } else {
            tv_myDropUpLocation!!.text = dAddress
            tv_myCurrentLocation!!.text = sAddress

        }




        //   tv_estimatedDistance!!.text = info.ride!!.estimatedTrackRide!!.distance + " km"
        tv_travelTime!!.text = info.ride!!.estimatedTrackRide!!.duration


        /* tv_distance!!.text =
             "(Est.Distance:" + info.ride!!.estimatedTrackRide!!.distance + " km) / " +
                     "(Est.Time:" + info.ride!!.estimatedTrackRide!!.duration + ")"*/

        mToolbar!!.title = "Track Ride"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

        if (Constants.IS_OLD_PICK_UP_CODE) {
            setHandler()
        } else {
            bindBackgroundService()
        }


    }


    private fun initLocationUpdates() {

        mService?.getMyCurrentLocationChange(object :
            BackgroundLocationService.LocationManagerTrackInterface {
            override fun onMyLocationChange(
                currentLocation: MutableList<Location>?,
                lastLocation: Location?
            ) {
                if (lastLocation != null) {
                    /**
                     * iLoma Team :- Mohasin 09 Jan 2021
                     */

                    addCurrentLocationMarker(lastLocation)



                    locationChangelatitude = lastLocation!!.latitude
                    locationChangelongitude = lastLocation!!.longitude


                    logicToShowActualDistanceTravelled()


                    var currentspeed = (((lastLocation.speed) * 3600) / 1000).toInt()

                    //tv_currentSpeed?.text = currentspeed.toString() + " Kmph"

                    if (prevLatLng != null && getDistanceBetweenTwoLatLng(
                            LatLng(locationChangelatitude, locationChangelongitude),
                            prevLatLng!!
                        )!! >= 0
                    ) {
                        if (isFirstTimeSetUpDone) {
                            trackBoard = "currentCordinate"
                            valueForDistanceandWaitTime()
                            currentFare()
                        }

                        drawRoute()
                    }



                    if (currentspeed!! < 2) {   //speed less than 10 km per hr
                        isWaiting = true
                        if (waitTime == null) {
                            waitTime = Date()
                            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            waitAt = formatter.format(waitTime)
                            waitStartLocationNew = getAddressFromLocation(lastLocation)
                        }

                        //   Toast.makeText(applicationContext, "On Wait Start Speed : + ${currentspeed.toString()}", Toast.LENGTH_LONG).show()


                    } else {
                        isWaiting = false
                        if (waitTime != null) {
                            val time = Date()

                            val calculateDate =
                                ((time!!.getTime() - waitTime!!.getTime()) / 1000) //time in second

                            if (calculateDate > 0) {
                                val timeDiffrence = (calculateDate - 0).toDouble()//time in min


                                var options = HashMap<String, String>()
                                options.put("waiting_time", timeDiffrence.toString())
                                options.put("full_address", waitStartLocationNew!!)

                                //   options.put("full_address", waitStartLocation!!)

                                options.put("wait_at", waitAt!!)
                                options.put("lat", waitLat!!)
                                options.put("long", waitLong!!)

                                //To Prevent Duplicate entries
                                if (waitStartLocationNew != null && arrWaitTime.size > 0 && waitStartLocationNew?.equals(
                                        arrWaitTime[arrWaitTime.size - 1].getValue("full_address"),
                                        ignoreCase = true
                                    )!!
                                ) {
                                    val totalTime =
                                        timeDiffrence + arrWaitTime[arrWaitTime.size - 1].getValue("waiting_time")
                                            .toDouble()
                                    arrWaitTime[arrWaitTime.size - 1]["waiting_time"] =
                                        totalTime.toString()
                                    //   Toast.makeText(applicationContext, "On Ride wait location same as previous added Speed : + ${currentspeed.toString()}", Toast.LENGTH_LONG).show()
                                } else {
                                    arrWaitTime.add(options!!)
                                    //   Toast.makeText(applicationContext, "On Ride wait time added Speed : + ${currentspeed.toString()}", Toast.LENGTH_LONG).show()
                                }


                            }
                            waitTime = null
                            waitStartLocationNew = null
                            calculateWaitTime()

                        }


                    }

                    addLocationListToCreateCSVFile(lastLocation)
                }
            }

        })
    }

    private fun addLocationListToCreateCSVFile(lastLocation: Location) {
        var location: com.fairfareindia.ui.trackRide.NearByPlacesPOJO.Location = com.fairfareindia.ui.trackRide.NearByPlacesPOJO.Location()
        location.lat = lastLocation.latitude
        location.lng = lastLocation.longitude
        location.timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())

        googlePathListForTolls.add(location)

    }

    private fun logicToShowActualDistanceTravelled() {
        OriginM = LatLng(locationChangelatitude, locationChangelongitude)

        if (globalmarkerPoints!!.size > 0) {

            distanceBetweenCurrent = getDistanceBetweenTwoLatLng(
                OriginM!!,
                globalmarkerPoints?.get(globalmarkerPoints!!.size - 1)!!
            )

            //distanceBetweenCurrent in meter
            if (distanceBetweenCurrent!! >= 10) {
                globalmarkerPoints!!.add(OriginM)
                if (globalmarkerPoints!!.size >= 2) {

                    actulDis = distanceBetweenCurrent!!
                    actualTravelDistance.add(actulDis)


                    val sdf = SimpleDateFormat("HH:mm:ss")
                    val currentTime = sdf.format(Date())

                    for (i in cityPojoList.indices) {

                        if (City_ID!!.toInt().equals(cityPojoList.get(i).id)) {

                            if (cityPojoList.get(i).nightFromHours != null && cityPojoList.get(i).nightToHours != null && cityPojoList.get(i).nightFromHours <= currentTime && cityPojoList.get(i).nightToHours >= currentTime) {

                                actualTravelDistanceForNightCharges.add(actulDis)

                            } else {
                                Log.d("wsdqasedf", "NotBetween")

                            }

                        }

                    }



                    logDistance(
                        actulDis,
                        (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.latitude),
                        (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.longitude),

                        (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.latitude),
                        (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.longitude)
                    )
                }

            }

        } else {
            globalmarkerPoints!!.add(OriginM)
        }
    }

    private fun setHandler() {

        handler.postDelayed(object : Runnable {
            override fun run() {




                OriginM = LatLng(locationChangelatitude, locationChangelongitude)

                if (globalmarkerPoints!!.size > 0) {


                    //10 meter

                    val R = 6371
                    val dLat = deg2rad(
                        locationChangelatitude - (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.latitude)
                    )
                    val dLon = deg2rad(
                        locationChangelongitude - (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.longitude)
                    )
                    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(
                        deg2rad(locationChangelatitude)
                    ) * Math.cos(
                        deg2rad(globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.latitude)
                    ) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
                    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

                    distanceBetweenCurrent = (R * c) * 1000


                    //distanceBetweenCurrent in meter
                    if (distanceBetweenCurrent!! >= 10) {
                        globalmarkerPoints!!.add(OriginM)
                        trackBoard = "currentCordinate"
                        drawRoute()

                        if (globalmarkerPoints!!.size >= 2) {

                            actulDis = distanceBetweenCurrent!!
                            actualTravelDistance.add(actulDis)
                            //  actualTravelDistanceForNightCharges.add(actulDis)



                            logDistance(
                                actulDis,
                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.latitude),
                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.longitude),

                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.latitude),
                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.longitude)
                            )

                            // calDistance()
                            //  drawNewRoute()

                            valueForDistanceandWaitTime()
                            currentFare()
                            nearByPlaceses()
                        }

                    }

                } else {
                    globalmarkerPoints!!.add(OriginM)
                }

                handler.postDelayed(this, 10000)
            }
        }, 10000)

    }


    private fun calDistance() {

        val newOriginLat = globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.latitude
        val newOriginLong = globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.longitude


        val newDestLat = globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.latitude
        val newDestLong = globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.longitude

        val call = ApiClient.clientPlaces.distanceMatrix(

            "$newOriginLat,$newOriginLong",
            "$newDestLat,$newDestLong"

        )

        call!!.enqueue(object : Callback<DistanceMatrixResponse?> {
            override fun onResponse(
                call: Call<DistanceMatrixResponse?>,
                response: Response<DistanceMatrixResponse?>
            ) {
                if (response.code() == 200) {
                    if ((response.body()!!.rows!!.get(0).elements!!.get(0).distance!!.value) != null)
                        actulDis =
                            response.body()!!.rows!!.get(0).elements!!.get(0).distance!!.value.toDouble()
                    actualTravelDistance.add(actulDis)

                    logDistance(
                        actulDis,
                        newOriginLat,
                        newOriginLong,
                        newDestLat,
                        newDestLong
                    )

                }
            }

            override fun onFailure(
                call: Call<DistanceMatrixResponse?>,
                t: Throwable
            ) {
            }
        })
    }

    private fun logDistance(
        actualDistance: Double,
        newOriginLat: Double,
        newOriginLong: Double,
        newDestLat: Double,
        newDestLong: Double
    ) {


        var jsonMainObj: JSONObject? = null

        try {
            jsonMainObj = JSONObject()
            jsonMainObj.accumulate("title", "testing")
            jsonMainObj.accumulate("message", "testing message")
            jsonMainObj.accumulate("ride_id", info.ride!!.id)


            val jsonProductObj = JSONObject()
            jsonProductObj.accumulate("distance", actualDistance)
            jsonProductObj.accumulate("origins", "$newOriginLat,$newOriginLong")
            jsonProductObj.accumulate("destinations", "$newDestLat,$newDestLong")
            jsonMainObj.accumulate("data", jsonProductObj)


        } catch (e: JSONException) {
            e.printStackTrace()
        }


        val call = ApiClient.client.logRide(
            "Bearer $token",
            jsonMainObj.toString()
        )

        call!!.enqueue(object : Callback<DeleteDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<DeleteDisputResponsePOJO?>,
                response: Response<DeleteDisputResponsePOJO?>
            ) {
                if (response.code() == 200) {

                }
            }

            override fun onFailure(
                call: Call<DeleteDisputResponsePOJO?>,
                t: Throwable
            ) {
            }
        })

    }

    private fun getcurrentDate() {
        today = Date()
        // val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        Log.d("cDaye", today.toString())

    }

    private fun snaptoRada(waypoints: String) {
        val call = ApiClient.clientPlaces.getsnaPTOROAD(waypoints)

        call!!.enqueue(object : Callback<SnapTORoadResponse?> {
            override fun onResponse(
                call: Call<SnapTORoadResponse?>,
                response: Response<SnapTORoadResponse?>
            ) {
                if (response.code() == 200) {

                    var markerPointsNew: ArrayList<LatLng?>? = null
                    markerPointsNew = ArrayList()

                    for (i in response.body()!!.snappedPoints!!.indices) {
                        if (response!!.body()!!.snappedPoints.get(i).originalIndex >= 0) {
                            markerPointsNew.add(
                                LatLng(
                                    ((response!!.body()!!.snappedPoints).get(i).location.latitude),
                                    (response!!.body()!!.snappedPoints).get(i).location.longitude
                                )
                            )
                        }

                    }

                    Log.d("newsded", markerPointsNew.size.toString())

                    // markerPointsNew!!.add(0,(LatLng(markerPoints!!.get(0)!!.latitude,markerPoints!!.get(0)!!.longitude)))
                    //     markerPointsNew!!.add((markerPointsNew.size-1),(LatLng(markerPoints!!.get((markerPoints!!.size-1))!!.latitude,markerPoints!!.get((markerPoints!!.size-1))!!.longitude)))


                }
            }

            override fun onFailure(
                call: Call<SnapTORoadResponse?>,
                t: Throwable
            ) {
            }
        })
    }


    @OnClick(R.id.tvhideShow)
    fun hideshow() {
        if (hideshow.equals("show")) {
            hideshow = "hide"
            rlhideview?.visibility = View.GONE
        } else {
            hideshow = "show"
            rlhideview?.visibility = View.VISIBLE
        }
    }


    private fun setStatusBarGradiant(activity: TrackRideActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
         menuInflater.inflate(R.menu.menu_home_lang, menu)
         return true
     }
 */
    /* override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
             R.id.action_home -> {
                 preferencesManager!!.setStringValue(
                     Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                     "LOCALITY"
                 )
                 sharedpreferences!!.edit().clear().commit()
                 val intent = Intent(this@TrackRideActivity, HomeActivity::class.java)
                 startActivity(intent)
             }
         }
         return true
     }
 */
    @RequiresApi(Build.VERSION_CODES.N)
    fun round(unrounded: Double, precision: Int, roundingMode: Int): Double {
        val bd = BigDecimal(unrounded)
        val rounded = bd.setScale(precision, roundingMode)
        return rounded.toDouble()
    }

    fun GetDistanceFromLatLonInmeter(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(
            deg2rad(lat2)
        ) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        distanceBetweenCurrent = (R * c) * 1000
        return distanceBetweenCurrent!!
    }


    fun valueForDistanceandWaitTime() {

        val todayRefresh = Date()

        different = ((todayRefresh.getTime() - today!!.getTime()) / 1000).toDouble()//seconds
        different = (different / 60)//minutes

        progressBarTime!!.progress = different.toInt()
        val strescimal = String.format("%.1f", different)

        actualTime = strescimal

        //ILOMADEV
        if (Constants.IS_OLD_PICK_UP_CODE) {
            tvTravelTime!!.text = strescimal + " Min"
        } else {
            if (estCurrentDurationInMin != null){
                tvTravelTime!!.text = formatTime(todayRefresh.getTime() - today!!.getTime()) + "/" + formatTime(estCurrentDurationInMin!!.toLong()*1000)
            }else{
                tvTravelTime!!.text = formatTime(todayRefresh.getTime() - today!!.getTime())
            }

        }


        var totalActualDistance = 0.0

        progressBarDistance!!.getProgressDrawable().setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)

        // actualTravelDistance add value when draw new route

        if (actualTravelDistance.size > 0) {
            for (i in actualTravelDistance.indices) {
                totalActualDistance = totalActualDistance + actualTravelDistance[i]

            }

            strDistCal = (totalActualDistance / 1000).toString()//meter
            // progressBarDistance!!.progress = (totalActualDistance/1000).toInt()//km
            progressBarDistance!!.progress = (totalActualDistance).toInt()//km
            val dist = String.format("%.1f", strDistCal!!.toDouble())

            actualDistance = dist
            tv_travelledDistance!!.text = dist + " km/" + estCurrentDistanceInKm
            tv_estimatedDistance!!.text = strDistCal.toString() + " km"

        }



        calculateWaitTime()


    }


    fun GetDistanceFromLatLonInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(
            deg2rad(lat2)
        ) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        travelledDistance = R * c
        if (info?.ride?.canToll.equals("Yes", ignoreCase = true)){
            uploadCSVFile()
        }else{
            tracelledPopUP(travelledDistance)
        }

        return R * c
    }

    private fun tracelledPopUP(Distancetravelled: Double?) {

        // var twoDecimal = Distancetravelled!!.toFloat()
        var twoDecimal = String.format("%.02f", Distancetravelled!!.toFloat())


        if (twoDecimal.equals("0.00")) {
            actualRemainingDistanceInMeter = 100
        } else {

            var InMeter: Float

            InMeter = twoDecimal!!.toFloat()
            actualRemainingDistanceInMeter = (InMeter * 1000).toInt()


        }

        if (actualRemainingDistanceInMeter >= 500) {

            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("FairFareIndia")
            alertDialog.setMessage("You have not reached the destination yet. Are you sure you want to end the ride?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Yes") { dialog, which ->

                if (Constants.IS_OLD_PICK_UP_CODE) {
                    handler.removeCallbacksAndMessages(null)
                } else {
                    unBindBackgroundService()

                }


                var totalActualDistanceForNightCharges = 0.0


                if (actualTravelDistanceForNightCharges.size > 0) {
                    for (i in actualTravelDistanceForNightCharges.indices) {
                        totalActualDistanceForNightCharges = totalActualDistanceForNightCharges + actualTravelDistanceForNightCharges[i]

                    }

                    val strDistCalForKM = (totalActualDistanceForNightCharges / 1000).toString()//meter
                    val distForNightCharges = String.format("%.02f", strDistCalForKM!!.toDouble())

                    actualDistanceForNightCharg = distForNightCharges

                }


                val intentr = Intent(applicationContext, EndRidesActivity::class.java)
                if ((intent.getStringExtra("MyRidesLat")) != null) {

                    intentr.putExtra("RideID", intent.getStringExtra("MyRidesID"))
                    intentr.putExtra("sAddress", tv_myCurrentLocation!!.text.toString())
                    intentr.putExtra("dAddress", tv_myDropUpLocation!!.text.toString())
                    intentr.putExtra("originLat", intent.getStringExtra("MyRidesLat"))
                    intentr.putExtra("originLong", intent.getStringExtra("MyRidesLong"))
                    intentr.putExtra("destLat", intent.getStringExtra("MyRidesDLat"))
                    intentr.putExtra("destLong", intent.getStringExtra("MyRidesDLong"))
                    intentr.putExtra("ride_waitings", arrWaitTimePostEndRide())
                    intentr.putExtra("actualDistanceTravelled", actualDistance)
                    intentr.putExtra("actualDistanceTravelledForNightCharges", actualDistanceForNightCharg)
                    intentr.putExtra("actualTimeTravelled", actualTime)
                    intentr.putExtra("EndRideCurrentLat", waitStartLat)
                    intentr.putExtra("EndRideCurrentLon", waitStartLong)
                    intentr.putExtra("EndRideCurrentAddress", waitStartLocation)
                    intentr.putExtra("tollGuruJsonArray", tollsJSONArrayFromTollGuru.toString())




                    startActivity(intentr)
                    finish()

                } else {


                    intentr.putExtra("RideID", (info.ride!!.id).toString())
                    intentr.putExtra("sAddress", tv_myCurrentLocation!!.text.toString())
                    intentr.putExtra("dAddress", tv_myDropUpLocation!!.text.toString())
                    intentr.putExtra("originLat", (info.ride!!.estimatedTrackRide!!.originPlaceLat)!!)
                    intentr.putExtra("originLong", (info.ride!!.estimatedTrackRide!!.originPlaceLong)!!)
                    intentr.putExtra(
                        "destLat",
                        (info.ride!!.estimatedTrackRide!!.destinationPlaceLat)!!
                    )
                    intentr.putExtra(
                        "destLong",
                        (info.ride!!.estimatedTrackRide!!.destinationPlaceLong)!!
                    )
                    intentr.putExtra("ride_waitings", arrWaitTimePostEndRide())
                    intentr.putExtra("actualDistanceTravelled", actualDistance)
                    intentr.putExtra("actualDistanceTravelledForNightCharges", actualDistanceForNightCharg)
                    intentr.putExtra("actualTimeTravelled", actualTime)
                    intentr.putExtra("EndRideCurrentLat", waitStartLat)
                    intentr.putExtra("EndRideCurrentLon", waitStartLong)
                    intentr.putExtra("EndRideCurrentAddress", waitStartLocation)
                    intentr.putExtra("tollGuruJsonArray", tollsJSONArrayFromTollGuru.toString())


                    startActivity(intentr)
                    finish()
                }


            }
            alertDialog.setNegativeButton("No") { dialog, which -> dialog.cancel() }
            alertDialog.show()


        } else {


            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("FairFareIndia")
            alertDialog.setMessage("Are you sure you want to end this Ride?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Yes") { dialog, which ->

                if (Constants.IS_OLD_PICK_UP_CODE) {
                    handler.removeCallbacksAndMessages(null)
                } else {
                    unBindBackgroundService()

                }


                var totalActualDistanceForNightCharges = 0.0


                if (actualTravelDistanceForNightCharges.size > 0) {
                    for (i in actualTravelDistanceForNightCharges.indices) {
                        totalActualDistanceForNightCharges = totalActualDistanceForNightCharges + actualTravelDistanceForNightCharges[i]

                    }

                    val strDistCalForKM = (totalActualDistanceForNightCharges / 1000).toString()//meter
                    val distForNightCharges = String.format("%.02f", strDistCalForKM!!.toDouble())

                    actualDistanceForNightCharg = distForNightCharges

                }


                val intentr = Intent(applicationContext, EndRidesActivity::class.java)
                if ((intent.getStringExtra("MyRidesLat")) != null) {

                    intentr.putExtra("RideID", intent.getStringExtra("MyRidesID"))
                    intentr.putExtra("sAddress", tv_myCurrentLocation!!.text.toString())
                    intentr.putExtra("dAddress", tv_myDropUpLocation!!.text.toString())
                    intentr.putExtra("originLat", intent.getStringExtra("MyRidesLat"))
                    intentr.putExtra("originLong", intent.getStringExtra("MyRidesLong"))
                    intentr.putExtra("destLat", intent.getStringExtra("MyRidesDLat"))
                    intentr.putExtra("destLong", intent.getStringExtra("MyRidesDLong"))
                    intentr.putExtra("ride_waitings", arrWaitTimePostEndRide())
                    intentr.putExtra("actualDistanceTravelled", actualDistance)
                    intentr.putExtra("actualDistanceTravelledForNightCharges", actualDistanceForNightCharg)
                    intentr.putExtra("actualTimeTravelled", actualTime)
                    intentr.putExtra("EndRideCurrentLat", waitStartLat)
                    intentr.putExtra("EndRideCurrentLon", waitStartLong)
                    intentr.putExtra("EndRideCurrentAddress", waitStartLocation)
                    intentr.putExtra("tollGuruJsonArray", tollsJSONArrayFromTollGuru.toString())



                    startActivity(intentr)
                    finish()

                } else {


                    intentr.putExtra("RideID", (info.ride!!.id).toString())
                    intentr.putExtra("sAddress", tv_myCurrentLocation!!.text.toString())
                    intentr.putExtra("dAddress", tv_myDropUpLocation!!.text.toString())
                    intentr.putExtra("originLat", (info.ride!!.estimatedTrackRide!!.originPlaceLat)!!)
                    intentr.putExtra("originLong", (info.ride!!.estimatedTrackRide!!.originPlaceLong)!!)
                    intentr.putExtra(
                        "destLat",
                        (info.ride!!.estimatedTrackRide!!.destinationPlaceLat)!!
                    )
                    intentr.putExtra(
                        "destLong",
                        (info.ride!!.estimatedTrackRide!!.destinationPlaceLong)!!
                    )
                    intentr.putExtra("ride_waitings", arrWaitTimePostEndRide())
                    intentr.putExtra("actualDistanceTravelled", actualDistance)
                    intentr.putExtra("actualDistanceTravelledForNightCharges", actualDistanceForNightCharg)
                    intentr.putExtra("actualTimeTravelled", actualTime)
                    intentr.putExtra("EndRideCurrentLat", waitStartLat)
                    intentr.putExtra("EndRideCurrentLon", waitStartLong)
                    intentr.putExtra("EndRideCurrentAddress", waitStartLocation)
                    intentr.putExtra("tollGuruJsonArray", tollsJSONArrayFromTollGuru.toString())


                    startActivity(intentr)
                    finish()
                }


            }
            alertDialog.setNegativeButton("No") { dialog, which -> dialog.cancel() }
            alertDialog.show()

        }

    }



    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    @OnClick(R.id.tv_ShowTrackBoard)
    fun showboard() {

        if(ProjectUtilities.checkInternetAvailable(this@TrackRideActivity)) {

            llshowData!!.visibility = View.GONE
            rlData!!.visibility = View.VISIBLE
            tv_ShowTrackBoard!!.visibility = View.GONE



            valueForDistanceandWaitTime()
            currentFare()
            nearByPlaceses()
        }else{
            ProjectUtilities.showToast(this@TrackRideActivity,getString(R.string.internet_error))
        }

    }

    @OnClick(R.id.tv_refresh)
    fun refresh() {
        if(ProjectUtilities.checkInternetAvailable(this@TrackRideActivity)) {

        valueForDistanceandWaitTime()
        currentFare()
        nearByPlaceses()

        }else{
            ProjectUtilities.showToast(this@TrackRideActivity,getString(R.string.internet_error))
        }



    }

    @OnClick(R.id.btnEndRide)
    fun endRide() {

        if(ProjectUtilities.checkInternetAvailable(this@TrackRideActivity)) {
            if ((intent.getStringExtra("MyRidesLat")) != null) {
                destinationLat = intent.getStringExtra("MyRidesDLat")!!.toString()
                destinationLong = intent.getStringExtra("MyRidesDLong")!!.toString()

            } else {
                destinationLat = (info!!.ride!!.estimatedTrackRide!!.destinationPlaceLat).toString()
                destinationLong =
                    (info!!.ride!!.estimatedTrackRide!!.destinationPlaceLong).toString()
            }
            GetDistanceFromLatLonInKm(
                destinationLat!!.toDouble(),
                destinationLong!!.toDouble(),
                locationChangelatitude,
                locationChangelongitude
            )
        }else{
            ProjectUtilities.showToast(this@TrackRideActivity,getString(R.string.internet_error))
        }


    }

    private fun uploadCSVFile() {
        // create RequestBody instance from file
        val file: File = ProjectUtilities.downloadCSVFile(googlePathListForTolls, this)

        val requestFile: RequestBody = RequestBody.create(
            "text/csv".toMediaTypeOrNull(),
            file
        )

        // MultipartBody.Part is used to send also the actual file name

        // MultipartBody.Part is used to send also the actual file name
        val body =
            MultipartBody.Part.create(requestFile)

        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show()

        val call = ApiClient.clientTollGuru.uploadCSVTollGuru(
            "RBGRLTfRBf3J4Jgt686H3QfFPrDN3D8N",
            requestFile
        )
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    val stringResponse = response.body()?.string()
                    val jObject = JSONObject(stringResponse)
                    tollsJSONArrayFromTollGuru = jObject.getJSONObject("route").getJSONArray("tolls")

                    tracelledPopUP(travelledDistance)

                } else {
                    /*Toast.makeText(
                        this@TrackRideActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()*/

                    tracelledPopUP(travelledDistance)
                }

                if (file.exists()){
                    file.delete()
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                if (file.exists()){
                    file.delete()
                }
            }
        })
    }

    private fun nearByPlaceses() {
        val call = ApiClient.clientPlaces.getNearbyPlaces(
            "$locationChangelatitude,$locationChangelongitude",
            1000
        )

        call!!.enqueue(object : Callback<NearByResponse?> {
            override fun onResponse(
                call: Call<NearByResponse?>,
                response: Response<NearByResponse?>
            ) {
                if (response.code() == 200) {
                    for (i in response!!.body()!!.results!!.indices) {

                        if (response.body()!!.results!!.get(i)!!.types!!.get(0)!!.contains("atm")) {
                            tv_atm!!.text = response.body()!!.results!!.get(i)!!.name
                            Glide.with(this@TrackRideActivity)
                                .load(response.body()!!.results!!.get(i)!!.icon)
                                .apply(
                                    RequestOptions()
                                        .centerCrop()
                                        .dontAnimate()
                                        .dontTransform()
                                ).into(ivAtm!!)


                            if (tv_atm!!.text.isNotEmpty()) {
                                llAtm!!.visibility = View.VISIBLE
                            } else {
                                llAtm!!.visibility = View.GONE
                            }


                        }

                        if (response.body()!!.results!!.get(i)!!.types!!.get(0)
                                .equals("point_of_interest")
                        ) {
                            museum!!.text = response.body()!!.results!!.get(i)!!.name
                            Glide.with(this@TrackRideActivity)
                                .load(response.body()!!.results!!.get(i)!!.icon)
                                .apply(
                                    RequestOptions()
                                        .centerCrop()
                                        .dontAnimate()
                                        .dontTransform()
                                ).into(ivMuseum!!)
                            if (museum!!.text.isNotEmpty()) {
                                llMuseum!!.visibility = View.VISIBLE
                            } else {
                                llMuseum!!.visibility = View.GONE
                            }


                        }

                        if (response.body()!!.results!!.get(i)!!.types!!.get(0)!!
                                .contains("health")
                        ) {
                            hospital!!.text = response.body()!!.results!!.get(i)!!.name
                            Glide.with(this@TrackRideActivity)
                                .load(response.body()!!.results!!.get(i)!!.icon)
                                .apply(
                                    RequestOptions()
                                        .centerCrop()
                                        .dontAnimate()
                                        .dontTransform()
                                ).into(ivHospital!!)

                            if (hospital!!.text.isNotEmpty()) {
                                llHospital!!.visibility = View.VISIBLE
                            } else {
                                llHospital!!.visibility = View.GONE
                            }


                        }

                        if (response.body()!!.results!!.get(i)!!.types!!.get(0)!!
                                .contains("bank")
                        ) {
                            tv_hotel!!.text = response.body()!!.results!!.get(i)!!.name
                            Glide.with(this@TrackRideActivity)
                                .load(response.body()!!.results!!.get(i)!!.icon)
                                .apply(
                                    RequestOptions()
                                        .centerCrop()
                                        .dontAnimate()
                                        .dontTransform()
                                ).into(ivHotel!!)

                            if (tv_hotel!!.text.isNotEmpty()) {
                                llHotel!!.visibility = View.VISIBLE
                            } else {
                                llHotel!!.visibility = View.GONE
                            }


                        }


                    }
                }
            }

            override fun onFailure(
                call: Call<NearByResponse?>,
                t: Throwable
            ) {
            }
        })

    }

    private fun currentFare() {


        val call = ApiClient.client.getCurrentFare(
            "Bearer $token",
            (info.ride!!.id),
            strDistCal,
            waitTimeForCurrentFare
        )

        call!!.enqueue(object : Callback<CurrentFareeResponse?> {
            override fun onResponse(
                call: Call<CurrentFareeResponse?>,
                response: Response<CurrentFareeResponse?>
            ) {
                if (response.code() == 200) {


                    var sTotal = response.body()!!.rate!!.subTotal!!.toFloat() + response.body()!!.rate!!.waitingCharges!!.toFloat()
                    tv_currentFare!!.text = sTotal.toString()+"0"

                }
            }

            override fun onFailure(
                call: Call<CurrentFareeResponse?>,
                t: Throwable
            ) {
            }
        })

    }

    @OnClick(R.id.tv_close)
    fun closeCoard() {
        rlData!!.visibility = View.GONE
        llshowData!!.visibility = View.VISIBLE
        tv_ShowTrackBoard!!.visibility = View.VISIBLE
        hideshow = "show"
        rlhideview?.visibility = View.VISIBLE
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        //  googleMap!!.clear()

        mMap = googleMap
        // mMap!!.isMyLocationEnabled = true
        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    (info.ride!!.estimatedTrackRide!!.originPlaceLat)!!.toDouble(),
                    (info.ride!!.estimatedTrackRide!!.originPlaceLong)!!.toDouble()
                ), 18.0f
            )
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (info.ride!!.estimatedTrackRide!!.originPlaceLat)!!.toDouble(),
                    (info.ride!!.estimatedTrackRide!!.originPlaceLong)!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)
            )
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (info.ride!!.estimatedTrackRide!!.destinationPlaceLat)!!.toDouble(),
                    (info.ride!!.estimatedTrackRide!!.destinationPlaceLong)!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_grey)
            )
        )

        //  updateCamera(getCompassBearing(startLocation, destLocation))




        drawRoute()
    }

    private fun drawRoute() {
        var mOrigin: LatLng? = null
        var mDestination: LatLng? = null


        if (trackBoard.equals("currentCordinate")) {
            mOrigin = LatLng(locationChangelatitude, locationChangelongitude)
            mDestination = LatLng(
                (info.ride!!.estimatedTrackRide!!.destinationPlaceLat)!!.toDouble(),
                (info.ride!!.estimatedTrackRide!!.destinationPlaceLong)!!.toDouble()
            )

            val url = getDirectionsUrl(mOrigin!!, mDestination!!)
            val downloadTask = DownloadTask()
            downloadTask.execute(url)


        } else {
            mOrigin = LatLng(
                (info.ride!!.estimatedTrackRide!!.originPlaceLat)!!.toDouble(),
                (info.ride!!.estimatedTrackRide!!.originPlaceLong)!!.toDouble()
            )
            mDestination = LatLng(
                (info.ride!!.estimatedTrackRide!!.destinationPlaceLat)!!.toDouble(),
                (info.ride!!.estimatedTrackRide!!.destinationPlaceLong)!!.toDouble()
            )


            val url = getDirectionsUrl(mOrigin!!, mDestination!!)
            val downloadTask = DownloadTask()
            downloadTask.execute(url)


        }


    }

    private fun getDirectionsUrl(mOrigin: LatLng, mDestination: LatLng): String {

        // Toast.makeText(applicationContext, "Direction API calling", Toast.LENGTH_LONG).show()

        val str_origin = "origin=" + mOrigin.latitude + "," + mOrigin.longitude
        val str_dest = "destination=" + mDestination.latitude + "," + mDestination.longitude

        // Key
        val key = "key=" + getString(R.string.google_maps_key)

        // Building the parameters to the web service
        // val parameters = "$str_origin&$str_dest&$key"
        val parameters = "$str_origin&$str_dest&alternatives=false&$key"
        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private inner class DownloadTask :
        AsyncTask<String, Void?, String>() {
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
            val parserTask = ParserTask()

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
        AsyncTask<String, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        protected override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            var routesqwqw: HashMap<String, String>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                val array = jObject.getJSONArray("routes")
                //  val geoCodedarray = jObject.getJSONArray("geocoded_waypoints")
                val routes1 = array.getJSONObject(0)
                val legs = routes1.getJSONArray("legs")
                val zerothLegs = legs.getJSONObject(0)
                val distance = zerothLegs.getJSONObject("distance")
                val duration = zerothLegs.getJSONObject("duration")


                estCurrentDistance = distance.getString("value")
                estCurrentDistanceInKm = distance.getString("text")
                estCurrentDuration = duration.getString("text")
                estCurrentDurationInMin = duration.getString("value")

                waitLocation = zerothLegs.getString("end_address")
                val endLocation = zerothLegs.getJSONObject("end_location")
                waitLat = endLocation.getString("lat")
                waitLong = endLocation.getString("lng")



                waitStartLocation = zerothLegs.getString("start_address")
                val startLocation = zerothLegs.getJSONObject("start_location")
                waitStartLat = startLocation.getString("lat")
                waitStartLong = startLocation.getString("lng")




                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
                e.printStackTrace()
            }


            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng?>? = null
            var lineOptions: PolylineOptions? = null


            // Traversing through all the routes


            if (result != null) {
                for (i in result!!.indices) {
                    points = ArrayList()
                    markerPoints = ArrayList()
                    lineOptions = PolylineOptions()


                    // Fetching i-th route
                    val path = result[i]



                    for (j in path.indices) {
                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position = LatLng(lat, lng)


                        points!!.add(position)
                        markerPoints!!.add(position)
                    }

                    if (points.size >= 2){
                        updateCamera(getCompassBearing(points[0]!!, points[1]!!))
                    }


                    // Fetching all the points in i-th route


                }

                lineOptions?.addAll(points)
                lineOptions?.width(15f)
                lineOptions?.color(this@TrackRideActivity.resources.getColor(R.color.gradientendcolor))

                if (lineOptions != null) {
                    // ILOMADEV :- 10 Feb 2021
                    updatedPolyline?.remove()
                    updatedPolyline = mMap!!.addPolyline(lineOptions)
                } else {
                    Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                        .show()
                }

                if (!trackBoard.equals("currentCordinate")) {
                    //ILOMADEV
                    isFirstTimeSetUpDone = true

                    // val estCurrDIst = (estCurrentDistance!!.toDouble() / 1000)
                    val estCurrDIst = (estCurrentDistance!!.toInt())

                    val estCurrentDist =
                        DecimalFormat("####.#").format((estCurrentDistance!!.toDouble() / 1000)) + " km"
                    tvEstDistance!!.text = "Est.Distance " + estCurrentDist
                    tvEstTime!!.text = "Est.Time " + estCurrentDuration
                    //   progressBarDistance!!.max = (estCurrDIst)!!.toFloat().toInt()
                    progressBarDistance!!.max = estCurrDIst


                    val timeInMin = (estCurrentDurationInMin!!.toInt() / 60).toString() + " mins"

                    val currentTravelledTime = timeInMin!!.replace(" mins", "")
                    if (currentTravelledTime.isNotEmpty()) {
                        progressBarTime!!.max = ((currentTravelledTime)!!.toFloat()).toInt()

                    }
                    tv_distance!!.text =
                        "(Est.Distance:" + estCurrentDist + ") / " + "(Est.Time:" + estCurrentDuration + ")"
                }
            }

        }
    }


    fun calculateWaitTime() {
        var totalWaitTime = 0.0

        if (arrWaitTime.size > 0) {
            for (i in arrWaitTime.indices) {
                totalWaitTime = totalWaitTime + (arrWaitTime[i].getValue("waiting_time").toDouble())

            }
        }



        if (waitTime != null) {
            val time = Date()
            val calculateDate = ((time!!.getTime() - waitTime!!.getTime()) / 1000)
            val timeDiffrence = (calculateDate).toDouble()
            totalWaitTime = totalWaitTime + timeDiffrence

            /* if (calculateDate > 60) {
                 val timeDiffrence = (calculateDate - 60).toDouble()
                 totalWaitTime = totalWaitTime + timeDiffrence
             }*/
        }

        var timeInMin = 0.0


        if (totalWaitTime > 59) {
            timeInMin = totalWaitTime / 60
            val strTimeCal = String.format("%.1f", timeInMin)

            //ILOMADEV
            if (Constants.IS_OLD_PICK_UP_CODE) {
                tv_currentwaitTime!!.text = strTimeCal + " Min"
            } else {
                tv_currentwaitTime!!.text = formatTimeMinSec((timeInMin * 60 * 1000).toLong())
            }


            val strTime = String.format("%.02f", timeInMin)
            waitTimeForCurrentFare = strTime


        } else {
            timeInMin = totalWaitTime
            //ILOMADEV
            if (Constants.IS_OLD_PICK_UP_CODE) {
                tv_currentwaitTime!!.text = timeInMin.toString() + " Sec"
            } else {
                tv_currentwaitTime!!.text = formatTimeMinSec((timeInMin * 1000).toLong())
            }


        }


    }

    fun arrWaitTimePostEndRide(): ArrayList<HashMap<String, String>> {
        var arrtime = arrWaitTime

        if (waitTime != null) {
            val time = Date()

            val calculateDate =
                ((time!!.getTime() - waitTime!!.getTime()) / 1000)//time calculate in second

            if (calculateDate > 0) {
                val timeDiffrence = (calculateDate - 0).toDouble()//time difference in min


                var options = HashMap<String, String>()
                options.put("waiting_time", timeDiffrence.toString())
                options.put("full_address", waitStartLocationNew!!)

                //  options.put("full_address", waitStartLocation!!)
                options.put("wait_at", waitAt!!)
                options.put("lat", waitStartLat!!)
                options.put("long", waitStartLong!!)
                arrtime.add(options!!)


            } else {

                /*var options = HashMap<String, String>()
                options.put("waiting_time", "")
                options.put("full_address", "")
                options.put("wait_at", "")
                options.put("lat", "")
                options.put("long", "")
                arrtime.add(options!!)
*/
            }


        }


        return arrtime
    }


    private fun drawNewRoute() {
        var Originnew: LatLng? = null
        var Destinationm: LatLng? = null


        Originnew = LatLng(
            (
                    globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.latitude),
            (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.longitude)
        )
        Destinationm = LatLng(
            (
                    globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.latitude),
            (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.longitude)
        )


        val url = getNewDirectionsUrl(Originnew!!, Destinationm!!)
        val newdownloadTask = NewDownloadTask()
        newdownloadTask.execute(url)


    }

    private fun getNewDirectionsUrl(mOrigin: LatLng, mDestination: LatLng): String {
        val str_origin = "origin=" + mOrigin.latitude + "," + mOrigin.longitude
        val str_dest = "destination=" + mDestination.latitude + "," + mDestination.longitude

        // Key
        val key = "key=" + getString(R.string.google_maps_key)

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&alternatives=false&$key"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private inner class NewDownloadTask :
        AsyncTask<String, Void?, String>() {
        // Downloading data in non-ui thread
        protected override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""
            try {
                // Fetching the data from web service
                data = newdownloadUrl(url[0])
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
            val newparserTask = NewParserTask()

            // Invokes the thread for parsing the JSON data
            newparserTask.execute(result)
        }
    }

    @Throws(IOException::class)
    private fun newdownloadUrl(strUrl: String): String {
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

    private inner class NewParserTask :
        AsyncTask<String, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        protected override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            var routesqwqw: HashMap<String, String>? = null
            try {
                jObject = JSONObject(jsonData[0])
                passjObject = JSONObject(jsonData[0])

                val parser = DirectionsJSONParser()
                val array = jObject.getJSONArray("routes")
                val routes1 = array.getJSONObject(0)
                val legs = routes1.getJSONArray("legs")
                val zerothLegs = legs.getJSONObject(0)
                val distance = zerothLegs.getJSONObject("distance")
                val duration = zerothLegs.getJSONObject("duration")


                Log.d(
                    "cvfdde",
                    actualTravelDistance.size.toString() + "       " + (actulDis!!.toDouble())
                )

                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var greypoints: ArrayList<LatLng?>? = null
            var greylineOptions: PolylineOptions? = null


            // Traversing through all the routes


            if (result != null) {
                for (i in result!!.indices) {
                    greypoints = ArrayList()
                    greylineOptions = PolylineOptions()


                    // Fetching i-th route
                    val path = result[i]



                    for (j in path.indices) {
                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position = LatLng(lat, lng)

                        greypoints!!.add(position)

                    }


                    // Fetching all the points in i-th route


                }

/*
                  greylineOptions!!.addAll(greypoints)
                  greylineOptions!!.width(15f)
                  greylineOptions!!.color(this@TrackRideActivity.resources.getColor(R.color.Grey))
                  if (greylineOptions != null) {
                      if (mGreyPolyline != null) {
                      }
                      mGreyPolyline = mMap!!.addPolyline(greylineOptions)
                  } else {
                      Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                          .show()
                  }*/

            }


        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onLocationChanged(location: Location) {

        if (Constants.IS_OLD_PICK_UP_CODE) {
            Log.d("onLocationChangedq", location!!.speed.toString())

            /**
             * iLoma Team :- Mohasin 09 Jan 2021
             */

            addCurrentLocationMarker(location)


            locationChangelatitude = location.latitude
            locationChangelongitude = location.longitude



            if (location != null) {


                var currentspeed = (((location.speed) * 3600) / 1000).toInt()





                if (currentspeed!! < 2) {   //speed less than 10 km per hr
                    if (waitTime == null) {
                        waitTime = Date()
                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        waitAt = formatter.format(waitTime)

                    }

                } else {

                    if (waitTime != null) {
                        val time = Date()

                        val calculateDate =
                            ((time!!.getTime() - waitTime!!.getTime()) / 1000) //time in second

                        if (calculateDate > 0) {
                            val timeDiffrence = (calculateDate - 0).toDouble()//time in min


                            var options = HashMap<String, String>()
                            options.put("waiting_time", timeDiffrence.toString())
                            options.put("full_address", waitLocation!!)
                            options.put("wait_at", waitAt!!)
                            options.put("lat", waitLat!!)
                            options.put("long", waitLong!!)
                            arrWaitTime.add(options!!)


                        }
                        waitTime = null
                        calculateWaitTime()

                    }


                }


            }
        }


    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    /**
     * iLoma Team :- Mohasin 8 Jan
     */

    private fun addCurrentLocationMarker(location: Location?) {

        val newPosition: LatLng = LatLng(location!!.latitude, location.longitude)
        if (myMarker != null) {
            if (prevLatLng != null && !isWaiting!!) {
                animateMarkerNew(prevLatLng!!, newPosition, myMarker)
            }
            myMarker!!.remove()
        }
        if (isWaiting!! && lastCompassBering != null) {
            myMarker = mMap!!.addMarker(
                MarkerOptions()
                    .position(newPosition)
                    .icon(getMarkerIcon(vehicleName))
                    .anchor(0.5f, 0.5f)
                    .draggable(true)
                    .flat(true)
            )
        } else {
            myMarker = mMap!!.addMarker(
                MarkerOptions()
                    .position(newPosition)
                    .icon(getMarkerIcon(vehicleName))
                    .anchor(0.5f, 0.5f)
                    .draggable(true)
                    .flat(true)
                    .rotation(location.bearing)
            )
        }


        prevLatLng = newPosition

        mMap!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(newPosition)
                    //  .bearing(getCompassBearing(startLocation, destLocation))
                    .zoom(getZoomLevel())
                    .build()
            )
        )

    }

    private fun getCompassBearing(location: Location): Float {
        location.bearingTo(destLocation);

        geoField = GeomagneticField(
            java.lang.Double.valueOf(location.latitude).toFloat(),
            java.lang.Double.valueOf(location.longitude).toFloat(),
            java.lang.Double.valueOf(location.altitude).toFloat(),
            System.currentTimeMillis()
        )

        return Math.round(-(location.bearing - (location.bearing - geoField?.declination!!) / 360 + 180))
            .toFloat()
    }

    private fun getZoomLevel(): Float {
        if (!isMapZoomed!!) {
            isMapZoomed = true
            return 18.0f
        } else if (mMap!!.cameraPosition.zoom < 10) {
            return 18.0f
        }
        return mMap!!.cameraPosition.zoom
    }


    fun getMarkerIcon(vehicalName: String?): BitmapDescriptor? {
        if (vehicalName.equals("Taxi", ignoreCase = true) || vehicalName.equals(
                "Local Taxi",
                ignoreCase = true
            )
        ) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_taxi)
        } else if (vehicalName.equals("Auto", ignoreCase = true) || vehicalName.equals(
                "Local Auto",
                ignoreCase = true
            )
        ) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_auto)
        }
        return BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_cab)
    }

    fun animateMarker(marker: Marker, location: Location) {
        val handler = Handler()
        val start: Long = SystemClock.uptimeMillis()
        val startLatLng = marker.position
        val startRotation = marker.rotation.toDouble()
        val duration: Long = 500
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            override fun run() {
                val elapsed: Long = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(
                    elapsed.toFloat()
                            / duration
                )
                val lng = t * location.longitude + (1 - t) * startLatLng.longitude
                val lat = t * location.latitude + (1 - t) * startLatLng.latitude
                val rotation = (t * location.bearing + (1 - t)
                        * startRotation).toFloat()
                marker.setPosition(LatLng(lat, lng))
                /*  marker.rotation = rotation
                  if (t < 1.0) {
                      // Post again 16ms later.
                      handler.postDelayed(this, 10000)
                  }*/
            }
        })
    }

    private fun animateMarkerNew(
        startPosition: LatLng,
        destination: LatLng,
        marker: Marker?
    ) {
        if (marker != null) {
            val endPosition =
                LatLng(
                    destination.latitude,
                    destination.longitude
                )
            val startRotation = marker.rotation
            val latLngInterpolator: LatLngInterpolatorNew = LatLngInterpolatorNew.LinearFixed()
            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            if (mMap!!.cameraPosition.zoom <= 18.0){
                valueAnimator.duration = 1000 // duration 2 second
            }else{
                valueAnimator.duration = 500 // duration 2 second
            }

            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    val newPosition: LatLng =
                        latLngInterpolator.interpolate(v, startPosition, endPosition)!!
                    myMarker!!.setPosition(newPosition)
                    mMap!!.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(newPosition)
                                .bearing(getCompassBearing(startPosition, endPosition))
                                .zoom(getZoomLevel())
                                .build()
                        )
                    )

                    myMarker!!.setRotation(
                        getBearing(
                            startPosition,
                            LatLng(destination.latitude, destination.longitude)
                        )
                    )
                } catch (ex: java.lang.Exception) {
                    //I don't care atm..
                }
            }
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    /*  if (myMarker != null) {
                     myMarker.remove();
                     }*/
                }
            })
            valueAnimator.start()
        }
    }

    private interface LatLngInterpolatorNew {
        fun interpolate(
            fraction: Float,
            a: LatLng?,
            b: LatLng?
        ): LatLng?

        class LinearFixed : LatLngInterpolatorNew {
            override fun interpolate(fraction: Float, a: LatLng?, b: LatLng?): LatLng? {
                val lat = (b!!.latitude - a!!.latitude) * fraction + a.latitude
                var lngDelta = b.longitude - a.longitude
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360
                }
                val lng = lngDelta * fraction + a.longitude
                return LatLng(lat, lng)
            }
        }
    }

    private fun getBearing(
        begin: LatLng,
        end: LatLng
    ): Float {
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)
        if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
                Math.atan(lng / lat)
            )
            .toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
            Math.atan(lng / lat)
        ) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 270).toFloat()
        return (-1).toFloat()

    }

    private fun getDistanceBetweenTwoLatLng(current: LatLng, previous: LatLng): Double? {
        var distance: Double = 0.0

        val R = 6371
        val dLat = deg2rad(current.latitude - previous.latitude)
        val dLon = deg2rad(current.longitude - previous.longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(
            deg2rad(current.latitude)
        ) * Math.cos(
            deg2rad(previous.latitude)
        ) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        distance = (R * c) * 1000

        //Toast.makeText(applicationContext, "Distance difference = " + distance.toString(), Toast.LENGTH_LONG).show()
        return distance

    }

    private fun drawRouteWithoutCallingDistanceAPI() {
        var latestPoints: ArrayList<LatLng?>? = ArrayList()
        locationChangelatitude
        locationChangelongitude


        if (markerPoints != null) {

            if (markerPoints?.get(0)?.latitude!! > markerPoints?.get(markerPoints!!.size - 1)?.latitude!!) {
                for (latLng: LatLng? in markerPoints!!) {
                    if (locationChangelatitude >= latLng?.latitude!! || locationChangelongitude >= latLng.longitude) {
                        latestPoints?.add(latLng)
                    }
                }
            } else {
                for (latLng: LatLng? in markerPoints!!) {
                    if (locationChangelatitude <= latLng?.latitude!! || locationChangelongitude <= latLng.longitude) {
                        latestPoints?.add(latLng)
                    }
                }
            }

            Toast.makeText(
                this,
                markerPoints?.size.toString() + " Max to less " + latestPoints?.size,
                Toast.LENGTH_SHORT
            ).show()

            if (mGreyPolyline != null) {
                //Remove the same line from map
                mGreyPolyline?.remove();
            }
            //Add line to map
            mGreyPolyline = mMap?.addPolyline(
                PolylineOptions()
                    .addAll(latestPoints)
                    .width(18f).color(Color.DKGRAY)
            )
        }

    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
        var address: String = ""

        val geocoder = Geocoder(this@TrackRideActivity, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size>0) {
                val returnedAddress = addresses[0]
                val strReturnedAddress =
                    StringBuilder()
                for (j in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j))
                }
                address = strReturnedAddress.toString()
            }else{
                address = ""
            }
        } catch (e: IOException) {
        }

        return address
    }

    private fun formatTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours == 0L && minutes == 0L -> String.format(
                resources.getString(R.string.time_seconds_formatter), seconds
            ) + " Sec"

            hours == 0L && minutes > 0L -> String.format(
                resources.getString(R.string.time_minutes_seconds_formatter), minutes, seconds
            ) + " Min"

            else -> resources.getString(
                R.string.time_hours_minutes_seconds_formatter,
                hours,
                minutes,
                seconds
            ) + " Hrs"
        }
    }

    private fun formatTimeMinSec(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours == 0L && minutes == 0L -> String.format(
                resources.getString(R.string.time_seconds_formatter), seconds
            ) + " Sec"

            hours == 0L && minutes > 0L -> String.format(
                resources.getString(R.string.time_minutes_seconds_formatter), minutes, seconds
            ) + " Min "+ String.format(
                resources.getString(R.string.time_seconds_formatter), seconds
            ) + " Sec"

            else -> resources.getString(
                R.string.time_hours_minutes_seconds_formatter,
                hours,
                minutes,
                seconds
            ) + " Hrs"
        }
    }


    fun updateCamera(bearing: Float) {
        val currentPlace: CameraPosition = CameraPosition.Builder()
            .target(LatLng(locationChangelatitude, locationChangelongitude))
            .bearing(bearing).zoom(getZoomLevel()).build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
    }

    private fun getCompassBearing(
        startlocation: LatLng,
        destlocation: LatLng
    ): Float {


        destLocation = Location("")
        destLocation.latitude = destlocation.latitude
        destLocation.longitude = destlocation.longitude

        startLocation = Location("")
        startLocation.latitude = startlocation.latitude
        startLocation.longitude = startlocation.longitude

        var bearTo: Float = startLocation.bearingTo(destLocation)

        if (bearTo < 0) {
            bearTo += 360;

        }


        /* geoField = GeomagneticField(
             java.lang.Double.valueOf(startLocation.latitude).toFloat(),
             java.lang.Double.valueOf(startLocation.longitude).toFloat(),
             java.lang.Double.valueOf(startLocation.altitude).toFloat(),
             System.currentTimeMillis()
         )*/

        if (isWaiting!! && lastCompassBering != null) {
            return lastCompassBering!!
        } else {
            lastCompassBering = bearTo
        }

        return bearTo
    }

    override fun onBackPressed() {

    }


    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as BackgroundLocationService.LocalBinder
            mService = binder.getService()
            mBound = true
            initLocationUpdates()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    private fun bindBackgroundService() {
        // Bind to LocalService
        Intent(this, BackgroundLocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unBindBackgroundService() {
        if (mBound) {
            mService.stopLocationUpdates()
            unbindService(connection)
            mBound = false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unBindBackgroundService()
    }

    private fun getAddressFromLocation(location: Location): String? {
        var address: String = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (addresses != null && addresses!!.size > 0) {
                val obj = addresses[0]
                address = obj.getAddressLine(0)
                var countryName = obj.countryName
                if (obj.countryName != null && obj.countryName.equals(
                        "United States",
                        ignoreCase = true
                    )
                ) {
                    obj.countryName = "USA"
                }
                if (!countryName.equals("")){
                    address = address.replace(", $countryName", "").replace("- $countryName", "")
                }

                if (obj != null && obj.postalCode!= null && !obj.postalCode.equals("")){
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

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


}
package com.example.fairfare.ui.trackRide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.icu.math.BigDecimal
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle

import org.slf4j.MDC.clear
import android.os.*
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fairfare.R
import com.example.fairfare.base.BaseLocationClass
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.example.fairfare.ui.endrides.EndRidesActivity
import com.example.fairfare.ui.placeDirection.DirectionsJSONParser
import com.example.fairfare.ui.trackRide.NearByPlacesPOJO.NearByResponse
import com.example.fairfare.ui.trackRide.currentFare.CurrentFareeResponse
import com.example.fairfare.ui.trackRide.distMatrixPOJP.DistanceMatrixResponse
import com.example.fairfare.ui.trackRide.snaptoRoad.SnapTORoadResponse
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONException
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackRideActivity : BaseLocationClass(), OnMapReadyCallback, LocationListener {
    lateinit var info: ScheduleRideResponsePOJO
    protected var locationManager: LocationManager? = null
    var waypoints = ""

    var hideshow: String? = "show"

    var sAddress: String? = null
    var dAddress: String? = null
    var trackBoard: String? = ""
    var mMap: GoogleMap? = null
    var sourecemarker: Marker? = null

    var today: Date? = null
    var different = 0.0
    var actulDis = 0.0

    var actualTravelDistance = ArrayList<Double>()


    var locationChangelatitude = 0.0
    var locationChangelongitude = 0.0
    var mPolyline: Polyline? = null
    var updatedPolyline: Polyline? = null
    var mGreyPolyline: Polyline? = null
    private var myMarker: Marker? = null
    private var isMapZoomed: Boolean? = false

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
    var strDistCal: String? = ""
    var actualTime: String? = "0"
    var actualDistance: String? = "0"


    var waitStartLocation: String? = ""
    var waitStartLat: String? = ""
    var waitStartLong: String? = ""

    var passjObject: JSONObject? = null


    var sorceAddress: String? = null
    var destinationAddress: String? = null
    var streetAddress: String? = null
    var deststreetAddress: String? = null

    var markerPoints: ArrayList<LatLng?>? = null
    var globalmarkerPoints: ArrayList<LatLng?>? = null
    var OriginM: LatLng? = null
    val ha = Handler()

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_ride)
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


        ha.postDelayed(object : Runnable {
            override fun run() {

                Log.d("onLocationChangedq", "every10sec")


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


                            logDistance(
                                actulDis,
                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.latitude),
                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 2)!!.longitude),

                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.latitude),
                                (globalmarkerPoints!!.get(globalmarkerPoints!!.size - 1)!!.longitude)
                            )

                            // calDistance()
                            drawNewRoute()
                        }

                    }

                } else {
                    globalmarkerPoints!!.add(OriginM)
                }

                ha.postDelayed(this, 10000)
            }
        }, 10000)




        preferencesManager = PreferencesManager.instance
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        sorceAddress = sharedpreferences!!.getString("SourceAddress", "")
        destinationAddress = sharedpreferences!!.getString("DestinationAddress", "")


        sAddress = intent.getStringExtra("SAddress")
        dAddress = intent.getStringExtra("DAddress")
        tv_carType!!.text = intent.getStringExtra("ImageName")
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

        if ((intent.getStringExtra("MyRidesLat")) != null) {

            val geocoder = Geocoder(this@TrackRideActivity, Locale.getDefault())
            try {
                val addresses =
                    geocoder.getFromLocation(
                        ((intent.getStringExtra("MyRidesLat")).toDouble()),
                        ((intent.getStringExtra("MyRidesLong")).toDouble()), 1
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


            val geocoderDestination = Geocoder(this@TrackRideActivity, Locale.getDefault())
            try {
                val addresses =
                    geocoderDestination.getFromLocation(
                        ((intent.getStringExtra("MyRidesDLat")).toDouble()),
                        ((intent.getStringExtra("MyRidesDLong")).toDouble()), 1
                    )
                if (addresses != null) {
                    val returnedAddress = addresses[0]
                    val strReturnedAddress =
                        StringBuilder()
                    for (j in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j))
                    }
                    deststreetAddress = strReturnedAddress.toString()
                }
            } catch (e: IOException) {
            }

            tv_myCurrentLocation!!.text = streetAddress
            tv_myDropUpLocation!!.text = deststreetAddress


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
        val strescimal = String.format("%.02f", different)

        actualTime = strescimal
        tvTravelTime!!.text = strescimal + " Min"

        var totalActualDistance = 0.0


        // actualTravelDistance add value when draw new route

        if (actualTravelDistance.size > 0) {
            for (i in actualTravelDistance.indices) {
                totalActualDistance = totalActualDistance + actualTravelDistance[i]

            }

            strDistCal = (totalActualDistance / 1000).toString()//meter
            progressBarDistance!!.progress = (totalActualDistance / 1000).toInt()//km
            val dist = String.format("%.02f", strDistCal!!.toDouble())

            actualDistance = dist
            tv_travelledDistance!!.text = dist + " km"
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
        return R * c
    }


    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    @OnClick(R.id.tv_ShowTrackBoard)
    fun showboard() {


        llshowData!!.visibility = View.GONE
        rlData!!.visibility = View.VISIBLE
        tv_ShowTrackBoard!!.visibility = View.GONE



        valueForDistanceandWaitTime()
        currentFare()
        nearByPlaceses()

    }

    @OnClick(R.id.tv_refresh)
    fun refresh() {

        valueForDistanceandWaitTime()
        currentFare()
        nearByPlaceses()


    }

    @OnClick(R.id.btnEndRide)
    fun endRide() {
        ha.removeCallbacksAndMessages(null)

        val alertDialog = AlertDialog.Builder(this)
        // Setting Dialog Title
        //  alertDialog.setTitle("GPS is settings")
        // Setting Dialog Message
        alertDialog.setTitle("FairFare")
        alertDialog.setMessage("Are you sure you want to end this Ride?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Yes") { dialog, which ->

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
                intentr.putExtra("actualTimeTravelled", actualTime)

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
                intentr.putExtra("actualTimeTravelled", actualTime)


                startActivity(intentr)
                finish()
            }


        }
        alertDialog.setNegativeButton("No") { dialog, which -> dialog.cancel() }
        alertDialog.show()

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


                    tv_currentFare!!.text = response.body()!!.rate!!.total


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

    }


    override fun onMapReady(googleMap: GoogleMap?) {
        //  googleMap!!.clear()

        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    (info.ride!!.estimatedTrackRide!!.originPlaceLat)!!.toDouble(),
                    (info.ride!!.estimatedTrackRide!!.originPlaceLong)!!.toDouble()
                ), 15.0f
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
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)
            )
        )


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
        val str_origin = "origin=" + mOrigin.latitude + "," + mOrigin.longitude
        val str_dest = "destination=" + mDestination.latitude + "," + mDestination.longitude

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
                estCurrentDuration = duration.getString("text")
                estCurrentDurationInMin = duration.getString("value")



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


                    // Fetching all the points in i-th route


                }








                if (trackBoard.equals("currentCordinate")) {

                    lineOptions!!.addAll(points)
                    lineOptions!!.width(13f)
                    //  lineOptions.color(Color.GREEN);
                    lineOptions!!.color(this@TrackRideActivity.resources.getColor(R.color.gradientendcolor))


                    if (lineOptions != null) {

                        if (updatedPolyline != null) {
                            updatedPolyline!!.remove()
                            if (mPolyline != null) {
                                mPolyline!!.remove()
                            }
                        }
                        updatedPolyline = mMap!!.addPolyline(lineOptions)
                    } else {
                        Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                            .show()
                    }

                } else {


                    val estCurrDIst = (estCurrentDistance!!.toDouble() / 1000)


                    val estCurrentDist =
                        DecimalFormat("####.##").format((estCurrentDistance!!.toDouble() / 1000)) + " km"
                    tvEstDistance!!.text = "Est.Distance " + estCurrentDist
                    tvEstTime!!.text = "Est.Time " + estCurrentDuration
                    progressBarDistance!!.max = (estCurrDIst)!!.toFloat().toInt()


                    val timeInMin = (estCurrentDurationInMin!!.toInt() / 60).toString() + " mins"

                    val currentTravelledTime = timeInMin!!.replace(" mins", "")
                    if (currentTravelledTime.isNotEmpty()) {
                        progressBarTime!!.max = ((currentTravelledTime)!!.toFloat()).toInt()

                    }
                    tv_distance!!.text =
                        "(Est.Distance:" + estCurrentDist + ") / " + "(Est.Time:" + estCurrentDuration + ")"




                    lineOptions!!.addAll(points)
                    lineOptions!!.width(8f)
                    //  lineOptions.color(Color.GREEN);
                    lineOptions!!.color(this@TrackRideActivity.resources.getColor(R.color.gradientstartcolor))

                    if (lineOptions != null) {

                        if (mPolyline != null) {
                            // mPolyline!!.remove()
                        }
                        mPolyline = mMap!!.addPolyline(lineOptions)
                    } else {
                        Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                            .show()
                    }
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

            if (calculateDate > 60) {
                val timeDiffrence = (calculateDate - 60).toDouble()
                totalWaitTime = totalWaitTime + timeDiffrence

            }
        }

        var timeInMin = 0.0

        if (totalWaitTime > 59) {
            timeInMin = totalWaitTime / 60
            val strTimeCal = String.format("%.02f", timeInMin)
            tv_currentwaitTime!!.text = strTimeCal + " Min"

            val strTime = String.format("%.02f", timeInMin)
            waitTimeForCurrentFare = strTime


        } else {
            timeInMin = totalWaitTime
            tv_currentwaitTime!!.text = timeInMin.toString() + " Sec"

        }


    }

    fun arrWaitTimePostEndRide(): ArrayList<HashMap<String, String>> {
        var arrtime = arrWaitTime

        if (waitTime != null) {
            val time = Date()

            val calculateDate =
                ((time!!.getTime() - waitTime!!.getTime()) / 1000)//time calculate in second

            if (calculateDate > 60) {
                val timeDiffrence = (calculateDate - 60).toDouble()//time difference in min


                var options = HashMap<String, String>()
                options.put("waiting_time", timeDiffrence.toString())
                options.put("full_address", waitStartLocation!!)
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
        val parameters = "$str_origin&$str_dest&$key"

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


                waitLocation = zerothLegs.getString("end_address")
                val endLocation = zerothLegs.getJSONObject("end_location")
                waitLat = endLocation.getString("lat")
                waitLong = endLocation.getString("lng")



                waitStartLocation = zerothLegs.getString("start_address")
                val startLocation = zerothLegs.getJSONObject("start_location")
                waitStartLat = startLocation.getString("lat")
                waitStartLong = startLocation.getString("lng")


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


        Log.d("onLocationChangedq", location!!.speed.toString())
        addCurrentLocationMarker(location)
        locationChangelatitude = location.latitude
        locationChangelongitude = location.longitude


        if (location != null) {


            var currentspeed = (((location.speed) * 3600) / 1000).toInt()





            if (currentspeed!! < 10) {   //speed less than 10 km per hr
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

                    if (calculateDate > 60) {
                        val timeDiffrence = (calculateDate - 60).toDouble()//time in min


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

    private fun addCurrentLocationMarker(location: Location?) {

        val newPosition: LatLng = LatLng(location!!.latitude, location.longitude)
        if (myMarker != null){
            myMarker!!.remove()
        }
        myMarker = mMap!!.addMarker(
            MarkerOptions()
                .position(newPosition)
                .icon(getMarkerIcon(tv_carType!!.text.toString()))
                .anchor(0.5f, 0.5f)
                .draggable(true)
                .flat(true)
                .rotation(location.bearing)
        )

        animateMarker(myMarker!!, location)

        mMap!!.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(newPosition)
                    .zoom(getZoomLevel())
                    .build()
            )
        )


    }

    private fun getZoomLevel(): Float {
        if (!isMapZoomed!!){
            isMapZoomed = true
            return 16f
        }
        return mMap!!.cameraPosition.zoom
    }


    fun getMarkerIcon(vehicalName: String?): BitmapDescriptor? {
        when (vehicalName) {
            "Taxi" -> return BitmapDescriptorFactory.fromResource(R.drawable.car_marker)
            "Auto" -> return BitmapDescriptorFactory.fromResource(R.drawable.car_marker)
        }
        return BitmapDescriptorFactory.fromResource(R.drawable.car_marker)
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
                val lng = t * location.longitude + (1 - t)* startLatLng.longitude
                val lat = t * location.latitude + (1 - t)* startLatLng.latitude
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
            valueAnimator.duration = 2000 // duration 3 second
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    val newPosition: LatLng =
                        latLngInterpolator.interpolate(v, startPosition, endPosition)!!
                    myMarker!!.setPosition(newPosition)
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(mMap!!.cameraPosition.zoom)
                                .build()
                        )
                    )

                    // myMarker.setRotation(getBearing(startPosition, new LatLng(destination.latitude, destination.longitude)));
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

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

}

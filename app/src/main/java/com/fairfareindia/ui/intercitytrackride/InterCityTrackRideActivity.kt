package com.fairfareindia.ui.intercitytrackride

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.BuildConfig
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.databinding.ActivityInterCityTrackRideBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.intercityrides.ridedetails.IntercityRideDetailsActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.intercity.GoogleDistanceModel
import com.fairfareindia.ui.intercitytrackpickup.DriverLocationModel
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.ui.trackRide.NearByPlacesPOJO.NearByResponse
import com.fairfareindia.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject
import java.util.*

class InterCityTrackRideActivity : BaseLocationClass(), OnMapReadyCallback,
    IIntercityTrackRideView {
    lateinit var binding: ActivityInterCityTrackRideBinding
    private var context: Context = this


    private var mMap: GoogleMap? = null

    private var rideID: String? = null
    private var token: String? = null
    private var preferencesManager: PreferencesManager? = null

    private var rideDetailModel: RideDetailModel? = null
    private var iInterCityTrackRidePresenter: IInterCityTrackRidePresenter? = null

    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null

    var driverLat: String? = null
    var driverLong: String? = null

    private var myMarker: Marker? = null
    private var prevLatLng: LatLng? = null
    private var isRouteDrawn: Boolean = false
    private var driverLocationModel: DriverLocationModel? = null
    private var mPolyline: Polyline? = null


    val handler: Handler? = Handler()
    private var myLocationManager: MyLocationManager? = MyLocationManager(this)
    var locationChangeLatitude = 0.0
    var locationChangeLongitude = 0.0
    private var IS_TRACKING_DRIVER_IN_TRACK_RIDE: String? = "false"

    private var remainingDistanceText: String? = null
    private var remainingDistance: Int = 0
    private var remainingTimeText: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterCityTrackRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        rideID = intent.getStringExtra("ride_id")


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        PreferencesManager.initializeInstance(context)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        iInterCityTrackRidePresenter = InterCityTrackRideImplementer(this)

        iInterCityTrackRidePresenter?.getRideDetails(token, rideID)

        IS_TRACKING_DRIVER_IN_TRACK_RIDE =
            AppUtils.getValueOfKeyFromGeneralSettings(context, Constants.IS_TRACKING_FROM_DRIVER)

        if (IS_TRACKING_DRIVER_IN_TRACK_RIDE == "true") {
            getDriverLocationAPI()
        } else {
            initLocationUpdates()
        }



        setListeners()

    }

    private fun initLocationUpdates() {
        myLocationManager?.getMyCurrentLocationChange(object :
            MyLocationManager.LocationManagerTrackInterface {
            override fun onMyLocationChange(
                currentLocation: MutableList<Location>?,
                lastLocation: Location?
            ) {
                if (lastLocation != null) {
                    if(rideDetailModel != null){
                        addCurrentLocationMarker(lastLocation)
                    }


                    locationChangeLatitude = lastLocation!!.latitude
                    locationChangeLongitude = lastLocation!!.longitude

                    getRouteAPI()
                    getDriverLocationAPI()
                    if (!isDestroyed){
                        iInterCityTrackRidePresenter?.getNearByPlaces(driverLat, driverLong)
                    }


                }
            }

        })
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            txtTrackBoard.setOnClickListener {
                llTrackData.visibility = View.VISIBLE
                txtTrackBoard.visibility = View.GONE
                llShowData.visibility = View.GONE
            }

            imgClose.setOnClickListener {
                llTrackData.visibility = View.GONE
                txtTrackBoard.visibility = View.VISIBLE
                llShowData.visibility = View.VISIBLE
            }

            imgRefresh.setOnClickListener {
                iInterCityTrackRidePresenter?.getRideDetails(token, rideID)
                iInterCityTrackRidePresenter?.getNearByPlaces(driverLat, driverLong)
            }

            rlHideShow.setOnClickListener {
                if (llHideView.visibility == View.GONE) {
                    llHideView.visibility = View.VISIBLE
                } else {
                    llHideView.visibility = View.GONE
                }
            }

            imgHome.setOnClickListener {
                var sharedpreferences: SharedPreferences =
                    getSharedPreferences("mypref", Context.MODE_PRIVATE)
                sharedpreferences.edit().clear().apply()
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }


        }
    }

    private fun setData() {
        binding.apply {

            txtPickUpLocation.text = rideDetailModel?.data?.originAddress
            txtDropUpLocation.text = rideDetailModel?.data?.destinationAddress

            txtDistanceTime.text = getString(R.string.str_est_distance) +
                    " - ${rideDetailModel?.data?.estimatedTrackRide?.distance} km / " + getString(
                R.string.str_est_time
            ) + " - ${rideDetailModel?.data?.estimatedTrackRide?.totalTime}"

            txtWaitTime.text = ProjectUtilities.timeInSecondsConvertingToString(
                context,
                rideDetailModel?.data?.total_wait_time.toString()
            )


            if (rideDetailModel?.data?.permitType == Constants.TYPE_LOCAL){
                txtCurrentFare.text = ProjectUtilities.getAmountInFormat(rideDetailModel?.data?.currentFareLocal)
            }else{
                txtCurrentFare.text = ProjectUtilities.getAmountInFormat(rideDetailModel?.data?.totalfare)
            }



            sourceLat = rideDetailModel?.data?.originLatitude
            sourceLong = rideDetailModel?.data?.originLongitude

            destinationLat = rideDetailModel?.data?.destinationLatitude
            destinationLong = rideDetailModel?.data?.destinationLongitude

            if (!sourceLat.isNullOrEmpty() && !destinationLat.isNullOrEmpty()) {
                drawSourceDestinationMarker()
            }

            //Driver and Vehical
            txtVehicleName.text = rideDetailModel?.data?.vehicleName

            Glide.with(context)
                .load(rideDetailModel?.data?.vehicleImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                ).into(imgVehicle)


            setTravelledDistanceTime(rideDetailModel?.data?.totalDistTravelled)
        }
    }

    private fun setTravelledDistanceTime(totalDistTravelled : String?) {
        if (remainingDistanceText != null) {
            binding.txtTravelledDistance.text =
                totalDistTravelled + " km / $remainingDistanceText"


            var rideDateTime =
                AppUtils.getDate(rideDetailModel?.data?.start_date, "yyyy-MM-dd HH:mm:ss")
            if (rideDateTime != null) {
                var calculateActTime = Date().time - rideDateTime.time
                var actTotalTimeInMin = ((calculateActTime / 1000) / 60).toInt()
                binding.txtTravelTime.text = ProjectUtilities.timeInMinutesConvertingToString(
                    context,
                    actTotalTimeInMin.toString()
                ) + " / $remainingTimeText"
            }
        } else {
            binding.txtTravelledDistance.text =
                totalDistTravelled + " km"


            var rideDateTime =
                AppUtils.getDate(rideDetailModel?.data?.start_date, "yyyy-MM-dd HH:mm:ss")
            if (rideDateTime != null) {
                var calculateActTime = Date().time - rideDateTime.time
                var actTotalTimeInMin = ((calculateActTime / 1000) / 60).toInt()
                binding.txtTravelTime.text = ProjectUtilities.timeInMinutesConvertingToString(
                    context,
                    actTotalTimeInMin.toString()
                )
            }
        }

    }


    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        if (!sourceLat.isNullOrEmpty() && !destinationLat.isNullOrEmpty()) {
            drawSourceDestinationMarker()
        }
    }

    private fun addCurrentLocationMarker(location: Location?) {

        val newPosition: LatLng = LatLng(location!!.latitude, location.longitude)
        if (myMarker != null) {
            if (prevLatLng != null) {
                animateMarkerNew(prevLatLng!!, newPosition, myMarker)
            }
            myMarker!!.remove()
        }
        myMarker = mMap!!.addMarker(
            MarkerOptions()
                .position(newPosition)
                .icon(getMarkerIcon(rideDetailModel?.data?.vehicleType))
                .anchor(0.5f, 0.5f)
                .draggable(true)
                .flat(true)
                .rotation(location.bearing)
        )


        prevLatLng = newPosition

        mMap!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(newPosition)
                    .zoom(getZoomLevel())
                    .build()
            )
        )

    }

    private fun addCurrentLocationMarker(driverLocationModel: DriverLocationModel?) {

        val newPosition: LatLng =
            LatLng(driverLocationModel?.data?.latitude!!, driverLocationModel.data?.longitude!!)

        if (myMarker == null) {
            myMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(newPosition)
                    .icon(getMarkerIcon(rideDetailModel?.data?.vehicleType))
                    .anchor(0.5f, 0.5f)
                    .draggable(true)
                    .flat(true)
                    .rotation(driverLocationModel.data?.bearing!!)
            )
        } else {
            myMarker?.rotation = driverLocationModel.data?.bearing!!
            myMarker?.position = newPosition
        }

        if (myMarker != null && prevLatLng != null) {
            animateMarkerNew(prevLatLng!!, newPosition, myMarker)
        }


        prevLatLng = newPosition

        mMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(newPosition)
                    .zoom(getZoomLevel())
                    .build()
            )
        )

    }

    private fun getZoomLevel(): Float {
        if (mMap?.cameraPosition?.zoom!! < 10) {
            return 18.0f
        }
        return mMap?.cameraPosition?.zoom!!
    }

    private fun getMarkerIcon(vehicleName: String?): BitmapDescriptor? {
        if (vehicleName.equals("Taxi", ignoreCase = true) || vehicleName.equals(
                "Local Taxi",
                ignoreCase = true
            )
        ) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_taxi)
        } else if (vehicleName.equals("Auto", ignoreCase = true) || vehicleName.equals(
                "Local Auto",
                ignoreCase = true
            )
        ) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_auto)
        }
        return BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_cab)
    }

    private fun drawSourceDestinationMarker() {
        mMap?.addMarker(
            MarkerOptions().position(
                com.google.android.gms.maps.model.LatLng(
                    sourceLat!!.toDouble(),
                    sourceLong!!.toDouble()
                )


            ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
        )

        mMap?.addMarker(
            MarkerOptions().position(
                com.google.android.gms.maps.model.LatLng(
                    destinationLat!!.toDouble(),
                    destinationLong!!.toDouble()
                )
            ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_grey))
        )
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
            val latLngInterpolator: LatLngInterpolatorNew = LatLngInterpolatorNew.LinearFixed()
            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)

            if (IS_TRACKING_DRIVER_IN_TRACK_RIDE == "true") {
                if (mMap!!.cameraPosition.zoom <= 18.0) {
                    valueAnimator.duration = 3000 // duration 2 second
                } else {
                    valueAnimator.duration = 1500 // duration 2 second
                }
            } else {
                if (mMap!!.cameraPosition.zoom <= 18.0) {
                    valueAnimator.duration = 1000 // duration 2 second
                } else {
                    valueAnimator.duration = 500 // duration 2 second
                }
            }


            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    val newPosition: LatLng =
                        latLngInterpolator.interpolate(v, startPosition, endPosition)!!
                    myMarker?.position = newPosition
                    mMap?.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(newPosition)
                                .bearing(getCompassBearing(startPosition, endPosition))
                                .zoom(getZoomLevel())
                                .build()
                        )
                    )

                    myMarker?.rotation = getBearing(
                        startPosition,
                        LatLng(destination.latitude, destination.longitude)
                    )
                } catch (ex: java.lang.Exception) {
                    //I don't care atm..
                }
            }
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    if (IS_TRACKING_DRIVER_IN_TRACK_RIDE == "true") {
                        myMarker?.position = destination
                        myMarker?.rotation = driverLocationModel?.data?.bearing!!
                    }
                }
            })
            valueAnimator.start()
        }
    }

    private fun getCompassBearing(startlocation: LatLng, destlocation: LatLng): Float {


        var destLocation = Location("")
        destLocation.latitude = destlocation.latitude
        destLocation.longitude = destlocation.longitude

        var startLocation = Location("")
        startLocation.latitude = startlocation.latitude
        startLocation.longitude = startlocation.longitude

        var bearTo: Float = startLocation.bearingTo(destLocation)

        if (bearTo < 0) {
            bearTo += 360;

        }


        return bearTo
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


    private fun getRouteAPI() {
        val url =
            "https://maps.googleapis.com/maps/api/directions/json?units=metric&origin=" + locationChangeLatitude + "," + locationChangeLongitude + "&destination=" + destinationLat + "," + destinationLong + "&key=" + getString(
                R.string.google_maps_key
            )

        APIManager.getInstance(context).postAPI(
            url,
            null,
            GoogleDistanceModel::class.java,
            context,
            object : APIManager.APIManagerInterface {
                override fun onSuccess(resultObj: Any?, jsonObject: JSONObject) {


                    var routes: List<List<HashMap<String, String>>>? = null

                    val parser = DirectionsJSONParser()
                    val array = jsonObject.getJSONArray("routes")
                    val routes1 = array.getJSONObject(0)
                    val legs = routes1.getJSONArray("legs")
                    val steps = legs.getJSONObject(0)
                    val distance = steps.getJSONObject("distance")
                    val duration = steps.getJSONObject("duration")
                    val actualSteps = steps.getJSONArray("steps")

                    remainingTimeText = duration.getString("text")
                    remainingDistanceText = distance.getString("text")
                    remainingDistance = distance.getInt("value")


                    if (rideDetailModel?.data?.estimatedTrackRide?.distance != null){
                        binding.progressDistance.max = 100
                        var percentage = (rideDetailModel?.data?.estimatedTrackRide?.distance?.times(1000)!! - remainingDistance)/ rideDetailModel?.data?.estimatedTrackRide?.distance?.times(1000)!!
                        binding.progressDistance.progress = percentage.times(100).toInt()
                    }

                    if(driverLocationModel?.data?.totalDistTravelled == null){
                        setTravelledDistanceTime(rideDetailModel?.data?.totalDistTravelled)
                    }else{
                        setTravelledDistanceTime(driverLocationModel?.data?.totalDistTravelled)
                    }



                    routes = parser.parse(jsonObject)

                    var points: ArrayList<LatLng?>?
                    var lineOptions: PolylineOptions? = null

                    // Traversing through all the routes
                    for (i in routes.indices) {
                        points = ArrayList()
                        lineOptions = PolylineOptions()

                        // Fetching i-th route
                        val path =
                            routes[i]

                        // Fetching all the points in i-th route
                        for (j in path.indices) {
                            val point = path[j]
                            val lat = point["lat"]!!.toDouble()
                            val lng = point["lng"]!!.toDouble()
                            val position =
                                com.google.android.gms.maps.model.LatLng(lat, lng)
                            points.add(position)
                        }

                        if (points.size >= 2) {
                            updateCamera(getCompassBearing(points[0]!!, points[1]!!))
                        }
                        lineOptions.addAll(points)
                        lineOptions.width(8f)
                        //  lineOptions.color(Color.GREEN);
                        lineOptions.color(
                            context.resources.getColor(R.color.gradientstartcolor)
                        )
                    }

                    // Drawing polyline in the Google Map for the i-th route
                    if (lineOptions != null) {
                        mPolyline?.remove()
                        mPolyline = mMap?.addPolyline(lineOptions)
                        isRouteDrawn = true
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.str_no_route_found),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

                }

                override fun onError(error: String?) {
                }

            })
    }


    fun updateCamera(bearing: Float) {
        val currentPlace: CameraPosition = CameraPosition.Builder()
            .target(LatLng(locationChangeLatitude, locationChangeLongitude))
            .bearing(bearing).zoom(getZoomLevel()).build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
    }

    private fun setHandler() {
        var trackDuration =
            AppUtils.getValueOfKeyFromGeneralSettings(context, Constants.KEY_TRACK_DURATION)
        if (trackDuration.isNullOrEmpty()) {
            trackDuration = Constants.LOCATION_HANDLER_TIME.toString()
        }
        handler?.postDelayed(object : Runnable {
            override fun run() {
                getDriverLocationAPI()
                handler.postDelayed(this, trackDuration.toLong())
            }
        }, trackDuration.toLong())

    }

    /**
     * API CALLING
     */

    private fun getDriverLocationAPI() {
        var url = BuildConfig.API_URL + Constants.API_GET_DRIVER_LOCATION
        var params: JSONObject = JSONObject()
        params.put("ride_id", rideID)
        params.put("token", token)
        APIManager.getInstance(context)
            .postAPI(url, params, DriverLocationModel::class.java, context, object :
                APIManager.APIManagerInterface {
                override fun onSuccess(resultObj: Any?, jsonObject: JSONObject) {
                    driverLocationModel = resultObj as DriverLocationModel?
                    driverLat = driverLocationModel?.data?.latitude.toString()
                    driverLong = driverLocationModel?.data?.longitude.toString()


                    if (IS_TRACKING_DRIVER_IN_TRACK_RIDE == "true") {
                        locationChangeLatitude = driverLocationModel?.data?.latitude!!
                        locationChangeLongitude = driverLocationModel?.data?.longitude!!
                        addCurrentLocationMarker(driverLocationModel)
                        getRouteAPI()
                        iInterCityTrackRidePresenter?.getNearByPlaces(driverLat, driverLong)
                    }

                    instantUpdateTrackBoard()
                    if (driverLocationModel?.data?.status == Constants.BOOKING_COMPLETED) {
                        handler?.removeCallbacksAndMessages(null)
                        myLocationManager?.stopLocationUpdates()
                        startActivity(
                            Intent(context, IntercityRideDetailsActivity::class.java)
                                .putExtra("ride_id", rideID)
                                .putExtra("isFromEndRide", true)
                        )
                        finish()
                    }


                }

                override fun onError(error: String?) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun instantUpdateTrackBoard() {
        setTravelledDistanceTime(driverLocationModel?.data?.totalDistTravelled)



        if (driverLocationModel?.data?.permit_type == Constants.TYPE_LOCAL){
            binding.txtCurrentFare.text = ProjectUtilities.getAmountInFormat(driverLocationModel?.data?.totalfare)
        }

        binding.txtWaitTime.text = ProjectUtilities.timeInSecondsConvertingToString(
            context,
            driverLocationModel?.data?.ride_wait_time.toString()
        )

    }


    override fun getRideDetails(model: RideDetailModel?) {
        rideDetailModel = model
        setData()
        if (IS_TRACKING_DRIVER_IN_TRACK_RIDE == "true") {
            setHandler()
        }

    }

    override fun getNearByPlaces(model: NearByResponse?) {
        for (i in model?.results?.indices!!) {

            if (model.results[i]?.types?.get(0)?.contains("atm")!!) {
                binding.txtAtm.text = model.results[i]!!.name
                Glide.with(context)
                    .load(model.results[i]?.icon)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(binding.imgAtm)


                if (binding.txtAtm.text.isNotEmpty()) {
                    binding.llAtm.visibility = View.VISIBLE
                } else {
                    binding.llAtm.visibility = View.GONE
                }


            }

            if (model.results[i]?.types?.get(0).equals("point_of_interest")
            ) {
                binding.txtMuseum.text = model.results[i]?.name
                Glide.with(context)
                    .load(model.results[i]?.icon)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(binding.imgMuseum)
                if (binding.txtMuseum.text.isNotEmpty()) {
                    binding.llMuseum.visibility = View.VISIBLE
                } else {
                    binding.llMuseum.visibility = View.GONE
                }


            }

            if (model.results[i]?.types?.get(0)
                    ?.contains("health")!! || model.results[i]?.types?.get(0)
                    ?.contains("doctor")!! || model.results[i]?.types?.get(0)
                    ?.contains("hospital")!!
            ) {
                binding.txtHospital.text = model.results[i]?.name
                Glide.with(context)
                    .load(model.results[i]?.icon)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(binding.imgHospital)

                if (binding.txtHospital.text.isNotEmpty()) {
                    binding.llHospital.visibility = View.VISIBLE
                } else {
                    binding.llHospital.visibility = View.GONE
                }


            }

            if (model.results[i]?.types?.get(0)?.contains("bank")!!) {
                binding.txtHotel.text = model?.results!!.get(i)!!.name
                Glide.with(context)
                    .load(model?.results!!.get(i)!!.icon)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(binding.imgHotel)

                if (binding.txtHotel.text.isNotEmpty()) {
                    binding.llHotel.visibility = View.VISIBLE
                } else {
                    binding.llHotel.visibility = View.GONE
                }


            }


        }
    }


    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            context,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()


    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(context)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(context, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        handler?.removeCallbacksAndMessages(null)
        if (SessionManager.getInstance(context).isSplashDisplayed) {
            super.onBackPressed()
        } else {
            //It means its directly came here from notification click
            ProjectUtilities.restartWithSplash(context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myLocationManager?.stopLocationUpdates()
        handler?.removeCallbacksAndMessages(null)
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
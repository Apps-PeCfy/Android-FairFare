package com.fairfareindia.ui.intercitytrackpickup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.BuildConfig
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.databinding.ActivityTrackPickUpBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.common.CommonMessageDialog
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.intercity.GoogleDistanceModel
import com.fairfareindia.ui.intercitytrackride.InterCityTrackRideActivity
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.ui.ridedetails.RideDetailsActivity
import com.fairfareindia.utils.APIManager
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject
import java.util.*

class TrackPickUpActivity :  BaseLocationClass(), OnMapReadyCallback, IIntercityTrackPickUpView {
    lateinit var binding: ActivityTrackPickUpBinding
    private var context: Context = this


    private var mMap: GoogleMap? = null

    private var token: String? = null
    private var rideID: String? = null
    private var model: RideDetailModel ?= null
    private var driverLocationModel: DriverLocationModel ?= null
    var commonMessageDialog: CommonMessageDialog? = null
    private var preferencesManager: PreferencesManager? = null
    private var iInterCityTrackPickUpPresenter: IInterCityTrackPickUpPresenter? = null

    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null

    var driverLat: String? = null
    var driverLong: String? = null

    private var sourceMarker: Marker ?= null
    private var myMarker: Marker ?= null
    private var prevLatLng: LatLng? = null
    private var isRouteDrawn: Boolean = false

    val handler : Handler ?= Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackPickUpBinding.inflate(layoutInflater)
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

        iInterCityTrackPickUpPresenter = InterCityTrackPickUpImplementer(this)

        iInterCityTrackPickUpPresenter?.getRideDetails(token, rideID)

        iInterCityTrackPickUpPresenter?.getDriverLocation(token, rideID)



        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbarHome.setNavigationOnClickListener { onBackPressed() }

            rlHideShow.setOnClickListener {
                if (llHideView.visibility == View.VISIBLE){
                    llHideView.visibility = View.GONE
                }else{
                    llHideView.visibility = View.VISIBLE
                }
            }

            rlAdditional.setOnClickListener {
                if (llAdditionalCharges.visibility == View.VISIBLE) {
                    llAdditionalCharges.visibility = View.GONE
                } else {
                    llAdditionalCharges.visibility = View.VISIBLE
                }

            }

            btnCancelRide.setOnClickListener {
                openConfirmationDialog()
            }

            crdCall.setOnClickListener {
                if (!model?.data?.driver?.phoneNo.isNullOrEmpty()){
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model?.data?.driver?.phoneNo))
                    startActivity(intent)
                }

            }
        }
    }

    private fun openConfirmationDialog() {
        commonMessageDialog = CommonMessageDialog(context,getString(R.string.str_cancel_ride_message), getString(R.string.btn_cancel_not), getString(R.string.btn_proceed), object : CommonMessageDialog.CommonMessageDialogInterface{
            override fun onPositiveButtonClick() {
                commonMessageDialog?.dismiss()
            }

            override fun onNegativeButtonClick() {
                iInterCityTrackPickUpPresenter?.cancelRide(token, rideID, Constants.BOOKING_CANCELLED)
                commonMessageDialog?.dismiss()
            }

        })
        commonMessageDialog?.setCancelable(false)
        commonMessageDialog?.show()
    }

    private fun setData() {
        binding.apply {

            txtPickUpLocation.text = model?.data?.originAddress
            txtDropOffLocation.text = model?.data?.destinationAddress

            txtPickUpLocationHide.text = model?.data?.originAddress
            txtDropOffLocationHide.text = model?.data?.destinationAddress
            txtOtp.text = "Start OTP - " + model?.data?.rideOtp

            tvLuggageCharges.text = "₹ " + model?.data?.estimatedTrackRide?.luggageCharges

            txtBaseFare.text = "₹ " + model?.data?.estimatedTrackRide?.basicFare
            txtTollCharges.text = "₹ " + model?.data?.estimatedTrackRide?.tollCharges

            sourceLat =  model?.data?.originLatitude
            sourceLong =  model?.data?.originLongitude

            destinationLat = model?.data?.destinationLatitude
            destinationLong = model?.data?.destinationLongitude

            if (!sourceLat.isNullOrEmpty() && sourceMarker == null && mMap != null){
                drawSourceMarker()
            }


            if (model?.data?.estimatedTrackRide?.distance!! > model?.data?.estimatedTrackRide?.baseDistance!!) {
                var extraDistance = model?.data?.estimatedTrackRide?.distance!!.toInt() - model?.data?.estimatedTrackRide?.baseDistance!!.toInt()
                txtChargesForAdditionalKmLabel.text =
                    getString(R.string.str_charges_for_additional) + " $extraDistance " + "Km"
            } else {
                txtChargesForAdditionalKmLabel.text = getString(R.string.str_charges_for_additional)

            }
            txtBaseFareLabel.text =
                getString(R.string.str_base_fare) + "( ${model?.data?.estimatedTrackRide?.baseDistance!!.toInt()} Km )"


            tvChargesForAdditionalKm.text = "₹ " + model?.data?.estimatedTrackRide?.additionalDistanceCharges

            tvSurCharges.text = "₹ " + model?.data?.estimatedTrackRide?.surCharge

            tvConvenienceFees.text = "₹ " + model?.data?.estimatedTrackRide?.convenienceFees
            txtTotalPayable.text = "₹ " + model?.data?.estimatedTrackRide?.totalCharges
            txtAdditionalCharges.text = "₹ " +model?.data?.estimatedTrackRide?.totalAdditionalCharges

            if (model?.data?.status == Constants.BOOKING_SCHEDULED){
                btnCancelRide.visibility = View.VISIBLE
                txtStatusMessage.text = getString(R.string.lbl_ride_on_way)
            }else{
                txtStatusMessage.text = getString(R.string.lbl_ride_cancelled)
                btnCancelRide.visibility = View.GONE
            }

            if(model?.data?.rules.isNullOrEmpty()){
                txtRulesLabel.visibility = View.GONE
                txtRules.visibility = View.GONE
            }else{
                txtRules.text = model?.data?.rules
                txtRulesLabel.visibility = View.VISIBLE
                txtRules.visibility = View.VISIBLE
            }


            //Driver and Vehical
            txtVehicleName.text =  model?.data?.vehicleName
            txtVehicleNumber.text = model?.data?.vehicleNo
            txtDriverName.text = model?.data?.driver?.name

            Glide.with(context)
                .load(model?.data?.vehicleImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                ).into(imgVehicle)
        }
    }

    /**
     * Google Direction Related
     */

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        if (!sourceLat.isNullOrEmpty() && sourceMarker == null){
            drawSourceMarker()
        }
    }

    private fun addCurrentLocationMarker(driverLocationModel: DriverLocationModel?) {

        var location = Location("")
        location.latitude = driverLocationModel?.data?.latitude!!
        location.longitude = driverLocationModel.data?.longitude!!

        val newPosition: LatLng = LatLng(driverLocationModel?.data?.latitude!!, driverLocationModel.data?.longitude!!)
        if (prevLatLng != null) {
            animateMarkerNew(prevLatLng!!, newPosition, myMarker)
        }

        if (!driverLocationModel.data?.bearing?.isNaN()!! && driverLocationModel.data?.bearing != 0F) {
            myMarker?.remove()
            myMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(newPosition)
                    .icon(getMarkerIcon(model?.data?.vehicleName))
                    .anchor(0.5f, 0.5f)
                    .draggable(true)
                    .flat(true)
                    .rotation(driverLocationModel.data?.bearing!!)
            )
        }else{
            myMarker?.remove()
            myMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(newPosition)
                    .icon(getMarkerIcon(model?.data?.vehicleName))
                    .anchor(0.5f, 0.5f)
                    .draggable(true)
                    .flat(true)
            )
        }


        prevLatLng = newPosition

        mMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(newPosition)
                    //  .bearing(getCompassBearing(startLocation, destLocation))
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

    private fun drawSourceMarker() {
        sourceMarker = mMap?.addMarker(
            MarkerOptions().position(
                com.google.android.gms.maps.model.LatLng(
                    sourceLat!!.toDouble(),
                    sourceLong!!.toDouble()
                )
            ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
        )

        val customInfoWindow = CustomInfoWindowGoogleMap(this)
        sourceMarker?.title = "16"
        sourceMarker?.snippet = "min"
        mMap?.setInfoWindowAdapter(customInfoWindow)
        sourceMarker?.showInfoWindow()
    }

    private fun updateTimeToPickUP(duration: JSONObject) {
        val customInfoWindow = CustomInfoWindowGoogleMap(this)
        var timeInSeconds = duration.getString("value")

        sourceMarker?.title = (timeInSeconds.toDouble()/60).toInt().toString()
        sourceMarker?.snippet = "Min"
        mMap?.setInfoWindowAdapter(customInfoWindow)
        sourceMarker?.showInfoWindow()
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

                    myMarker?.rotation = getBearing(startPosition, LatLng(destination.latitude, destination.longitude))
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
        val url = "https://maps.googleapis.com/maps/api/directions/json?units=metric&origin=" + driverLat + "," + driverLong + "&destination=" + sourceLat + "," + sourceLong + "&key=" + getString(R.string.google_maps_key)

        APIManager.getInstance(context).postAPI(
            url,
            null,
            GoogleDistanceModel::class.java,
            context,
            object : APIManager.APIManagerInterface {
                override fun onSuccess(resultObj: Any?, jsonObject: JSONObject) {


                    var mPolyline: Polyline? = null


                    var routes: List<List<HashMap<String, String>>>? = null

                    val parser = DirectionsJSONParser()
                    val array = jsonObject.getJSONArray("routes")
                    val routes1 = array.getJSONObject(0)
                    val legs = routes1.getJSONArray("legs")
                    val steps = legs.getJSONObject(0)
                    val distance = steps.getJSONObject("distance")
                    val duration = steps.getJSONObject("duration")
                    val actualSteps = steps.getJSONArray("steps")

                    updateTimeToPickUP(duration)

                    routes = parser.parse(jsonObject)

                    var points: ArrayList<com.google.android.gms.maps.model.LatLng?>?
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
                    } else{
                        Toast.makeText(context, "No route is found", Toast.LENGTH_LONG)
                            .show()
                    }

                }

                override fun onError(error: String?) {
                }

            })
    }

    private fun setHandler() {
        handler?.postDelayed(object : Runnable {
            override fun run() {
                getDriverLocationAPI()
                handler.postDelayed(this, Constants.LOCATION_HANDLER_TIME)
            }
        }, Constants.LOCATION_HANDLER_TIME)

    }

    /**
     * API CALLING
     */

    private fun getDriverLocationAPI(){
        var url = BuildConfig.API_URL + "getDriverLocation"
        var params : JSONObject = JSONObject()
        params.put("ride_id", rideID)
        params.put("token", token)
        APIManager.getInstance(context).postAPI(url, params, DriverLocationModel::class.java, context, object :APIManager.APIManagerInterface{
            override fun onSuccess(resultObj: Any?, jsonObject: JSONObject) {
                driverLocationModel = resultObj as DriverLocationModel?
                if (!isRouteDrawn){
                    getRouteAPI()
                }

                if (driverLocationModel?.data?.status == Constants.BOOKING_ACTIVE){
                    handler?.removeCallbacksAndMessages(null)
                    startActivity(Intent(context, InterCityTrackRideActivity::class.java)
                        .putExtra("ride_id", rideID))
                    finish()
                }

                addCurrentLocationMarker(driverLocationModel)
            }

            override fun onError(error: String?) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun getRideDetails(model: RideDetailModel?) {
        this.model = model
        setData()
    }

    override fun getDriverLocation(model: DriverLocationModel?) {
        driverLocationModel = model
        addCurrentLocationMarker(driverLocationModel)
        driverLat = driverLocationModel?.data?.latitude.toString()
        driverLong = driverLocationModel?.data?.longitude.toString()


        if (!isRouteDrawn){
            getRouteAPI()
        }

        setHandler()

    }

    override fun getCancelRideSuccess(getRideResponsePOJO: GetRideResponsePOJO?) {
        iInterCityTrackPickUpPresenter?.getRideDetails(token, rideID)
        handler?.removeCallbacksAndMessages(null)
        Constants.SHOULD_RELOAD = true
        finish()
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
        super.onBackPressed()
        handler?.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
      //  handler?.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
    }
}
package com.fairfareindia.ui.intercitytrackpickup

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.databinding.ActivityTrackPickUpBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercity.GoogleDistanceModel
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
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
    private var preferencesManager: PreferencesManager? = null
    private var iInterCityTrackPickUpPresenter: IInterCityTrackPickUpPresenter? = null

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
        }
    }

    private fun setData() {
        binding.apply {

            txtPickUpLocation.text = model?.data?.originAddress
            txtDropOffLocation.text = model?.data?.destinationAddress

            txtPickUpLocationHide.text = model?.data?.originAddress
            txtDropOffLocationHide.text = model?.data?.destinationAddress
            txtOtp.text = model?.data?.rideOtp

            tvLuggageCharges.text = "₹ " + model?.data?.estimatedTrackRide?.luggageCharges

            txtBaseFare.text = "₹ " + model?.data?.estimatedTrackRide?.basicFare
            txtTollCharges.text = "₹ " + model?.data?.estimatedTrackRide?.tollCharges


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

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        if (! model?.data?.originLatitude.isNullOrEmpty() && !model?.data?.destinationLatitude.isNullOrEmpty()) {
            mMap!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    com.google.android.gms.maps.model.LatLng(
                        model?.data?.originLatitude?.toDouble()!!,
                        model?.data?.originLongitude?.toDouble()!!
                    ), 12.0f
                )
            )
            mMap?.addMarker(
                MarkerOptions().position(
                    com.google.android.gms.maps.model.LatLng(
                        model?.data?.originLatitude!!.toDouble(),
                        model?.data?.originLongitude!!.toDouble()
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
            )
            mMap?.addMarker(
                MarkerOptions().position(
                    com.google.android.gms.maps.model.LatLng(
                        model?.data?.destinationLatitude!!.toDouble(),
                        model?.data?.destinationLongitude!!.toDouble()
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_grey))
            )
            getRouteAPI()
        }
    }

    private fun getRouteAPI() {
        val url = "https://maps.googleapis.com/maps/api/directions/json?units=metric&origin=" + model?.data?.originLatitude + "," + model?.data?.originLongitude + "&destination=" + model?.data?.destinationLatitude + "," + model?.data?.destinationLongitude + "&key=" + getString(
            R.string.google_maps_key)

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
                    } else{
                        Toast.makeText(context, "No route is found", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onError(error: String?) {
                }

            })
    }

    /**
     * API CALLING
     */

    override fun getRideDetails(model: RideDetailModel?) {
        this.model = model
        setData()
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
}
package com.fairfareindia.ui.intercitytrackpickup

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.databinding.ActivityTrackPickUpBinding
import com.fairfareindia.ui.intercity.GoogleDistanceModel
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.utils.APIManager
import com.fairfareindia.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject
import java.util.*

class TrackPickUpActivity :  BaseLocationClass(), OnMapReadyCallback {
    lateinit var binding: ActivityTrackPickUpBinding
    private var context: Context = this

    private var sourceAddress: String? = null
    private var destinationAddress: String? = null
    private var sourceLat: String? = null
    private var sourceLong: String? = null
    private var destinationLat: String? = null
    private var destinationLong: String? = null

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackPickUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        sourceAddress = intent.getStringExtra("SourceAddress")
        destinationAddress = intent.getStringExtra("DestinationAddress")
        sourceLat = intent.getStringExtra("SourceLat")
        sourceLong = intent.getStringExtra("SourceLong")
        destinationLat = intent.getStringExtra("DestinationLat")
        destinationLong = intent.getStringExtra("DestinationLong")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        setData()
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
        }
    }

    private fun setData() {
        binding.apply {

            txtPickUpLocation.text = sourceAddress
            txtDropOffLocation.text = destinationAddress

            txtPickUpLocationHide.text = sourceAddress
            txtDropOffLocationHide.text = destinationAddress
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        if (!sourceLat.isNullOrEmpty() && !destinationLat.isNullOrEmpty()) {
            mMap!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    com.google.android.gms.maps.model.LatLng(
                        sourceLat?.toDouble()!!,
                        sourceLong?.toDouble()!!
                    ), 12.0f
                )
            )
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
            getRouteAPI()
        }
    }

    private fun getRouteAPI() {
        val url = "https://maps.googleapis.com/maps/api/directions/json?units=metric&origin=" + sourceLat + "," + sourceLong + "&destination=" + destinationLat + "," + destinationLong + "&key=" + getString(
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
}
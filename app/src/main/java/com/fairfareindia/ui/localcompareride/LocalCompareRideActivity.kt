package com.fairfareindia.ui.localcompareride

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.databinding.ActivityLocalCompareRideBinding
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.intercity.GoogleDistanceModel
import com.fairfareindia.ui.intercityviewride.IntercityViewRideActivity
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.utils.APIManager
import com.fairfareindia.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*

class LocalCompareRideActivity : BaseLocationClass(), OnMapReadyCallback {
    lateinit var binding: ActivityLocalCompareRideBinding
    private var context: Context = this

    private lateinit var info: CompareRideResponsePOJO
    private var mAdapter: LocalCompareRideAdapter ?= null
    private var list: ArrayList<CompareRideResponsePOJO.VehiclesItem> = ArrayList()

    var sourceAddress: String? = null
    var destinationAddress: String? = null
    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null
    private var estTimeInSeconds: String? = null
    private var scheduleType: String? = null

    var mMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalCompareRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        Places.initialize(this, resources.getString(R.string.google_maps_key))
        Places.createClient(context)

        info = intent.getSerializableExtra("MyPOJOClass") as CompareRideResponsePOJO
        sourceAddress = intent.getStringExtra("SourceAddress")
        destinationAddress = intent.getStringExtra("DestinationAddress")
        sourceLat = intent.getStringExtra("SourceLat")
        sourceLong = intent.getStringExtra("SourceLong")
        destinationLat = intent.getStringExtra("DestinationLat")
        destinationLong = intent.getStringExtra("DestinationLong")
        estTimeInSeconds = intent.getStringExtra("time_in_seconds")
        scheduleType = intent.getStringExtra("schedule_type")



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setData()
        setRecyclerView()
        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbarHome.setNavigationOnClickListener { onBackPressed() }

            rlHideShow.setOnClickListener {
                if (rlHideView.visibility == View.VISIBLE){
                    rlHideView.visibility = View.GONE
                }else{
                    rlHideView.visibility = View.VISIBLE
                }

            }
        }
    }

    private fun setData() {
        binding.apply {
            txtTime.text = AppUtils.changeDateFormat(
                info.scheduleDatetime,
                "yyyy-MM-dd HH:mm:ss",
                "dd MMM yyyy, hh:mm a"
            )
            if (info.luggage == "0") {
                txtLuggage.text = getString(R.string.str_no_bags)
            } else if (info.luggage == "1") {
                txtLuggage.text = info.luggage + " " + getString(R.string.str_bag)
            } else {
                txtLuggage.text = info.luggage + " " + getString(R.string.str_bags)
            }

            txtPickUpLocation.text = sourceAddress
            txtDropOffLocation.text = destinationAddress

        }
    }

    private fun setRecyclerView() {
        if (info.vehicles?.isNotEmpty()!!) {
            list.addAll(info.vehicles!!)

        }
        mAdapter = LocalCompareRideAdapter(
            context,
            info.distance,
            info.travelTime,
            list,
            object : LocalCompareRideAdapter.LocalCompareRideAdapterInterface {
                override fun onItemSelected(
                    position: Int,
                    model: CompareRideResponsePOJO.VehiclesItem
                ) {
                  /*  val intent = Intent(applicationContext, IntercityViewRideActivity::class.java)
                    intent.putExtra("SourceAddress", sourceAddress)
                    intent.putExtra("DestinationAddress", destinationAddress)
                    intent.putExtra("SourceLat", sourceLat)
                    intent.putExtra("SourceLong", sourceLong)
                    intent.putExtra("DestinationLat", destinationLat)
                    intent.putExtra("DestinationLong", destinationLong)
                    intent.putExtra("vehicle_model", Gson().toJson(model))
                    intent.putExtra("schedule_type", scheduleType)
                    intent.putExtra("time_in_seconds", estTimeInSeconds)
                    intent.putExtra("MyPOJOClass", info)

                    startActivity(intent)*/
                }

            })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mAdapter
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
                        Toast.makeText(context,  getString(R.string.str_no_route_found), Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onError(error: String?) {
                }

            })
    }
}
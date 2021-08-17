package com.fairfareindia.ui.compareride

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.ui.viewride.ViewRideActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.OnSwipeTouchListener
import com.fairfareindia.utils.PreferencesManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
import com.google.maps.model.LatLng
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@Suppress("DEPRECATION")
class CompareRideActivity : BaseLocationClass(), OnMapReadyCallback,
    ICompareRideView {
    @JvmField
    @BindView(R.id.recycler_view_compareview)
    var recyclerviewcompareview: RecyclerView? = null

    @JvmField
    @BindView(R.id.tv_myCurrentLocation)
    var tv_myCurrentLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_baggs)
    var tv_baggs: TextView? = null

    @JvmField
    @BindView(R.id.homeView)
    var homeView: LinearLayout? = null

    @JvmField
    @BindView(R.id.rlhideview)
    var rlhideview: RelativeLayout? = null

    @JvmField
    @BindView(R.id.tvhideShow)
    var tvhideShow: RelativeLayout? = null

    @JvmField
    @BindView(R.id.locationCardView)
    var locationCardView: CardView? = null

    @JvmField
    @BindView(R.id.spinner_time)
    var spinner_time: TextView? = null

    @JvmField
    @BindView(R.id.tv_sort)
    var tv_sort: TextView? = null

    @JvmField
    @BindView(R.id.tv_myDropUpLocation)
    var tv_myDropUpLocation: TextView? = null
    var placesClient: PlacesClient? = null

    @JvmField
    @BindView(R.id.toolbar_home)
    var mToolbar: Toolbar? = null
    var extras: Bundle? = null
    var token: String? = null
    var PortAir: String? = null
    var isSorted: Boolean = false
    var sourecemarker: Marker? = null
    var mMap: GoogleMap? = null
    var Airport: String? = null
    var formatedCurrentDate: String? = null
    var sourceLat: String? = null
    var sourceLong: String? = null
    var destLat: String? = null
    var destLong: String? = null
    var airportYesOrNO: String? = null
    var distance: String? = null
    var replacedistance: String? = null
    var replacebags: String? = null
    var currentDate: String? = null
    var estTime: String? = null
    var CITY_ID: String? = null
    var CITY_NAME: String? = null
    var currentPlaceId: String? = null

    var hideshow: String? = null
    var sharedpreferences: SharedPreferences? = null

    var baggs: String? = null
    var timeSpinnertxt: String? = null
    var SourceAddress: String? = null
    var destinationAddress: String? = null
    var compareRideAdapter: CompareRideAdapter? = null
    var preferencesManager: PreferencesManager? = null
    lateinit var info: CompareRideResponsePOJO
    private var iCompareRidePresenter: ICompareRidePresenter? = null
    private var compareRideList = ArrayList<CompareRideResponsePOJO.VehiclesItem?>()
    private var mPolyline: Polyline? = null
    var sourcePlaceID: String? = null
    var DestinationPlaceID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare_ride)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)




        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        Places.initialize(this, resources.getString(R.string.google_maps_key))
        placesClient = Places.createClient(this@CompareRideActivity)
        PreferencesManager.initializeInstance(this@CompareRideActivity)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        PortAir = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT)

        info = intent.getSerializableExtra("MyPOJOClass") as CompareRideResponsePOJO


        val intent = intent
        extras = intent.extras
        if (extras != null) {
            CITY_ID = extras!!.getString("CITY_ID")
            CITY_NAME = extras!!.getString("CITY_NAME")
            sourceLat = extras!!.getString("SourceLat")
            sourceLong = extras!!.getString("SourceLong")
            destLat = extras!!.getString("DestinationLat")
            destLong = extras!!.getString("DestinationLong")
            distance = extras!!.getString("Distance")
            estTime = extras!!.getString("EstTime")
            baggs = extras!!.getString("Liggage")
            timeSpinnertxt = extras!!.getString("TimeSpinner")
            SourceAddress = extras!!.getString("SourceAddress")
            destinationAddress = extras!!.getString("DestinationAddress")
            currentDate = extras!!.getString("currentDate")
            Airport = extras!!.getString("Airport")
            currentPlaceId = extras!!.getString("currentPlaceId")
            formatedCurrentDate = extras!!.getString("currentFormatedDate")

        }

        hideshow = "show"
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
            .build()
        var results = arrayOfNulls<GeocodingResult>(0)
        try {
            results = GeocodingApi.newRequest(context)
                .latlng(LatLng(sourceLat!!.toDouble(), sourceLong!!.toDouble()))
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
                .latlng(LatLng(destLat!!.toDouble(), destLong!!.toDouble()))
                .await()
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }



        if (info!!.vehicles!!.size > 0) {


            compareRideList.addAll(info.vehicles!!)

            compareRideAdapter = CompareRideAdapter(this, compareRideList, distance, baggs, estTime)
            recyclerviewcompareview!!.layoutManager = LinearLayoutManager(this)
            recyclerviewcompareview!!.adapter = compareRideAdapter
            compareRideAdapter!!.notifyDataSetChanged()

            compareRideAdapter!!.SetOnItemClickListener(object :
                CompareRideAdapter.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {


                    val intent = Intent(applicationContext, ViewRideActivity::class.java)
                    intent.putExtra("spinnerdata", compareRideList)
                    intent.putExtra("listPosition", position)
                    intent.putExtra("SourceLat", sourceLat)
                    intent.putExtra("SourceLong", sourceLong)
                    intent.putExtra("CITY_ID", CITY_ID)
                    intent.putExtra("CITY_NAME", CITY_NAME)
                    intent.putExtra("destLat", destLat)
                    intent.putExtra("destLong", destLong)
                    intent.putExtra("SourceAddess", SourceAddress)
                    intent.putExtra("DestAddress", destinationAddress)
                    intent.putExtra("CurrentDateTime", currentDate)
                    intent.putExtra("timeSpinnertxt", timeSpinnertxt)
                    intent.putExtra(
                        "vehicle_rate_card_id",
                        info!!.vehicles!!.get(position).vehicleRateCardId
                    )
                    intent.putExtra(
                        "airport_rate_card_id",
                        info!!.vehicles!!.get(position).airportRateCardId
                    )


                    intent.putExtra("luggages_quantity", info.luggage)
                    intent.putExtra("formatedDate", info.scheduleDatetime)
                    intent.putExtra("ViewRideDistance", info.distance)
                    intent.putExtra("canStartRide", info.canStartRide)
                    intent.putExtra("distance", "$distance ($estTime)")



                    startActivity(intent)

                }
            })
        }



        spinner_time!!.text = currentDate
        tv_myCurrentLocation!!.text = SourceAddress
        tv_myDropUpLocation!!.text = destinationAddress

        if (baggs.equals("Luggage")) {
            tv_baggs!!.text = "No " + baggs

        } else {
            tv_baggs!!.text = baggs

        }
        sourcePlaceID = results[0]!!.placeId
        DestinationPlaceID = results1[0]!!.placeId
        mToolbar!!.title = "Compare Rides"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }



        if (PortAir.equals("AIRPORT", ignoreCase = true)) {
            airportYesOrNO = "Yes"
        } else {
            airportYesOrNO = "NO"
        }

        replacedistance = distance!!.replace(" km", "")

        if ((baggs == "1 Luggage") || (baggs == "Luggage")) {
            replacebags = baggs!!.replace(" Luggage", "")

        } else {
            replacebags = baggs!!.replace(" Luggage", "")

        }

        iCompareRidePresenter = CompareRideImplementer(this)


/*        iCompareRidePresenter!!.getCompareRideData(
            token,
            replacedistance,
            CITY_ID,
            sourcePlaceID,
            DestinationPlaceID,
            replacebags,
            airportYesOrNO,
            formatedCurrentDate,
            currentPlaceId!!
        )*/

        setListeners()

    }

    private fun setListeners() {
        homeView?.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
                hideshow = "hide"
                locationCardView!!.visibility = View.GONE
                recyclerviewcompareview!!.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                recyclerviewcompareview!!.requestLayout()
                rlhideview!!.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                rlhideview!!.requestLayout()
                slideUp(rlhideview)
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                hideshow = "show"
                //  rlhideview?.visibility = View.VISIBLE
                slideDown(rlhideview)

                locationCardView!!.visibility = View.VISIBLE
                rlhideview!!.layoutParams.height = 750
                rlhideview!!.requestLayout()
            }
        })
    }


    override fun onDestroy() {
        //  preferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT, "LOCALITY")
        // sharedpreferences!!.edit().clear().commit()
        super.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home_lang, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "LOCALITY"
                )
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(this@CompareRideActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(intent)
            }
        }
        return true
    }


    private fun setStatusBarGradiant(activity: CompareRideActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    @OnClick(R.id.tv_sort)
    fun tvSort() {

        if(isSorted){

            Collections.reverse(compareRideList)
            compareRideAdapter!!.notifyDataSetChanged()
        }else{
            isSorted =true
            compareRideList.clear()

            compareRideList.addAll(info.vehicles!!.sortedBy { it.total!!.toDouble() })


            compareRideAdapter = CompareRideAdapter(this, compareRideList, distance, baggs, estTime)
            recyclerviewcompareview!!.layoutManager = LinearLayoutManager(this)
            recyclerviewcompareview!!.adapter = compareRideAdapter
            compareRideAdapter!!.notifyDataSetChanged()

            compareRideAdapter!!.SetOnItemClickListener(object :
                CompareRideAdapter.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {


                    val intent = Intent(applicationContext, ViewRideActivity::class.java)
                    intent.putExtra("spinnerdata", compareRideList)
                    intent.putExtra("listPosition", position)
                    intent.putExtra("SourceLat", sourceLat)
                    intent.putExtra("SourceLong", sourceLong)
                    intent.putExtra("CITY_ID", CITY_ID)
                    intent.putExtra("CITY_NAME", CITY_NAME)
                    intent.putExtra("destLat", destLat)
                    intent.putExtra("destLong", destLong)
                    intent.putExtra("SourceAddess", SourceAddress)
                    intent.putExtra("DestAddress", destinationAddress)
                    intent.putExtra("CurrentDateTime", currentDate)
                    intent.putExtra("timeSpinnertxt", timeSpinnertxt)
                    intent.putExtra(
                        "vehicle_rate_card_id",
                        info!!.vehicles!!.get(position).vehicleRateCardId
                    )
                    intent.putExtra(
                        "airport_rate_card_id",
                        info!!.vehicles!!.get(position).airportRateCardId
                    )
                    intent.putExtra("luggages_quantity", info.luggage)
                    intent.putExtra("formatedDate", info.scheduleDatetime)
                    intent.putExtra("ViewRideDistance", info.distance)
                    intent.putExtra("canStartRide", info.canStartRide)
                    intent.putExtra("distance", "$distance ($estTime)")

                    startActivity(intent)

                }
            })
        }


     //   compareRideList.sortedWith(compareBy({it!!.total}))
    //    compareRideAdapter!!.notifyDataSetChanged()
    }

/*

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
*/
    

    @OnClick(R.id.tvhideShow)
    fun hideshow() {
        if (hideshow.equals("show")) {
            hideshow = "hide"
            locationCardView!!.visibility = View.GONE
            recyclerviewcompareview!!.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            recyclerviewcompareview!!.requestLayout()
            rlhideview!!.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            rlhideview!!.requestLayout()
            slideUp(rlhideview)

        } else {
            hideshow = "show"
            //  rlhideview?.visibility = View.VISIBLE
            slideDown(rlhideview)

            locationCardView!!.visibility = View.VISIBLE
            rlhideview!!.layoutParams.height = 750
            rlhideview!!.requestLayout()


        }
    }

    private fun slideUp(view: RelativeLayout?) {
        val animate = TranslateAnimation(
            0.0F,  // fromXDelta
            0.0F,  // toXDelta
            view!!.height.toFloat(),  // fromYDelta
            0.0F
        ) // toYDelta

        animate.duration = 500
        animate.fillAfter = true
        view!!.startAnimation(animate)
    }

    private fun slideDown(view: RelativeLayout?) {
        val animate = TranslateAnimation(
            0.0F,  // fromXDelta
            0.0F,  // toXDelta
            0.0F,  // fromYDelta
            0.0F
        ) // toYDelta

        animate.duration = 500
        animate.fillAfter = true
        view!!.startAnimation(animate)


    }


    override fun showWait() {}
    override fun removeWait() {}
    override fun onFailure(appErrorMessage: String?) {}
    override fun onSuccess(info: CompareRideResponsePOJO?) {

    }

    override fun validationError(validationResponse: ValidationResponse?) {
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (!sourceLat!!.isEmpty() && !destLat!!.isEmpty()) {
            mMap!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    com.google.android.gms.maps.model.LatLng(
                        sourceLat!!.toDouble(),
                        sourceLong!!.toDouble()
                    ), 15.0f
                )
            )
            sourecemarker = mMap!!.addMarker(
                MarkerOptions().position(
                    com.google.android.gms.maps.model.LatLng(
                        sourceLat!!.toDouble(),
                        sourceLong!!.toDouble()
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
            )
            sourecemarker = mMap!!.addMarker(
                MarkerOptions().position(
                    com.google.android.gms.maps.model.LatLng(
                        destLat!!.toDouble(),
                        destLong!!.toDouble()
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_grey))
            )
            drawRoute()
        }
    }

    private fun drawRoute() {
        val mOrigin =
            com.google.android.gms.maps.model.LatLng(
                sourceLat!!.toDouble(),
                sourceLong!!.toDouble()
            )
        val mDestination =
            com.google.android.gms.maps.model.LatLng(destLat!!.toDouble(), destLong!!.toDouble())
        val url = getDirectionsUrl(mOrigin, mDestination)
        val downloadTask =
            DownloadTask()
        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(
        mOrigin: com.google.android.gms.maps.model.LatLng,
        mDestination: com.google.android.gms.maps.model.LatLng
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
            iStream = urlConnection.inputStream
            val br =
                BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String?
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


                // Starts parsing data
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<com.google.android.gms.maps.model.LatLng?>?
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
                        com.google.android.gms.maps.model.LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(8f)
                //  lineOptions.color(Color.GREEN);
                lineOptions.color(
                    this@CompareRideActivity.resources.getColor(R.color.gradientstartcolor)
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
}
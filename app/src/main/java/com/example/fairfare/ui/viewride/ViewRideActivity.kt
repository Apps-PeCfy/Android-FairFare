package com.example.fairfare.ui.viewride

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fairfare.R
import com.example.fairfare.base.BaseLocationClass
import com.example.fairfare.ui.compareride.pojo.CompareRideResponsePOJO
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.ui.placeDirection.DirectionsJSONParser
import com.example.fairfare.ui.ridedetails.RideDetailsActivity
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@Suppress("DEPRECATION")
class ViewRideActivity : BaseLocationClass(), OnMapReadyCallback {
    var additional = "close"
    var spinnerposition = 0
    var listPosition = 0
    var distance: String? = null

    @JvmField
    @BindView(R.id.toolbar_viewRide)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.llAdditionalCharges)
    var llAdditionalCharges: LinearLayout? = null

    @JvmField
    @BindView(R.id.tv_additional)
    var tv_additional: RelativeLayout? = null

    @JvmField
    @BindView(R.id.tv_carName)
    var tv_carName: TextView? = null

    @JvmField
    @BindView(R.id.iv_vehical)
    var iv_vehical: ImageView? = null

    @JvmField
    @BindView(R.id.tv_total)
    var tv_total: TextView? = null

    @JvmField
    @BindView(R.id.tv_time)
    var tv_time: TextView? = null

    @JvmField
    @BindView(R.id.tv_Wait_time_charge)
    var tv_Wait_time_charge: TextView? = null

    @JvmField
    @BindView(R.id.tv_tollCharge)
    var tv_tollCharge: TextView? = null

    @JvmField
    @BindView(R.id.tv_Luggage_Charges)
    var tv_Luggage_Charges: TextView? = null

    @JvmField
    @BindView(R.id.tv_SurCharges)
    var tv_SurCharges: TextView? = null

    @JvmField
    @BindView(R.id.tv_estcharge)
    var tv_estcharge: TextView? = null

    @JvmField
    @BindView(R.id.tv_additional_charges)
    var tv_additional_charges: TextView? = null

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
    @BindView(R.id.tvhideShow)
    var tvhideShow: RelativeLayout? = null

  @JvmField
    @BindView(R.id.homeView)
    var homeView: ScrollView? = null


   @JvmField
    @BindView(R.id.btnLogin)
    var btnLogin: Button? = null


 @JvmField
    @BindView(R.id.tv_dateandTime)
    var tv_dateandTime: TextView? = null

    @JvmField
    @BindView(R.id.switchdata)
    var switchdata: Switch? = null
    private var compareRideList =
        ArrayList<CompareRideResponsePOJO.VehiclesItem>()
    var sourceLat: String? = null
    var sourceLong: String? = null
    var destLat: String? = null
    var spntext: String? = null
    var destLong: String? = null
    var sAdd: String? = null
    var currentDate: String? = null
    var dAdd: String? = null
    var mMap: GoogleMap? = null
    var sharedpreferences: SharedPreferences? = null
    var preferencesManager: PreferencesManager? = null
    var hideshow: String? = null

    var sourecemarker: Marker? = null
    private var mPolyline: Polyline? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_ride)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        preferencesManager = PreferencesManager.instance

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        hideshow = "show"
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        compareRideList = intent.getSerializableExtra("spinnerdata") as ArrayList<CompareRideResponsePOJO.VehiclesItem>
        spinnerposition = intent.getIntExtra("spinnerposition", 0)
        listPosition = intent.getIntExtra("listPosition", 0)
        distance = intent.getStringExtra("distance")
        sourceLat = intent.getStringExtra("SourceLat")
        sourceLong = intent.getStringExtra("SourceLong")
        destLat = intent.getStringExtra("destLat")
        destLong = intent.getStringExtra("destLong")
        sAdd = intent.getStringExtra("SourceAddess")
        dAdd = intent.getStringExtra("DestAddress")
        currentDate = intent.getStringExtra("CurrentDateTime")
        spntext = intent.getStringExtra("timeSpinnertxt")

        tv_myCurrentLocation!!.text = sAdd
        tv_myDropUpLocation!!.text = dAdd
        tv_dateandTime!!.text = currentDate
        tv_carType!!.text = compareRideList[listPosition].providerName
        tv_carName!!.text = compareRideList[listPosition].fares?.get(spinnerposition)!!.name
        Glide.with(this@ViewRideActivity)
            .load(compareRideList[listPosition].vehicleImageUrl)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            )
            .into(iv_vehical!!)
        tv_total!!.text ="₹ "+ compareRideList[listPosition].fares?.get(spinnerposition)!!.total
        tv_time!!.text = distance
     //   tv_Wait_time_charge!!.text = "₹ " + 0.00

        if(spntext.equals("Now")){
            btnLogin!!.text ="Start Ride"
        }else{
            btnLogin!!.text ="Schedule Ride"
        }

        tv_tollCharge!!.text = "₹ " + compareRideList[listPosition].fares?.get(spinnerposition)!!.tollCharge

        tv_Luggage_Charges!!.text = "₹ " + compareRideList[listPosition].fares?.get(spinnerposition)!!.luggageCharge

        tv_SurCharges!!.text = "₹ " + compareRideList[listPosition].fares?.get(spinnerposition)!!.surCharge

        tv_estcharge!!.text = "₹ " + compareRideList[listPosition].fares?.get(spinnerposition)!!.subTotal

        tv_additional_charges!!.text = "₹ " + (compareRideList[listPosition].fares?.get(spinnerposition)!!.luggageCharge!!.toDouble()
                + compareRideList[listPosition].fares?.get(spinnerposition)!!.surCharge!!.toDouble()
                + compareRideList[listPosition].fares?.get(spinnerposition)!!.tollCharge!!.toDouble())

        if (compareRideList[listPosition].fares?.get(spinnerposition)!!.nightCharge == "0.00") {
            switchdata!!.isChecked = true
        } else {
            switchdata!!.isChecked = false
        }

        mToolbar!!.title = "View Ride"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setStatusBarGradiant(activity: ViewRideActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }


    override fun onDestroy() {
       // sharedpreferences!!.edit().clear().commit()
      //  preferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT,"LOCALITY")

        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home_lang, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                preferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT,"LOCALITY")
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(this@ViewRideActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }



    @OnClick(R.id.btnLogin)
    fun btnClick(){
        if(spntext.equals("Now")){
            val intent = Intent(applicationContext, RideDetailsActivity::class.java)
            startActivity(intent)
        }else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("FairFare")
            builder.setMessage("Your Ride is Scheduled")
            builder.setPositiveButton("OK"){dialogInterface, which ->
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    @OnClick(R.id.tvhideShow)
    fun  hideshow(){
        if(hideshow.equals("show")){
            hideshow = "hide"
            homeView?.visibility = View.GONE
        }else{
            hideshow = "show"
            homeView?.visibility = View.VISIBLE
        }
    }


    @OnClick(R.id.tv_additional)
    fun additional() {
        if (additional == "close") {
            additional = "open"
            llAdditionalCharges!!.visibility = View.VISIBLE
        } else {
            additional = "close"
            llAdditionalCharges!!.visibility = View.GONE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (!sourceLong!!.isEmpty() && !destLat!!.isEmpty()) {
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(sourceLat!!.toDouble(), sourceLong!!.toDouble()), 13.0f))
            sourecemarker = mMap!!.addMarker(
                MarkerOptions().position(LatLng(sourceLat!!.toDouble(), sourceLong!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)))
            sourecemarker = mMap!!.addMarker(MarkerOptions().position(LatLng(destLat!!.toDouble(), destLong!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)))
            drawRoute()
        }
    }

    private fun drawRoute() {
        val mOrigin =
            LatLng(sourceLat!!.toDouble(), sourceLong!!.toDouble())
        val mDestination =
            LatLng(destLat!!.toDouble(), destLong!!.toDouble())
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
                lineOptions.addAll(points)
                lineOptions.width(8f)
                //  lineOptions.color(Color.GREEN);
                lineOptions.color(
                    this@ViewRideActivity.resources.getColor(R.color.gradientstartcolor)
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
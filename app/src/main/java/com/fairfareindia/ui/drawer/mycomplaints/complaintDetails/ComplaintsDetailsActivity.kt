package com.fairfareindia.ui.drawer.mycomplaints.complaintDetails

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.drawer.mydisput.DisputWaitTimePopUpAdapter
import com.fairfareindia.ui.drawer.mydisput.disputDetail.TollPopUpAdapterDisputDetails
import com.fairfareindia.ui.drawer.mydisput.disputDetail.pojo.DisputDetailResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.disputDetail.setImgAdapter
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.ui.ridedetails.GridSpacingItemDecoration
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
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
import java.text.SimpleDateFormat
import java.util.*

class ComplaintsDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    var preferencesManager: PreferencesManager? = null


    @JvmField
    @BindView(R.id.toolbar_endRide)
    var mToolbar: Toolbar? = null

    var ID: String? = null
    var token: String? = null
    var streetAddress: String? = null
    var deststreetAddress: String? = null


    @JvmField
    @BindView(R.id.tvDisputReason)
    var tvDisputReason: TextView? = null

    @JvmField
    @BindView(R.id.tvActualTollCharges)
    var tvActualTollCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvEstTollCharges)
    var tvEstTollCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvDisputNo)
    var tvDisputNo: TextView? = null

    @JvmField
    @BindView(R.id.tv_vahicalNO)
    var tv_vahicalNO: TextView? = null

    @JvmField
    @BindView(R.id.tv_driverName)
    var tv_driverName: TextView? = null

    @JvmField
    @BindView(R.id.tv_bagCount)
    var tv_bagCount: TextView? = null

    @JvmField
    @BindView(R.id.tv_carName)
    var tv_carName: TextView? = null

    @JvmField
    @BindView(R.id.iv_vehical)
    var iv_vehical: ImageView? = null

    @JvmField
    @BindView(R.id.tv_myCurrentLocation)
    var tv_myCurrentLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_myDropUpLocation)
    var tv_myDropUpLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualDistance)
    var tv_actualDistance: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualTime)
    var tv_actualTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualFare)
    var tv_actualFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualLuggage)
    var tv_actualLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualTotalFare)
    var tv_actualTotalFare: TextView? = null

    @JvmField
    @BindView(R.id.editReview)
    var editReview: TextView? = null


    @JvmField
    @BindView(R.id.tv_estDistance)
    var tv_estDistance: TextView? = null

    @JvmField
    @BindView(R.id.tv_estTime)
    var tv_estTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_estFare)
    var tv_estFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_estLuggage)
    var tv_estLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tv_estTotalFare)
    var tv_estTotalFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualfare)
    var tv_actualfare: TextView? = null

    @JvmField
    @BindView(R.id.tv_startMeterReading)
    var tv_startMeterReading: TextView? = null

    @JvmField
    @BindView(R.id.tv_endMeterReading)
    var tv_endMeterReading: TextView? = null


    @JvmField
    @BindView(R.id.tvActualWaitCharge)
    var tvActualWaitCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvEstWaitCharge)
    var tvEstWaitCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvEstWaitTime)
    var tvEstWaitTime: TextView? = null


    var mMap: GoogleMap? = null
    var sourecemarker: Marker? = null
    var mPolyline: Polyline? = null


    @JvmField
    @BindView(R.id.tv_Datetime)
    var tv_Datetime: TextView? = null

    @JvmField
    @BindView(R.id.tvNightChages)
    var tvNightChages: TextView? = null

    @JvmField
    @BindView(R.id.tvActualNightChages)
    var tvActualNightChages: TextView? = null

    @JvmField
    @BindView(R.id.tvActualWaitTime)
    var tvActualWaitTime: TextView? = null


    @JvmField
    @BindView(R.id.selected_recycler_view)
    var selectedImageRecyclerView: RecyclerView? = null

    var selectedImageList: ArrayList<String?>? = null
    var selectedImageAdapter: setImgAdapter? = null


    @JvmField
    @BindView(R.id.ivViewInfo)
    var ivViewInfo: ImageView? = null

    @JvmField
    @BindView(R.id.ivViewTollInfo)
    var ivViewTollInfo: ImageView? = null

  @JvmField
    @BindView(R.id.llshowData)
    var llshowData: LinearLayout? = null

 @JvmField
    @BindView(R.id.homeView)
    var homeView: ScrollView? = null


    @JvmField
    @BindView(R.id.ivUserIcon)
    var ivUserIcon: ImageView? = null

    @JvmField
    @BindView(R.id.llComments)
    var llComments: LinearLayout? = null

    var eventInfoDialog: Dialog? = null
    var eventDialogBind: EventDialogBind? = null
    var waittimePopUpAdapter: DisputWaitTimePopUpAdapter? = null
    private var waitingList: List<DisputDetailResponsePOJO.WaitingsItem1> = ArrayList()
    var tollPopUpAdapter: TollPopUpAdapterDisputDetails? = null
    private var TollList: List<DisputDetailResponsePOJO.TollsItem> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaints_details)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        /*   val mapFragment = supportFragmentManager
               .findFragmentById(R.id.map) as SupportMapFragment?
           mapFragment!!.getMapAsync(this)

        */   PreferencesManager.initializeInstance(this@ComplaintsDetailsActivity)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        ID = intent.getStringExtra("Id")
        getDisputDetail()



        mToolbar!!.title = "Complaint Details"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

    }


    @OnClick(R.id.ivViewInfo)
    fun iiewInfo() {
        if (waitingList.size > 0) {
            eventInfoDialog = Dialog(this@ComplaintsDetailsActivity, R.style.dialog_style)
            eventInfoDialog!!.setCancelable(true)
            val inflater1 =
                this@ComplaintsDetailsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view12: View = inflater1.inflate(R.layout.event_info, null)
            eventInfoDialog!!.setContentView(view12)
            eventDialogBind = EventDialogBind()
            ButterKnife.bind(eventDialogBind!!, view12)

            eventDialogBind!!.rvEventInfo!!.layoutManager =
                LinearLayoutManager(
                    this@ComplaintsDetailsActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            eventDialogBind!!.rvEventInfo!!.layoutManager = LinearLayoutManager(
                this@ComplaintsDetailsActivity,
                LinearLayoutManager.VERTICAL,
                false
            ) // set LayoutManager to RecyclerView
            waittimePopUpAdapter = DisputWaitTimePopUpAdapter(this, waitingList)
            eventDialogBind!!.rvEventInfo!!.adapter = waittimePopUpAdapter

            eventDialogBind!!.ivPopUpClose

            eventInfoDialog!!.show()
        }

    }


    @OnClick(R.id.ivViewTollInfo)
    fun ivViewTollInfo() {

        if (TollList.size > 0) {
            eventInfoDialog = Dialog(this@ComplaintsDetailsActivity, R.style.dialog_style)

            eventInfoDialog!!.setCancelable(true)
            val inflater1 =
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view12: View = inflater1.inflate(R.layout.toll_layout, null)
            eventInfoDialog!!.setContentView(view12)
            eventDialogBind = EventDialogBind()
            ButterKnife.bind(eventDialogBind!!, view12)


            eventDialogBind!!.rvEventInfo!!.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            tollPopUpAdapter = TollPopUpAdapterDisputDetails(TollList)
            eventDialogBind!!.rvEventInfo!!.adapter = tollPopUpAdapter

            eventInfoDialog!!.show()
        }


    }


    inner class EventDialogBind {
        @JvmField
        @BindView(R.id.rvEventInfo)
        var rvEventInfo: RecyclerView? = null


        @JvmField
        @BindView(R.id.ivPopUpClose)
        var ivPopUpClose: ImageView? = null

        @OnClick(R.id.ivPopUpClose)
        fun ivPopUpClose() {
            eventInfoDialog!!.dismiss()
        }
    }


    private fun getDisputDetail() {


        val progressDialog = ProgressDialog(this@ComplaintsDetailsActivity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.getDisputeDetail("Bearer $token", ID)!!.enqueue(object :
            Callback<DisputDetailResponsePOJO?> {
            override fun onResponse(
                call: Call<DisputDetailResponsePOJO?>,
                response: Response<DisputDetailResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    homeView!!.visibility = View.VISIBLE
                    selectedImageList = response.body()!!.dispute!!.images

                    if(selectedImageList!!.size>0) {
                        selectedImageRecyclerView!!.visibility = View.VISIBLE
                        selectedImageAdapter =
                            setImgAdapter(this@ComplaintsDetailsActivity, selectedImageList!!)
                        val spanCount = 3
                        selectedImageRecyclerView!!.layoutManager =
                            GridLayoutManager(this@ComplaintsDetailsActivity, spanCount)
                        val spacing = 15
                        val includeEdge = true
                        selectedImageRecyclerView!!.addItemDecoration(
                            GridSpacingItemDecoration(
                                spanCount,
                                spacing,
                                includeEdge
                            )
                        )
                        selectedImageRecyclerView!!.adapter = selectedImageAdapter
                    }


                    waitingList = response.body()!!.dispute!!.ride!!.actualTrackRide!!.waitings!!
                    TollList = response.body()!!.dispute!!.ride!!.actualTrackRide!!.tolls!!

                    if (waitingList.size == 0) {
                        ivViewInfo!!.visibility = View.GONE
                    }

                    if (TollList.size == 0) {
                        ivViewTollInfo!!.visibility = View.GONE
                    }


                    var dReason: String? = ""
                    for (i in response.body()!!.dispute!!.reasons!!.indices) {
                        if (dReason!!.isEmpty()) {
                            dReason = response.body()!!.dispute!!.reasons!!.get(i).reason

                        } else {
                            dReason =
                                dReason + "\n" + response.body()!!.dispute!!.reasons!!.get(i).reason

                        }
                    }


                    tvDisputReason!!.text = dReason
                    tvDisputNo!!.text = "Complaint ID:" + response.body()!!.dispute!!.disputeNo
                    tv_vahicalNO!!.text = response.body()!!.dispute!!.vehicleNo
                    tv_driverName!!.text = response.body()!!.dispute!!.driverName
                    if (response.body()!!.dispute!!.driverName!!.isEmpty()) {
                        ivUserIcon?.visibility = View.GONE
                    }


                    if (response.body()!!.dispute!!.ride!!.luggageQuantity.equals("0")) {
                        tv_bagCount!!.text = "No Luggage"
                    } else {

                        if (response.body()!!.dispute!!.ride!!.luggageQuantity.equals("1")) {
                            tv_bagCount!!.text =
                                response.body()!!.dispute!!.ride!!.luggageQuantity + " Luggage"
                        } else {
                            tv_bagCount!!.text =
                                response.body()!!.dispute!!.ride!!.luggageQuantity + " Luggage"

                        }
                    }
                    tv_carName!!.text = response.body()!!.dispute!!.vehicleName


                    tv_actualfare!!.text =
                        "Total Fare Charged: ₹ " + response.body()!!.dispute!!.actualMeterCharges
                    tv_startMeterReading!!.text =
                        "Start Trip Meter: " + response.body()!!.dispute!!.startMeterReading
                    tv_endMeterReading!!.text =
                        "End Trip Meter: " + response.body()!!.dispute!!.endMeterReading


                    val comment = response.body()!!.dispute!!.comment!!

                    if (comment.isNotEmpty()) {
                        llComments!!.visibility = View.VISIBLE
                        editReview!!.visibility = View.VISIBLE
                        editReview!!.text = response.body()!!.dispute!!.comment
                    } else {
                        editReview!!.visibility = View.GONE
                        llComments!!.visibility = View.GONE

                    }


                    val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

                    val formatRide = SimpleDateFormat("dd MMM, hh:mm a")
                    val formaredDate =
                        formatRide.format(formatviewRide.parse((response.body()!!.dispute!!.dateTime).toString()))
                    val strformaredDate = formaredDate.replace("am", "AM").replace("pm", "PM")

                    tv_Datetime!!.text = strformaredDate


                    Glide.with(this@ComplaintsDetailsActivity)
                        .load(response.body()!!.dispute!!.vehicleImageUrl)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .dontTransform()
                        ).into(iv_vehical!!)


                    val geocoder = Geocoder(this@ComplaintsDetailsActivity, Locale.getDefault())
                    try {
                        val addresses =
                            geocoder.getFromLocation(
                                ((response!!.body()!!.dispute!!.originPlaceLat)!!.toDouble()),
                                (response!!.body()!!.dispute!!.originPlaceLong)!!.toDouble(), 1
                            )
                        if (addresses != null && addresses.size>0) {
                            val returnedAddress = addresses[0]
                            val strReturnedAddress =
                                StringBuilder()
                            for (j in 0..returnedAddress.maxAddressLineIndex) {
                                strReturnedAddress.append(returnedAddress.getAddressLine(j))
                            }
                            streetAddress = strReturnedAddress.toString()
                        }else{
                            streetAddress = ""
                        }
                    } catch (e: IOException) {
                    }


                    val geocoderDestination =
                        Geocoder(this@ComplaintsDetailsActivity, Locale.getDefault())
                    try {
                        val addresses =
                            geocoderDestination.getFromLocation(
                                ((response!!.body()!!.dispute!!.destinationPlaceLat)!!.toDouble()),
                                (response!!.body()!!.dispute!!.destinationPlaceLong)!!.toDouble(), 1
                            )
                        if (addresses != null && addresses.size>0) {
                            val returnedAddress = addresses[0]
                            val strReturnedAddress =
                                StringBuilder()
                            for (j in 0..returnedAddress.maxAddressLineIndex) {
                                strReturnedAddress.append(returnedAddress.getAddressLine(j))
                            }
                            deststreetAddress = strReturnedAddress.toString()
                        }else{
                            deststreetAddress = ""
                        }
                    } catch (e: IOException) {
                    }

                    tv_myCurrentLocation!!.text = streetAddress
                    tv_myDropUpLocation!!.text = deststreetAddress


                    if ((response!!.body()!!.dispute!!.ride!!.actualTrackRide) != null) {
                        tv_actualDistance!!.text =
                            response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.distance + " KM"

                        if(response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.duration.equals("1")){
                            tv_actualTime!!.text = response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.duration +" min"

                        }else{
                            tv_actualTime!!.text = response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.duration +" mins"

                        }


                        tv_actualFare!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.subTotalCharges
                        tv_actualLuggage!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.luggageCharges
                        tv_actualTotalFare!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.totalCharges

                        tvActualNightChages!!.text =
                            "₹ " + response.body()!!.dispute!!.ride!!.nightCharges

                        if(response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.tollCharges!!.equals("-")){

                        }else{
                            tvActualTollCharges!!.text =
                                "₹ " + response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.tollCharges

                        }



                        tvActualWaitTime!!.text =
                            response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.waitingTime
                        tvActualWaitCharge!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.actualTrackRide!!.waitingCharges
                    }



                    if ((response!!.body()!!.dispute!!.ride!!.estimatedTrackRide) != null) {

                        if(response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.tollCharges!!.equals("-")){

                        }else{
                            tvEstTollCharges!!.text =
                                "₹ " + response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.tollCharges

                        }



                        tv_estDistance!!.text =
                            response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.distance + " KM"
                        tv_estTime!!.text =
                            response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.duration
                        tv_estFare!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.subTotalCharges
                        tv_estLuggage!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.luggageCharges
                        tv_estTotalFare!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.totalCharges

                        tvNightChages!!.text =
                            "₹ " + response.body()!!.dispute!!.ride!!.nightCharges


                        tvEstWaitCharge!!.text =
                            "₹ " + response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.waitingCharges
                        tvEstWaitTime!!.text =
                            response!!.body()!!.dispute!!.ride!!.estimatedTrackRide!!.waitingTime


                    }


                } else {
                    Toast.makeText(
                        this@ComplaintsDetailsActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<DisputDetailResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })


    }


    private fun drawMap(body: DisputDetailResponsePOJO?) {

        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    (body!!.dispute!!.originPlaceLat)!!.toDouble(),
                    (body!!.dispute!!.originPlaceLong)!!.toDouble()
                ), 15.0f
            )
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (body!!.dispute!!.originPlaceLat)!!.toDouble(),
                    (body!!.dispute!!.originPlaceLong)!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)
            )
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (body!!.dispute!!.destinationPlaceLat)!!.toDouble(),
                    (body!!.dispute!!.destinationPlaceLong)!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_grey)
            )
        )


        drawRoute(body)


    }


    private fun drawRoute(body: DisputDetailResponsePOJO) {

        var mOrigin: LatLng? = null
        var mDestination: LatLng? = null


        mOrigin = LatLng(
            (body!!.dispute!!.originPlaceLat)!!.toDouble(),
            (body!!.dispute!!.originPlaceLong)!!.toDouble()
        )
        mDestination = LatLng(
            (body!!.dispute!!.destinationPlaceLat)!!.toDouble(),
            (body!!.dispute!!.destinationPlaceLong)!!.toDouble()
        )


        val url = getDirectionsUrl(mOrigin!!, mDestination!!)
        val downloadTask = DownloadTask()
        downloadTask.execute(url)


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
                val geoCodedarray = jObject.getJSONArray("geocoded_waypoints")
                val routes1 = array.getJSONObject(0)
                val legs = routes1.getJSONArray("legs")
                val steps = legs.getJSONObject(0)
                val distance = steps.getJSONObject("distance")
                val duration = steps.getJSONObject("duration")

                Log.d(
                    "DistanceTrack", distance.getString("text") + "   " + duration.getString("text")
                )


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
                val path = result[i]



                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points!!.add(position)
                }


                // Fetching all the points in i-th route


                lineOptions!!.addAll(points)
                lineOptions!!.width(8f)
                //  lineOptions.color(Color.GREEN);
                lineOptions!!.color(this@ComplaintsDetailsActivity.resources.getColor(R.color.gradientstartcolor))
            }



            if (lineOptions != null) {

                if (mPolyline != null) {
                    mPolyline!!.remove()
                }
                mPolyline = mMap!!.addPolyline(lineOptions)
            } else {
                Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                    .show()
            }


        }
    }


    private fun setStatusBarGradiant(activity: ComplaintsDetailsActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
    }

}

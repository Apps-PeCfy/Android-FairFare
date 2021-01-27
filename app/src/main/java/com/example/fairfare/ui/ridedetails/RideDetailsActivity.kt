package com.example.fairfare.ui.ridedetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color

import android.location.*

import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.fairfare.R
import com.example.fairfare.base.BaseLocationClass
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.ui.placeDirection.DirectionsJSONParser
import com.example.fairfare.ui.trackRide.TrackRideActivity
import com.example.fairfare.ui.trackRide.currentFare.CurrentFareeResponse
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PhotoSelector
import com.example.fairfare.utils.PreferencesManager
import com.example.fairfare.utils.ProjectUtilities
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
import kotlinx.android.synthetic.main.activity_ride_details.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class RideDetailsActivity : BaseLocationClass(), IRideDetaisView, LocationListener,
    OnMapReadyCallback {
    var locationChangelatitude = 0.0
    var locationChangelongitude = 0.0
    protected var locationManager: LocationManager? = null
    protected var photoSelector: PhotoSelector? = null
    var strFirstTime: String? = null
    var mMap: GoogleMap? = null
    private var mPolyline: Polyline? = null
    var sourecemarker: Marker? = null
    var context: Context = this


    private var iRidePresenter: IRidePresenter? = null
    var imageList: ArrayList<ImageModel>? = null
    var selectedImageList: ArrayList<String>? = null
    var sharedpreferences: SharedPreferences? = null
    var selectedImageAdapter: SelectedImageAdapter? = null
    val REQUEST_IMAGE_CAPTURE = 1
    val PICK_IMAGES = 2
    var image: File? = null
    var filePath: Uri? = null
    var mCurrentPhotoPath: String? = null
    var filePath: Uri? = null
    var projection =
        arrayOf(MediaStore.MediaColumns.DATA)

    @JvmField
    @BindView(R.id.toolbar_rideDetails)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.tv_uploadPhoto)
    var tv_uploadPhoto: TextView? = null

    @JvmField
    @BindView(R.id.btnTrackRide)
    var btnTrackRide: Button? = null

    @JvmField
    @BindView(R.id.selected_recycler_view)
    var selectedImageRecyclerView: RecyclerView? = null


    var vahicalRateCardID: String? = null
    var luggagesQuantity: String? = null
    var formatedDateTime: String? = null
    var originPlaceID: String? = null
    var destinationPlaceID: String? = null
    var overviewPolyLine: String? = null
    var distance_ViewRide: String? = null
    var durationRide: String? = null
    var airportCardID: String? = null
    var originLat: String? = null
    var originLong: String? = null
    var destiLat: String? = null
    var destiLong: String? = null
    var token: String? = null
    var formaredDateqw: String? = null
    var sAddress: String? = null
    var dAddress: String? = null
    var CITY_ID: String? = null
    var preferencesManager: PreferencesManager? = null


    var MyRides_vehicle_rate_card_id: String? = null
    var MyRides_airport_ratr_card_id: String? = null
    var MyRides_RidesID: String? = null
    var MyRidesLat: String? = null
    var MyRidesLong: String? = null
    var MyRidesDLat: String? = null
    var MyRidesDLong: String? = null

    var travelledDistance: Double? = null

    private var pDialog: ProgressDialog? = null
    var strDescimal: String? = null
    var estCurrentDuration: String? = null
    var estCurrentDurationValue: String? = null
    var estCurrentDistance: String? = null
    var estCurrentFare: String? = null
    var actualDistanceInMeter: Int = 0

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_details)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
     //   locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
     //   locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
     //   locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
     /*   val provider: String = locationManager!!.getBestProvider(Criteria(), true)
        val loc: Location = locationManager!!.getLastKnownLocation(provider)
        locationChangelatitude = loc.latitude
        locationChangelongitude = loc.longitude
*/

        strFirstTime = "firstClick"
        PreferencesManager.initializeInstance(this@RideDetailsActivity)
        preferencesManager = PreferencesManager.instance
        photoSelector = PhotoSelector(this)







        pDialog = ProgressDialog(this@RideDetailsActivity)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()


        CITY_ID = intent.getStringExtra("CITY_ID")
        vahicalRateCardID = intent.getStringExtra("vehicle_rate_card_id")
        luggagesQuantity = intent.getStringExtra("luggage_quantity")
        formatedDateTime = intent.getStringExtra("schedule_date")
        originPlaceID = intent.getStringExtra("origin_place_id")
        destinationPlaceID = intent.getStringExtra("destination_place_id")
        overviewPolyLine = intent.getStringExtra("overview_polyline")
        distance_ViewRide = intent.getStringExtra("distance")
        durationRide = intent.getStringExtra("duration")
        airportCardID = intent.getStringExtra("airport_rate_card_id")
        originLat = intent.getStringExtra("originLat")
        originLong = intent.getStringExtra("originLong")
        destiLat = intent.getStringExtra("destinationLat")
        destiLong = intent.getStringExtra("destinationLong")
        sAddress = intent.getStringExtra("SAddress")
        dAddress = intent.getStringExtra("DAddress")


        MyRides_vehicle_rate_card_id = intent.getStringExtra("MyRides_vehicle_rate_card_id")
        MyRides_airport_ratr_card_id = intent.getStringExtra("MyRides_airport_ratr_card_id")
        MyRides_RidesID = intent.getStringExtra("MyRides_RideID")
        MyRidesLat = intent.getStringExtra("MyRidessLat")
        MyRidesLong = intent.getStringExtra("MyRidessLong")
        MyRidesDLat = intent.getStringExtra("MyRidesdLat")
        MyRidesDLong = intent.getStringExtra("MyRidesdLong")


        getcurrentDate()
        if (isStoragePermissionGranted()) {
            init()
            setSelectedImageList()
        }

        iRidePresenter = RideDetailsImplementer(this)

        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)


        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        mToolbar!!.title = "Ride details"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }


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
        tracelledPopUP(travelledDistance)
        return R * c
    }

    private fun tracelledPopUP(Distancetravelled: Double?) {

        var twoDecimal = Distancetravelled!!.toFloat()
        strDescimal = String.format("%.02f", twoDecimal)


        if (strDescimal.equals("0.00")) {
            actualDistanceInMeter = 100
        } else {

            var InMeter: Float

            InMeter = strDescimal!!.toFloat()
            actualDistanceInMeter = (InMeter * 1000).toInt()


        }

        if (actualDistanceInMeter >= 500) {
            if (MyRides_RidesID != null) {
                currentFare()
            } else {
                drawRoute()

                Handler().postDelayed({
                    calculateCurrentFare()

                }, 2000)


            }


        } else {
            pDialog!!.dismiss()

        }

    }

    private fun currentFare() {


        val call = ApiClient.client.getCurrentFare(
            "Bearer $token",
            (MyRides_RidesID)!!.toInt(),
            (travelledDistance).toString(),""
        )

        call!!.enqueue(object : Callback<CurrentFareeResponse?> {
            override fun onResponse(
                call: Call<CurrentFareeResponse?>,
                response: Response<CurrentFareeResponse?>
            ) {
                pDialog!!.dismiss()
                if (response.code() == 200) {


                    estCurrentFare = response.body()!!.rate!!.total
                    showAlertDialog()


                }
            }

            override fun onFailure(
                call: Call<CurrentFareeResponse?>,
                t: Throwable
            ) {
            }
        })

    }

    private fun calculateCurrentFare() {

        travelledDistance = (estCurrentDistance!!.replace(" km","")).toDouble()

        val call = ApiClient.client.getCurrentFareWithoutID(
            "Bearer $token",
            (travelledDistance).toString(),
            vahicalRateCardID,
            airportCardID,
            luggagesQuantity)

        call!!.enqueue(object : Callback<CurrentFareeResponse?> {
            override fun onResponse(
                call: Call<CurrentFareeResponse?>,
                response: Response<CurrentFareeResponse?>
            ) {
                if (response.code() == 200) {
                    pDialog!!.dismiss()
                    estCurrentFare = response.body()!!.rate!!.total
                    showAlertDialog()

                }
            }

            override fun onFailure(
                call: Call<CurrentFareeResponse?>,
                t: Throwable
            ) {
            }
        })


    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this@RideDetailsActivity)
        val customLayout: android.view.View? = getLayoutInflater().inflate(R.layout.my_dialog, null)
        alertDialog.setView(customLayout)
        val tvCalculatedDistance: TextView = customLayout!!.findViewById(R.id.tvCalculatedDistance)
        val tvCalculatedDuration: TextView = customLayout!!.findViewById(R.id.tvCalculatedDuration)
        val tvCalculatedNewFare: TextView = customLayout!!.findViewById(R.id.tvCalculatedNewFare)
        tvCalculatedDistance.text = "New Calculated Distance : " + estCurrentDistance.toString()
        tvCalculatedDuration.text = "New Calculated Duration : " + estCurrentDuration
        tvCalculatedNewFare.text = "New Calculated Fare : " + "Rs " + estCurrentFare

        alertDialog.setPositiveButton("Proceed") { dialog, which ->
            val editText: TextView = customLayout!!.findViewById(R.id.test)


            estCurrentDistance = estCurrentDistance!!.replace(" km","")

            val currentTravelledTime = estCurrentDuration!!.replace(" mins", "")

             distance_ViewRide = (estCurrentDistance!!.toFloat() ).toString()
             durationRide=(estCurrentDurationValue!!.toInt()/60).toFloat().toString()
            originLat = locationChangelatitude.toString()
            originLong = locationChangelongitude.toString()


            val context1 = GeoApiContext.Builder()
                .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                .build()
            var results1 = arrayOfNulls<GeocodingResult>(0)
            try {
                results1 = GeocodingApi.newRequest(context1)
                    .latlng(
                        com.google.maps.model.LatLng(
                            locationChangelatitude!!.toDouble(),
                            locationChangelongitude!!.toDouble()
                        )
                    )
                    .await()
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            originPlaceID = results1[0]!!.placeId



            val geocoder = Geocoder(this@RideDetailsActivity, Locale.getDefault())
            try {
                val addresses =
                    geocoder.getFromLocation(
                        (locationChangelatitude),
                        (locationChangelongitude), 1
                    )
                if (addresses != null) {
                    val returnedAddress = addresses[0]
                    val strReturnedAddress =
                        StringBuilder()
                    for (j in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j))
                    }
                    sAddress = strReturnedAddress.toString()
                }
            } catch (e: IOException) {
            }




        }


        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            val editText: TextView = customLayout!!.findViewById(R.id.test)

            Toast.makeText(this@RideDetailsActivity, "Cancel", Toast.LENGTH_LONG).show()
        }


        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }


    private fun getcurrentDate() {

        val today = Date()
        val format = SimpleDateFormat("dd MMM, hh:mm a")
        val formatviewRide = SimpleDateFormat("YYYY-MM-dd HH:MM:ss")
        val dateToStr = format.format(today)

        formaredDateqw = formatviewRide.format(today).toString()

    }

    @OnClick(R.id.btnTrackRide)
    fun btnTrack() {

  /*      if(((edt_vehicalNO!!.text).toString()).isEmpty()){
            Toast.makeText(
                this@RideDetailsActivity,
                "Enter Vehicle Number", Toast.LENGTH_LONG).show()

        }else   if(((edt_DriverName!!.text).toString()).isEmpty()){
            Toast.makeText(
                this@RideDetailsActivity,
                "Enter Driver Name", Toast.LENGTH_LONG).show()

        }else {*/


            if ((MyRides_RidesID != null)) {


                if (actualDistanceInMeter >= 500) {

                    iRidePresenter!!.startRide(
                        token,
                        MyRides_RidesID,
                        MyRides_vehicle_rate_card_id,
                        "",
                        "",
                        originPlaceID,
                        "",
                        "",
                        distance_ViewRide,
                        durationRide,
                        "",
                        MyRides_airport_ratr_card_id,
                        edt_DriverName!!.text.toString(),
                        edt_vehicalNO!!.text.toString(),
                        edt_bagsCount!!.text.toString(),
                        edt_meterReading!!.text.toString(),
                        originLat, originLong, "", "", imageList,"",""
                    )

                } else
                {
                    iRidePresenter!!.startRide(
                        token,
                        MyRides_RidesID,
                        MyRides_vehicle_rate_card_id,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        MyRides_airport_ratr_card_id,
                        edt_DriverName!!.text.toString(),
                        edt_vehicalNO!!.text.toString(),
                        edt_bagsCount!!.text.toString(),
                        edt_meterReading!!.text.toString(),
                        "", "", "", "", imageList,"",""
                    )

                }


            } else
            {


                iRidePresenter!!.startRide(
                    token,
                    "",
                    vahicalRateCardID,
                    luggagesQuantity,
                    formaredDateqw,
                    originPlaceID,
                    destinationPlaceID,
                    overviewPolyLine,
                    distance_ViewRide,
                    durationRide,
                    CITY_ID,
                    airportCardID,
                    edt_DriverName!!.text.toString(),
                    edt_vehicalNO!!.text.toString(),
                    edt_bagsCount!!.text.toString(),
                    edt_meterReading!!.text.toString(),
                    originLat, originLong, destiLat, destiLong,imageList,sAddress,dAddress
                )

            }


    }

    private fun setSelectedImageList() {


        selectedImageAdapter = SelectedImageAdapter(this, selectedImageList!!, object : SelectedImageAdapter.SelectedImageAdapterInterface{
            override fun itemClick(position: Int, imageName: String?) {

            }

            override fun onRemoveClick(position: Int, imageName: String?) {
                showConfirmationDialog(position)
            }

        } )

        val spanCount = 2
        selectedImageRecyclerView!!.layoutManager = GridLayoutManager(this, spanCount)
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

    private fun isStoragePermissionGranted(): Boolean {
        val ACCESS_EXTERNAL_STORAGE: Int = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (ACCESS_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100
            )
            return false
        }
        return true
    }

    private fun init() {
        selectedImageList = ArrayList<String>()
        imageList = ArrayList()
    }
    private fun showConfirmationDialog(position: Int) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("FairFare")
        alertDialog.setMessage("Are you sure you remove this image?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Yes") { dialog, which ->
            imageList!!.removeAt(position)
            selectedImageList!!.removeAt(position)
            selectedImageAdapter!!.notifyDataSetChanged()
        }
        alertDialog.setNegativeButton("No") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun showConfirmationDialog(position: Int) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("FairFare")
        alertDialog.setMessage("Are you sure you remove this image?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Yes") { dialog, which ->
            imageList!!.removeAt(position)
            selectedImageList!!.removeAt(position)
            selectedImageAdapter!!.notifyDataSetChanged()
        }
        alertDialog.setNegativeButton("No") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }


    override fun onDestroy() {
        // sharedpreferences!!.edit().clear().commit()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home_lang, menu)
        return true
    }

    @OnClick(R.id.tv_uploadPhoto)
    fun btnClick() {
        setImageList()
    }

    private fun setImageList() {

      /*  val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder =
            android.app.AlertDialog.Builder(this@RideDetailsActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                takePicture()
            } else if (options[item] == "Choose from Gallery") {
                getPickImageIntent()
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()*/

        /**
         * iLoma Team :- Mohasin 8 Jan
         */

        if(photoSelector!!.isPermissionGranted(context)){
            photoSelector!!.selectImage(null)
        }



    }

    fun getPickImageIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES)
    }

    private fun takePicture() {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = createImageFile()
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
            startActivityForResult(
                cameraIntent, REQUEST_IMAGE_CAPTURE
            )
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        mCurrentPhotoPath = image.absolutePath
        return image
    }

    /**
     * LIFECYCLE
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        //Profile Picture
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PhotoSelector.SELECT_FILE) {
                filePath = photoSelector!!.onSelectFromGalleryResult(data)
                val imageModel = ImageModel()
                imageModel.filePath = photoSelector!!.getPath(filePath, context)
                imageModel.image = photoSelector!!.getPath(filePath, context)
                imageModel.isSelected
                imageList!!.add(0, imageModel)
                selectedImageList!!.add(0, imageModel.image!!)
                selectedImageAdapter!!.notifyDataSetChanged()
            } else if (requestCode == PhotoSelector.REQUEST_CAMERA) {
                filePath = photoSelector!!.onCaptureImageResult()
                val imageModel = ImageModel()
                imageModel.filePath = photoSelector!!.getPath(filePath, context)
                imageModel.image = photoSelector!!.getPath(filePath, context)
                imageModel.isSelected
                imageList!!.add(0, imageModel)
                selectedImageList!!.add(0, imageModel.image!!)
                selectedImageAdapter!!.notifyDataSetChanged()
            }
        }
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (mCurrentPhotoPath != null) {
                    addImage(mCurrentPhotoPath)
                }
            } else if (requestCode == PICK_IMAGES) {
                if (data!!.clipData != null) {
                    val mClipData = data.clipData
                    for (i in 0 until mClipData!!.itemCount) {
                        val item = mClipData.getItemAt(i)
                        val uri = item.uri
                        getImageFilePath(uri)
                    }
                } else if (data.data != null) {
                    val uri = data.data
                    getImageFilePath(uri)
                }
            }
        }
    }*/


    fun getImageFilePath(uri: Uri?) {
        val cursor =
            contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val absolutePathOfImage =
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                absolutePathOfImage?.let { checkImage(it) } ?: checkImage(uri.toString())
            }
        }
    }

    fun checkImage(filePath: String?) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedImageList!!.contains(filePath!!)) {
            for (pos in imageList!!.indices) {
                if (imageList!!.get(pos).image != null) {
                    if (imageList!!.get(pos).image.equals(filePath)) {
                        imageList!!.removeAt(pos)
                    }
                }
            }
            addImage(filePath)
        }
    }


    fun addImage(filePath: String?) {
        val imageModel = ImageModel()
        imageModel.image = filePath
        imageModel.isSelected
        imageList!!.add(0, imageModel)
        selectedImageList!!.add(0, filePath!!)
        selectedImageAdapter!!.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(this@RideDetailsActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(intent)
            }
        }
        return true
    }


    private fun setStatusBarGradiant(activity: RideDetailsActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            init()
            // getAllImages();
            setImageList()
            setSelectedImageList()
        }
    }

    override fun schduleRideSuccess(info: ScheduleRideResponsePOJO?) {

        Toast.makeText(
            this@RideDetailsActivity,
            info!!.message, Toast.LENGTH_LONG
        ).show()


        if (MyRides_RidesID != null) {
            val intentr = Intent(applicationContext, TrackRideActivity::class.java)
            intentr.putExtra("SAddress", sAddress)
            intentr.putExtra("DAddress", dAddress)
            intentr.putExtra("ImageUrl", intent.getStringExtra("ImgUrl"))
            intentr.putExtra("ImageName", intent.getStringExtra("ImgName"))
            intentr.putExtra("ResponsePOJOScheduleRide", info)

            intentr.putExtra("MyRidesLat", MyRidesLat)
            intentr.putExtra("MyRidesLong", MyRidesLong)
            intentr.putExtra("MyRidesDLat", MyRidesDLat)
            intentr.putExtra("MyRidesDLong", MyRidesDLong)
            intentr.putExtra("MyRidesID", MyRides_RidesID)
            startActivity(intentr)
            finish()

        } else {


            val intentr = Intent(applicationContext, TrackRideActivity::class.java)
            intentr.putExtra("SAddress", sAddress)
            intentr.putExtra("DAddress", dAddress)
            intentr.putExtra("ImageUrl", intent.getStringExtra("ImgUrl"))
            intentr.putExtra("ImageName", intent.getStringExtra("ImgName"))
            intentr.putExtra("ResponsePOJOScheduleRide", info)
            startActivity(intentr)
            finish()

        }


    }

    override fun validationError(validationResponse: ValidationResponse?) {

        Toast.makeText(
            this@RideDetailsActivity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()

    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(this@RideDetailsActivity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(this@RideDetailsActivity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onLocationChanged(location: Location) {

        if (strFirstTime.equals("firstClick")) {

            strFirstTime = "secondClick"

            if (location != null) {
                locationChangelatitude = location.latitude
                locationChangelongitude = location.longitude
            }
            drawRoute()
            if ((MyRides_RidesID != null)) {

                GetDistanceFromLatLonInKm(
                    MyRidesLat!!.toDouble(),
                    MyRidesLong!!.toDouble(),
                    locationChangelatitude,
                    locationChangelongitude
                )

            } else{
                GetDistanceFromLatLonInKm(
                    originLat!!.toDouble(),
                    originLong!!.toDouble(),
                    locationChangelatitude,
                    locationChangelongitude
                )
            }


        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onMapReady(p0: GoogleMap?) {

        if ((MyRides_RidesID != null)) {

            originLat = MyRidesLat
            originLong = MyRidesLong
        }


        mMap = p0

        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    (originLat)!!.toDouble(),
                    (originLong)!!.toDouble()
                ), 15.0f
            )
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (originLat)!!.toDouble(),
                    (originLong)!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)
            )
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (locationChangelatitude)!!.toDouble(),
                    (locationChangelongitude)!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)
            )
        )


    }

    private fun drawRoute() {
        var mOrigin: LatLng? = null
        var mDestination: LatLng? = null



        if ((MyRides_RidesID != null)) {
            mOrigin = LatLng(locationChangelatitude, locationChangelongitude)
            mDestination = LatLng(MyRidesLat!!.toDouble(), MyRidesLong!!.toDouble())
        } else if (actualDistanceInMeter >= 500) {
            mOrigin = LatLng(locationChangelatitude, locationChangelongitude)
            mDestination = LatLng(destiLat!!.toDouble(), destiLong!!.toDouble())


        } else {
            mOrigin = LatLng(locationChangelatitude, locationChangelongitude)
            mDestination = LatLng(originLat!!.toDouble(), originLong!!.toDouble())

        }


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
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser =
                    DirectionsJSONParser()
                val array = jObject.getJSONArray("routes")
                val geoCodedarray = jObject.getJSONArray("geocoded_waypoints")
                val routes1 = array.getJSONObject(0)
                val legs = routes1.getJSONArray("legs")
                val steps = legs.getJSONObject(0)
                val distance = steps.getJSONObject("distance")
                val duration = steps.getJSONObject("duration")

                estCurrentDuration = duration.getString("text")
                estCurrentDurationValue = duration.getString("value")
                estCurrentDistance = distance.getString("text")


                /*if (actualDistanceInMeter>=500){
                    distance_ViewRide = distance.getString("text")
                    durationRide = duration.getString("text")
                }
*/

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
            if(result!=null) {
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
                        this@RideDetailsActivity.resources.getColor(R.color.gradientstartcolor)
                    )
                }
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


}


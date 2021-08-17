package com.fairfareindia.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient.client
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myaccount.pojo.UpdateProfileResponsePOJO
import com.fairfareindia.ui.home.PlacesAutoCompleteAdapter.ClickListener
import com.fairfareindia.ui.home.RecyclerViewAdapter.IClickListener
import com.fairfareindia.ui.home.pojo.DeleteSaveDataResponsePOJO
import com.fairfareindia.ui.home.pojo.GetSaveLocationResponsePOJO
import com.fairfareindia.ui.home.pojo.PickUpLocationModel
import com.fairfareindia.ui.home.pojo.SaveLocationResponsePojo
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class PickUpDropActivity : FragmentActivity(), OnMapReadyCallback, ClickListener,
    IClickListener {
    var placesClient: PlacesClient? = null
    private var mMap: GoogleMap? = null
    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentAddressLatitude = 0.0
    var currentAddressLongitude = 0.0
    var currentUserAddress: String? = null



    var extras: Bundle? = null
    var isSource: Boolean? = false

    private var plotedLocation: Location? = null

    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null
    var selectedTab = "Searches"
    var marker: Marker? = null

    @JvmField
    @BindView(R.id.homeView)
    var homeView: LinearLayout? = null

    @JvmField
    @BindView(R.id.tv_currentLocation)
    var tv_currentLocation: TextView? = null

    @JvmField
    @BindView(R.id.lladdress)
    var lladdress: LinearLayout? = null

    @JvmField
    @BindView(R.id.custom_title)
    var custom_title: TextView? = null

    @JvmField
    @BindView(R.id.tv_searches)
    var tv_searches: TextView? = null

    @JvmField
    @BindView(R.id.tv_saved)
    var tv_saved: TextView? = null

    @JvmField
    @BindView(R.id.tv_recent)
    var tv_recent: TextView? = null

    @JvmField
    @BindView(R.id.tv_view_search)
    var tv_view_search: TextView? = null

    @JvmField
    @BindView(R.id.tv_view_saved)
    var tv_view_saved: TextView? = null

    @JvmField
    @BindView(R.id.tv_view_recent)
    var tv_view_recent: TextView? = null

    @JvmField
    @BindView(R.id.tvAddress)
    var tvAddress: TextView? = null

    @JvmField
    @BindView(R.id.ivFavLocateOnMap)
    var ivFavLocateOnMap: ImageView? = null

    @JvmField
    @BindView(R.id.recycler_view_saved)
    var recycler_view_saved: RecyclerView? = null

    @JvmField
    @BindView(R.id.recycler_view_fragment)
    var recyclerView: RecyclerView? = null

    @JvmField
    @BindView(R.id.edt_pick_up_drop)
    var edt_pick_up_drop: EditText? = null
    var sharedpreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var token: String? = null
    var locateOnMapAddress: String? = null
    var locateOnMapCurrentAddress: String? = null
    var keyAirport: String? = null
    var preferencesManager: PreferencesManager? = null
    private var mAutoCompleteAdapter: PlacesAutoCompleteAdapter? = null
    private var savedLocationList: List<GetSaveLocationResponsePOJO.DataItem> = ArrayList()
    var recyclerViewAdapter: RecyclerViewAdapter? = null
    var recentRecyclerViewAdapter: RecentRecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_up_drop)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        PreferencesManager.initializeInstance(this@PickUpDropActivity)
        preferencesManager = PreferencesManager.instance
        Places.initialize(
            this,
            resources.getString(R.string.google_maps_key)
        )
        placesClient = Places.createClient(this@PickUpDropActivity)

        getPickUpDropUpData()




        edt_pick_up_drop!!.addTextChangedListener(filterTextWatcher)
        mAutoCompleteAdapter = PlacesAutoCompleteAdapter(this)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        mAutoCompleteAdapter!!.setClickListener(this)
        recyclerView!!.adapter = mAutoCompleteAdapter
        mAutoCompleteAdapter!!.notifyDataSetChanged()
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE)
        editor = sharedpreferences!!.edit()
        homeView!!.visibility = View.VISIBLE
        lladdress!!.visibility = View.GONE
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        val intent = intent
        extras = intent.extras
        if (extras != null) {
            currentLatitude = extras!!.getDouble("currentLatitude")
            currentLongitude = extras!!.getDouble("currentLongitude")

            currentAddressLatitude = extras!!.getDouble("currentUserLatitude")
            currentAddressLongitude = extras!!.getDouble("currentUserLongitude")
            currentUserAddress = extras!!.getString("currentUserAddress")

        }
        setToolbar()
    }

    private fun getPickUpDropUpData() {
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.getRecentLocation("Bearer $token")!!.enqueue(object :
            Callback<GetSaveLocationResponsePOJO?> {
            override fun onResponse(
                call: Call<GetSaveLocationResponsePOJO?>,
                response: Response<GetSaveLocationResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    recyclerView!!.visibility = View.GONE
                    savedLocationList = response.body()?.data!!
                    if (savedLocationList.size > 0) {
                        recentRecyclerViewAdapter =
                            RecentRecyclerViewAdapter(this@PickUpDropActivity, savedLocationList!!)
                        recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                        recycler_view_saved!!.layoutManager =
                            LinearLayoutManager(this@PickUpDropActivity)
                        recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                        recentRecyclerViewAdapter!!.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(
                        this@PickUpDropActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<GetSaveLocationResponsePOJO?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
            }
        })
    }

    private val filterTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)){
            if (selectedTab == "Saved") {
                val listClone: MutableList<GetSaveLocationResponsePOJO.DataItem> =
                    ArrayList()
                for (i in savedLocationList!!.indices) {
                    if (savedLocationList[i].fullAddress!!.toLowerCase()
                            .contains(s.toString()!!.toLowerCase())
                    ) {
                        listClone.add(savedLocationList[i])
                    }
                }
                recyclerViewAdapter = RecyclerViewAdapter(this@PickUpDropActivity, listClone)
                recycler_view_saved!!.adapter = recyclerViewAdapter
                recyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
            }
            if (selectedTab == "Recent") {
                val listCloneRecent: MutableList<GetSaveLocationResponsePOJO.DataItem> =
                    ArrayList()
                for (i in savedLocationList!!.indices) {
                    if (savedLocationList[i]!!.fullAddress!!.toLowerCase()
                            .contains(s.toString().toLowerCase())
                    ) {
                        listCloneRecent.add(savedLocationList[i])
                    }
                }
                if (listCloneRecent.size > 0) {
                    recentRecyclerViewAdapter =
                        RecentRecyclerViewAdapter(this@PickUpDropActivity, listCloneRecent)
                    recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                    recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                }
            }
            if (selectedTab == "Searches") {
                if (s.toString() != "") {
                    mAutoCompleteAdapter!!.filter.filter(s.toString())
                    recycler_view_saved!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                } else {
                    recyclerView!!.visibility = View.GONE
                    recycler_view_saved!!.visibility = View.VISIBLE
                    val listCloneRecent: MutableList<GetSaveLocationResponsePOJO.DataItem> =
                        ArrayList()
                    for (i in savedLocationList.indices) {
                        if (savedLocationList[i].fullAddress!!.toLowerCase()
                                .contains(s.toString()!!.toLowerCase())
                        ) {
                            listCloneRecent.add(savedLocationList[i])
                        }
                    }
                    if (listCloneRecent.size > 0) {
                        recentRecyclerViewAdapter =
                            RecentRecyclerViewAdapter(this@PickUpDropActivity, listCloneRecent)
                        recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                        recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                    }
                }
            }
        }else{
                ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
            }
        }

        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }
    }

    private fun setToolbar() {
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }
        mToolbar!!.title = ""
        custom_title!!.text = extras!!.getString("Toolbar_Title")
    }

    @OnClick(R.id.tv_locateonmap)
    fun locatemap() {

        if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)) {
            homeView!!.visibility = View.GONE
            lladdress!!.visibility = View.VISIBLE
        }else{
            ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
        }
    }

    @OnClick(R.id.tv_searches)
    fun search() {

        if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)) {
            edt_pick_up_drop!!.setText("")
            selectedTab = "Searches"
            tv_view_recent!!.setBackgroundColor(Color.GRAY)
            tv_view_saved!!.setBackgroundColor(Color.GRAY)
            tv_view_search!!.setBackgroundColor(Color.RED)
            if (edt_pick_up_drop!!.text.length == 0) {
                client.getRecentLocation("Bearer $token")!!.enqueue(object :
                    Callback<GetSaveLocationResponsePOJO?> {
                    override fun onResponse(
                        call: Call<GetSaveLocationResponsePOJO?>,
                        response: Response<GetSaveLocationResponsePOJO?>
                    ) {
                        if (response.code() == 200) {
                            recyclerView!!.visibility = View.GONE
                            recycler_view_saved!!.visibility = View.VISIBLE
                            savedLocationList = response.body()!!.data!!
                            if (savedLocationList.size > 0) {
                                recentRecyclerViewAdapter = RecentRecyclerViewAdapter(
                                    this@PickUpDropActivity,
                                    savedLocationList
                                )
                                recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                                recycler_view_saved!!.layoutManager =
                                    LinearLayoutManager(this@PickUpDropActivity)
                                recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                                recentRecyclerViewAdapter!!.notifyDataSetChanged()
                            }
                        } else {
                            Toast.makeText(
                                this@PickUpDropActivity,
                                "Internal server error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<GetSaveLocationResponsePOJO?>,
                        t: Throwable
                    ) {
                        Log.d("response", t.stackTrace.toString())
                    }
                })
            } else {
                edt_pick_up_drop!!.addTextChangedListener(filterTextWatcher)
                mAutoCompleteAdapter = PlacesAutoCompleteAdapter(this)
                recyclerView!!.layoutManager = LinearLayoutManager(this)
                mAutoCompleteAdapter!!.setClickListener(this)
                recyclerView!!.adapter = mAutoCompleteAdapter
                mAutoCompleteAdapter!!.notifyDataSetChanged()
            }
        }else{
            ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
        }
    }

    @OnClick(R.id.tv_saved)
    fun saved() {

        if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)) {
            edt_pick_up_drop!!.setText("")
            recyclerView!!.visibility = View.GONE
            selectedTab = "Saved"
            tv_view_recent!!.setBackgroundColor(Color.GRAY)
            tv_view_saved!!.setBackgroundColor(Color.RED)
            tv_view_search!!.setBackgroundColor(Color.GRAY)

            getSavedLocation()
        }else{
            ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
        }

    }

    private fun getSavedLocation() {
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.getSavedLocation("Bearer $token")!!.enqueue(object :
            Callback<GetSaveLocationResponsePOJO?> {
            override fun onResponse(
                call: Call<GetSaveLocationResponsePOJO?>,
                response: Response<GetSaveLocationResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    savedLocationList = response.body()!!.data!!
                    recyclerViewAdapter =
                        RecyclerViewAdapter(this@PickUpDropActivity, savedLocationList)
                    recycler_view_saved!!.adapter = recyclerViewAdapter
                    recyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)

                } else {
                    Toast.makeText(
                        this@PickUpDropActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<GetSaveLocationResponsePOJO?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
            }
        })
    }

    @OnClick(R.id.tv_recent)
    fun recent() {
        selectedTab = "Recent"
        tv_view_recent!!.setBackgroundColor(Color.RED)
        tv_view_saved!!.setBackgroundColor(Color.GRAY)
        tv_view_search!!.setBackgroundColor(Color.GRAY)
        token =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.getRecentLocation("Bearer $token")!!.enqueue(object :
            Callback<GetSaveLocationResponsePOJO?> {
            override fun onResponse(
                call: Call<GetSaveLocationResponsePOJO?>,
                response: Response<GetSaveLocationResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    savedLocationList = response.body()!!.data!!
                    if (savedLocationList.size > 0) {
                        recentRecyclerViewAdapter =
                            RecentRecyclerViewAdapter(this@PickUpDropActivity, savedLocationList)
                        recyclerView!!.adapter = recentRecyclerViewAdapter
                        recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                    }
                } else {
                    Toast.makeText(
                        this@PickUpDropActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<GetSaveLocationResponsePOJO?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
            }
        })
    }

    @OnClick(R.id.ivFavLocateOnMap)
    fun favClick() {
        if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)) {
            val context = GeoApiContext.Builder()
                .apiKey("AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU")
                .build()
            var results = arrayOfNulls<GeocodingResult>(0)
            try {
                results = GeocodingApi.newRequest(context)
                    .latlng(com.google.maps.model.LatLng(currentLatitude, currentLongitude)).await()
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("sdsdesdsds", results[0]!!.placeId)
            var addresses: List<Address?>? = null
            val geocoder = Geocoder(this@PickUpDropActivity, Locale.getDefault())
            try {
                addresses =
                    geocoder.getFromLocation(
                        currentLatitude,
                        currentLongitude, 1
                    )
            } catch (e: IOException) {
            }
            val token =
                preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
            client.SaveLocation(
                "Bearer $token", results[0]!!.placeId, addresses!!.get(0)!!.subAdminArea,
                addresses!!.get(0)!!.adminArea,
                addresses.get(0)!!.getAddressLine(0),
                addresses!!.get(0)!!.countryName
            )!!.enqueue(object : Callback<SaveLocationResponsePojo?> {
                override fun onResponse(
                    call: Call<SaveLocationResponsePojo?>,
                    response: Response<SaveLocationResponsePojo?>
                ) {
                    if (response.code() == 200) {
                        ivFavLocateOnMap!!.setBackgroundResource(R.drawable.ic_fav_checked)
                        Toast.makeText(
                            this@PickUpDropActivity,
                            "Location saved successfully !!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@PickUpDropActivity,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<SaveLocationResponsePojo?>,
                    t: Throwable
                ) {
                    Log.d("response", t.stackTrace.toString())
                }
            })
        }else{
            ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
        }
    }

    override fun onDestroy() {
        //  sharedpreferences!!.edit().clear().commit()
        super.onDestroy()
    }

    @OnClick(R.id.btnContinue)
    fun btnContinue() {

        if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)) {
            if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
                isSource = true
                sharedpreferences!!.edit().remove("SourceLat")
                sharedpreferences!!.edit().remove("SourceLong")
                editor!!.putString(SourceLat, currentLatitude.toString())
                editor!!.putString(SourceLong, currentLongitude.toString())
                editor!!.putString(fromLocation, locateOnMapAddress)
            } else {
                isSource = false
                sharedpreferences!!.edit().remove("DestinationLat")
                sharedpreferences!!.edit().remove("DestinationLong")
                editor!!.putString(DestinationLat, currentLatitude.toString())
                editor!!.putString(DestinationLong, currentLongitude.toString())
                editor!!.putString(destiNationLocation, locateOnMapAddress)
            }
            editor!!.commit()
            editor!!.apply()
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("Toolbar_Title", extras!!.getString("Toolbar_Title"))
            intent.putExtra("currentLatitude", currentLatitude)
            intent.putExtra("currentLongitude", currentLongitude)
            intent.putExtra("spnbg", extras!!.getInt("spinnerbag"))
            intent.putExtra("spnTime", extras!!.getInt("spinnerTime"))
            intent.putExtra("getcity", extras!!.getString("City"))
            intent.putExtra("TvDateTime", extras!!.getString("spinnerTimeDate"))
            intent.putExtra("formaredDateLater", extras!!.getString("formaredDateLater"))

            //Old Code
            if (Constants.IS_OLD_PICK_UP_CODE) {
                startActivity(intent)
            } else {
                // ILOMADEV
                finish()

                EventBus.getDefault().post(
                    PickUpLocationModel(
                        currentLatitude,
                        currentLongitude,
                        isSource,
                        locateOnMapAddress,
                        keyAirport, ""
                    )
                )
            }
        }else{
            ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
        }


    }


    @OnClick(R.id.tv_currentLocation)
    fun cLocation() {


        if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)) {
            if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
                isSource = true
                sharedpreferences!!.edit().remove("SourceLat")
                sharedpreferences!!.edit().remove("SourceLong")
                editor!!.putString(SourceLat, currentAddressLatitude.toString())
                editor!!.putString(SourceLong, currentAddressLongitude.toString())
                editor!!.putString(fromLocation, currentUserAddress)
            } else {
                isSource = false
                sharedpreferences!!.edit().remove("DestinationLat")
                sharedpreferences!!.edit().remove("DestinationLong")
                editor!!.putString(DestinationLat, currentAddressLatitude.toString())
                editor!!.putString(DestinationLong, currentAddressLongitude.toString())
                editor!!.putString(destiNationLocation, currentUserAddress)
            }
            editor!!.commit()
            editor!!.apply()
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("Toolbar_Title", extras!!.getString("Toolbar_Title"))
            intent.putExtra("currentLatitude", currentAddressLatitude)
            intent.putExtra("currentLongitude", currentAddressLongitude)
            intent.putExtra("spnbg", extras!!.getInt("spinnerbag"))
            intent.putExtra("spnTime", extras!!.getInt("spinnerTime"))
            intent.putExtra("getcity", extras!!.getString("City"))
            intent.putExtra("TvDateTime", extras!!.getString("spinnerTimeDate"))
            intent.putExtra("formaredDateLater", extras!!.getString("formaredDateLater"))

            //Old Code
            if (Constants.IS_OLD_PICK_UP_CODE) {
                startActivity(intent)
            } else {
                // ILOMADEV
                finish()

                EventBus.getDefault().post(
                    PickUpLocationModel(
                        currentAddressLatitude,
                        currentAddressLongitude,
                        isSource,
                        currentUserAddress,
                        keyAirport, ""
                    )
                )
            }

        }else{
            ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
        }

    }

    private fun setStatusBarGradiant(activity: PickUpDropActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapCalled(googleMap)
    }

    private fun mapCalled(googleMap: GoogleMap?) {
        val markerOptionscurrent = MarkerOptions()
        markerOptionscurrent.position(
            LatLng(
                currentLatitude,
                currentLongitude
            )
        )
        val currentAddress: String? = getAddressFromLocation()

        Log.d("sdewddwasPick", currentLatitude.toString())

        mMap = googleMap
        locateOnMapAddress = currentAddress
        tvAddress!!.text = currentAddress
        markerOptionscurrent.title(currentAddress)

        googleMap!!.animateCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    currentLatitude,
                    currentLongitude
                )
            )
        )
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLatitude,
                    currentLongitude
                ), 15.0f
            )
        )
        //  mMap!!.addMarker(MarkerOptions().position(LatLng(currentLatitude, currentLongitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)))


        /* googleMap.setOnMapClickListener { latLng ->
             ivFavLocateOnMap!!.setBackgroundResource(R.drawable.ic_fav_unchecked)
             getAddress(latLng)
         }*/


        /**
         * iLoma Team :- Mohsin 12 jan 2021
         */

        mMap!!.setOnCameraIdleListener {
            val mapCenter =
                mMap!!.cameraPosition.target
            plotedLocation = Location("")
            plotedLocation!!.setLatitude(mapCenter.latitude)
            plotedLocation!!.setLongitude(mapCenter.longitude)

            moveToCurrentLocation(mapCenter)

        }
    }

    private fun getAddress(latLng: LatLng) {

        if(ProjectUtilities.checkInternetAvailable(this@PickUpDropActivity)) {
            currentLatitude = latLng.latitude
            currentLongitude = latLng.longitude
            val street: String? = getAddressFromLocation()

            locateOnMapAddress = street
            tvAddress!!.text = street
        }else{
            ProjectUtilities.showToast(this@PickUpDropActivity,getString(R.string.internet_error))
        }


    }

    override fun click(place: Place?, selectedAddress: String) {
        if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
            if ((place!!.types!!.get(0).name) == "AIRPORT") {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "AIRPORT"
                )
            } else {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    place!!.types!!.get(0).name
                )
            }

            keyAirport = place!!.types!!.get(0).name
        }

        currentLatitude = place!!.latLng!!.latitude
        currentLongitude = place!!.latLng!!.longitude

        //ILOMADEV To Remove CountryName From Selected AutoPlaceAddress
        var newSelectedAddress: String = removeCountryFromAddress(selectedAddress, place?.latLng)

        if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
            isSource = true
            sharedpreferences!!.edit().remove("SourceLat")
            sharedpreferences!!.edit().remove("SourceLong")
            editor!!.putString(SourceLat, currentLatitude.toString())
            editor!!.putString(SourceLong, currentLongitude.toString())
            editor!!.putString(fromLocation, newSelectedAddress)

        } else {
            isSource = false
            sharedpreferences!!.edit().remove("DestinationLat")
            sharedpreferences!!.edit().remove("DestinationLong")
            editor!!.putString(DestinationLat, currentLatitude.toString())
            editor!!.putString(DestinationLong, currentLongitude.toString())
            editor!!.putString(destiNationLocation, newSelectedAddress)
        }
        editor!!.commit()
        editor!!.apply()
        mapCalled(mMap)
        // Toast.makeText(this, place.getAddress() + ", " + place.getLatLng().latitude + place.getLatLng().longitude, Toast.LENGTH_LONG).show();
        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra("Toolbar_Title", extras!!.getString("Toolbar_Title"))
        intent.putExtra("currentLatitude", currentLatitude)
        intent.putExtra("currentLongitude", currentLongitude)
        intent.putExtra("keyAirport", keyAirport)
        intent.putExtra("spnbg", extras!!.getInt("spinnerbag"))
        intent.putExtra("spnTime", extras!!.getInt("spinnerTime"))
        intent.putExtra("getcity", extras!!.getString("City"))
        intent.putExtra("formaredDateLater", extras!!.getString("formaredDateLater"))

        intent.putExtra("TvDateTime", extras!!.getString("spinnerTimeDate"))

        intent.putExtra("splacedi", newSelectedAddress)

        //Old Code
        if (Constants.IS_OLD_PICK_UP_CODE) {
            startActivity(intent)
        } else {
            // ILOMADEV
            finish()

            EventBus.getDefault().post(
                PickUpLocationModel(
                    currentLatitude,
                    currentLongitude,
                    isSource,
                    newSelectedAddress,
                    keyAirport, ""
                )
            )
        }

    }


    override fun favClick(place: Place?) {
        Toast.makeText(this, "Added in fav" + place!!.id, Toast.LENGTH_LONG).show()
    }

    override fun seveRecent(placeID: String?, selectedadd: String?, addressType: String?) {
        val placeFields =
            Arrays.asList(
                Place.Field.LAT_LNG,
                Place.Field.NAME
            )
        val request = FetchPlaceRequest.newInstance(placeID!!, placeFields)
        placesClient!!.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                // ILOMADEV
                var newSelectedAddress: String =
                    removeCountryFromAddress(selectedadd!!, place?.latLng)

                mMap!!.clear()
                if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
                    isSource = true
                    sharedpreferences!!.edit().remove("SourceLat")
                    sharedpreferences!!.edit().remove("SourceLong")
                    editor!!.putString(SourceLat, place.latLng!!.latitude.toString())
                    editor!!.putString(SourceLong, place.latLng!!.longitude.toString())
                    editor!!.putString(fromLocation, newSelectedAddress)

                } else {
                    isSource = false
                    sharedpreferences!!.edit().remove("DestinationLat")
                    sharedpreferences!!.edit().remove("DestinationLong")
                    editor!!.putString(DestinationLat, place.latLng!!.latitude.toString())
                    editor!!.putString(DestinationLong, place.latLng!!.longitude.toString())
                    editor!!.putString(destiNationLocation, newSelectedAddress)
                }
                editor!!.commit()
                editor!!.apply()
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.putExtra("Toolbar_Title", extras!!.getString("Toolbar_Title"))
                intent.putExtra("currentLatitude", place.latLng!!.latitude)
                intent.putExtra("currentLongitude", place.latLng!!.longitude)
                intent.putExtra("spnbg", extras!!.getInt("spinnerbag"))
                intent.putExtra("spnTime", extras!!.getInt("spinnerTime"))
                intent.putExtra("getcity", extras!!.getString("City"))
                intent.putExtra("formaredDateLater", extras!!.getString("formaredDateLater"))
                intent.putExtra("AddressType", addressType)

                intent.putExtra("TvDateTime", extras!!.getString("spinnerTimeDate"))


                //Old Code
                if (Constants.IS_OLD_PICK_UP_CODE) {
                    startActivity(intent)
                } else {
                    // ILOMADEV
                    finish()

                    EventBus.getDefault().post(
                        PickUpLocationModel(
                            place.latLng!!.latitude,
                            place.latLng!!.longitude,
                            isSource,
                            newSelectedAddress,
                            keyAirport, addressType
                        )
                    )
                }




                Log.d(
                    "Placefound: ",
                    place.latLng!!.latitude.toString() + "   " + place.latLng!!.longitude
                )
            }.addOnFailureListener { exception: Exception ->
                if (exception is com.google.android.gms.common.api.ApiException) {
                    val statusCode = exception.statusCode
                }
            }
    }

    override fun favClick(fullAddress: Int) {

        val tokenLogin =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.deleteRecentLocation("Bearer $tokenLogin", fullAddress.toString() + "")!!
            .enqueue(object : Callback<DeleteSaveDataResponsePOJO?> {
                override fun onResponse(
                    call: Call<DeleteSaveDataResponsePOJO?>,
                    response: Response<DeleteSaveDataResponsePOJO?>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(
                            this@PickUpDropActivity,
                            "Location deleted successfully !!",
                            Toast.LENGTH_SHORT
                        ).show()

                        client.getSavedLocation("Bearer $token")!!.enqueue(object :
                            Callback<GetSaveLocationResponsePOJO?> {
                            override fun onResponse(
                                call: Call<GetSaveLocationResponsePOJO?>,
                                response: Response<GetSaveLocationResponsePOJO?>
                            ) {
                                if (response.code() == 200) {


                                    savedLocationList = response.body()!!.data!!
                                    recyclerViewAdapter = RecyclerViewAdapter(
                                        this@PickUpDropActivity,
                                        savedLocationList
                                    )
                                    recycler_view_saved!!.adapter = recyclerViewAdapter
                                    recyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)


                                    /* savedLocationList = response.body()!!.data!!.locations!!
                                     recyclerViewAdapter = RecyclerViewAdapter(this@PickUpDropActivity, savedLocationList)
                                     recyclerView!!.adapter = recyclerViewAdapter
                                     recyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)*/
                                } else {
                                    Toast.makeText(
                                        this@PickUpDropActivity,
                                        "Internal server error",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<GetSaveLocationResponsePOJO?>,
                                t: Throwable
                            ) {
                                Log.d("response", t.stackTrace.toString())
                            }
                        })
                    } else {
                        Toast.makeText(
                            this@PickUpDropActivity,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<DeleteSaveDataResponsePOJO?>,
                    t: Throwable
                ) {
                    Log.d("response", t.stackTrace.toString())
                }
            })

    }

    override fun update(id: Int, fulladdress: String?) {

        client.updateLocation("Bearer $token", fulladdress, id.toString())!!.enqueue(object :
            Callback<UpdateProfileResponsePOJO?> {
            override fun onResponse(
                call: Call<UpdateProfileResponsePOJO?>,
                response: Response<UpdateProfileResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    val inputMethodManager: InputMethodManager =
                        this@PickUpDropActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    getSavedLocation()

                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(this@PickUpDropActivity, pojo.message, Toast.LENGTH_LONG)
                            .show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        this@PickUpDropActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<UpdateProfileResponsePOJO?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
            }
        })
    }

    companion object {
        const val mypreference = "mypref"
        const val SourceLat = "SourceLat"
        const val SourceLong = "SourceLong"
        const val DestinationLat = "DestinationLat"
        const val DestinationLong = "DestinationLong"
        const val fromLocation = "fromLocation"
        const val destiNationLocation = "destiNationLocation"


        const val SourceAddress = "SourceAddress"
        const val DestinationAddress = "DestinationAddress"

    }



    /**
     * iLoma Team :- Mohasin 12 Jan 2021
     */
    private fun moveToCurrentLocation(currentLocation: LatLng?) {
        if (currentLocation != null) {
            if (plotedLocation != null) {
                plotedLocation!!.longitude = currentLocation.longitude
                plotedLocation!!.latitude = currentLocation.latitude
            }

            getAddress(currentLocation)
            mMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    currentLocation,
                    mMap!!.cameraPosition.zoom
                )
            )

            mMap!!.animateCamera(
                CameraUpdateFactory.zoomTo(mMap!!.cameraPosition.zoom),
                2000,
                null
            )

        }
    }

    private fun getAddressFromLocation(): String? {
        var address: String = ""
        val geocoder = Geocoder(this@PickUpDropActivity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1)

            if (addresses != null && addresses!!.size > 0) {
                val obj = addresses[0]

                address = obj.getAddressLine(0)
                var countryName = obj.countryName
                if (obj.countryName != null && obj.countryName.equals(
                        "United States",
                        ignoreCase = true
                    )
                ) {
                    obj.countryName = "USA"
                }
                if (!countryName.equals("")) {
                    address = address.replace(", $countryName", "").replace("- $countryName", "")
                }

                if (obj != null && obj.postalCode != null && !obj.postalCode.equals("")) {
                    address = address.replace(" " + obj.postalCode, "")
                }

            } else {
                address = ""
            }
        } catch (e: IOException) {
        }

        return address
    }

    private fun removeCountryFromAddress(
        selectedAddress: String,
        latLng: LatLng?
    ): String {

        var countryName: String = ""
        val geocoder = Geocoder(this@PickUpDropActivity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng?.latitude!!, latLng.longitude, 1)

            if (addresses != null && addresses!!.size > 0) {
                val obj = addresses[0]
                countryName = obj.countryName
                if (countryName != null && countryName.equals("United States", ignoreCase = true)) {
                    countryName = "USA"
                }


            }
        } catch (e: IOException) {
        }
        if (countryName.equals("")) {
            return selectedAddress
        }

        return selectedAddress.replace(", $countryName", "").replace("- $countryName", "")


    }


}
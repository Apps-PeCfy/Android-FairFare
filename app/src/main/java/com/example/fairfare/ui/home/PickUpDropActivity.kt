package com.example.fairfare.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.fairfare.R
import com.example.fairfare.networking.ApiClient.client
import com.example.fairfare.ui.home.PickUpDropActivity
import com.example.fairfare.ui.home.PlacesAutoCompleteAdapter.ClickListener
import com.example.fairfare.ui.home.RecyclerViewAdapter.IClickListener
import com.example.fairfare.ui.home.pojo.DeleteSaveDataResponsePOJO
import com.example.fairfare.ui.home.pojo.GetSaveLocationResponsePOJO
import com.example.fairfare.ui.home.pojo.GetSaveLocationResponsePOJO.LocationsItem
import com.example.fairfare.ui.home.pojo.SaveLocationResponsePojo
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
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
    var extras: Bundle? = null

    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null
    var selectedTab = "Searches"
    var marker: Marker? = null

    @JvmField
    @BindView(R.id.homeView)
    var homeView: LinearLayout? = null

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
    var preferencesManager: PreferencesManager? = null
    private var mAutoCompleteAdapter: PlacesAutoCompleteAdapter? = null
    private var savedLocationList: List<LocationsItem> = ArrayList()
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
        placesClient =
            Places.createClient(this@PickUpDropActivity)
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.getRecentLocation("Bearer $token")!!.enqueue(object :
            Callback<GetSaveLocationResponsePOJO?> {
            override fun onResponse(
                call: Call<GetSaveLocationResponsePOJO?>,
                response: Response<GetSaveLocationResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    recyclerView!!.visibility = View.GONE
                    savedLocationList = response.body()?.data!!.locations!!
                    recentRecyclerViewAdapter =
                        RecentRecyclerViewAdapter(this@PickUpDropActivity, savedLocationList!!)
                    recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                    recycler_view_saved!!.layoutManager =
                        LinearLayoutManager(this@PickUpDropActivity)
                    recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                    recentRecyclerViewAdapter!!.notifyDataSetChanged()
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
        edt_pick_up_drop!!.addTextChangedListener(filterTextWatcher)
        mAutoCompleteAdapter = PlacesAutoCompleteAdapter(this)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        mAutoCompleteAdapter!!.setClickListener(this)
        recyclerView!!.adapter = mAutoCompleteAdapter
        mAutoCompleteAdapter!!.notifyDataSetChanged()
        sharedpreferences = getSharedPreferences(
            mypreference,
            Context.MODE_PRIVATE
        )
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
        }
        setToolbar()
    }

    private val filterTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (selectedTab == "Saved") {
                val listClone: MutableList<LocationsItem> =
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
                val listCloneRecent: MutableList<LocationsItem> =
                    ArrayList()
                for (i in savedLocationList!!.indices) {
                    if (savedLocationList[i]!!.fullAddress!!.toLowerCase()
                            .contains(s.toString().toLowerCase())
                    ) {
                        listCloneRecent.add(savedLocationList[i])
                    }
                }
                recentRecyclerViewAdapter =
                    RecentRecyclerViewAdapter(this@PickUpDropActivity, listCloneRecent)
                recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
            }
            if (selectedTab == "Searches") {
                if (s.toString() != "") {
                    mAutoCompleteAdapter!!.filter.filter(s.toString())
                    recycler_view_saved!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                } else {
                    recyclerView!!.visibility = View.GONE
                    recycler_view_saved!!.visibility = View.VISIBLE
                    val listCloneRecent: MutableList<LocationsItem> =
                        ArrayList()
                    for (i in savedLocationList.indices) {
                        if (savedLocationList[i].fullAddress!!.toLowerCase()
                                .contains(s.toString()!!.toLowerCase())
                        ) {
                            listCloneRecent.add(savedLocationList[i])
                        }
                    }
                    recentRecyclerViewAdapter =
                        RecentRecyclerViewAdapter(this@PickUpDropActivity, listCloneRecent)
                    recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                    recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                }
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
        homeView!!.visibility = View.GONE
        lladdress!!.visibility = View.VISIBLE
    }

    @OnClick(R.id.tv_searches)
    fun search() {
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
                        savedLocationList = response.body()!!.data!!.locations!!
                        recentRecyclerViewAdapter =
                            RecentRecyclerViewAdapter(this@PickUpDropActivity, savedLocationList)
                        recycler_view_saved!!.adapter = recentRecyclerViewAdapter
                        recycler_view_saved!!.layoutManager =
                            LinearLayoutManager(this@PickUpDropActivity)
                        recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
                        recentRecyclerViewAdapter!!.notifyDataSetChanged()
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
    }

    @OnClick(R.id.tv_saved)
    fun saved() {
        edt_pick_up_drop!!.setText("")
        recyclerView!!.visibility = View.GONE
        selectedTab = "Saved"
        tv_view_recent!!.setBackgroundColor(Color.GRAY)
        tv_view_saved!!.setBackgroundColor(Color.RED)
        tv_view_search!!.setBackgroundColor(Color.GRAY)
        token =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.getSavedLocation("Bearer $token")!!.enqueue(object :
            Callback<GetSaveLocationResponsePOJO?> {
            override fun onResponse(
                call: Call<GetSaveLocationResponsePOJO?>,
                response: Response<GetSaveLocationResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    savedLocationList = response.body()!!.data!!.locations!!
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
                    savedLocationList = response.body()!!.data!!.locations!!
                    recentRecyclerViewAdapter =
                        RecentRecyclerViewAdapter(this@PickUpDropActivity, savedLocationList)
                    recyclerView!!.adapter = recentRecyclerViewAdapter
                    recentRecyclerViewAdapter!!.setClickListener(this@PickUpDropActivity)
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
        var returnedAddress: Address? = null
        val geocoder = Geocoder(this@PickUpDropActivity, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(
                    currentLatitude,
                    currentLongitude, 1
                )
            if (addresses != null) {
                returnedAddress = addresses[0]
            }
        } catch (e: IOException) {
        }
        val token =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.SaveLocation(
            "Bearer $token", results[0]!!.placeId, returnedAddress!!.subAdminArea,
            returnedAddress.adminArea,
            returnedAddress.countryName,
            returnedAddress.getAddressLine(0)
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
                        Toast.LENGTH_LONG
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
    }

    override fun onDestroy() {
        sharedpreferences!!.edit().clear().commit()
        super.onDestroy()
    }

    @OnClick(R.id.btnContinue)
    fun btnContinue() {
        if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
            sharedpreferences!!.edit().remove("SourceLat")
            sharedpreferences!!.edit().remove("SourceLong")
            editor!!.putString(SourceLat, currentLatitude.toString())
            editor!!.putString(SourceLong, currentLongitude.toString())
        } else {
            sharedpreferences!!.edit().remove("DestinationLat")
            sharedpreferences!!.edit().remove("DestinationLong")
            editor!!.putString(
                DestinationLat,
                currentLatitude.toString()
            )
            editor!!.putString(
                DestinationLong,
                currentLongitude.toString()
            )
        }
        editor!!.commit()
        editor!!.apply()
        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra("Toolbar_Title", extras!!.getString("Toolbar_Title"))
        intent.putExtra("currentLatitude", currentLatitude)
        intent.putExtra("currentLongitude", currentLongitude)
        startActivity(intent)
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
        var currentAddress: String? = null
        val geocoder = Geocoder(this@PickUpDropActivity, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(currentLatitude, currentLongitude, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder()
                for (j in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j))
                }
                currentAddress = strReturnedAddress.toString()
            }
        } catch (e: IOException) {
        }
        mMap = googleMap
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
                ), 13.0f
            )
        )
        mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    currentLatitude,
                    currentLongitude
                )
            ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
        )


        //   googleMap.addMarker(markerOptionscurrent);
        googleMap.setOnMapClickListener { latLng ->
            ivFavLocateOnMap!!.setBackgroundResource(R.drawable.ic_fav_unchecked)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            currentLatitude = latLng.latitude
            currentLongitude = latLng.longitude
            var street: String? = null
            val geocoder =
                Geocoder(this@PickUpDropActivity, Locale.getDefault())
            try {
                val addresses =
                    geocoder.getFromLocation(currentLatitude, currentLongitude, 1)
                if (addresses != null) {
                    val returnedAddress = addresses[0]
                    val strReturnedAddress = StringBuilder()
                    for (j in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j))
                    }
                    street = strReturnedAddress.toString()
                }
            } catch (e: IOException) {
            }
            tvAddress!!.text = street
            markerOptions.title(street)
            googleMap.clear()
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f))
            mMap!!.addMarker(
                MarkerOptions().position(
                    LatLng(
                        latLng.latitude,
                        latLng.longitude
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
            )

            //  googleMap.addMarker(markerOptions);
        }
    }

    override fun click(place: Place?) {
        currentLatitude = place!!.latLng!!.latitude
        currentLongitude = place!!.latLng!!.longitude
        if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
            sharedpreferences!!.edit().remove("SourceLat")
            sharedpreferences!!.edit().remove("SourceLong")
            editor!!.putString(SourceLat, currentLatitude.toString())
            editor!!.putString(SourceLong, currentLongitude.toString())
        } else {
            sharedpreferences!!.edit().remove("DestinationLat")
            sharedpreferences!!.edit().remove("DestinationLong")
            editor!!.putString(
                DestinationLat,
                currentLatitude.toString()
            )
            editor!!.putString(
                DestinationLong,
                currentLongitude.toString()
            )
        }
        editor!!.commit()
        editor!!.apply()
        mapCalled(mMap)
        // Toast.makeText(this, place.getAddress() + ", " + place.getLatLng().latitude + place.getLatLng().longitude, Toast.LENGTH_LONG).show();
        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra("Toolbar_Title", extras!!.getString("Toolbar_Title"))
        intent.putExtra("currentLatitude", currentLatitude)
        intent.putExtra("currentLongitude", currentLongitude)
        startActivity(intent)
    }

    override fun favClick(place: Place?) {
        Toast.makeText(this, "Added in fav" + place!!.id, Toast.LENGTH_LONG).show()
    }

    override fun seveRecent(placeID: String?) {
        val placeFields =
            Arrays.asList(
                Place.Field.LAT_LNG,
                Place.Field.NAME
            )
        val request = FetchPlaceRequest.newInstance(placeID!!, placeFields)
        placesClient!!.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                mMap!!.clear()
                if (extras!!.getString("Toolbar_Title") == "Pick-Up") {
                    sharedpreferences!!.edit().remove("SourceLat")
                    sharedpreferences!!.edit().remove("SourceLong")
                    editor!!.putString(
                        SourceLat,
                        place.latLng!!.latitude.toString()
                    )
                    editor!!.putString(
                        SourceLong,
                        place.latLng!!.longitude.toString()
                    )
                } else {
                    sharedpreferences!!.edit().remove("DestinationLat")
                    sharedpreferences!!.edit().remove("DestinationLong")
                    editor!!.putString(
                        DestinationLat,
                        place.latLng!!.latitude.toString()
                    )
                    editor!!.putString(
                        DestinationLong,
                        place.latLng!!.longitude.toString()
                    )
                }
                editor!!.commit()
                editor!!.apply()
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.putExtra("Toolbar_Title", extras!!.getString("Toolbar_Title"))
                intent.putExtra("currentLatitude", place.latLng!!.latitude)
                intent.putExtra("currentLongitude", place.latLng!!.longitude)
                startActivity(intent)

                // mapCalled(mMap);
                Log.d(
                    "Placefound: ",
                    place.latLng!!.latitude.toString() + "   " + place.latLng!!.longitude
                )
            }.addOnFailureListener { exception: Exception ->
                if (exception is com.google.android.gms.common.api.ApiException) {
                    Log.d("Place not found: ", exception.message)
                    val statusCode = exception.statusCode
                }
            }
    }

    override fun favClick(fullAddress: Int) {
        val tokenLogin =
            preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        client.deleteRecentLocation(
            "Bearer $tokenLogin",
            fullAddress.toString() + ""
        )!!.enqueue(object : Callback<DeleteSaveDataResponsePOJO?> {
            override fun onResponse(
                call: Call<DeleteSaveDataResponsePOJO?>,
                response: Response<DeleteSaveDataResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(
                        this@PickUpDropActivity,
                        "Location deleted successfully !!",
                        Toast.LENGTH_LONG
                    ).show()
                    client.getSavedLocation("Bearer $tokenLogin")!!.enqueue(object :
                        Callback<GetSaveLocationResponsePOJO?> {
                        override fun onResponse(
                            call: Call<GetSaveLocationResponsePOJO?>,
                            response: Response<GetSaveLocationResponsePOJO?>
                        ) {
                            if (response.code() == 200) {
                                savedLocationList = response.body()!!.data!!.locations!!
                                recyclerViewAdapter =
                                    RecyclerViewAdapter(this@PickUpDropActivity, savedLocationList)
                                recyclerView!!.adapter = recyclerViewAdapter
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

    companion object {
        const val mypreference = "mypref"
        const val SourceLat = "SourceLat"
        const val SourceLong = "SourceLong"
        const val DestinationLat = "DestinationLat"
        const val DestinationLong = "DestinationLong"
    }
}
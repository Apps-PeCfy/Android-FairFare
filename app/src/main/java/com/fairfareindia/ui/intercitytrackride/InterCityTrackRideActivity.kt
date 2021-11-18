package com.fairfareindia.ui.intercitytrackride

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.databinding.ActivityInterCityTrackRideBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class InterCityTrackRideActivity : BaseLocationClass(), OnMapReadyCallback, IIntercityTrackRideView {
    lateinit var binding: ActivityInterCityTrackRideBinding
    private var context: Context = this


    private var mMap: GoogleMap? = null

    private var rideID: String? = null
    private var token: String? = null
    private var preferencesManager: PreferencesManager? = null

    private var rideDetailModel: RideDetailModel ?= null
    private var iInterCityTrackRidePresenter: IInterCityTrackRidePresenter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterCityTrackRideBinding.inflate(layoutInflater)
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

        iInterCityTrackRidePresenter = InterCityTrackRideImplementer(this)

        iInterCityTrackRidePresenter?.getRideDetails(token, rideID)

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            txtTrackBoard.setOnClickListener {
                llTrackData.visibility = View.VISIBLE
                txtTrackBoard.visibility = View.GONE
                llShowData.visibility = View.GONE
            }

            imgClose.setOnClickListener {
                llTrackData.visibility = View.GONE
                txtTrackBoard.visibility = View.VISIBLE
                llShowData.visibility = View.VISIBLE
            }

            rlHideShow.setOnClickListener {
                if (llHideView.visibility == View.GONE){
                    llHideView.visibility = View.VISIBLE
                }else{
                    llHideView.visibility = View.GONE
                }
            }


        }
    }

    private fun setData() {
        binding.apply {

            txtPickUpLocation.text = rideDetailModel?.data?.originAddress
            txtDropUpLocation.text = rideDetailModel?.data?.destinationAddress


            txtEstDistance.text = "Est.Distance ${rideDetailModel?.data?.estimatedTrackRide?.distance?.toInt()} Km"
            txtEstTime.text = "Est.Time ${rideDetailModel?.data?.estimatedTrackRide?.totalTime}"

            //Driver and Vehical
            txtVehicleName.text =  rideDetailModel?.data?.vehicleName

            Glide.with(context)
                .load(rideDetailModel?.data?.vehicleImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                ).into(imgVehicle)
        }
    }


    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
    }

    /**
     * API CALLING
     */

    override fun getRideDetails(model: RideDetailModel?) {
        rideDetailModel = model
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
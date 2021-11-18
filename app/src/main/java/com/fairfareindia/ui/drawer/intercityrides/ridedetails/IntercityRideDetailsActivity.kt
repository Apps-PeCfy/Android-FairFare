package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityRideDetailsBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.utils.AppUtils
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities

class IntercityRideDetailsActivity : AppCompatActivity(), IRideDetailView {
    lateinit var binding: ActivityIntercityRideDetailsBinding
    private var context: Context = this
    private var token: String? = null
    private var rideID: String? = null
    private var model: RideDetailModel ?= null
    private var preferencesManager: PreferencesManager? = null
    private var iRidesDetailPresenter: IRidesDetailPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntercityRideDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        rideID = intent.getStringExtra("ride_id")

        PreferencesManager.initializeInstance(context)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        iRidesDetailPresenter = RidesDetailImplementer(this)

        iRidesDetailPresenter?.getRideDetail(token, rideID)

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setData() {
        binding.apply {

            txtEstLuggageCharges.text = "₹ " + model?.data?.estimatedTrackRide?.luggageCharges
            txtEstDistance.text = model?.data?.estimatedTrackRide?.baseDistance.toString()
            txtEstAddDistance.text = model?.data?.estimatedTrackRide?.additionalDistance.toString()
            txtEstRideTime.text = model?.data?.estimatedTrackRide?.totalTime
            txtEstWaitTime.text =  model?.data?.estimatedTrackRide?.waitingTime
            txtEstWaitCharges.text = "₹ " + model?.data?.estimatedTrackRide?.waitingCharges
            txtEstBaseFare.text = "₹ " + model?.data?.estimatedTrackRide?.basicFare
            txtEstTollCharges.text = "₹ " + model?.data?.estimatedTrackRide?.tollCharges
            txtEstAddDistanceCharges.text = "₹ " + model?.data?.estimatedTrackRide?.additionalDistanceCharges
            txtEstSurcharges.text = "₹ " + model?.data?.estimatedTrackRide?.surCharge
            txtEstConvenience.text = "₹ " + model?.data?.estimatedTrackRide?.convenienceFees
            txtEstTotalFare.text = "₹ " + model?.data?.estimatedTrackRide?.totalCharges



            txtActualLuggageCharges.text = "₹ " + model?.data?.actualTrackRide?.luggageCharges
            txtActualDistance.text = model?.data?.actualTrackRide?.baseDistance.toString()
            txtActualAddDistance.text = model?.data?.actualTrackRide?.additionalDistance.toString()
            txtActualRideTime.text = model?.data?.actualTrackRide?.totalTime
            txtActualWaitTime.text =  model?.data?.actualTrackRide?.waitingTime
            txtActualWaitCharges.text = "₹ " + model?.data?.actualTrackRide?.waitingCharges
            txtActualBaseFare.text = "₹ " + model?.data?.actualTrackRide?.basicFare
            txtActualTollCharges.text = "₹ " + model?.data?.actualTrackRide?.tollCharges
            txtActualAddDistanceCharges.text = "₹ " + model?.data?.actualTrackRide?.additionalDistanceCharges
            txtActualSurcharges.text = "₹ " + model?.data?.actualTrackRide?.surCharge
            txtActualConvenience.text = "₹ " + model?.data?.actualTrackRide?.convenienceFees
            txtActualTotalFare.text = "₹ " + model?.data?.actualTrackRide?.totalCharges


            //Driver and Vehical
            txtVehicleName.text =  model?.data?.vehicleName
            txtVehicleNumber.text = model?.data?.vehicleNo
            txtDriverName.text = model?.data?.driver?.name
            txtDate.text = AppUtils.changeDateFormat(model?.data?.dateTime, "yyyy-MM-dd HH:mm:ss", "dd MMM, hh:mm a")

            if (model?.data?.luggageQuantity == "0") {
                txtLuggage.text = getString(R.string.str_no_bags)
            } else if (model?.data?.luggageQuantity == "1") {
                txtLuggage.text = model?.data?.luggageQuantity + " " + getString(R.string.str_bag)
            } else {
                txtLuggage.text = model?.data?.luggageQuantity + " " + getString(R.string.str_bags)
            }

            Glide.with(context)
                .load(model?.data?.vehicleImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                ).into(imgVehicle)





            if(model?.data?.rewards != null && model?.data?.rewards != "0"){
                txtRewardsPoint.visibility = View.VISIBLE
                txtRewardsPoint.text = "Reward points earned for this ride " + model?.data?.rewards
            }else{
                txtRewardsPoint.visibility = View.GONE
            }
        }
    }

    /**
     * API RESPONSE
     */

    override fun getRideDetailSuccess(model: RideDetailModel?) {
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
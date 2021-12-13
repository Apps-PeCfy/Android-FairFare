package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityRideDetailsBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.disputs.RegisterDisputActivity
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.ridereview.RideReviewActivity
import com.fairfareindia.utils.*
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class IntercityRideDetailsActivity : AppCompatActivity(), IRideDetailView,
    PaymentResultWithDataListener {
    lateinit var binding: ActivityIntercityRideDetailsBinding
    private var context: Context = this
    private var token: String? = null
    private var rideID: String? = null
    private var model: RideDetailModel ?= null
    private var preferencesManager: PreferencesManager? = null
    private var iRidesDetailPresenter: IRidesDetailPresenter? = null
    private var isFromEndRide : Boolean = false
    private var waitingInfoDialog : WaitingInfoDialog ?= null
    private var tollInfoDialog : TollInfoDialog ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntercityRideDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        rideID = intent.getStringExtra("ride_id")
        isFromEndRide = intent.getBooleanExtra("isFromEndRide", false)

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

            btnPayNow.setOnClickListener {
                startPayment()
            }

            imgWaitInfo.setOnClickListener {
                if(model?.data?.waitings?.isNotEmpty()!!){
                    openWaitInfoDialog()
                }
            }

            imgTollInfo.setOnClickListener {
                if(model?.data?.estimatedTrackRide?.tolls?.isNotEmpty()!!){
                    openTollInfoDialog()
                }
            }

            btnRateRide.setOnClickListener {
                if(ProjectUtilities.checkInternetAvailable(context)) {
                    val intent = Intent(context, RideReviewActivity::class.java)
                    intent.putExtra("RIDEID", rideID)
                    startActivity(intent)
                }else{
                    ProjectUtilities.showToast(context,getString(R.string.internet_error))
                }
            }

            btnRegisterDispute.setOnClickListener {
                if(ProjectUtilities.checkInternetAvailable(context)) {

                    val intent = Intent(context, RegisterDisputActivity::class.java)
                    intent.putExtra("vahicalNo", model?.data?.vehicleNo)
                    intent.putExtra("driverName", model?.data?.driverName)
                    intent.putExtra("bagCount", model?.data?.luggageQuantity)
                    intent.putExtra("Datetime", model?.data?.dateTime)
                    intent.putExtra("vahicalName", model?.data?.vehicleName)
                    intent.putExtra("vahicalImg", model?.data?.vehicleImageUrl)
                    intent.putExtra("RIDEID", rideID)
                    intent.putExtra("permit_type", model?.data?.permitType)
                    startActivity(intent)
                }else{
                    ProjectUtilities.showToast(context,getString(R.string.internet_error))
                }
            }
        }
    }

    private fun openTollInfoDialog() {
        tollInfoDialog = TollInfoDialog(context, model?.data?.estimatedTrackRide?.tolls!!)
        tollInfoDialog?.show()
        tollInfoDialog?.setCancelable(false)
    }

    private fun openWaitInfoDialog() {
        waitingInfoDialog = WaitingInfoDialog(context, model?.data?.waitings!!)
        waitingInfoDialog?.show()
        waitingInfoDialog?.setCancelable(false)
    }

    private fun setData() {
        binding.apply {

            txtEstLuggageCharges.text = "₹ " + model?.data?.estimatedTrackRide?.luggageCharges
            txtEstDistance.text = model?.data?.estimatedTrackRide?.baseDistance.toString()
            txtEstAddDistance.text = model?.data?.estimatedTrackRide?.additionalDistance.toString()
            txtEstRideTime.text = model?.data?.estimatedTrackRide?.totalTime
            txtEstWaitTime.text =  ProjectUtilities.timeInMinutesConvertingToString(context, model?.data?.estimatedTrackRide?.waitingTime!!)
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
            txtActualWaitTime.text =  ProjectUtilities.timeInMinutesConvertingToString(context, model?.data?.actualTrackRide?.waitingTime!!)
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
                txtRewardsPoint.text = getString(R.string.str_reward_points_earned_for_this_ride) + " " + model?.data?.rewards
            }else{
                txtRewardsPoint.visibility = View.GONE
            }

            if (model?.data?.totalunPaid != null && model?.data?.totalunPaid!! > 0){
                txtBalanceAmount.text = "₹ " + model?.data?.totalunPaid
                if (model?.data?.balancePaymentStatus == Constants.PAYMENT_PAID){
                    txtBalanceAmountLabel.text = getString(R.string.txt_balance_fare_paid)
                    btnPayNow.visibility = View.GONE
                    if (isFromEndRide){
                        btnRateRide.visibility = View.VISIBLE
                        btnRegisterDispute.visibility = View.VISIBLE
                        rlBalanceAmount.visibility = View.VISIBLE
                    }
                    txtBalanceAmountLabel.setTextColor(getColor(R.color.colorGreen))
                    txtBalanceAmount.setTextColor(getColor(R.color.colorGreen))
                }else{
                    txtBalanceAmountLabel.text = getString(R.string.txt_balance_fare_paid)
                    rlBalanceAmount.visibility = View.VISIBLE
                    btnPayNow.visibility = View.VISIBLE
                    btnRateRide.visibility = View.GONE
                    btnRegisterDispute.visibility = View.GONE
                    txtBalanceAmountLabel.setTextColor(getColor(R.color.colorRed))
                    txtBalanceAmount.setTextColor(getColor(R.color.colorRed))
                }

            }else{
                btnPayNow.visibility = View.GONE
                if (isFromEndRide){
                    btnRateRide.visibility = View.VISIBLE
                    btnRegisterDispute.visibility = View.VISIBLE
                    txtRewardsPoint.visibility = View.GONE
                }
            }

            if (isFromEndRide){
                txtPickUpLocation.text = model?.data?.originAddress
                txtDropUpLocation.text = model?.data?.destinationAddress
                llEndRideTop.visibility = View.VISIBLE
            }else{
                llEndRideTop.visibility = View.GONE
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

    override fun updatePaymentStatusSuccess(model: RideDetailModel?) {
       iRidesDetailPresenter?.getRideDetail(token, rideID)
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

    /**
     * RAZOR PAY INTEGRATION
     */

    /**
     * Razor Pay Payment Gateway Integration
     */

    private fun startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        val activity: Activity = this
        val checkout = Checkout()
        checkout.setKeyID(Constants.RAZOR_PAY_KEY)
        try {
            val options = JSONObject()
            options.put("name", SessionManager.getInstance(context).getUserModel()?.name)
            options.put("description", "Demoing Charges")
            options.put("send_sms_hash", true)
            options.put("allow_rotation", false)
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            options.put("amount", (model?.data?.totalunPaid?.times(100)).toString())
            val preFill = JSONObject()
            preFill.put("email", "test@razorpay.com")
            preFill.put("contact", SessionManager.getInstance(context).getUserModel()?.phoneNo)
            options.put("prefill", preFill)
            checkout.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        try {
            Toast.makeText(
                this,
                "Payment failed: " + code.toString() + " " + response,
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Exception in onPaymentError: $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?, paymentData: PaymentData?) {
        try {
            iRidesDetailPresenter?.updatePaymentStatus(token, rideID, "Online", model?.data?.totalunPaid.toString(), Constants.PAYMENT_PAID, "Razorpay", razorpayPaymentID)
            //    Toast.makeText(this, "Payment Success: " + paymentData, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Exception in onPaymentSuccess: $e", Toast.LENGTH_SHORT).show()
        }
    }
}
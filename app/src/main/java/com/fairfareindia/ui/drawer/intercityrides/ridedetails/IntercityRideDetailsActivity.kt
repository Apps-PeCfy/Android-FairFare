package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityRideDetailsBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.disputs.RegisterDisputActivity
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.intercityviewride.RazorPayModel
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

    private var razorPayModel: RazorPayModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntercityRideDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        rideID = intent.getStringExtra("ride_id")
        isFromEndRide = intent.getBooleanExtra("isFromEndRide", false)

        if (isFromEndRide){
            binding.toolbar.title = getString(R.string.title_end_ride)
        }else{
            binding.toolbar.title = getString(R.string.title_ride_details)
        }

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
               // startPayment(model)
                iRidesDetailPresenter?.getRazorPayOrderID(token, model?.data?.driver?.union_id, model?.data?.totalunPaid?.times(100)?.toInt().toString() )
            }

            imgRefresh.setOnClickListener {
                iRidesDetailPresenter?.getRideDetail(token, rideID)
            }

            imgWaitInfo.setOnClickListener {
                if(model?.data?.waitings?.isNotEmpty()!!){
                    openWaitInfoDialog()
                }
            }

            imgTollInfo.setOnClickListener {
                if (model?.data?.estimatedTrackRide?.tolls != null && model?.data?.estimatedTrackRide?.tolls?.size!! > 0) {
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

            if (model?.data?.permitType == Constants.TYPE_INTERCITY){
                llAdditionalDistance.visibility = View.VISIBLE
                llAdditionalDistanceCharges.visibility = View.VISIBLE
                txtBaseDistanceLabel.text = getString(R.string.str_base_distance)
                txtBaseFareLabel.text = getString(R.string.str_base_fare)
                txtEstDistance.text =  ProjectUtilities.getDistanceInFormat(model?.data?.estimatedTrackRide?.baseDistance)
                txtActualDistance.text =  ProjectUtilities.getDistanceInFormat(model?.data?.actualTrackRide?.baseDistance)
                txtEstBaseFare.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.basicFare?.toDouble())
                txtActualBaseFare.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.basicFare?.toDouble())

            }else{
                llAdditionalDistance.visibility = View.GONE
                llAdditionalDistanceCharges.visibility = View.GONE
                txtBaseDistanceLabel.text = getString(R.string.str_distance)
                txtBaseFareLabel.text = getString(R.string.str_basic_fare)
                txtEstDistance.text =  ProjectUtilities.getDistanceInFormat(model?.data?.estimatedTrackRide?.distance)
                txtActualDistance.text =  ProjectUtilities.getDistanceInThreeDigitFormat(model?.data?.actualTrackRide?.distance)
                var estBaseFare = model?.data?.estimatedTrackRide?.basicFare!! + model?.data?.estimatedTrackRide?.additionalDistanceCharges!!
                txtEstBaseFare.text = ProjectUtilities.getAmountInFormat(estBaseFare)

                var actualBaseFare = model?.data?.actualTrackRide?.basicFare!! + model?.data?.actualTrackRide?.additionalDistanceCharges!!
                txtActualBaseFare.text = ProjectUtilities.getAmountInFormat(actualBaseFare)
            }

            if ( model?.data?.actualTrackRide?.cancellationCharges != null && model?.data?.actualTrackRide?.cancellationCharges!! > 0.0){
                txtEstCancellation.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.cancellationCharges)
                txtActualCancellation.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.cancellationCharges)
                llCancellationCharges.visibility = View.VISIBLE
            }else{
                llCancellationCharges.visibility = View.GONE
            }

            txtUnionName.text = model?.data?.vehicle?.union?.name
            txtRideNumber.text =  getString(R.string.str_ride_id) +" : " + model?.data?.ride_number
            txtEstLuggageCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.luggageCharges?.toDouble())
            txtEstAddDistance.text = ProjectUtilities.getDistanceInFormat(model?.data?.estimatedTrackRide?.additionalDistance)
            txtEstRideTime.text = model?.data?.estimatedTrackRide?.totalTime
            txtEstWaitTime.text =  ProjectUtilities.timeInSecondsConvertingToString(context, model?.data?.estimatedTrackRide?.waitingTime!!)
            txtEstWaitCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.waitingCharges?.toDouble())
            txtEstNightCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.nightCharges?.toDouble())

            txtEstTollCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.tollCharges?.toDouble())
            txtEstAddDistanceCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.additionalDistanceCharges?.toDouble())
            txtEstSurcharges.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.surCharge?.toDouble())
            txtEstConvenience.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.convenienceFees?.toDouble())
            txtEstTotalFare.text = ProjectUtilities.getAmountInFormat(model?.data?.estimatedTrackRide?.totalCharges?.toDouble())



            txtActualLuggageCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.luggageCharges?.toDouble())
            txtActualAddDistance.text = ProjectUtilities.getDistanceInThreeDigitFormat(model?.data?.actualTrackRide?.additionalDistance)
            txtActualRideTime.text = model?.data?.actualTrackRide?.totalTime
          //  txtActualWaitTime.text =  ProjectUtilities.timeInSecondsConvertingToString(context, model?.data?.actualTrackRide?.waitingTime!!)
            txtActualWaitTime.text =  ProjectUtilities.timeInSecondsConvertingToString(context, model?.data?.actualTrackRide?.waitingTime!!)
            txtActualWaitCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.waitingCharges?.toDouble())
            txtActualNightCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.nightCharges?.toDouble())
            txtActualTollCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.tollCharges?.toDouble())
            txtActualAddDistanceCharges.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.additionalDistanceCharges?.toDouble())
            txtActualSurcharges.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.surCharge?.toDouble())
            txtActualConvenience.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.convenienceFees?.toDouble())
            txtActualTotalFare.text = ProjectUtilities.getAmountInFormat(model?.data?.actualTrackRide?.totalCharges?.toDouble())


            //Driver and Vehical
            txtVehicleName.text =  model?.data?.vehicleName
            txtVehicleNumber.text = model?.data?.vehicleNo
            txtDriverName.text = model?.data?.driver?.name

            if (model?.data?.start_date.isNullOrEmpty()) {
                txtDate.text = AppUtils.changeDateFormat(
                    model?.data?.scheduleDate,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM, hh:mm a"
                )
            } else {
                txtDate.text = AppUtils.changeDateFormat(
                    model?.data?.start_date,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM, hh:mm a"
                )

            }

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
                txtRewardsPoint.visibility = View.GONE
                txtRewardsPoint.text = getString(R.string.str_reward_points_earned_for_this_ride) + " " + model?.data?.rewards
            }else{
                txtRewardsPoint.visibility = View.GONE
            }

            if (model?.data?.totalunPaid != null && model?.data?.totalunPaid!! > 0){
                txtBalanceAmount.text = ProjectUtilities.getAmountInFormat(model?.data?.totalunPaid)
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
                    txtBalanceAmountLabel.text = getString(R.string.txt_balance_fare_not_paid)
                    rlBalanceAmount.visibility = View.VISIBLE
                    btnPayNow.visibility = View.VISIBLE
                    btnRateRide.visibility = View.GONE
                    btnRegisterDispute.visibility = View.GONE
                    txtBalanceAmountLabel.setTextColor(getColor(R.color.colorRed))
                    txtBalanceAmount.setTextColor(getColor(R.color.colorRed))
                }

            }else{
                txtBalanceAmountLabel.text = getString(R.string.txt_balance_fare_paid)
                txtBalanceAmountLabel.setTextColor(getColor(R.color.colorGreen))
                txtBalanceAmount.setTextColor(getColor(R.color.colorGreen))
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

            if (model?.data?.waitings?.isNullOrEmpty()!!){
                imgWaitInfo.visibility = View.GONE
            }else{
                imgWaitInfo.visibility = View.VISIBLE
            }

            if (model?.data?.estimatedTrackRide?.tolls != null && model?.data?.estimatedTrackRide?.tolls?.size!! > 0) {
                imgTollInfo.visibility = View.VISIBLE
            }else{
                imgTollInfo.visibility = View.GONE
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

    override fun getRazorPayIdSuccess(model: RazorPayModel?) {
        startPayment(model)
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

    private fun startPayment(razorPayModel: RazorPayModel?) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        val activity: Activity = this
        val checkout = Checkout()
        checkout.setKeyID(razorPayModel?.data?.razorpay_key)

        this.razorPayModel = razorPayModel
       /* var unionRazorpayKey = model?.data?.driver?.razorpay_key
        if (AppUtils.getValueOfKeyFromGeneralSettings(context, Constants.IS_RAZORPAY_MERCHANT_ADMIN) == "false" && !unionRazorpayKey.isNullOrEmpty()){
            checkout.setKeyID(unionRazorpayKey)
        }else{
            checkout.setKeyID(AppUtils.getValueOfKeyFromGeneralSettings(context, Constants.RAZORPAY_KEY))
        }*/
        try {
            val options = JSONObject()
            options.put("order_id", razorPayModel?.data?.order_id)
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
            iRidesDetailPresenter?.updatePaymentStatus(token, rideID, "Online", model?.data?.totalunPaid.toString(), Constants.PAYMENT_PAID, "Razorpay", razorpayPaymentID, paymentData?.orderId, paymentData?.paymentId, razorPayModel?.data?.razorpay_key, razorPayModel?.data?.razorpay_secret_key)
            //    Toast.makeText(this, "Payment Success: " + paymentData, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Exception in onPaymentSuccess: $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        setup()
    }


    /**
     * BROADCAST RECEIVER FOR UNREAD MESSAGE INDICATION
     */
    private fun setup() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mCountReceiver,
            IntentFilter("payment_status_change")
        )
    }

    private val mCountReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == "payment_status_change") {
              iRidesDetailPresenter?.getRideDetail(token, rideID)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCountReceiver)
    }

    override fun onBackPressed() {
        if (SessionManager.getInstance(context).isSplashDisplayed) {
            super.onBackPressed()
        } else {
            //It means its directly came here from notification click
            ProjectUtilities.restartWithSplash(context)
        }
    }
}
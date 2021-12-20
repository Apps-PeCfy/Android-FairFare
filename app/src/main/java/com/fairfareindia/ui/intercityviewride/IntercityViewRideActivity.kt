package com.fairfareindia.ui.intercityviewride

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityViewRideBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel
import com.fairfareindia.ui.intercitytrackpickup.TrackPickUpActivity
import com.fairfareindia.ui.viewride.ViewRideTollsPopUp
import com.fairfareindia.utils.*
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class IntercityViewRideActivity : AppCompatActivity(), IIntercityViewRideView,
    PaymentResultWithDataListener {
    lateinit var binding: ActivityIntercityViewRideBinding
    private var context: Context = this

    private var sourceAddress: String? = null
    private var destinationAddress: String? = null
    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null
    private var estTimeInSeconds: String? = null
    private var scheduleType: String? = null

    private lateinit var info: InterCityCompareRideModel
    private var vehicleModel: InterCityCompareRideModel.VehiclesItem? = null
    private var model: ViewRideModel? = null

    private var token: String? = null
    private var preferencesManager: PreferencesManager? = null
    private var iInterCityViewRidePresenter: IInterCityViewRidePresenter? = null

    var eventInfoDialog: Dialog? = null
    var viewRideTollsPopUp: ViewRideTollsPopUp? = null
    var paymentDialog: PaymentDialog? = null

    var sharedpreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntercityViewRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        PreferencesManager.initializeInstance(context)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        iInterCityViewRidePresenter = InterCityViewRideImplementer(this)

        info = intent.getSerializableExtra("MyPOJOClass") as InterCityCompareRideModel
        vehicleModel = Gson().fromJson(
            intent.getStringExtra("vehicle_model"),
            InterCityCompareRideModel.VehiclesItem::class.java
        )
        sourceAddress = intent.getStringExtra("SourceAddress")
        destinationAddress = intent.getStringExtra("DestinationAddress")
        sourceLat = intent.getStringExtra("SourceLat")
        sourceLong = intent.getStringExtra("SourceLong")
        destinationLat = intent.getStringExtra("DestinationLat")
        destinationLong = intent.getStringExtra("DestinationLong")
        estTimeInSeconds = intent.getStringExtra("time_in_seconds")
        scheduleType = intent.getStringExtra("schedule_type")

        iInterCityViewRidePresenter?.getViewRideDetails(
            token,
            info.permitType,
            vehicleModel?.id,
            info.distance,
            info.luggage,
            info.originPlaceId,
            info.destinationPlaceId,
            sourceLat,
            sourceLong,
            destinationLat,
            destinationLong
        )

        setListeners()
    }

    private fun setData() {
        binding.apply {
            txtPickUpLocation.text = sourceAddress
            txtDropOffLocation.text = destinationAddress

            txtVehicleName.text = model?.ride?.name
            txtPerson.text = model?.ride?.vehicle?.noOfSeater.toString()


            txtDate.text = AppUtils.changeDateFormat(
                info.scheduleDatetime,
                "yyyy-MM-dd HH:mm:ss",
                "EEE, MMM dd yyyy, hh:mm a"
            )
            txtDistanceTime.text = info.distance + " KM, " + info.travelTime

            tvLuggageCharges.text = "₹ " + model?.ride?.chargesLuggage

            txtBaseFare.text = "₹ " + model?.ride?.baseFare
            txtTollCharges.text = "₹ " + model?.ride?.tollCharges


            if (model?.ride?.actualDistance!! > model?.ride?.baseDistance!!) {
                var extraDistance =
                    (model?.ride?.actualDistance!! - model?.ride?.baseDistance!!).toInt()
                txtChargesForAdditionalKmLabel.text =
                    getString(R.string.str_charges_for_additional) + " $extraDistance " + model?.ride?.distanceType
            } else {
                txtChargesForAdditionalKmLabel.text = getString(R.string.str_charges_for_additional)

            }
            txtBaseFareLabel.text =
                getString(R.string.str_base_fare) + "( ${model?.ride?.baseDistance!!.toInt()} ${model?.ride?.distanceType}) ${info.wayFlag}"


            tvChargesForAdditionalKm.text = "₹ " + model?.ride?.additionalDistCharges

            tvSurCharges.text = "₹ " + model?.ride?.surcharges

            tvConvenienceFees.text = "₹ " + model?.ride?.convenienceFees
            txtTotalPayable.text = "₹ " + model?.ride?.totalPayableCharges
            txtAdditionalCharges.text = "₹ " + model?.ride?.totalAdditionalCharges

            if(model?.ride?.rules.isNullOrEmpty()){
                txtRulesLabel.visibility = View.GONE
                txtRules.visibility = View.GONE
            }else{
                txtRules.text = model?.ride?.rules
                txtRulesLabel.visibility = View.VISIBLE
                txtRules.visibility = View.VISIBLE
            }


            Glide.with(context)
                .load(vehicleModel?.vehicle?.image)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                )
                .into(imgVehical)

            if (info.permitType == Constants.TYPE_LOCAL){
                llPaymentMethod.visibility = View.GONE
            }else{
                llPaymentMethod.visibility = View.VISIBLE
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            ivViewTollInfo.setOnClickListener {
                showTollInfoDialog()
            }

            rlAdditional.setOnClickListener {
                if (llAdditionalCharges.visibility == View.VISIBLE) {
                    llAdditionalCharges.visibility = View.GONE
                } else {
                    llAdditionalCharges.visibility = View.VISIBLE
                }

            }

            btnBookRide.setOnClickListener {
                if (info.permitType == Constants.TYPE_INTERCITY){
                    if (!rdBtnOnline.isChecked && !rdBtnOffline.isChecked){
                        Toast.makeText(context, getString(R.string.err_select_payment_method), Toast.LENGTH_SHORT).show()
                    }else if (rdBtnOnline.isChecked){
                        var message =
                            getString(R.string.msg_payment_dialog_one) + model?.ride?.totalPayableCharges + getString(R.string.msg_payment_dialog_two)
                        openPaymentDialog(getString(R.string.btn_pay_now), message, "")
                    }else if (rdBtnOffline.isChecked){
                        callBookRideAPI()
                    }

                }else{
                    callLocalBookRideAPI()
                }

            }


        }
    }

    private fun callLocalBookRideAPI() {
        iInterCityViewRidePresenter?.localBookingRequest(
            token,
            Constants.TYPE_LOCAL,
            model?.ride?.city_id,
            "",
            sourceAddress,
            destinationAddress,
            sourceLat,
            sourceLong,
            destinationLat,
            destinationLong,
            info.scheduleDatetime,
            info.wayFlag,
            model?.ride?.id,
            scheduleType,
            info.luggage,
            model?.ride?.chargesLuggage.toString(),
            model?.ride?.actualDistance.toString(),
            info.travelTime,
            estTimeInSeconds,
            model?.ride?.totalPayableCharges.toString(),
            "",
            "Cash",
            Constants.PAYMENT_UNPAID,
            "Offline"
        )
    }

    private fun callBookRideAPI() {
        iInterCityViewRidePresenter?.bookingRequest(
            token,
            Constants.TYPE_INTERCITY,
            info.fromCityId,
            info.toCityId,
            sourceAddress,
            destinationAddress,
            sourceLat,
            sourceLong,
            destinationLat,
            destinationLong,
            info.scheduleDatetime,
            info.wayFlag,
            model?.ride?.id,
            scheduleType,
            info.luggage,
            model?.ride?.chargesLuggage.toString(),
            model?.ride?.actualDistance.toString(),
            info.travelTime,
            estTimeInSeconds,
            model?.ride?.totalPayableCharges.toString(),
            "",
            "Cash",
            Constants.PAYMENT_UNPAID,
            "Offline"
        )
    }

    private fun openPaymentDialog(btnName: String, message: String, title: String) {
        paymentDialog =
            PaymentDialog(context, btnName, message, title, object : PaymentDialog.PaymentDialogInterface {
                override fun onButtonClick() {
                    paymentDialog?.dismiss()
                    if (btnName == getString(R.string.btn_pay_now)) {
                        startPayment()
                    } else {
                        sharedpreferences!!.edit().clear().commit()
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                      //  startTrackPickUP()
                    }
                }

            })

        if (btnName != getString(R.string.btn_pay_now)) {
            paymentDialog?.setCancelable(false)
        }

        paymentDialog?.show()
    }

    private fun startTrackPickUP() {
        val intent = Intent(applicationContext, TrackPickUpActivity::class.java)
        intent.putExtra("SourceAddress", sourceAddress)
        intent.putExtra("DestinationAddress", destinationAddress)
        intent.putExtra("SourceLat", sourceLat)
        intent.putExtra("SourceLong", sourceLong)
        intent.putExtra("DestinationLat", destinationLat)
        intent.putExtra("DestinationLong", destinationLong)

        startActivity(intent)
    }

    private fun showTollInfoDialog() {
        /*  if(vehicleModel?.tolls?.size!! >0) {
              eventInfoDialog = Dialog(context, R.style.dialog_style)

              eventInfoDialog?.setCancelable(true)
              val inflater1 =
                  this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
              val view12: View = inflater1.inflate(R.layout.toll_layout, null)
              eventInfoDialog?.setContentView(view12)

              var recyclerView: RecyclerView = view12.findViewById(R.id.rvEventInfo)

              recyclerView.layoutManager =
                  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
              viewRideTollsPopUp = ViewRideTollsPopUp( vehicleModel?.tolls!!)
              recyclerView.adapter = viewRideTollsPopUp

              eventInfoDialog!!.show()
          }*/
    }

    /**
     * API CALLING
     */

    override fun bookingRequestSuccess(model: BookingRequestModel?) {
        var title = ""
        if (binding.rdBtnOffline.isChecked){
            title = getString(R.string.title_booked_successfully)
        }else{
            title = getString(R.string.title_your_payment_successfull)
        }
        var message1 =
            getString(R.string.dialog_booking_request_msg_one) +  " ${binding.txtDate.text}. " + getString(R.string.dialog_booking_request_msg_two)
        openPaymentDialog(getString(R.string.btn_ok), message1, title)
      //  Toast.makeText(context, "Booking ID :- ${model?.data?.id}", Toast.LENGTH_LONG).show()
    }

    override fun localBookingRequestSuccess(model: BookingRequestModel?) {
        var title = getString(R.string.title_booked_successfully)
        var message1 =
            getString(R.string.dialog_booking_request_msg_one) +  " ${binding.txtDate.text}. " + getString(R.string.dialog_booking_request_msg_two)
        openPaymentDialog(getString(R.string.btn_ok), message1, title)
    }

    override fun getViewRideDetails(model: ViewRideModel?) {
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
            options.put("amount", (model?.ride?.totalPayableCharges?.times(100)).toString())
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
            iInterCityViewRidePresenter?.bookingRequest(
                token,
                Constants.TYPE_INTERCITY,
                info.fromCityId,
               info.toCityId,
                sourceAddress,
                destinationAddress,
                sourceLat,
                sourceLong,
                destinationLat,
                destinationLong,
                info.scheduleDatetime,
                info.wayFlag,
                model?.ride?.id,
                scheduleType,
                info.luggage,
                model?.ride?.chargesLuggage.toString(),
                model?.ride?.actualDistance.toString(),
                info.travelTime,
                estTimeInSeconds,
                model?.ride?.totalPayableCharges.toString(),
                razorpayPaymentID,
                "Online",
                Constants.PAYMENT_PAID,
                "Razorpay"

            )

        //    Toast.makeText(this, "Payment Success: " + paymentData, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Exception in onPaymentSuccess: $e", Toast.LENGTH_SHORT).show()
        }
    }
}
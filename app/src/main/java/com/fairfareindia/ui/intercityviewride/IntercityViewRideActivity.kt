package com.fairfareindia.ui.intercityviewride

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityViewRideBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel
import com.fairfareindia.ui.intercitytrackpickup.TrackPickUpActivity
import com.fairfareindia.ui.viewride.ViewRideTollsPopUp
import com.fairfareindia.utils.AppUtils
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.gson.Gson

class IntercityViewRideActivity : AppCompatActivity(), IIntercityViewRideView {
    lateinit var binding: ActivityIntercityViewRideBinding
    private var context: Context = this

    private var sourceAddress: String? = null
    private var destinationAddress: String? = null
    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null

    private lateinit var info: InterCityCompareRideModel
    private  var vehicleModel : InterCityCompareRideModel.VehiclesItem ? = null

    private var token: String? = null
    private var preferencesManager: PreferencesManager? = null
    private var iInterCityViewRidePresenter: IInterCityViewRidePresenter? = null

    var eventInfoDialog: Dialog? = null
    var viewRideTollsPopUp: ViewRideTollsPopUp? = null
    var paymentDialog: PaymentDialog? = null

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

        iInterCityViewRidePresenter = InterCityViewRideImplementer(this)

        info = intent.getSerializableExtra("MyPOJOClass") as InterCityCompareRideModel
        vehicleModel = Gson().fromJson(intent.getStringExtra("vehicle_model"), InterCityCompareRideModel.VehiclesItem::class.java)
        sourceAddress = intent.getStringExtra("SourceAddress")
        destinationAddress = intent.getStringExtra("DestinationAddress")
        sourceLat = intent.getStringExtra("SourceLat")
        sourceLong = intent.getStringExtra("SourceLong")
        destinationLat = intent.getStringExtra("DestinationLat")
        destinationLong = intent.getStringExtra("DestinationLong")

        setListeners()
        setData()
    }

    private fun setData() {
        binding.apply {
            txtPickUpLocation.text = sourceAddress
            txtDropOffLocation.text = destinationAddress

            txtVehicleName.text = vehicleModel?.name
            txtPerson.text = vehicleModel?.vehicle?.noOfSeater.toString()


            txtDate.text = AppUtils.changeDateFormat(info.scheduleDatetime,"yyyy-MM-dd HH:mm:ss", "EEE, MMM dd yyyy, hh:mm a")
            txtDistanceTime.text = info.distance + " KM, " + info.travelTime

            tvLuggageCharges.text = "₹ " + vehicleModel?.chargesPerLuggage

            txtBaseFare.text = "₹ " + vehicleModel?.baseFare


           /* tvTollCharge.text = "₹ " + vehicleModel?.tollCharges


            tvNightCharges.text = "₹ " + vehicleModel?.nightCharge

            tvSurCharges.text = "₹ " + vehicleModel?.surCharge



            txtAdditionalCharges.text = "₹ " + vehicleModel?.additionalCharges
            txtTotalPayable.text = "₹ " + vehicleModel?.total*/

            Glide.with(context)
                .load(vehicleModel?.vehicle?.image)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                )
                .into(imgVehical)
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            ivViewTollInfo.setOnClickListener {
                showTollInfoDialog()
            }

            rlAdditional.setOnClickListener {
                if (llAdditionalCharges.visibility == View.VISIBLE){
                    llAdditionalCharges.visibility = View.GONE
                }else{
                    llAdditionalCharges.visibility = View.VISIBLE
                }

            }

            btnBookRide.setOnClickListener {
                var message = "Kindly pay the amount ₹${vehicleModel?.baseFare} for the booking and confirm."
                openPaymentDialog(getString(R.string.btn_pay_now), message)
            }


        }
    }

    private fun openPaymentDialog(btnName: String, message: String) {
        paymentDialog = PaymentDialog(context, btnName, message, object : PaymentDialog.PaymentDialogInterface{
            override fun onButtonClick() {
                paymentDialog?.dismiss()
                if (btnName == getString(R.string.btn_pay_now)){
                   iInterCityViewRidePresenter?.bookingRequest(token, "4", "Intercity", sourceAddress, destinationAddress, sourceLat, sourceLong, destinationLat, destinationLong, info.scheduleDatetime, "Oneway",  "1", "1", "Pending")
                }else{
                    startTrackPickUP()
                }
            }

        })

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
        var  message1 = "Your ride is confirmed on ${binding.txtDate.text}. Driver details will be shared 15 minutes before the ride."
        openPaymentDialog(getString(R.string.btn_ok), message1)
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
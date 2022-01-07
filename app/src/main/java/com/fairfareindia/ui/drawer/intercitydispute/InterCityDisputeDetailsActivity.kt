package com.fairfareindia.ui.drawer.intercitydispute

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityInterCityDisputeDetailsBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.intercityrides.ridedetails.WaitingInfoDialog
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.AppUtils
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities

class InterCityDisputeDetailsActivity : AppCompatActivity(), IDisputeDetailView {
    lateinit var binding: ActivityInterCityDisputeDetailsBinding
    private var context: Context = this
    private var token: String? = null
    private var disputeID: String? = null
    private var model: DisputeDetailModel?= null
    private var preferencesManager: PreferencesManager? = null
    private var iDisputeDetailPresenter: IDisputeDetailPresenter? = null
    private var waitingInfoDialog : WaitingInfoDialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterCityDisputeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        disputeID = intent.getStringExtra("dispute_id")

        PreferencesManager.initializeInstance(context)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        iDisputeDetailPresenter = DisputeDetailImplementer(this)

        iDisputeDetailPresenter?.getDisputeDetail(token, disputeID)

        setListeners()
    }

    private fun setData() {
        binding.apply {

            if (model?.dispute?.type == "Complaint"){
                btnFileComplaint.visibility = View.GONE
                toolbarDisputeDetails.title = getString(R.string.title_complaint_detail)
                txtDisputeId.text =  getString(R.string.str_complaint_id) +" : " + model?.dispute?.disputeNo
            }else{
                btnFileComplaint.visibility = View.VISIBLE
                toolbarDisputeDetails.title = getString(R.string.title_dispute_detail)
                txtDisputeId.text = getString(R.string.str_dispute_id)+ " : " + model?.dispute?.disputeNo
            }

            var dReason: String? = ""
            for (i in model?.dispute?.reasons?.indices!!) {
                if (dReason?.isEmpty()!!) {
                    dReason = model?.dispute?.reasons?.get(i)?.reason

                } else {
                    dReason =
                        dReason + "\n" + model?.dispute?.reasons?.get(i)?.reason

                }
            }


            txtDisputeReason.text = dReason



            txtActualFare.text = getString(R.string.str_total_fare_charged)  + " : " + ProjectUtilities.getAmountInFormat(model?.dispute?.actualMeterCharges?.toDouble())
            txtStartMeterReading.text = getString(R.string.str_start_trip_meter) + " : " + model?.dispute?.startMeterReading
            txtEndMeterReading.text = getString(R.string.hint_end_trip_meter) + " : " + model?.dispute?.endMeterReading

            if (model?.dispute?.comment?.isNotEmpty()!!) {
                txtComments.visibility = View.VISIBLE
                txtComments.text = model?.dispute?.comment
            } else {
                txtComments.visibility = View.GONE
            }



            txtEstLuggageCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.luggageCharges?.toDouble())
            txtEstDistance.text = ProjectUtilities.getDistanceInFormat(model?.dispute?.ride?.estimatedTrackRide?.baseDistance)
            txtEstAddDistance.text = ProjectUtilities.getDistanceInFormat(model?.dispute?.ride?.estimatedTrackRide?.additionalDistance)
            txtEstRideTime.text = model?.dispute?.ride?.estimatedTrackRide?.totalTime
            txtEstWaitTime.text =  ProjectUtilities.timeInMinutesConvertingToString(context, model?.dispute?.ride?.estimatedTrackRide?.waitingTime!!)
            txtEstWaitCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.waitingCharges?.toDouble())
            txtEstBaseFare.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.basicFare?.toDouble())
            txtEstTollCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.tollCharges?.toDouble())
            txtEstAddDistanceCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.additionalDistanceCharges?.toDouble())
            txtEstSurcharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.surCharge?.toDouble())
            txtEstConvenience.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.convenienceFees?.toDouble())
            txtEstTotalFare.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.estimatedTrackRide?.totalCharges?.toDouble())



            txtActualLuggageCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.luggageCharges?.toDouble())
            txtActualDistance.text = ProjectUtilities.getDistanceInFormat(model?.dispute?.ride?.actualTrackRide?.baseDistance)
            txtActualAddDistance.text = ProjectUtilities.getDistanceInFormat(model?.dispute?.ride?.actualTrackRide?.additionalDistance)
            txtActualRideTime.text = model?.dispute?.ride?.actualTrackRide?.totalTime
            txtActualWaitTime.text =  ProjectUtilities.timeInMinutesConvertingToString(context, model?.dispute?.ride?.actualTrackRide?.waitingTime!!)
            txtActualWaitCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.waitingCharges?.toDouble())
            txtActualBaseFare.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.basicFare?.toDouble())
            txtActualTollCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.tollCharges?.toDouble())
            txtActualAddDistanceCharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.additionalDistanceCharges?.toDouble())
            txtActualSurcharges.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.surCharge?.toDouble())
            txtActualConvenience.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.convenienceFees?.toDouble())
            txtActualTotalFare.text = ProjectUtilities.getAmountInFormat(model?.dispute?.ride?.actualTrackRide?.totalCharges?.toDouble())


            //Driver and Vehical
            txtVehicleName.text =  model?.dispute?.ride?.vehicleName
            txtVehicleNumber.text = model?.dispute?.ride?.vehicleNo
            txtDriverName.text = model?.dispute?.ride?.driver?.name
            txtDate.text = AppUtils.changeDateFormat(model?.dispute?.ride?.dateTime, "yyyy-MM-dd HH:mm:ss", "dd MMM, hh:mm a")

            if (model?.dispute?.ride?.luggageQuantity == "0") {
                txtLuggage.text = getString(R.string.str_no_bags)
            } else if (model?.dispute?.ride?.luggageQuantity == "1") {
                txtLuggage.text = model?.dispute?.ride?.luggageQuantity + " " + getString(R.string.str_bag)
            } else {
                txtLuggage.text = model?.dispute?.ride?.luggageQuantity + " " + getString(R.string.str_bags)
            }



            Glide.with(context)
                .load(model?.dispute?.ride?.vehicleImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                ).into(imgVehicle)
        }
    }


    private fun setListeners() {
        binding.apply {
            btnFileComplaint.setOnClickListener {
                showConfirmationDialog()
            }

            toolbarDisputeDetails.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun showConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(context, R.style.alertDialog)
        alertDialog.setTitle(getString(R.string.str_fair_fare_india))

        alertDialog.setMessage(getString(R.string.msg_dialog_file_complaint))
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(getString(R.string.str_yes)) { dialog, which ->
            dialog.dismiss()
            iDisputeDetailPresenter?.fileComplaint(token, disputeID)
        }
        alertDialog.setNegativeButton(getString(R.string.str_no)) { dialog, which -> dialog.dismiss() }
        alertDialog.show()
    }

    /**
     * API SUCCESS HANDLE
     */

    override fun getDisputeDetailSuccess(model: DisputeDetailModel?) {
        this.model = model
        setData()
    }

    override fun fileComplaintSuccess(model: DeleteDisputResponsePOJO?) {
        val intent = Intent(context, HomeActivity::class.java)
        intent.action = "filecomplaintSuccess"
        startActivity(intent)
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
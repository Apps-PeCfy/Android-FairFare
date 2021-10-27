package com.fairfareindia.ui.intercityviewride

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityViewRideBinding
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.viewride.ViewRideActivity
import com.fairfareindia.ui.viewride.ViewRideTollsPopUp
import com.fairfareindia.utils.AppUtils
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.gson.Gson

class IntercityViewRideActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntercityViewRideBinding
    private var context: Context = this

    private var sourceAddress: String? = null
    private var destinationAddress: String? = null
    private lateinit var info: CompareRideResponsePOJO
    private  var vehicleModel : CompareRideResponsePOJO.VehiclesItem ? = null

    private var token: String? = null
    private var preferencesManager: PreferencesManager? = null

    var eventInfoDialog: Dialog? = null
    var viewRideTollsPopUp: ViewRideTollsPopUp? = null

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

        info = intent.getSerializableExtra("MyPOJOClass") as CompareRideResponsePOJO
        sourceAddress = intent.getStringExtra("SourceAddress")
        destinationAddress = intent.getStringExtra("DestinationAddress")
        vehicleModel = Gson().fromJson(intent.getStringExtra("vehicle_model"), CompareRideResponsePOJO.VehiclesItem::class.java)

        setListeners()
        setData()
    }

    private fun setData() {
        binding.apply {
            txtPickUpLocation.text = sourceAddress
            txtDropOffLocation.text = destinationAddress

            txtVehicleName.text = vehicleModel?.vehicleName
            txtPerson.text = vehicleModel?.noOfSeater.toString()


            txtDate.text = AppUtils.changeDateFormat(info.scheduleDatetime,"yyyy-MM-dd HH:mm:ss", "EEE, MMM dd yyyy, hh:mm a")
            txtDistanceTime.text = info.distance + " KM, " + info.travelTime


            tvTollCharge.text = "₹ " + vehicleModel?.tollCharges

            tvLuggageCharges.text = "₹ " + vehicleModel?.luggageCharge
            tvNightCharges.text = "₹ " + vehicleModel?.nightCharge

            tvSurCharges.text = "₹ " + vehicleModel?.surCharge

            txtBaseFare.text = "₹ " + vehicleModel?.subTotal

            txtAdditionalCharges.text = "₹ " + vehicleModel?.additionalCharges
            txtTotalPayable.text = "₹ " + vehicleModel?.total

            Glide.with(context)
                .load(vehicleModel?.vehicleImageUrl)
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


        }
    }

    private fun showTollInfoDialog() {
        if(vehicleModel?.tolls?.size!! >0) {
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
        }
    }
}
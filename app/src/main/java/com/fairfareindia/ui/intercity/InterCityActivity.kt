package com.fairfareindia.ui.intercity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityInterCityBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.CompareRideActivity
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.home.PickUpDropActivity
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.home.pojo.PickUpLocationModel
import com.fairfareindia.utils.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InterCityActivity : AppCompatActivity(), IIntercityView {
    lateinit var binding: ActivityInterCityBinding
    private var context: Context = this

    private var toCityName: String? = null
    private var toCityID: String? = null
    private var fromCityName: String? = null
    private var fromCityID: String? = null

    private var cityList: ArrayList<GetAllowCityResponse.CitiesItem> = ArrayList()

    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null
    var estTime: String? = null
    var estDistance: String? = null
    var citySelectionDialog: CitySelectionDialog? = null

    var preferencesManager: PreferencesManager? = null

    private var currentLatitude : Double = 0.0
    private var currentLongitude : Double = 0.0

    private var iInterCityPresenter: IInterCityPresenter? = null
    var token: String? = null
    var calendar: Calendar? = null

    var timeSpinner : Array<String> ?= null
    var luggageSpinner = arrayOf<String?>(
        "Luggage", "1 Luggage", "2 Luggage", "3 Luggage", "4 Luggage", "5 Luggage"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
       // setSupportActionBar(binding.toolbarHome)

        currentLatitude = intent.getDoubleExtra("current_latitude", 0.0)
        currentLongitude = intent.getDoubleExtra("current_longitude", 0.0)

        PreferencesManager.initializeInstance(context)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)



        iInterCityPresenter = InterCityImplementer(this)
        iInterCityPresenter?.getCity(token, "", "")

        EventBus.getDefault().register(this)
        setSpinners()
        setListeners()
        setInitialDate()

    }

    private fun setInitialDate() {
        calendar = Calendar.getInstance()
        if (binding.spinnerTime.selectedItem.toString().equals(getString(R.string.str_now), ignoreCase = true)) {
            calendar?.add(Calendar.MINUTE, 50)
            binding.txtRideScheduled.text = AppUtils.dateToRequiredFormat(calendar?.time, "dd MMM hh:mm a")
        }
    }

    private fun setSpinners() {
        timeSpinner = arrayOf<String>(context.resources.getString(R.string.str_now), context.resources.getString(R.string.str_later))
        val NowLater: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, timeSpinner!!)
        NowLater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTime.adapter = NowLater



        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, luggageSpinner)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLuggage.adapter = spinnerLuggage

    }

    private fun setListeners() {
        binding.apply {
            toolbarHome.setNavigationOnClickListener { onBackPressed() }

            btnCompareRides.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(context)) {
                    if (isValid()){
                      /*  iInterCityPresenter?.getCompareRideData(
                            token,
                            replacedistance,
                            cityID,
                            sourcePlaceID,
                            DestinationPlaceID,
                            replacebags,
                            airportYesOrNO,
                            formaredDate,
                            CurrentPlaceID!!, legDuration
                        )*/
                    }
                } else {
                    ProjectUtilities.showToast(context, getString(R.string.internet_error))
                }
            }

            txtPickUpLocation.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(context)) {

                    val intent = Intent(applicationContext, PickUpDropActivity::class.java)
                    intent.putExtra("Toolbar_Title", "Pick-Up")
                    intent.putExtra("currentLatitude", currentLatitude)
                    intent.putExtra("currentLongitude", currentLongitude)
                    startActivity(intent)
                } else {
                    ProjectUtilities.showToast(context, getString(R.string.internet_error))
                }
            }

            txtDropUpLocation.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(context)) {
                    val intent = Intent(applicationContext, PickUpDropActivity::class.java)
                    intent.putExtra("Toolbar_Title", "Drop-off")
                    intent.putExtra("currentLatitude", currentLatitude)
                    intent.putExtra("currentLongitude", currentLongitude)
                    startActivity(intent)
                } else {
                    ProjectUtilities.showToast(context, getString(R.string.internet_error))
                }
            }

            spinnerLuggage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (timeSpinner?.get(position)?.equals(getString(R.string.str_now))!!){
                        txtRideBook.text = getString(R.string.str_book_ride_on)
                        txtRideBook.text = "Ride will be serviced 45 minutes after the payment is done"
                        txtRideScheduled.visibility = View.GONE

                    }else{
                       txtRideBook.text = getString(R.string.str_ride_scheduled_on)
                        txtRideScheduled.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            txtRideScheduled.setOnClickListener {
                showDatePickerDialog(txtRideScheduled)
            }

            edtFromCity.setOnClickListener {
                openCitySelectionDialog("Select From City")
            }

            edtToCity.setOnClickListener {
                openCitySelectionDialog("Select To City")
            }
        }
    }

    private fun openCitySelectionDialog(title: String) {
        citySelectionDialog =  CitySelectionDialog(context, title, cityList, object : CitySelectionDialog.SelectionDialogInterface{
            override fun onItemSelected(model: GetAllowCityResponse.CitiesItem?) {
                citySelectionDialog?.dismiss()
                if (title == "Select From City"){
                    binding.edtFromCity.setText(model?.name)
                    fromCityID = model?.id.toString()
                }else{
                    binding.edtToCity.setText(model?.name)
                    toCityID = model?.id.toString()
                }
            }

        })

        citySelectionDialog?.show()
    }

    private fun isValid(): Boolean {
        binding.apply {
            if (!rdOneWay.isChecked && !rdRoundTrip.isChecked){
                Toast.makeText(context, "Please select journey type", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true

    }

    private fun showDatePickerDialog(txtDate: TextView) {
        var mYear = 0
        var mMonth = 0
        var mDay = 0

        // Get Current Date

        mYear = calendar?.get(Calendar.YEAR)!!
        mMonth = calendar?.get(Calendar.MONTH)!!
        mDay = calendar?.get(Calendar.DAY_OF_MONTH)!!
        val datePickerDialog = DatePickerDialog(
            context,
            { view, year, monthOfYear, dayOfMonth ->

                var strMonth = ""
                val iMonth = monthOfYear + 1
                strMonth = if (iMonth < 10) {
                    "0$iMonth"
                } else {
                    "" + iMonth
                }

                var strDay = ""
                strDay = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    "" + dayOfMonth
                }
                var dateString = "$year-$strMonth-$strDay"
                showTimePickerDialog(dateString, txtDate)
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, 1)
        datePickerDialog.datePicker.maxDate = cal.timeInMillis
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(dateString: String, txtDate: TextView) {
        var hour = calendar?.get(Calendar.HOUR_OF_DAY)
        var minute = calendar?.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                var dateTimeString = dateString + " $hourOfDay:$minute"
                if (validateDate(dateTimeString)){
                    txtDate.text = AppUtils.changeDateFormat(dateTimeString, "yyyy-MM-dd HH:mm", "dd MMM yyyy hh:mm a")
                }else{
                    Toast.makeText(context, "Please select valid date and time", Toast.LENGTH_SHORT).show()
                }

            }

        },hour!!, minute!!, DateFormat.is24HourFormat(this))

        timePickerDialog.show()
    }

    private fun validateDate(selectedDate: String): Boolean {
        var returnVal = false
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val selectedDateTime = dateFormat.parse(selectedDate)
            val todayMinDateTimeDateFormat = SimpleDateFormat("dd MM yyyy HH:mm:ss")
            val todayMinDateTime = todayMinDateTimeDateFormat.parse(minTimeToShedule())

            returnVal = !selectedDateTime.before(todayMinDateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return returnVal
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPickUpLocationModel(model: PickUpLocationModel) {
        if (model != null) {
            if (model.isSource!!) {
                sourceLat = model.latitude.toString()
                sourceLong = model.longitude.toString()
                binding.txtPickUpLocation.text = model.address

            } else {
                destinationLat = model.latitude.toString()
                destinationLong = model.longitude.toString()
                binding.txtDropUpLocation.text = model.address

            }

            if (!sourceLat.isNullOrEmpty() && !destinationLat.isNullOrEmpty()){
                getDistanceAPI()
            }
        }

    }

    private fun getDistanceAPI() {
        val url =
            "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + sourceLat + "," + sourceLong + "&destinations=" + destinationLat + "," + destinationLong + "&key=" + getString(R.string.google_maps_key)

        APIManager.getInstance(context).postAPI(url, null, GoogleDistanceModel::class.java, context, object : APIManager.APIManagerInterface{
            override fun onSuccess(resultObj: Any?) {
                var model: GoogleDistanceModel = resultObj as GoogleDistanceModel
                binding.rlEstimation.visibility = View.VISIBLE
                estDistance = model.rows?.elementAt(0)?.elements?.get(0)?.distance?.text
                estTime = model.rows?.elementAt(0)?.elements?.get(0)?.duration?.text
                binding.txtEstDistance.text = "Est.Distance $estDistance"
                binding.txtEstTime.text = "Est.Time $estTime"
            }

            override fun onError(error: String?) {
            }

        })
    }


    private fun minTimeToShedule(): String {
        val ONE_MINUTE_IN_MILLIS: Long = 60000 //millisecs
        val date = Calendar.getInstance()
        val t = date.timeInMillis
        val afterRequiredMins = Date(t + 45 * ONE_MINUTE_IN_MILLIS)

        val minDateTime = SimpleDateFormat("dd MM yyyy HH:mm:ss").format(afterRequiredMins)
        return minDateTime;

    }


    /**
     * API RESPONSES
     *
     */



    override fun compareRideSuccess(info: CompareRideResponsePOJO?) {
        val intent = Intent(applicationContext, CompareRideActivity::class.java)
        intent.putExtra("SourceLat", sourceLat)
        intent.putExtra("SourceLong", sourceLong)
        intent.putExtra("DestinationLat", destinationLat)
        intent.putExtra("DestinationLong", destinationLong)
        intent.putExtra("Distance", estDistance)
        intent.putExtra("CITY_ID", toCityID)
        intent.putExtra("CITY_NAME", toCityName)
        intent.putExtra("EstTime", estTime)
        intent.putExtra("Liggage", binding.spinnerLuggage.selectedItem.toString())
        intent.putExtra("TimeSpinner", binding.spinnerTime.selectedItem.toString())


        intent.putExtra("Airport", "NO")
        intent.putExtra("SourceAddress", binding.txtPickUpLocation.text.toString())
        intent.putExtra("DestinationAddress", binding.txtDropUpLocation.text.toString())
        intent.putExtra("currentDate", binding.txtRideScheduled.text.toString())
        intent.putExtra("currentFormatedDate", AppUtils.changeDateFormat(binding.txtRideScheduled.text.toString(), "dd MMM yyyy hh:mm a", "yyyy-MM-dd hh:mm:ss"))
     //   intent.putExtra("currentPlaceId", CurrentPlaceID)
        intent.putExtra("MyPOJOClass", info)


        startActivity(intent)
    }

    override fun getCitySuccess(getAllowCityResponse: GetAllowCityResponse?) {
        toCityName = getAllowCityResponse?.getallowCityName()
        cityList = getAllowCityResponse?.cities!!

    }



    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            this@InterCityActivity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()


    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(this@InterCityActivity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {

        Toast.makeText(this@InterCityActivity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
        } catch (ex: Exception) {

        }
    }
}
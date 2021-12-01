package com.fairfareindia.ui.intercity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityInterCityBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.home.PickUpDropActivity
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.ui.home.pojo.PickUpLocationModel
import com.fairfareindia.ui.intercitycompareride.InterCityCompareRideModel
import com.fairfareindia.ui.intercitycompareride.IntercityCompareRideActivity
import com.fairfareindia.utils.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class InterCityActivity : AppCompatActivity(), IIntercityView {
    lateinit var binding: ActivityInterCityBinding
    private var context: Context = this

    private var toCityID: String? = null
    private var fromCityID: String? = null
    private var luggage: String = "0"

    private var fromCityList: ArrayList<GetAllowCityResponse.CitiesItem> = ArrayList()
    private var toCityList: ArrayList<GetAllowCityResponse.CitiesItem> = ArrayList()

    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null
    var estTime: String? = null
    var estTimeInSeconds: String? = null
    var estDistance: String? = null
    var estDistanceInKM: Double? = null
    var citySelectionDialog: CitySelectionDialog? = null

    var preferencesManager: PreferencesManager? = null

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private var iInterCityPresenter: IInterCityPresenter? = null
    var token: String? = null
    var calendar: Calendar? = null

    private var timeSpinner: Array<String>? = null
    private var luggageSpinner: Array<String>? = null
    var wayFlag: String? = null
    private var myLocationManager: MyLocationManager? = MyLocationManager(this)

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
        iInterCityPresenter?.getFromInterCities(token)

        EventBus.getDefault().register(this)
        setSpinners()
        setListeners()
        setInitialDate()

    }

    private fun initLocationUpdates() {
        myLocationManager?.getMyCurrentLocationChange(object :
            MyLocationManager.LocationManagerTrackInterface {
            override fun onMyLocationChange(
                currentLocation: MutableList<Location>?,
                lastLocation: Location?
            ) {
                if (lastLocation != null) {
                    currentLatitude = lastLocation.latitude
                    currentLongitude = lastLocation.longitude
                    binding.txtPickUpLocation.text = getAddressFromLocation()
                    sourceLat = currentLatitude.toString()
                    sourceLong = currentLongitude.toString()
                }
            }

        })
    }

    private fun setInitialDate() {
        if (currentLatitude == null && currentLatitude == 0.0){
            initLocationUpdates()
        }else{
            binding.txtPickUpLocation.text = getAddressFromLocation()
            sourceLat = currentLatitude.toString()
            sourceLong = currentLongitude.toString()
        }
        calendar = Calendar.getInstance()
        if (binding.spinnerTime.selectedItem.toString()
                .equals(getString(R.string.str_now), ignoreCase = true)
        ) {
            calendar?.add(Calendar.MINUTE, 50)
            binding.txtRideScheduled.text =
                AppUtils.dateToRequiredFormat(calendar?.time, "dd MMM yyyy hh:mm a")
        }
    }

    private fun setSpinners() {
        timeSpinner = arrayOf<String>(
            context.resources.getString(R.string.str_now),
            context.resources.getString(R.string.str_later)
        )
        luggageSpinner = arrayOf<String>(
            getString(R.string.str_luggage),
            getString(R.string.str_luggage_1),
            getString(R.string.str_luggage_2),
            getString(R.string.str_luggage_3),
            getString(R.string.str_luggage_4),
            getString(R.string.str_luggage_5)
        )

        val NowLater: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, timeSpinner!!)
        NowLater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTime.adapter = NowLater


        val spinnerLuggage: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.simple_spinner, luggageSpinner!!)
        spinnerLuggage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLuggage.adapter = spinnerLuggage

    }

    private fun setListeners() {
        binding.apply {
            toolbarHome.setNavigationOnClickListener { onBackPressed() }

            btnCompareRides.setOnClickListener {
                if (ProjectUtilities.checkInternetAvailable(context)) {
                    if (isValid()) {
                        iInterCityPresenter?.getCompareRideData(
                            token,
                            estDistanceInKM?.roundToInt().toString(),
                            estTime.toString(),
                            Constants.TYPE_INTERCITY,
                            fromCityID,
                            toCityID,
                            AppUtils.getPlaceID(context, sourceLat, sourceLong),
                            AppUtils.getPlaceID(context, destinationLat, destinationLong),
                            luggage,
                            wayFlag,
                            AppUtils.changeDateFormat(
                                txtRideScheduled.text.toString(),
                                "dd MMM yyyy hh:mm a",
                                "yyyy-MM-dd HH:mm:ss"
                            ),
                            sourceLat,
                            sourceLong,
                            destinationLat,
                            destinationLong
                        )
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
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    luggage = position.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (timeSpinner?.get(position)?.equals(getString(R.string.str_now))!!) {
                        txtRideBook.text = getString(R.string.str_book_ride_on)
                        txtRideBook.text =
                            "Ride will be serviced 45 minutes after the payment is done"
                        txtRideScheduled.visibility = View.GONE

                    } else {
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
                if (fromCityList.isNullOrEmpty()) {
                    iInterCityPresenter?.getFromInterCities(token)
                } else {
                    openCitySelectionDialog("Select From City", fromCityList)
                }

            }

            edtToCity.setOnClickListener {
                if (fromCityID.isNullOrEmpty()) {
                    Toast.makeText(context, "Please select From City first.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (toCityList.isNullOrEmpty()) {
                        iInterCityPresenter?.getToInterCities(token, fromCityID)
                    } else {
                        openCitySelectionDialog("Select To City", toCityList)
                    }
                }
            }
        }
    }


    private fun openCitySelectionDialog(
        title: String,
        cityList: ArrayList<GetAllowCityResponse.CitiesItem>
    ) {
        citySelectionDialog = CitySelectionDialog(
            context,
            title,
            cityList,
            object : CitySelectionDialog.SelectionDialogInterface {
                override fun onItemSelected(model: GetAllowCityResponse.CitiesItem?) {
                    citySelectionDialog?.dismiss()
                    if (title == "Select From City") {
                        binding.edtFromCity.setText(model?.name)
                        fromCityID = model?.id.toString()
                        iInterCityPresenter?.getToInterCities(token, fromCityID)
                    } else {
                        binding.edtToCity.setText(model?.name)
                        toCityID = model?.id.toString()
                    }
                }

            })

        citySelectionDialog?.show()
    }

    private fun isValid(): Boolean {
        binding.apply {
            if (!rdOneWay.isChecked && !rdRoundTrip.isChecked) {
                Toast.makeText(context, "Please select journey type", Toast.LENGTH_SHORT).show()
                return false
            } else if (fromCityID.isNullOrEmpty()) {
                Toast.makeText(context, "Please select from city", Toast.LENGTH_SHORT).show()
                return false
            } else if (toCityID.isNullOrEmpty()) {
                Toast.makeText(context, "Please select to city", Toast.LENGTH_SHORT).show()
                return false
            } else if (sourceLat.isNullOrEmpty()) {
                Toast.makeText(context, "Please select pick up location", Toast.LENGTH_SHORT).show()
                return false
            } else if (destinationLat.isNullOrEmpty()) {
                Toast.makeText(context, "Please select drop off location", Toast.LENGTH_SHORT)
                    .show()
                return false
            }else if(fromCityID == toCityID){
                Toast.makeText(context, "The From and To City can’t be same for Intercity travel.", Toast.LENGTH_SHORT).show()
                return false
            }

            if (rdOneWay.isChecked) {
                wayFlag = Constants.ONE_WAY_FLAG
            } else if (rdRoundTrip.isChecked) {
                wayFlag = Constants.BOTH_WAY_FLAG
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
        val timePickerDialog =
            TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    var dateTimeString = dateString + " $hourOfDay:$minute"
                    if (validateDate(dateTimeString)) {
                        txtDate.text = AppUtils.changeDateFormat(
                            dateTimeString,
                            "yyyy-MM-dd HH:mm",
                            "dd MMM yyyy hh:mm a"
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Please select valid date and time",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }, hour!!, minute!!, DateFormat.is24HourFormat(this))

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

            if (!sourceLat.isNullOrEmpty() && !destinationLat.isNullOrEmpty()) {
                getDistanceAPI()
            }
        }

    }

    private fun getDistanceAPI() {
        val url =
            "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + sourceLat + "," + sourceLong + "&destinations=" + destinationLat + "," + destinationLong + "&key=" + getString(
                R.string.google_maps_key
            )

        APIManager.getInstance(context).postAPI(
            url,
            null,
            GoogleDistanceModel::class.java,
            context,
            object : APIManager.APIManagerInterface {
                override fun onSuccess(resultObj: Any?, jsonObject: JSONObject) {
                    var model: GoogleDistanceModel = resultObj as GoogleDistanceModel
                    binding.rlEstimation.visibility = View.VISIBLE
                    estDistance = model.rows?.elementAt(0)?.elements?.get(0)?.distance?.text
                    estDistanceInKM =
                        (model.rows?.elementAt(0)?.elements?.get(0)?.distance?.value)?.div(
                            1000
                        )
                    estTime = model.rows?.elementAt(0)?.elements?.get(0)?.duration?.text
                    estTimeInSeconds = model.rows?.elementAt(0)?.elements?.get(0)?.duration?.value.toString()
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


    override fun compareRideSuccess(info: InterCityCompareRideModel?) {
        val intent = Intent(applicationContext, IntercityCompareRideActivity::class.java)
        intent.putExtra("SourceLat", sourceLat)
        intent.putExtra("SourceLong", sourceLong)
        intent.putExtra("DestinationLat", destinationLat)
        intent.putExtra("DestinationLong", destinationLong)

        intent.putExtra("time_in_seconds", estTimeInSeconds)
        intent.putExtra("schedule_type", binding.spinnerTime.selectedItem.toString())
        intent.putExtra("SourceAddress", binding.txtPickUpLocation.text.toString())
        intent.putExtra("DestinationAddress", binding.txtDropUpLocation.text.toString())
        intent.putExtra("MyPOJOClass", info)


        startActivity(intent)
    }

    override fun getCitySuccess(getAllowCityResponse: GetAllowCityResponse?) {

    }

    override fun getFromInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?) {
        fromCityList = getAllowCityResponse?.cities!!
    }

    override fun getToInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?) {
        toCityList = getAllowCityResponse?.cities!!
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

    private fun getAddressFromLocation(): String? {
        var address: String = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1)

            if (addresses != null && addresses!!.size > 0) {
                val obj = addresses[0]
                address = obj.getAddressLine(0)
                var countryName = obj?.countryName
                if (obj?.countryName != null && obj.countryName.equals(
                        "United States",
                        ignoreCase = true
                    )
                ) {
                    obj.countryName = "USA"
                }
                if (!countryName.equals("")) {
                    address =
                        address.replace(", $countryName", "").replace("- $countryName", "")
                }

                if (obj != null && obj.postalCode != null && !obj.postalCode.equals("")) {
                    address = address.replace(" " + obj.postalCode, "")
                }

                //   city = obj.subAdminArea
            } else {
                address = ""
            }
        } catch (e: IOException) {
            Toast.makeText(
                this,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }

        return address
    }
}
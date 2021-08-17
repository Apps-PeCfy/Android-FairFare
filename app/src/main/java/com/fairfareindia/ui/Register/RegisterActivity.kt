package com.fairfareindia.ui.Register

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.fairfareindia.R
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.otp.OtpAvtivity
import com.fairfareindia.ui.privacypolicy.PrivacyPolicyActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.MyLocationManager
import com.fairfareindia.utils.ProjectUtilities
import com.fairfareindia.utils.ProjectUtilities.dismissProgressDialog
import com.fairfareindia.utils.ProjectUtilities.showProgressDialog
import com.rilixtech.widget.countrycodepicker.Country
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import java.util.regex.Pattern.compile

class RegisterActivity : AppCompatActivity(),
    CountryCodePicker.OnCountryChangeListener,
    IRegisterVIew, LocationListener {


    private val emailRegex = compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )


    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+"
    var MobileNo: String? = null
    var UserName: String? = null
    var UserEmail: String? = null
    var LoginType: String? = "NOR"
    var Gender: String? = null
    var GoogleToken: String? = null
    var CountryCode: String? = null
    var countryCodeCCP = "91"
    var countryCodeISO: String? = "IN"

    @JvmField
    @BindView(R.id.edit_text_register)
    var edit_text_register: EditText? = null

    @JvmField
    @BindView(R.id.edt_email)
    var edt_email: EditText? = null

    @JvmField
    @BindView(R.id.btnRegister)
    var btnRegister: Button? = null

    @JvmField
    @BindView(R.id.tvEmailError)
    var tvEmailError: TextView? = null

    @JvmField
    @BindView(R.id.tvPhoneNumberError)
    var tvPhoneNumberError: TextView? = null

    @JvmField
    @BindView(R.id.ccpr)
    var ccp: CountryCodePicker? = null

    @JvmField
    @BindView(R.id.tvNameError)
    var tvNameError: TextView? = null

    @JvmField
    @BindView(R.id.sign_up)
    var sign_up: TextView? = null

    @JvmField
    @BindView(R.id.radioButtonmale)
    var radioButtonmale: RadioButton? = null

    @JvmField
    @BindView(R.id.radioButtonfemale)
    var radioButtonfemale: RadioButton? = null

    @JvmField
    @BindView(R.id.radioButtonOther)
    var radioButtonOther: RadioButton? = null

    @JvmField
    @BindView(R.id.ivViewInfo)
    var ivViewInfo: ImageView? = null

    var eventInfoDialog: Dialog? = null
    var eventDialogBind: EventDialogBind1? = null


    @JvmField
    @BindView(R.id.tvPrivacy)
    var tvPrivacy: TextView? = null

    @JvmField
    @BindView(R.id.tvTerms)
    var tvTerms: TextView? = null


    @JvmField
    @BindView(R.id.edt_name)
    var edt_name: EditText? = null
    var loginResponsepojo: LoginResponsepojo? = null
    var selectedCountryCode: Country? = null
    var iRegisterPresenter: IRegisterPresenter? = null
    protected var locationManager: LocationManager? = null
    protected var myLocationManager: MyLocationManager? = MyLocationManager(this)
    var register_Latitude = 0.0
    var register_Longitude = 0.0


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ButterKnife.bind(this)

        setStatusBarGradiant(this)


        if (Constants.IS_OLD_PICK_UP_CODE) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
        } else {
            // ILOMADEV :- New method for location update
            initLocationUpdates()
        }


        ccp!!.setOnCountryChangeListener(this)
        iRegisterPresenter = RegisterImplementer(this)
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            MobileNo = extras.getString("Mobile_No")
            UserName = extras.getString("UserName")
            UserEmail = extras.getString("UserEmail")
            LoginType = extras.getString("LoginType")
            GoogleToken = extras.getString("GoogleToken")
            CountryCode = extras.getString("CountryCode")
            countryCodeISO = extras.getString("countryCodeISO")
            if (!TextUtils.isEmpty(MobileNo)) {
                edit_text_register!!.setText(MobileNo)
                edit_text_register!!.isEnabled = true
            }
            if (!TextUtils.isEmpty(UserEmail)) {
                edt_email!!.setText(UserEmail)
                edt_email!!.isEnabled = false
            }
            if (!TextUtils.isEmpty(UserName)) {
                edt_name!!.setText(UserName)
                edt_name!!.isEnabled = false
            }
        } else {
            CountryCode = "91"
        }

        //  numbervalidation ="true";
        if (TextUtils.isEmpty(MobileNo)) {
            CountryCode = "91"
            ccp!!.setCountryForNameCode("in")
        } else {
            ccp!!.setCountryForNameCode(countryCodeISO!!)
        }
        if (LoginType == "GGL") {
            sign_up!!.text = "Please enter Phone number and Gender."
        }
    }

    private fun initLocationUpdates() {
        myLocationManager?.getMyCurrentLocationChange(object :
            MyLocationManager.LocationManagerTrackInterface {
            override fun onMyLocationChange(
                currentLocation: MutableList<Location>?,
                lastLocation: Location?
            ) {
                if (lastLocation != null) {


                    register_Latitude = lastLocation!!.latitude
                    register_Longitude = lastLocation!!.longitude

                    //Stop Location Updates
                    if (  register_Latitude != null && register_Latitude != 0.0) {
                        myLocationManager?.stopLocationUpdates()
                    }

                    Log.d("register_Latitude", "  "+register_Latitude+"  "+register_Longitude)

                }
            }

        })
    }

    private fun setStatusBarGradiant(activity: RegisterActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }


    @OnClick(R.id.ivViewInfo)
    fun iiewInfo() {

        eventInfoDialog = Dialog(this@RegisterActivity, R.style.dialog_style)

        eventInfoDialog!!.setCancelable(true)
        val inflater1 = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view12: View = inflater1.inflate(R.layout.gender_info, null)
        eventInfoDialog!!.setContentView(view12)
        eventDialogBind = EventDialogBind1()
        ButterKnife.bind(eventDialogBind!!, view12)
        eventInfoDialog!!.show()


    }

    inner class EventDialogBind1 {
        @JvmField
        @BindView(R.id.ivPopUpClose)
        var ivPopUpClose: ImageView? = null

        @OnClick(R.id.ivPopUpClose)
        fun ivPopUpClose() {
            eventInfoDialog!!.dismiss()
        }
    }


    @OnClick(R.id.tvPrivacy)
    fun tvPrivacy() {

        val intent = Intent(applicationContext, PrivacyPolicyActivity::class.java)
        intent.putExtra("ContentPageData", "Privacy Policy")
        startActivity(intent)

    }

    @OnClick(R.id.tvTerms)
    fun tvTerms() {
        val intent = Intent(applicationContext, PrivacyPolicyActivity::class.java)
        intent.putExtra("ContentPageData", "Terms of Use")
        startActivity(intent)

    }


    @OnClick(R.id.tvLogin)
    fun tvLogin() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    @OnTextChanged(R.id.edt_email)
    fun edtEmail() {
        if (tvEmailError!!.visibility == View.VISIBLE) {
            tvEmailError!!.visibility = View.GONE
            return
        }
    }

    @OnTextChanged(R.id.edit_text_register)
    fun editPhone() {
        if (tvPhoneNumberError!!.visibility == View.VISIBLE) {
            tvPhoneNumberError!!.visibility = View.GONE
            return
        }
    }

    @OnTextChanged(R.id.edt_name)
    fun editName() {
        if (tvNameError!!.visibility == View.VISIBLE) {
            tvNameError!!.visibility = View.GONE
            return
        }
    }

    @OnClick(R.id.btnRegister)
    fun btnCLick() {


        if(ProjectUtilities.checkInternetAvailable(this@RegisterActivity)) {


            if (radioButtonmale!!.isChecked) {
                Gender = "Male"
            } else if (radioButtonfemale!!.isChecked) {
                Gender = "Female"
            } else {
                Gender = "Other"
            }


            val phoneNumberUtil: PhoneNumberUtil
            var phoneNumber: PhoneNumber? = null
            var numbervalidation = "false"
            var isMobile: PhoneNumberUtil.PhoneNumberType? = null
            phoneNumberUtil = PhoneNumberUtil.createInstance(this)
            try {
                phoneNumber = phoneNumberUtil.parse(
                    edit_text_register!!.text.toString().trim { it <= ' ' },
                    countryCodeISO!!.toUpperCase()
                )
                isMobile = phoneNumberUtil.getNumberType(phoneNumber)
            } catch (e: NumberParseException) {
                e.printStackTrace()
            }
            numbervalidation = if (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile) {
                "true"
            } else if (PhoneNumberUtil.PhoneNumberType.FIXED_LINE == isMobile) {
                "false"
            } else {
                "false"
            }

            if (numbervalidation == "false") {
                tvPhoneNumberError!!.text = "Please enter a valid phone no."
                tvPhoneNumberError!!.visibility = View.VISIBLE
            } else if (edt_email!!.text.toString()
                    .isNotEmpty() && (!emailRegex.matcher(edt_email!!.text.toString()).matches())
            ) {


                tvEmailError!!.text = "Please enter a valid email."
                tvEmailError!!.visibility = View.VISIBLE


            } else if (TextUtils.isEmpty(edt_name!!.text.toString())) {
                tvNameError!!.text = "Please enter Full Name"
                tvNameError!!.visibility = View.VISIBLE
            } else {


                if (LoginType == "NOR") {
                    GoogleToken = ""
                }
                iRegisterPresenter!!.register(
                    edit_text_register!!.text.toString(),
                    "Register",
                    "Android",
                    LoginType,
                    countryCodeCCP,
                    edt_name!!.text.toString(),
                    edt_email!!.text.toString(),
                    GoogleToken
                )
            }

        }else{
            ProjectUtilities.showToast(this@RegisterActivity,getString(R.string.internet_error))
        }
    }

    override fun onCountrySelected(selectedCountry: Country) {
        selectedCountryCode = selectedCountry
        countryCodeCCP = selectedCountry.phoneCode
        countryCodeISO = selectedCountryCode!!.iso
    }

    override fun onLoginSUccess(loginResponsepojo: LoginResponsepojo?) {
        if (loginResponsepojo!!.redirectTo == "Otp") {
            val intent = Intent(applicationContext, OtpAvtivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Mobile_No", edit_text_register!!.text.toString())
            intent.putExtra("CountryCode", countryCodeCCP)
            intent.putExtra("UserMail", edt_email!!.text.toString())
            intent.putExtra("UserName", edt_name!!.text.toString())
            intent.putExtra("LoginType", LoginType)
            intent.putExtra("Gender", Gender)
            intent.putExtra("register_Latitude", register_Latitude.toString())
            intent.putExtra("register_Longitude", register_Longitude.toString())


            intent.putExtra("Activity", "Register")
            intent.putExtra("GoogleToken", GoogleToken)
            startActivity(intent)
            finish()
        }
    }

    override fun validationError(pojo: ValidationResponse?) {
        for (i in pojo!!.errors!!.indices) {
            if (pojo.errors!![i].key == "email") {
                tvEmailError!!.text = pojo.errors!![i].message
                tvEmailError!!.visibility = View.VISIBLE
            }
            if (pojo.errors!![i].key == "phone_no") {
                tvPhoneNumberError!!.text = pojo.errors!![i].message
                tvPhoneNumberError!!.visibility = View.VISIBLE
            }
        }
    }

    override fun showWait() {
        showProgressDialog(this@RegisterActivity)
    }

    override fun removeWait() {
        dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(this@RegisterActivity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onLocationChanged(p0: Location) {
    }
}
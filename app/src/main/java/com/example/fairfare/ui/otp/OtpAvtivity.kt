package com.example.fairfare.ui.otp

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.fairfare.R
import com.example.fairfare.receiver.SmsReceiver
import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.ui.otp.pojo.VerifyOTPResponsePojo
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.example.fairfare.utils.ProjectUtilities.dismissProgressDialog
import com.example.fairfare.utils.ProjectUtilities.showProgressDialog
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlin.random.Random


class OtpAvtivity : AppCompatActivity(), IOtpView {
    var MobileNo: String? = null
    var CountryCode: String? = null
    var UserMail: String? = null
    var Username: String? = null
    var type: String? = null
    var GoogleToken: String? = null
    var LoginType: String? = null
    var gender: String? = null
    var deviceID: String? = null
    var smsReceiver: SmsReceiver? = null

    @JvmField
    @BindView(R.id.otp)
    var otp: TextView? = null

    @JvmField
    @BindView(R.id.txtResendTimer)
    var txtResendTimer: TextView? = null

    @JvmField
    @BindView(R.id.txt_resend_otp)
    var txt_resend_otp: TextView? = null

    @JvmField
    @BindView(R.id.lltimer)
    var lltimer: LinearLayout? = null

    @JvmField
    @BindView(R.id.edt_otp)
    var edt_otp: EditText? = null

    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null
    var verifyOTPResponsePojo: VerifyOTPResponsePojo? = null
    private var mPreferencesManager: PreferencesManager? = null
    var iOtpPresenter: IOtpPresenter? = null
    override fun onBackPressed() {

        //super.onBackPressed();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_avtivity)
        ButterKnife.bind(this)
        iOtpPresenter = OtpPresenterImplmenter(this)
        PreferencesManager.initializeInstance(this@OtpAvtivity)
        mPreferencesManager = PreferencesManager.instance
        deviceID = String.format("%08d", Random.nextInt(100000000))


        setToolbar()
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            MobileNo = extras.getString("Mobile_No")
            CountryCode = extras.getString("CountryCode")
            UserMail = extras.getString("UserMail")
            Username = extras.getString("UserName")
            type = extras.getString("Activity")
            LoginType = extras.getString("LoginType")
            gender = extras.getString("Gender")
            GoogleToken = extras.getString("GoogleToken")
            otp!!.text = "+$CountryCode $MobileNo"
        }







        object : CountDownTimer(31000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if((millisUntilFinished / 1000).toString().length==1){
                    txtResendTimer!!.setText("(00:0" + millisUntilFinished / 1000+")")

                }else{
                    txtResendTimer!!.setText("(00:" + millisUntilFinished / 1000+")")

                }
            }

            override fun onFinish() {
                lltimer!!.visibility = View.GONE
                txt_resend_otp!!.visibility = View.VISIBLE

            }
        }.start()





        registerReceiver()
    }

    //    private void readOTPAutomatically(){
    //        smsVerifyCatcher = new SmsVerifyCatcher((Activity) context, new OnSmsCatchListener<String>() {
    //            @Override
    //            public void onSmsCatch(String message) {
    //                String code = message;
    //              String otp = code.substring(code.length() - 4);
    //
    //              otpView.setText (otp);
    //
    //                if (isValid()){
    //                    verifyOTPAPI();
    //                }
    //
    //            }
    //        });
    //    }
    private fun registerReceiver() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            smsReceiver = SmsReceiver()
            registerReceiver(smsReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
            val otpListener: SmsReceiver.OTPListener = object : SmsReceiver.OTPListener {
                override fun onOTPReceived(otpData: String?) {
                    edt_otp?.setText(otpData)
                }

                override fun onOTPTimeOut() {
                    edt_otp?.setText("")
                    Toast.makeText(this@OtpAvtivity, "TimeOut", Toast.LENGTH_SHORT).show()
                }
            }
            smsReceiver!!.injectOTPListener(otpListener)
        }
        task.addOnFailureListener { // Failed to start retriever, inspect Exception for more details
            Toast.makeText(this, "Problem to start listener", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setToolbar() {
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    @OnClick(R.id.btnFinish)
    fun btnLogin() {
        if (TextUtils.isEmpty(edt_otp!!.text.toString()) || edt_otp!!.textSize < 6) {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_LONG).show()
        } else {

            if (LoginType == "NOR") {
                GoogleToken = ""
            }
            iOtpPresenter!!.verifyOtp(
                MobileNo, type, "Android",
                LoginType, CountryCode, Username, UserMail, gender, edt_otp!!.text.toString(),deviceID)
        }
    }

    @OnClick(R.id.txt_resend_otp)
    fun resendOTP() {
        edt_otp!!.setText("")
        if (LoginType == "NOR") {
            GoogleToken = ""
        }
        iOtpPresenter!!.resendOtp(
            MobileNo, type, "Android",
            LoginType, CountryCode, "", "", GoogleToken
        )

    }

    override fun otpSuccess(verifyOTPResponsePojo: VerifyOTPResponsePojo?) {
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN, verifyOTPResponsePojo!!.token)
        mPreferencesManager!!.setIntegerValue(Constants.SHARED_PREFERENCE_LOGIN_ID, verifyOTPResponsePojo!!.user!!.id)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_NAME, verifyOTPResponsePojo!!.user!!.name)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_EMAIL, verifyOTPResponsePojo!!.user!!.email)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_PHONENO, verifyOTPResponsePojo!!.user!!.phoneNo)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_GENDER, verifyOTPResponsePojo!!.user!!.gender)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFESTION, verifyOTPResponsePojo!!.user!!.profession)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_DOB, verifyOTPResponsePojo!!.user!!.dateOfBirth)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK, verifyOTPResponsePojo!!.user!!.profilePic)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_LOCATION, verifyOTPResponsePojo!!.user!!.location)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_DEVICEID, deviceID)
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_ISLOGIN, "true")
        edt_otp!!.setText("")
        val intent = Intent(this@OtpAvtivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun reSendOTPSuccess(loginResponsepojo: LoginResponsepojo?) {

        lltimer!!.visibility = View.VISIBLE
        txt_resend_otp!!.visibility = View.GONE

        Toast.makeText(this@OtpAvtivity, loginResponsepojo!!.message, Toast.LENGTH_LONG).show()

        object : CountDownTimer(31000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if((millisUntilFinished / 1000).toString().length==1){
                    txtResendTimer!!.setText("(00:0" + millisUntilFinished / 1000+")")

                }else{
                    txtResendTimer!!.setText("(00:" + millisUntilFinished / 1000+")")

                }
            }

            override fun onFinish() {
                lltimer!!.visibility = View.GONE
                txt_resend_otp!!.visibility = View.VISIBLE

            }
        }.start()


    }

    override fun validationError(validationResponse: ValidationResponse?) {
        for (i in validationResponse!!.errors!!.indices) {
            if (validationResponse.errors!![i].key == "otp") {
                Toast.makeText(this@OtpAvtivity, validationResponse.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun showWait() {
        showProgressDialog(this@OtpAvtivity)
    }

    override fun removeWait() {
        dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(this@OtpAvtivity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }
}
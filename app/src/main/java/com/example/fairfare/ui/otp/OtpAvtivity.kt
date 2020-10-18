package com.example.fairfare.ui.otp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.fairfare.R
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.ui.otp.pojo.VerifyOTPResponsePojo
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.example.fairfare.utils.ProjectUtilities.dismissProgressDialog
import com.example.fairfare.utils.ProjectUtilities.showProgressDialog

class OtpAvtivity : AppCompatActivity(), IOtpView {
    var MobileNo: String? = null
    var CountryCode: String? = null
    var UserMail: String? = null
    var Username: String? = null
    var type: String? = null
    var GoogleToken: String? = null
    var LoginType: String? = null

    @JvmField
    @BindView(R.id.otp)
    var otp: TextView? = null

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
            GoogleToken = extras.getString("GoogleToken")
            otp!!.text = "+$CountryCode $MobileNo"
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
            /* final ProgressDialog progressDialog = new ProgressDialog(OtpAvtivity.this);
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage("Please Wait"); // set message
            progressDialog.show(); // show progress dialog
*/
            if (LoginType == "NOR") {
                GoogleToken = ""
            }
            iOtpPresenter!!.verifyOtp(
                MobileNo, type, "Android",
                LoginType, CountryCode, Username, UserMail, "Male", edt_otp!!.text.toString()
            )

            /*   (ApiClient.getClient().verifyOtp(MobileNo, type, "Android",
                    LoginType, CountryCode, Username, UserMail, "Male", edt_otp.getText().toString())).enqueue(new Callback<VerifyOTPResponsePojo>() {
                @Override
                public void onResponse(Call<VerifyOTPResponsePojo> call, Response<VerifyOTPResponsePojo> response) {
                    verifyOTPResponsePojo = response.body();
                    if (response.code() == 200) {

                        mPreferencesManager.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN, response.body().getToken());
                        mPreferencesManager.setStringValue(Constants.SHARED_PREFERENCE_ISLOGIN, "true");
                        edt_otp.setText("");
                        Intent intent = new Intent(OtpAvtivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else if(response.code()==400) {
                        Gson gson = new GsonBuilder().create();
                        ValidationResponse pojo = new ValidationResponse();

                        try {
                            pojo = gson.fromJson(response.errorBody().string(), ValidationResponse.class);

                            for (int i = 0; i < pojo.getErrors().size(); i++) {
                                if (pojo.getErrors().get(i).getKey().equals("otp")) {
                                    Toast.makeText(OtpAvtivity.this, pojo.getMessage(), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }


                        } catch (IOException exception) {
                        }

                    }else {
                        Toast.makeText(OtpAvtivity.this, "Internal server error", Toast.LENGTH_LONG).show();
                    }


                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<VerifyOTPResponsePojo> call, Throwable t) {
                    Log.d("response", t.getStackTrace().toString());
                    progressDialog.dismiss();

                }
            });*/
        }
    }

    @OnClick(R.id.txt_resend_otp)
    fun resendOTP() {
        edt_otp!!.setText("")
        /*  final ProgressDialog progressDialog = new ProgressDialog(OtpAvtivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show(); // show progress dialog
*/if (LoginType == "NOR") {
            GoogleToken = ""
        }
        iOtpPresenter!!.resendOtp(
            MobileNo, type, "Android",
            LoginType, CountryCode, "", "", GoogleToken
        )

        /*  (ApiClient.getClient().login(MobileNo, type, "Android",
                LoginType, CountryCode, "", "", GoogleToken)).enqueue(new Callback<LoginResponsepojo>() {
            @Override
            public void onResponse(Call<LoginResponsepojo> call, Response<LoginResponsepojo> response) {

                if (response.code() == 200) {


                } else {

                    Toast.makeText(OtpAvtivity.this, "Internal server error", Toast.LENGTH_LONG).show();
                }


                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<LoginResponsepojo> call, Throwable t) {
                Log.d("response", t.getStackTrace().toString());
                progressDialog.dismiss();

            }
        });
*/
    }

    override fun otpSuccess(verifyOTPResponsePojo: VerifyOTPResponsePojo?) {
        mPreferencesManager!!.setStringValue(
            Constants.SHARED_PREFERENCE_LOGIN_TOKEN,
            verifyOTPResponsePojo!!.token
        )
        mPreferencesManager!!.setStringValue(
            Constants.SHARED_PREFERENCE_ISLOGIN,
            "true"
        )
        edt_otp!!.setText("")
        val intent = Intent(this@OtpAvtivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
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
}
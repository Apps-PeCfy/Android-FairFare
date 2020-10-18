package com.example.fairfare.ui.Register

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.example.fairfare.R
import com.example.fairfare.ui.Login.LoginActivity
import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.otp.OtpAvtivity
import com.example.fairfare.utils.ProjectUtilities.dismissProgressDialog
import com.example.fairfare.utils.ProjectUtilities.showProgressDialog
import com.rilixtech.widget.countrycodepicker.Country
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber

class RegisterActivity : AppCompatActivity(),
    CountryCodePicker.OnCountryChangeListener,
    IRegisterVIew {

    var emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+")
    var MobileNo: String? = null
    var UserName: String? = null
    var UserEmail: String? = null
    var LoginType: String? = "NOR"
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
    @BindView(R.id.edt_name)
    var edt_name: EditText? = null
    var loginResponsepojo: LoginResponsepojo? = null
    var selectedCountryCode: Country? = null
    var iRegisterPresenter: IRegisterPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ButterKnife.bind(this)
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

        /* try {

            phoneNumber = phoneNumberUtil.parse(edit_text_register.getText().toString(), countryCodeISO.toUpperCase());
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);

            numbervalidation = String.valueOf(isValid);

        } catch (NumberParseException e) {
            e.printStackTrace();
        }*/if (numbervalidation == "false") {
            tvPhoneNumberError!!.text = "Please enter a valid phone no."
            tvPhoneNumberError!!.visibility = View.VISIBLE
        } else if (!edt_email!!.text.toString().trim { it <= ' ' }.matches(emailPattern)) {
            // Toast.makeText(this, "wrong", Toast.LENGTH_LONG).show();
            tvEmailError!!.text = "Please enter a valid email."
            tvEmailError!!.visibility = View.VISIBLE
        } else if (TextUtils.isEmpty(edt_name!!.text.toString())) {
            tvNameError!!.text = "Please enter Full Name"
            tvNameError!!.visibility = View.VISIBLE
        } else {

/*

            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage("Please Wait"); // set message
            progressDialog.show(); // show progress dialog
*/
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
            /*  (ApiClient.getClient().login(edit_text_register.getText().toString(), "Register", "Android",
                    LoginType, countryCode, edt_name.getText().toString(), edt_email.getText().toString(), GoogleToken)).enqueue(new Callback<LoginResponsepojo>() {
                @Override
                public void onResponse(Call<LoginResponsepojo> call, Response<LoginResponsepojo> response) {
                    LoginResponsepojo loginResponsepojo = response.body();

                    if (response.code() == 200) {
                        if (loginResponsepojo.getRedirectTo().equals("Otp")) {
                            Intent intent = new Intent(getApplicationContext(), OtpAvtivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("Mobile_No", edit_text_register.getText().toString());
                            intent.putExtra("CountryCode", countryCode);
                            intent.putExtra("UserMail", edt_email.getText().toString());
                            intent.putExtra("UserName", edt_name.getText().toString());
                            intent.putExtra("LoginType", LoginType);
                            intent.putExtra("Activity", "Register");
                            intent.putExtra("GoogleToken", GoogleToken);

                            startActivity(intent);
                            finish();
                        }
                    } else if(response.code()==400) {

                        Gson gson = new GsonBuilder().create();
                        ValidationResponse pojo = new ValidationResponse();

                        try {
                            pojo = gson.fromJson(response.errorBody().string(), ValidationResponse.class);
                            for (int i = 0; i < pojo.getErrors().size(); i++) {

                                if (pojo.getErrors().get(i).getKey().equals("email")) {
                                    tvEmailError.setText(pojo.getErrors().get(i).getMessage());
                                    tvEmailError.setVisibility(View.VISIBLE);
                                }

                                if (pojo.getErrors().get(i).getKey().equals("phone_no")) {
                                    tvPhoneNumberError.setText(pojo.getErrors().get(i).getMessage());
                                    tvPhoneNumberError.setVisibility(View.VISIBLE);
                                }


                            }


                        } catch (IOException exception) {
                        }


                    }else {
                        Toast.makeText(RegisterActivity.this, "Internal server error", Toast.LENGTH_LONG).show();
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
}
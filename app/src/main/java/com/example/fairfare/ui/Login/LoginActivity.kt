package com.example.fairfare.ui.Login

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.example.fairfare.R
import com.example.fairfare.base.BaseLocationClass
import com.example.fairfare.networking.ApiClient.client
import com.example.fairfare.ui.Login.LoginActivity
import com.example.fairfare.ui.Login.pojo.LoginResponsepojo
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.Register.RegisterActivity
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.ui.otp.OtpAvtivity
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.example.fairfare.utils.ProjectUtilities.dismissProgressDialog
import com.example.fairfare.utils.ProjectUtilities.showProgressDialog
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import com.rilixtech.widget.countrycodepicker.Country
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.squareup.okhttp.FormEncodingBuilder
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class LoginActivity : BaseLocationClass(),
    CountryCodePicker.OnCountryChangeListener, ILoginView {
    var facebookLoginClick = 0
    var googleAccessTokem: String? = null

    @JvmField
    @BindView(R.id.iv_gmail)
    var iv_gmail: ImageView? = null
    var countryCodeISO = "IN"
    var countryCode = "91"
    private var callbackManager: CallbackManager? = null

    @JvmField
    @BindView(R.id.btnLogin)
    var btnLogin: Button? = null

    @JvmField
    @BindView(R.id.tvPhoneNumberError)
    var tvPhoneNumberError: TextView? = null

    @JvmField
    @BindView(R.id.edit_text)
    var edit_text: EditText? = null

    @JvmField
    @BindView(R.id.ccpr)
    var ccp: CountryCodePicker? = null

    @JvmField
    @BindView(R.id.btnLoginFacebook)
    var btnLoginFacebook: Button? = null

    @JvmField
    @BindView(R.id.btnFBK)
    var btnFBK: LoginButton? = null
    var loginResponsepojo: LoginResponsepojo? = null
    private val SIGN_IN = 30
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var gmailPersonName: String? = null
    private var gmailFirstName: String? = null
    private var gmailLastName: String? = null
    private var gmailPersonEmail: String? = null
    private var gToken: String? = null
    private var providerid: String? = null
    private var facebook_uid: String? = null
    private var fbFirstName: String? = null
    private var fbEmail: String? = null
    private var token: String? = null
    var selectedCountryCode: Country? = null
    private var mPreferencesManager: PreferencesManager? = null
    var iLoginPresenter: ILoginPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
        // FacebookSdk.sdkInitialize(getApplicationContext());
        // hashkey();
        iLoginPresenter = LoginPresenterImplementer(this)
        PreferencesManager.initializeInstance(this@LoginActivity)
        mPreferencesManager = PreferencesManager.instance
        setStatusBarGradiant(this)
        FacebookSdk.sdkInitialize(this@LoginActivity)
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()
        facebookLogin()
        ccp!!.setOnCountryChangeListener(this)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("254736596560-cnkg23q193qpnffh7u0mpcdbdmofua28.apps.googleusercontent.com")
                .requestServerAuthCode("254736596560-cnkg23q193qpnffh7u0mpcdbdmofua28.apps.googleusercontent.com")
                .requestId()
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //  ccp.registerPhoneNumberTextView(edit_text);
    }

    private fun setStatusBarGradiant(activity: LoginActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    private fun hashkey() {
        try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("printHashKey", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
        } catch (e: Exception) {
        }
    }

    @OnTextChanged(R.id.edit_text)
    fun editPhone() {
        if (tvPhoneNumberError!!.visibility == View.VISIBLE) {
            tvPhoneNumberError!!.visibility = View.GONE
            return
        }
    }

    @OnClick(R.id.iv_gmail)
    fun gmail() {
        edit_text!!.setText("")
        ccp!!.setCountryForNameCode("in")
        signIn()
    }

    @OnTextChanged(R.id.edit_text)
    fun edttxt() {
    }

    @OnClick(R.id.btnLoginFacebook)
    fun onClickLoginWithFAcebook(view: View?) {
        edit_text!!.setText("")
        ccp!!.setCountryForNameCode("in")
        if (facebookLoginClick == 0) {
            facebookLoginClick = 1
            btnFBK!!.performClick()
        }
    }

    @OnClick(R.id.btnFBK)
    fun facebookButtonclick() {
        facebookLogin()
    }

    private fun facebookLogin() {
        btnFBK!!.setReadPermissions(Arrays.asList("email", "public_profile"))
        btnFBK!!.loginBehavior = LoginBehavior.WEB_VIEW_ONLY
        btnFBK!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                val request =
                    GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken()
                    ) { `object`, response ->
                        Log.i(
                            "LoginActivity",
                            "$response * $`object`"
                        )
                        if (response.error == null) {
                            try {
                                facebook_uid = `object`["id"].toString()
                                fbFirstName = `object`["first_name"].toString()
                                fbEmail = `object`["email"].toString()
                                val accessToken =
                                    AccessToken.getCurrentAccessToken()
                                token = accessToken.token
                                val profile_pic_url =
                                    `object`.getJSONObject("picture").getJSONObject("data")
                                        .getString("url")
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    LoginManager.getInstance().logOut()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }


                        /* final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                                                    progressDialog.setCancelable(false); // set cancelable to false
                                                    progressDialog.setMessage("Please Wait"); // set message
                                                    progressDialog.show(); // show progress dialog
                                                   */

                        /*(ApiClient.getClient().sociallogin("Android", "FBK", fbFirstName,
                                                            facebook_uid, token, fbEmail)).enqueue(new Callback<LoginResponsepojo>() {
                                                        @Override
                                                        public void onResponse(Call<LoginResponsepojo> call, Response<LoginResponsepojo> response) {
                                                            loginResponsepojo = response.body();
                                                            progressDialog.dismiss();

                                                            if (response.code() == 200) {

                                                                if (loginResponsepojo.getRedirectTo().equalsIgnoreCase("Register")) {
                                                                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    intent.putExtra("Mobile_No", "");
                                                                    intent.putExtra("UserEmail", fbEmail);
                                                                    intent.putExtra("UserName", fbFirstName);
                                                                    intent.putExtra("CountryCode", countryCode);
                                                                    intent.putExtra("LoginType", "FBK");
                                                                    intent.putExtra("GoogleToken", token);
                                                                    intent.putExtra("countryCodeISO", countryCodeISO);


                                                                    startActivity(intent);
                                                                    finish();
                                                                } else if (
                                                                        loginResponsepojo.getRedirectTo().equalsIgnoreCase("home")) {
                                                                    assert response.body() != null;
                                                                    mPreferencesManager.setStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN, response.body().getToken());
                                                                    mPreferencesManager.setStringValue(Constants.SHARED_PREFERENCE_ISLOGIN, "true");
                                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                                    startActivity(intent);
                                                                    finish();

                                                                    //  Toast.makeText(LoginActivity.this, loginResponsepojo.getMessage(), Toast.LENGTH_LONG).show();

                                                                }
                                                            } else if(response.code()==400){


                                                                Gson gson = new GsonBuilder().create();
                                                                ValidationResponse pojo = new ValidationResponse();

                                                                try {
                                                                    pojo = gson.fromJson(response.errorBody().string(), ValidationResponse.class);

                                                                    for (int i = 0; i < pojo.getErrors().size(); i++) {
                                                                        Toast.makeText(LoginActivity.this, pojo.getErrors().get(0).getMessage(), Toast.LENGTH_LONG).show();

                                                                    }


                                                                } catch (IOException exception) {
                                                                }

                                                            }else {
                                                                Toast.makeText(LoginActivity.this, "Internal server error", Toast.LENGTH_LONG).show();
                                                            }


                                                        }

                                                        @Override
                                                        public void onFailure(Call<LoginResponsepojo> call, Throwable t) {
                                                            Log.d("response", t.getStackTrace().toString());
                                                            progressDialog.dismiss();

                                                        }
                                                    });
                    */iLoginPresenter!!.socialLogin(
                        "Android", "FBK", fbFirstName,
                        facebook_uid, token, fbEmail
                    )
                    }
                val parameters = Bundle()
                parameters.putString(
                    "fields",
                    "id, first_name,last_name, email,  picture.type(large)"
                )
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                error.printStackTrace()
            }
        })
    }

    @OnClick(R.id.tvSignUp)
    fun tvSignUp() {
        val intent = Intent(applicationContext, RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("Mobile_No", "")
        intent.putExtra("UserEmail", "")
        intent.putExtra("CountryCode", "91")
        intent.putExtra("UserName", "")
        intent.putExtra("LoginType", "NOR")
        intent.putExtra("GoogleToken", "")
        intent.putExtra("countryCodeISO", "in")
        startActivity(intent)
        finish()
    }

    @OnClick(R.id.btnLogin)
    fun callLogin() {
        val phoneNumberUtil: PhoneNumberUtil
        var phoneNumber: PhoneNumber? = null
        var isMobile: PhoneNumberUtil.PhoneNumberType? = null
        var strNumberValidation = "false"
        phoneNumberUtil = PhoneNumberUtil.createInstance(this)
        strNumberValidation = ""
        try {
            phoneNumber = phoneNumberUtil.parse(
                edit_text!!.text.toString().trim { it <= ' ' },
                countryCodeISO.toUpperCase()
            )
            isMobile = phoneNumberUtil.getNumberType(phoneNumber)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        strNumberValidation = if (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile) {
            "true"
        } else if (PhoneNumberUtil.PhoneNumberType.FIXED_LINE == isMobile) {
            "false"
        } else {
            "false"
        }


        /* try {

            phoneNumber = phoneNumberUtil.parse(edit_text.getText().toString(), countryCodeISO.toUpperCase());
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);

            strNumberValidation = String.valueOf(isValid);

        } catch (NumberParseException e) {
            e.printStackTrace();
        }*/if (strNumberValidation == "false") {
            tvPhoneNumberError!!.text = "Please enter a valid phone no."
            tvPhoneNumberError!!.visibility = View.VISIBLE
            //   Toast.makeText(this, "Plese enter valid phone no.", Toast.LENGTH_LONG).show();
        } else {
            val progressDialog = ProgressDialog(this@LoginActivity)
            progressDialog.setCancelable(false) // set cancelable to false
            progressDialog.setMessage("Please Wait") // set message
            progressDialog.show() // show progress dialog
            iLoginPresenter!!.login(
                edit_text!!.text.toString(), "Login", "Android",
                "NOR", countryCode, "", "", ""
            )

            /* (ApiClient.getClient().login(edit_text.getText().toString(), "Login", "Android",
                    "NOR", countryCode, "", "", "")).enqueue(new Callback<LoginResponsepojo>() {
                @Override
                public void onResponse(Call<LoginResponsepojo> call, Response<LoginResponsepojo> response) {
                    loginResponsepojo = response.body();

                    if (response.code() == 200) {
                        if (loginResponsepojo.getRedirectTo().equals("Register")) {
                            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("Mobile_No", edit_text.getText().toString());
                            intent.putExtra("UserEmail", "");
                            intent.putExtra("CountryCode", countryCode);
                            intent.putExtra("UserName", "");
                            intent.putExtra("LoginType", "NOR");
                            intent.putExtra("GoogleToken", "");
                            intent.putExtra("countryCodeISO", countryCodeISO);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), OtpAvtivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("Mobile_No", edit_text.getText().toString());
                            intent.putExtra("CountryCode", countryCode);
                            intent.putExtra("UserMail", "");
                            intent.putExtra("UserName", "");
                            intent.putExtra("Activity", "Login");
                            intent.putExtra("LoginType", "NOR");
                            intent.putExtra("GoogleToken", "");

                            startActivity(intent);
                            finish();

                        }


                        progressDialog.dismiss();
                    }else{

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Internal server error", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<LoginResponsepojo> call, Throwable t) {
                    Log.d("response", t.getStackTrace().toString());
                    progressDialog.dismiss();

                }
            });*/
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, SIGN_IN)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                handleSignInResult(task)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        } else {
            facebookLoginClick = 0
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Throws(ApiException::class)
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        //   GoogleSignInAccount acctu = GoogleSignIn.getLastSignedInAccount(this);
        val acct =
            task.getResult(ApiException::class.java)
        if (acct != null) {
            val client = OkHttpClient()
            val requestBody = FormEncodingBuilder()
                .add("grant_type", "authorization_code")
                .add(
                    "client_id",
                    "254736596560-cnkg23q193qpnffh7u0mpcdbdmofua28.apps.googleusercontent.com"
                )
                .add("client_secret", "-ZAJiv8WoSGTb5pHPl_kdtSA")
                .add("redirect_uri", "")
                .add("code", acct.serverAuthCode)
                .build()
            val request = Request.Builder()
                .url("https://www.oogleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : com.squareup.okhttp.Callback {
                override fun onFailure(
                    request: Request,
                    e: IOException
                ) {
                }

                @Throws(IOException::class)
                override fun onResponse(response: com.squareup.okhttp.Response) {
                    try {
                        val jsonObject = JSONObject(response.body().string())
                        gToken = jsonObject.getString("access_token")
                        Log.d("access_token", gToken)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
            gmailPersonName = acct.displayName
            gmailFirstName = acct.givenName
            gmailLastName = acct.familyName
            gmailPersonEmail = acct.email
            gToken = acct.idToken //Token
            providerid = acct.id //proderid
            val auth = acct.serverAuthCode
            val personPhoto = acct.photoUrl
            Log.d("GmailData", auth)
            mGoogleSignInClient!!.signOut().addOnCompleteListener(
                this
            ) { }
        }
        if (!TextUtils.isEmpty(gToken)) {
            CallAPI()
        }
    }

    private fun CallAPI() {
        val progressDialoggmail = ProgressDialog(this@LoginActivity)
        progressDialoggmail.setCancelable(false) // set cancelable to false
        progressDialoggmail.setMessage("Please Wait") // set message
        progressDialoggmail.show() // show progress dialog
        client.sociallogin(
            "Android", "GGL", gmailPersonName,
            providerid, gToken, gmailPersonEmail
        )!!.enqueue(object : Callback<LoginResponsepojo?> {
            override fun onResponse(
                call: Call<LoginResponsepojo?>,
                response: Response<LoginResponsepojo?>
            ) {
                loginResponsepojo = response.body()
                progressDialoggmail.dismiss()
                if (response.code() == 200) {
                    if (loginResponsepojo!!.redirectTo.equals("Register", ignoreCase = true)) {
                        val intent =
                            Intent(applicationContext, RegisterActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("Mobile_No", "")
                        intent.putExtra("UserEmail", gmailPersonEmail)
                        intent.putExtra("UserName", gmailPersonName)
                        intent.putExtra("CountryCode", countryCode)
                        intent.putExtra("LoginType", "GGL")
                        intent.putExtra("GoogleToken", gToken)
                        intent.putExtra("countryCodeISO", countryCodeISO)
                        startActivity(intent)
                        finish()
                    } else if (loginResponsepojo!!.redirectTo.equals("home", ignoreCase = true)) {
                        assert(response.body() != null)
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_TOKEN,
                            response.body()!!.token
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_ISLOGIN,
                            "true"
                        )
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                        //  Toast.makeText(LoginActivity.this, loginResponsepojo.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 400) {
                    val gson = GsonBuilder().create()
                    var pojo = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        for (i in pojo.errors!!.indices) {
                            if (pojo.errors!![i].key == "token") {
                                CallAPIRepeate()
                                //progressDialog.dismiss();
                            } else {
                                progressDialoggmail.dismiss()
                                Toast.makeText(
                                    this@LoginActivity,
                                    pojo.errors!![0].message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (exception: IOException) {
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Internal server error", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(
                call: Call<LoginResponsepojo?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
                // progressDialoggmail.dismiss();
            }
        })
    }

    private fun CallAPIRepeate() {
        client.sociallogin(
            "Android", "GGL", gmailPersonName,
            providerid, gToken, gmailPersonEmail
        )!!.enqueue(object : Callback<LoginResponsepojo?> {
            override fun onResponse(
                call: Call<LoginResponsepojo?>,
                response: Response<LoginResponsepojo?>
            ) {
                loginResponsepojo = response.body()
                if (response.code() == 200) {
                    if (loginResponsepojo!!.redirectTo.equals("Register", ignoreCase = true)) {
                        val intent =
                            Intent(applicationContext, RegisterActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("Mobile_No", "")
                        intent.putExtra("UserEmail", gmailPersonEmail)
                        intent.putExtra("UserName", gmailPersonName)
                        intent.putExtra("CountryCode", countryCode)
                        intent.putExtra("LoginType", "GGL")
                        intent.putExtra("GoogleToken", gToken)
                        intent.putExtra("countryCodeISO", countryCodeISO)
                        startActivity(intent)
                        finish()
                    } else if (loginResponsepojo!!.redirectTo.equals("home", ignoreCase = true)) {
                        assert(response.body() != null)
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_TOKEN,
                            response.body()!!.token
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_ISLOGIN,
                            "true"
                        )
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                        // Toast.makeText(LoginActivity.this, loginResponsepojo.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 400) {
                    val gson = GsonBuilder().create()
                    var pojo = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        for (i in pojo.errors!!.indices) {
                            if (pojo.errors!![i].key == "token") {
                                CallAPIRepeate()
                                //progressDialog.dismiss();
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    pojo.errors!![0].message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (exception: IOException) {
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Internal server error", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(
                call: Call<LoginResponsepojo?>,
                t: Throwable
            ) {
                Log.d("response", t.stackTrace.toString())
                // progressDialoggmail.dismiss();
            }
        })
    }

    override fun onCountrySelected(selectedCountry: Country) {
        selectedCountryCode = selectedCountry
        countryCode = selectedCountryCode!!.phoneCode
        countryCodeISO = selectedCountryCode!!.iso
        // Toast.makeText(this, selectedCountry.getPhoneCode(), Toast.LENGTH_LONG).show();
    }

    override fun showWait() {
        showProgressDialog(this@LoginActivity)
    }

    override fun removeWait() {
        dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(this@LoginActivity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onLoginSUccess(loginResponsepojo: LoginResponsepojo?) {
        if (loginResponsepojo!!.redirectTo == "Register") {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Mobile_No", edit_text!!.text.toString())
            intent.putExtra("UserEmail", "")
            intent.putExtra("CountryCode", countryCode)
            intent.putExtra("UserName", "")
            intent.putExtra("LoginType", "NOR")
            intent.putExtra("GoogleToken", "")
            intent.putExtra("countryCodeISO", countryCodeISO)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(applicationContext, OtpAvtivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Mobile_No", edit_text!!.text.toString())
            intent.putExtra("CountryCode", countryCode)
            intent.putExtra("UserMail", "")
            intent.putExtra("UserName", "")
            intent.putExtra("Activity", "Login")
            intent.putExtra("LoginType", "NOR")
            intent.putExtra("GoogleToken", "")
            startActivity(intent)
            finish()
        }
    }

    override fun validationError(validationResponse: ValidationResponse?) {}
    override fun socialLoginSuccess(loginResponsepojo: LoginResponsepojo?) {
        if (loginResponsepojo!!.redirectTo.equals("Register", ignoreCase = true)) {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Mobile_No", "")
            intent.putExtra("UserEmail", fbEmail)
            intent.putExtra("UserName", fbFirstName)
            intent.putExtra("CountryCode", countryCode)
            intent.putExtra("LoginType", "FBK")
            intent.putExtra("GoogleToken", token)
            intent.putExtra("countryCodeISO", countryCodeISO)
            startActivity(intent)
            finish()
        } else if (loginResponsepojo.redirectTo.equals("home", ignoreCase = true)) {
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_TOKEN,
                loginResponsepojo.token
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_ISLOGIN,
                "true"
            )
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()

            //  Toast.makeText(LoginActivity.this, loginResponsepojo.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
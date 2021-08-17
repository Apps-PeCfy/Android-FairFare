package com.fairfareindia.ui.Login

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient.client
import com.fairfareindia.ui.Login.pojo.LoginResponsepojo
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.Register.RegisterActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.otp.OtpAvtivity
import com.fairfareindia.ui.privacypolicy.PrivacyPolicyActivity
import com.fairfareindia.utils.AppSignatureHelper
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
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
import kotlin.random.Random

class LoginActivity : AppCompatActivity(),
    CountryCodePicker.OnCountryChangeListener, ILoginView {
    var facebookLoginClick = 0
    var googleAccessTokem: String? = null
    var strNumberValidation: String? = "false"

    var doubleBackPressed: Boolean? = false

    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null
    var appSignatureHelper: AppSignatureHelper? = null


    @JvmField
    @BindView(R.id.iv_gmail)
    var iv_gmail: ImageView? = null
    var countryCodeISO = "IN"
    var countryCode = "91"
    var deviceID: String? = null
    var device_token: String? = null

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
    @BindView(R.id.tvPrivacy)
    var tvPrivacy: TextView? = null

    @JvmField
    @BindView(R.id.tvTerms)
    var tvTerms: TextView? = null

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
        deviceID = String.format("%08d", Random.nextInt(100000000))

      //  appSignatureHelper = AppSignatureHelper(this)
       // appSignatureHelper!!.appSignatures




        // FacebookSdk.sdkInitialize(getApplicationContext());
         hashkey()


        device_token = FirebaseMessaging.getInstance().token.toString()


        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                device_token = it.result.toString()
                // DO your thing with your firebase token
            }
        }




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
               // .requestIdToken("254736596560-cnkg23q193qpnffh7u0mpcdbdmofua28.apps.googleusercontent.com")
                .requestIdToken("637140199197-qoruvu97s5pp16apnqv9tf7gopd277r8.apps.googleusercontent.com")
                .requestServerAuthCode("637140199197-qoruvu97s5pp16apnqv9tf7gopd277r8.apps.googleusercontent.com")
             //   .requestServerAuthCode("254736596560-cnkg23q193qpnffh7u0mpcdbdmofua28.apps.googleusercontent.com")
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


    override fun onBackPressed() {
        if (doubleBackPressed!!) {
            finishAffinity()
        } else {
            doubleBackPressed = true
            Toast.makeText(
                this,
                "Press once again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
        Handler().postDelayed({ doubleBackPressed = false }, 3000)
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


    fun replaceFragment(fragment: Fragment?) {

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.container_framelayout, fragment!!)
        fragmentTransaction!!.commit()
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

        if(ProjectUtilities.checkInternetAvailable(this@LoginActivity)) {

            edit_text!!.setText("")
            ccp!!.setCountryForNameCode("in")
            signIn()
        }else{
            ProjectUtilities.showToast(this@LoginActivity,getString(R.string.internet_error))
        }
    }

    @OnTextChanged(R.id.edit_text)
    fun edttxt() {
    }

    @OnClick(R.id.btnLoginFacebook)
    fun onClickLoginWithFAcebook(view: View?) {

        if(ProjectUtilities.checkInternetAvailable(this@LoginActivity)) {

            edit_text!!.setText("")
            ccp!!.setCountryForNameCode("in")
            if (facebookLoginClick == 0) {
                facebookLoginClick = 1
                btnFBK!!.performClick()
            }
        }else{
            ProjectUtilities.showToast(this@LoginActivity,getString(R.string.internet_error))
        }


    }

    @OnClick(R.id.btnFBK)
    fun facebookButtonclick() {
        if(ProjectUtilities.checkInternetAvailable(this@LoginActivity)) {

            facebookLogin()
        }else{
            ProjectUtilities.showToast(this@LoginActivity,getString(R.string.internet_error))
        }
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


                        iLoginPresenter!!.socialLogin(
                            "Android",
                            "FBK",
                            fbFirstName,
                            facebook_uid,
                            token,
                            fbEmail,
                            deviceID,
                            device_token
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

        if(ProjectUtilities.checkInternetAvailable(this@LoginActivity)) {

            val phoneNumberUtil: PhoneNumberUtil
        var phoneNumber: PhoneNumber? = null
        var isMobile: PhoneNumberUtil.PhoneNumberType? = null
        phoneNumberUtil = PhoneNumberUtil.createInstance(this)
        try {
            phoneNumber = phoneNumberUtil.parse(
                edit_text!!.text.toString().trim { it <= ' ' },
                countryCodeISO.toUpperCase()
            )
            isMobile = phoneNumberUtil.getNumberType(phoneNumber)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        if (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile) {
            strNumberValidation = "true"
        } else if (PhoneNumberUtil.PhoneNumberType.FIXED_LINE == isMobile) {
            strNumberValidation = "false"
        } else {
            strNumberValidation = "false"
        }


        if (strNumberValidation == "false") {
            tvPhoneNumberError!!.text = "Please enter a valid phone no."
            tvPhoneNumberError!!.visibility = View.VISIBLE
            //   Toast.makeText(this, "Plese enter valid phone no.", Toast.LENGTH_LONG).show();
        } else {

            iLoginPresenter!!.login(
                edit_text!!.text.toString(), "Login", "Android",
                "NOR", countryCode, "", "", ""
            )

        }
        }else{
            ProjectUtilities.showToast(this@LoginActivity,getString(R.string.internet_error))
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
                .add("client_id", "637140199197-qoruvu97s5pp16apnqv9tf7gopd277r8.apps.googleusercontent.com")
              //  .add("client_id", "254736596560-cnkg23q193qpnffh7u0mpcdbdmofua28.apps.googleusercontent.com")
                .add("client_secret", "2jw6dz4Fmqf6ce7uvFjFrp55")
                .add("redirect_uri", "")
                .add("code", acct.serverAuthCode)
                .build()
            val request = Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
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
                        Log.d("access_token", gToken!!)
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
            mGoogleSignInClient!!.signOut().addOnCompleteListener(this) { }
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
            providerid, gToken, gmailPersonEmail, deviceID,device_token
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
                        mPreferencesManager!!.setIntegerValue(
                            Constants.SHARED_PREFERENCE_LOGIN_ID,
                            loginResponsepojo!!.user!!.id
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_NAME,
                            loginResponsepojo!!.user!!.name
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_USER_REWARD,
                            loginResponsepojo!!.user!!.rewards
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_EMAIL,
                            loginResponsepojo!!.user!!.email
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_PHONENO,
                            loginResponsepojo!!.user!!.phoneNo
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_GENDER,
                            loginResponsepojo!!.user!!.gender
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_PROFESTION,
                            loginResponsepojo!!.user!!.profession
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_DOB,
                            loginResponsepojo!!.user!!.dateOfBirth
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK,
                            loginResponsepojo!!.user!!.profilePic
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_LOCATION,
                            loginResponsepojo!!.user!!.location
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_DEVICEID,
                            deviceID
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
                } else if (response.code() == 400 || response.code() == 422) {
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
            providerid, gToken, gmailPersonEmail, deviceID,device_token
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
                        mPreferencesManager!!.setIntegerValue(
                            Constants.SHARED_PREFERENCE_LOGIN_ID,
                            loginResponsepojo!!.user!!.id
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_NAME,
                            loginResponsepojo!!.user!!.name
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_USER_REWARD,
                            loginResponsepojo!!.user!!.rewards
                        )

                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_EMAIL,
                            loginResponsepojo!!.user!!.email
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_PHONENO,
                            loginResponsepojo!!.user!!.phoneNo
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_GENDER,
                            loginResponsepojo!!.user!!.gender
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_PROFESTION,
                            loginResponsepojo!!.user!!.profession
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_DOB,
                            loginResponsepojo!!.user!!.dateOfBirth
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK,
                            loginResponsepojo!!.user!!.profilePic
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_LOCATION,
                            loginResponsepojo!!.user!!.location
                        )
                        mPreferencesManager!!.setStringValue(
                            Constants.SHARED_PREFERENCE_LOGIN_DEVICEID,
                            deviceID
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
                } else if (response.code() == 400 || response.code() == 422) {
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
        ProjectUtilities.showProgressDialog(this@LoginActivity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
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
            intent.putExtra("register_Latitude", "")
            intent.putExtra("register_Longitude", "")

            startActivity(intent)
            finish()
        }
    }

    override fun validationError(pojo: ValidationResponse?) {


        Toast.makeText(
            this@LoginActivity,
            pojo!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()


    }

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
            mPreferencesManager!!.setIntegerValue(
                Constants.SHARED_PREFERENCE_LOGIN_ID,
                loginResponsepojo!!.user!!.id
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_NAME,
                loginResponsepojo!!.user!!.name
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_USER_REWARD,
                loginResponsepojo!!.user!!.rewards
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_EMAIL,
                loginResponsepojo!!.user!!.email
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_PHONENO,
                loginResponsepojo!!.user!!.phoneNo
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_GENDER,
                loginResponsepojo!!.user!!.gender
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_PROFESTION,
                loginResponsepojo!!.user!!.profession
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_DOB,
                loginResponsepojo!!.user!!.dateOfBirth
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_PROFILEPICK,
                loginResponsepojo!!.user!!.profilePic
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_LOCATION,
                loginResponsepojo!!.user!!.location
            )
            mPreferencesManager!!.setStringValue(
                Constants.SHARED_PREFERENCE_LOGIN_DEVICEID,
                deviceID
            )
            mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_ISLOGIN, "true")


            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()

            //  Toast.makeText(LoginActivity.this, loginResponsepojo.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
package com.fairfareindia.ui.privacypolicy

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.privacypolicy.ContentResponsePOJO
import com.fairfareindia.utils.PreferencesManager
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PrivacyPolicyActivity : AppCompatActivity() {


    var preferencesManager: PreferencesManager? = null
    var token: String? = null
    var contentPadeText: String? = null
    @JvmField
    @BindView(R.id.webView)
    var webView: WebView? = null

    @JvmField
    @BindView(R.id.toolbar_viewRide)
    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)

        contentPadeText = intent.getStringExtra("ContentPageData")

        mToolbar!!.title = contentPadeText
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

        if(contentPadeText.equals("Privacy Policy")){

            callApi("Privacy-Policy")

        }else{
            callApi("Terms-Of-Use")

        }


    }

    private fun callApi(contentData: String) {

        val progressDialog = ProgressDialog(this@PrivacyPolicyActivity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog


        val call = ApiClient.client.pageContents( contentData)
        call!!.enqueue(object : Callback<ContentResponsePOJO?> {
            override fun onResponse(
                call: Call<ContentResponsePOJO?>,
                response: Response<ContentResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        webView!!.settings.javaScriptEnabled = true
                        webView!!.loadDataWithBaseURL(
                            null,
                            response.body()!!.pageContent!!.content!!,
                            "text/html",
                            "utf-8",
                            null
                        )

                    }
                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(this@PrivacyPolicyActivity, pojo.message, Toast.LENGTH_LONG).show()


                    } catch (exception: IOException) {
                    }

                }
            }

            override fun onFailure(
                call: Call<ContentResponsePOJO?>, t: Throwable
            ) {
                progressDialog.dismiss()
                Toast.makeText(this@PrivacyPolicyActivity, t.stackTrace.toString(), Toast.LENGTH_LONG).show()
                Log.d("response", t.stackTrace.toString())
            }
        })
    }


    private fun setStatusBarGradiant(activity: PrivacyPolicyActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }
}

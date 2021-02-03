package com.example.fairfare.ui.drawer.ratecard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.example.fairfare.R
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.ratecard.pojo.RateCardResponsePOJO
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.ui.home.pojo.GetAllowCityResponse
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class RateCard : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    var activityIsRunning = false
    var selectedPosition = 0
    var itemSelectedPosition = 0


    var check = 0


    var cityPojoList: List<GetAllowCityResponse.CitiesItem> = ArrayList()
    var getRateCardList: List<RateCardResponsePOJO.RateCardsItem> = ArrayList()

    var TabCalled: String? = "1"


    @JvmField
    @BindView(R.id.viewPager)
    var viewPager: ViewPager? = null

    @JvmField
    @BindView(R.id.llRateCard)
    var llRateCard: LinearLayout? = null


    @JvmField
    @BindView(R.id.tvCarName)
    var tvCarName: TextView? = null

    @JvmField
    @BindView(R.id.tvNightCharges)
    var tvNightCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvWaitingCharges)
    var tvWaitingCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvSurCHarges)
    var tvSurCHarges: TextView? = null

    @JvmField
    @BindView(R.id.tvLuggage)
    var tvLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tvFare)
    var tvFare: TextView? = null

    @JvmField
    @BindView(R.id.tvNightChargeTime)
    var tvNightChargeTime: TextView? = null


    @JvmField
    @BindView(R.id.spinner_type_rate_card)
    var spinner_type_rate_card: Spinner? = null


    @JvmField
    @BindView(R.id.spinner_city)
    var spinner_city: Spinner? = null


    @JvmField
    @BindView(R.id.myDynamicLayout)
    var myDynamicLayout: RelativeLayout? = null

    @JvmField
    @BindView(R.id.nCharge)
    var nCharge: TextView? = null

    @JvmField
    @BindView(R.id.wCharge)
    var wCharge: TextView? = null

    @JvmField
    @BindView(R.id.sCharge)
    var sCharge: TextView? = null

    @JvmField
    @BindView(R.id.sFare)
    var sFare: TextView? = null

    @JvmField
    @BindView(R.id.cLuggage)
    var cLuggage: TextView? = null

    var mRgAllButtons: RadioGroup? = null


    @JvmField
    @BindView(R.id.recycler_view_rateCard)
    var recycler_view_rateCard: RecyclerView? = null


    var spinnr: List<String?>? = null

    var cityspinner = ArrayList<String>()

    @JvmField
    @BindView(R.id.tabs)
    var tabLayout: TabLayout? = null

    var token: String? = null

    var citycalles: String? = "first"
    var cityID: String? = null
    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_rate_card, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        return rootView
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_home_lang, menu!!)
        super.onCreateOptionsMenu(menu!!, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "LOCALITY"
                )
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        return true
    }


    private fun initView() {
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "Rate Card"




        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        cityID = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CITY_ID)

        getCity()


    }

    private fun getCity() {


        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog


        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Accept"] = "application/json"
        headers["Authorization"] = "Bearer $token"


        ApiClient.client.getAllowCities(headers)!!.enqueue(object :
            Callback<GetAllowCityResponse?> {
            override fun onResponse(
                call: Call<GetAllowCityResponse?>,
                response: Response<GetAllowCityResponse?>
            ) {

                progressDialog.dismiss()
                if (response.code() == 200) {

                    cityPojoList = response!!.body()!!.cities
                    cityspinner.clear()
                    for (i in response.body()!!.cities.indices) {
                        cityspinner.add(response!!.body()!!.cities.get(i).name)

                    }


                    val cityspinneradapter: ArrayAdapter<*> =
                        ArrayAdapter<Any?>(
                            context!!,
                            android.R.layout.simple_spinner_dropdown_item,
                            cityspinner!! as List<Any?>
                        )
                    cityspinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner_city!!.adapter = cityspinneradapter

                    for (i in cityspinner!!.indices) {
                        val c = cityPojoList[i].id
                        if ((cityID!!.toInt()).equals(c)) {
                            spinner_city!!.setSelection(i)
                        }

                    }


                    spinner_city!!.onItemSelectedListener = this@RateCard

                    citycalles = "first"



                    getRateCard(cityID)


                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(activity, pojo.message, Toast.LENGTH_LONG).show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        activity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<GetAllowCityResponse?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })

    }


    fun getRateCard(cityId: String?) {

/*
          val progressDialog = ProgressDialog(activity)
          progressDialog.setCancelable(false) // set cancelable to false
          progressDialog.setMessage("Please Wait") // set message
          progressDialog.show() // show progress dialog
*/

        ApiClient.client.rateCards("Bearer $token", cityId)!!.enqueue(object :
            Callback<RateCardResponsePOJO?> {
            override fun onResponse(
                call: Call<RateCardResponsePOJO?>,
                response: Response<RateCardResponsePOJO?>
            ) {
                // progressDialog.dismiss()
                if (response.code() == 200) {

                    llRateCard!!.visibility = View.VISIBLE
                    getRateCardList = response.body()!!.rateCards!!
                    if (getRateCardList.size > 0) {


                        tabLayout!!.removeAllTabs()
                        for (j in getRateCardList.indices) {
                            tabLayout!!.addTab(
                                tabLayout!!.newTab()
                                    .setCustomView(createTabItemView(getRateCardList[j].image!!))
                            )
                            tvCarName!!.text = getRateCardList[0].name


                            tvFare!!.text =
                                "₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).minBaseFare +
                                        " for first 1.50 km and thereafter ₹ " +
                                        getRateCardList[0].rateCards!!.get(0).rateCards!!.get(0).fareAfterMinbdist +
                                        " for every additional km."



                            tvNightCharges!!.text =
                                getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).nightCharges + "%" + " of the Total Fare"

                            tvWaitingCharges!!.text =
                                "₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).waitingCharges + " per minute "

                            if ((getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).surcharge).equals(
                                    "0"
                                )
                            ) {
                                tvSurCHarges!!.text = "Surcharge Not Applicable"
                            } else {
                                tvSurCHarges!!.text =
                                    "₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).surcharge + " per booking"

                            }





                            tvLuggage!!.text =
                                "₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).chargesPerLuggage +
                                        " shall be charged as extra luggage charges. The Driver / Operator shall not apply any Luggage charges for shopping bags and small suitcases."

                            tvNightChargeTime!!.text = "(00:00 AM to 5:00 AM)"
                            nCharge!!.text = "Night Charges"
                            wCharge!!.text = "Waiting Charges"
                            sCharge!!.text = "Surcharges"
                            cLuggage!!.text = "Luggage"
                            sFare!!.text = "Fare"


                        }








                        spinnr = java.util.ArrayList()
                        for (i in (getRateCardList.get(0).rateCards)!!.indices) {
                            (spinnr as ArrayList<String?>).add(
                                getRateCardList[0]!!.rateCards!!.get(i).name
                            )

                        }



                        mRgAllButtons = activity!!.findViewById(R.id.radiogroup)

                        mRgAllButtons!!.removeAllViews()

                        mRgAllButtons!!.setOrientation(LinearLayout.VERTICAL)
                        for (i in (getRateCardList.get(0).rateCards)!!.get(0).rateCards!!.indices) {
                            val rdbtn = RadioButton(activity)
                            rdbtn.id = View.generateViewId()
                            rdbtn.setOnClickListener(this@RateCard)
                            if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(i).rateCardType)!!.contains("DOMS")) {
                                val imgResource: Int = R.drawable.domestic_icon
                                rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                                rdbtn.setCompoundDrawablePadding(100)
                            }


                            if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(i).rateCardType)!!.contains("INTL")) {
                                val imgResource: Int = R.drawable.international_icon
                                rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                                rdbtn.setCompoundDrawablePadding(130)
                            }
                            rdbtn.text =
                                (getRateCardList.get(0).rateCards!!.get(0).rateCards!!.get(i).rateCardType) + ""
                            mRgAllButtons!!.addView(rdbtn)

                            if (i == 0) {
                                mRgAllButtons!!.check(rdbtn.id)
                            }
                        }


                        spinner_type_rate_card!!.visibility = View.VISIBLE
                        spinner_city!!.visibility = View.VISIBLE

                        val auto: ArrayAdapter<*> = ArrayAdapter<Any?>(
                            context!!,
                            android.R.layout.simple_spinner_dropdown_item,
                            spinnr!!
                        )
                        auto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_type_rate_card!!.adapter = auto


                        //  viewPager!!.adapter = adapter
                        viewPager!!.addOnPageChangeListener(
                            TabLayout.TabLayoutOnPageChangeListener(
                                tabLayout
                            )
                        )
                        tabLayout!!.addOnTabSelectedListener(object :
                            TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {

                                selectedPosition = tab.position

                                tvCarName!!.text = getRateCardList[tab.position].name


                                spinnr = java.util.ArrayList()
                                for (i in (getRateCardList.get(tab.position).rateCards)!!.indices) {
                                    (spinnr as ArrayList<String?>).add(
                                        getRateCardList[tab.position]!!.rateCards!!.get(
                                            i
                                        ).name
                                    )

                                }


                                val auto: ArrayAdapter<*> = ArrayAdapter<Any?>(
                                    context!!,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    spinnr!!
                                )
                                auto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinner_type_rate_card!!.adapter = auto
                                spinner_type_rate_card!!.onItemSelectedListener = this@RateCard


                                //
                                // viewPager!!.currentItem = tab.position
                                // adapter.notifyDataSetChanged()
                                // adapter.setSelectedPositio(selectedPosition)

                            }


                            override fun onTabUnselected(tab: TabLayout.Tab) {}
                            override fun onTabReselected(tab: TabLayout.Tab) {}
                        })

                    }


                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(activity, pojo.errors!!.get(0).message, Toast.LENGTH_LONG)
                            .show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        activity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<RateCardResponsePOJO?>,
                t: Throwable
            ) {
                //  progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })
    }


    private fun createTabItemView(imgUri: String): View? {
        val imageView = ImageView(activity)
        val params = FrameLayout.LayoutParams(
            180,
            120
        )
        imageView.setLayoutParams(params)
        Picasso.get().load(imgUri).into(imageView)
        return imageView
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!!.id == R.id.spinner_city) {


            if (citycalles.equals("second")) {

                citycalles = "second"
                cityID = (cityPojoList.get(position).id).toString()

                getCity()
            } else {
                citycalles = "second"
            }

        } else {

            itemSelectedPosition = position
            mRgAllButtons = activity!!.findViewById(R.id.radiogroup)

            mRgAllButtons!!.removeAllViews()
            mRgAllButtons!!.setOrientation(LinearLayout.VERTICAL)
            for (i in (getRateCardList.get(selectedPosition).rateCards)!!.get(position).rateCards!!.indices) {
                val rdbtn = RadioButton(activity)
                rdbtn.id = View.generateViewId()

                rdbtn.setOnClickListener(this)
                if ((getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(i).rateCardType)!!.contains("DOMS")) {
                    val imgResource: Int = R.drawable.domestic_icon
                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                    rdbtn.setCompoundDrawablePadding(100)
                }


                if ((getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(i).rateCardType)!!.contains("INTL")) {
                    val imgResource: Int = R.drawable.international_icon
                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                    rdbtn.setCompoundDrawablePadding(130)
                }
                rdbtn.text =
                    (getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(i).rateCardType)
                mRgAllButtons!!.addView(rdbtn)

                if (i == 0) {
                    mRgAllButtons!!.check(rdbtn.id)
                }
            }


            tvCarName!!.text = getRateCardList[selectedPosition].rateCards!!.get(position).name


            tvFare!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                    position
                ).minBaseFare +
                        " for first 1.50 km and thereafter ₹ " +
                        getRateCardList[selectedPosition].rateCards!!.get(position).rateCards!!.get(
                            position
                        ).fareAfterMinbdist +
                        " for every additional km."



            tvNightCharges!!.text =
                getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                    position
                ).nightCharges + "%" + " of the Total Fare"

            tvWaitingCharges!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                    position
                ).waitingCharges + " per minute "







            if ((getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(0).surcharge).equals(
                    "0"
                )
            ) {
                tvSurCHarges!!.text = "Surcharge Not Applicable"

            } else {
                tvSurCHarges!!.text =
                    "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                        0
                    ).surcharge + " per booking"

            }



            tvLuggage!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                    position
                ).chargesPerLuggage +
                        " shall be charged as extra luggage charges. The Driver / Operator shall not apply any Luggage charges for shopping bags and small suitcases."


        }

    }

    override fun onClick(v: View?) {

        val position: Int = mRgAllButtons!!.indexOfChild(v)



        if (position == 0) {


            tvFare!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).minBaseFare +
                        " for first 1.50 km and thereafter ₹ " +
                        getRateCardList[selectedPosition].rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                            position
                        ).fareAfterMinbdist +
                        " for every additional km."


            tvNightCharges!!.text =
                getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).nightCharges + "%" + " of the Total Fare"

            tvWaitingCharges!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).waitingCharges + " per minute "


            if ((getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).surcharge).equals("0")
            ) {
                tvSurCHarges!!.text = "Surcharge Not Applicable"

            } else {
                tvSurCHarges!!.text =
                    "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                        position
                    ).surcharge +
                            " per booking"

            }

            tvLuggage!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).chargesPerLuggage +
                        " shall be charged as extra luggage charges. The Driver / Operator shall not apply any Luggage charges for shopping bags and small suitcases."


        } else {
            tvFare!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).minBaseFare +
                        " for minimum base distance of " + getRateCardList[selectedPosition].rateCards!!.get(
                    itemSelectedPosition
                ).rateCards!!.get(
                    position
                ).minBaseDistance + " km and thereafter ₹ " +
                        getRateCardList[selectedPosition].rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                            position
                        ).fareAfterMinbdist +
                        " for every distance slab of " + getRateCardList[selectedPosition].rateCards!!.get(
                    itemSelectedPosition
                ).rateCards!!.get(
                    position
                ).minBaseDistance + " km"


            tvNightCharges!!.text =
                getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).nightCharges + "%" + " of the Total Fare"

            tvWaitingCharges!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).waitingCharges + " per minute "


            if ((getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).surcharge).equals("0")
            ) {
                tvSurCHarges!!.text = "Surcharge Not Applicable"

            } else {
                tvSurCHarges!!.text =
                    "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                        position
                    ).surcharge +
                            " per booking"

            }

            tvLuggage!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).chargesPerLuggage +
                        " shall be charged as extra luggage charges. The Driver / Operator shall not apply any Luggage charges for shopping bags and small suitcases."

        }


    }


}


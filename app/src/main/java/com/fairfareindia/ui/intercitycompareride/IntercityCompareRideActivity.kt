package com.fairfareindia.ui.intercitycompareride

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityCompareRideBinding
import com.fairfareindia.ui.compareride.CompareRideAdapter
import com.fairfareindia.ui.compareride.ICompareRidePresenter
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.intercityviewride.IntercityViewRideActivity
import com.fairfareindia.ui.viewride.ViewRideActivity
import com.fairfareindia.utils.AppUtils
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.android.libraries.places.api.Places
import java.util.ArrayList

class IntercityCompareRideActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntercityCompareRideBinding
    private var context: Context = this

    private var mAdapter: IntercityCompareRideAdapter? = null
    private lateinit var info: CompareRideResponsePOJO
    private var list : ArrayList<CompareRideResponsePOJO.VehiclesItem> = ArrayList()

    private var token: String? = null
    private var preferencesManager: PreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntercityCompareRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        Places.initialize(this, resources.getString(R.string.google_maps_key))
        PreferencesManager.initializeInstance(context)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        info = intent.getSerializableExtra("MyPOJOClass") as CompareRideResponsePOJO

        setData()
        setRecyclerView()
        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbarHome.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setData() {
        binding.apply {
           txtTime.text = AppUtils.changeDateFormat(info.scheduleDatetime, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy, hh:mm a")
           txtLuggage.text = info.luggage + " Bags"
        }
    }

    private fun setRecyclerView() {
        if (info.vehicles?.isNotEmpty()!!) {
            list.addAll(info.vehicles!!)

        }
        mAdapter = IntercityCompareRideAdapter(context, info.distance, info.travelTime, list, object : IntercityCompareRideAdapter.IntercityCompareRideAdapterInterface{
            override fun onItemSelected(position: Int, model: CompareRideResponsePOJO.VehiclesItem) {
                val intent = Intent(applicationContext, IntercityViewRideActivity::class.java)
                startActivity(intent)
            }

        })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mAdapter
    }
}
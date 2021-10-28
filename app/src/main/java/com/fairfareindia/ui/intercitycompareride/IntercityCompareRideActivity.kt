package com.fairfareindia.ui.intercitycompareride

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityIntercityCompareRideBinding
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.ui.intercityviewride.IntercityViewRideActivity
import com.fairfareindia.utils.AppUtils
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import java.util.*

class IntercityCompareRideActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntercityCompareRideBinding
    private var context: Context = this

    private var mAdapter: IntercityCompareRideAdapter? = null
    private lateinit var info: CompareRideResponsePOJO
    private var list: ArrayList<CompareRideResponsePOJO.VehiclesItem> = ArrayList()

    var sourceAddress: String? = null
    var destinationAddress: String? = null
    var sourceLat: String? = null
    var sourceLong: String? = null
    var destinationLat: String? = null
    var destinationLong: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntercityCompareRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        Places.initialize(this, resources.getString(R.string.google_maps_key))


        info = intent.getSerializableExtra("MyPOJOClass") as CompareRideResponsePOJO
        sourceAddress = intent.getStringExtra("SourceAddress")
        destinationAddress = intent.getStringExtra("DestinationAddress")
        sourceLat = intent.getStringExtra("SourceLat")
        sourceLong = intent.getStringExtra("SourceLong")
        destinationLat = intent.getStringExtra("DestinationLat")
        destinationLong = intent.getStringExtra("DestinationLong")


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
            txtTime.text = AppUtils.changeDateFormat(
                info.scheduleDatetime,
                "yyyy-MM-dd HH:mm:ss",
                "dd MMM yyyy, hh:mm a"
            )
            if (info.luggage == "0") {
                txtLuggage.text = getString(R.string.str_no_bags)
            } else if (info.luggage == "1") {
                txtLuggage.text = info.luggage + " " + getString(R.string.str_bag)
            } else {
                txtLuggage.text = info.luggage + " " + getString(R.string.str_bags)
            }

            txtPickUpLocation.text = sourceAddress
            txtDropOffLocation.text = destinationAddress
        }
    }

    private fun setRecyclerView() {
        if (info.vehicles?.isNotEmpty()!!) {
            list.addAll(info.vehicles!!)

        }
        mAdapter = IntercityCompareRideAdapter(
            context,
            info.distance,
            info.travelTime,
            list,
            object : IntercityCompareRideAdapter.IntercityCompareRideAdapterInterface {
                override fun onItemSelected(
                    position: Int,
                    model: CompareRideResponsePOJO.VehiclesItem
                ) {
                    val intent = Intent(applicationContext, IntercityViewRideActivity::class.java)
                    intent.putExtra("SourceAddress", sourceAddress)
                    intent.putExtra("DestinationAddress", destinationAddress)
                    intent.putExtra("SourceLat", sourceLat)
                    intent.putExtra("SourceLong", sourceLong)
                    intent.putExtra("DestinationLat", destinationLat)
                    intent.putExtra("DestinationLong", destinationLong)
                    intent.putExtra("vehicle_model", Gson().toJson(model))
                    intent.putExtra("MyPOJOClass", info)

                    startActivity(intent)
                }

            })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mAdapter
    }
}
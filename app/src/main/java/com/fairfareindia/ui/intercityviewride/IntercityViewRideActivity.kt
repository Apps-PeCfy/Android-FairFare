package com.fairfareindia.ui.intercityviewride

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fairfareindia.databinding.ActivityIntercityViewRideBinding

class IntercityViewRideActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntercityViewRideBinding
    private var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntercityViewRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }
}
package com.fairfareindia.ui.intercity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fairfareindia.databinding.ActivityInterCityBinding

class InterCityActivity : AppCompatActivity() {
    lateinit var binding: ActivityInterCityBinding
    private var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        setSupportActionBar(binding.toolbarHome)

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbarHome.setNavigationOnClickListener { onBackPressed() }
        }
    }
}
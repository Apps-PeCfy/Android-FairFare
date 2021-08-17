package com.fairfareindia.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.fairfareindia.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

class GooglePlace : AppCompatActivity() {
    var edit_txt: EditText? = null
    var placesClient: PlacesClient? = null
    var autocompleteFragment: AutocompleteSupportFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_place)
        edit_txt = findViewById<View>(R.id.edit_txt) as EditText
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyDTtO6dht-M6tX4uL28f8HTLwIQrT_ivUU"
            )
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this)
        edit_txt!!.isFocusable = false
        edit_txt!!.setOnClickListener {
            val fields =
                Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.OPENING_HOURS)
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this@GooglePlace)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place =
                    Autocomplete.getPlaceFromIntent(data!!)
                edit_txt!!.setText(place.address)
                Log.i(
                    "PlaceData", "Place: " + place.name + ",      "
                            + place.id + "    " + place.latLng + "   "
                            + place.openingHours + "    " + place.address
                )
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status =
                    Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return
        }
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}
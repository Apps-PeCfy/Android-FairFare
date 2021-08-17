package com.fairfareindia.ui.home

import android.content.Context
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient.client
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.home.PlacesAutoCompleteAdapter.PredictionHolder
import com.fairfareindia.ui.home.pojo.SaveLocationResponsePojo
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PlacesAutoCompleteAdapter(private val mContext: Context) :
    RecyclerView.Adapter<PredictionHolder>(), Filterable {
    private var mResultList: ArrayList<PlaceAutocomplete>? =
        ArrayList()
    var token: String? = null
    var preferencesManager: PreferencesManager
    private val STYLE_BOLD: CharacterStyle
    private val STYLE_NORMAL: CharacterStyle
    private val placesClient: PlacesClient
    private var clickListener: ClickListener? = null
    fun setClickListener(clickListener: ClickListener?) {
        this.clickListener = clickListener
    }

    var favClick: String? = "first"

    interface ClickListener {
        fun click(place: Place?, address: String)
        fun favClick(place: Place?)
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint)
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList
                        results.count = mResultList!!.size
                    }
                }
                return results
            }

            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        }
    }

    private fun getPredictions(constraint: CharSequence): ArrayList<PlaceAutocomplete> {
        val resultList =
            ArrayList<PlaceAutocomplete>()

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()
        val northEast = LatLng(19.076090, 72.877426)
        val southwest = LatLng(19.2183, 72.9781)


        //https://gist.github.com/graydon/11198540
        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request =
            FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setTypeFilter(TypeFilter.CITIES)
                //   .setLocationRestriction(RectangularBounds.newInstance(northEast,northEast))
                // .setLocationBias(RectangularBounds.newInstance(northEast,southwest))
                .setSessionToken(token)
                //  .setCountry("IN")

                .setQuery(constraint.toString())
                .build()
        val autocompletePredictions =
            placesClient.findAutocompletePredictions(request)

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(
                autocompletePredictions,
                60,
                TimeUnit.SECONDS
            )
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        return if (autocompletePredictions.isSuccessful) {
            val findAutocompletePredictionsResponse =
                autocompletePredictions.result
            if (findAutocompletePredictionsResponse != null) for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                Log.i(TAG, prediction.placeId)
                resultList.add(
                    PlaceAutocomplete(
                        prediction.placeId,
                        prediction.getPrimaryText(STYLE_NORMAL).toString(),
                        prediction.getFullText(STYLE_BOLD).toString()
                    )
                )
            }
            resultList
        } else {
            resultList
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PredictionHolder {
        val layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView =
            layoutInflater.inflate(R.layout.place_recycler_item_layout, viewGroup, false)
        return PredictionHolder(convertView)
    }

    override fun onBindViewHolder(mPredictionHolder: PredictionHolder, i: Int) {
        mPredictionHolder.address.text = mResultList!![i].address

        mPredictionHolder.area.visibility = View.GONE
        mPredictionHolder.ivEdit.visibility = View.GONE
        mPredictionHolder.area.text = mResultList!![i].area
    }

    override fun getItemCount(): Int {
        return mResultList!!.size
    }

    fun getItem(position: Int): PlaceAutocomplete {
        return mResultList!![position]
    }

    inner class PredictionHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val address: TextView
        val ivEdit: RelativeLayout
        val area: TextView
        private val mRoW: RelativeLayout
        private val iv_fav: ImageView
        override fun onClick(v: View) {
            val item = mResultList!![adapterPosition]
            if (v.id == R.id.place_item) {
                val placeId = item.placeId.toString()
                val placeFields =
                    Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS,
                        Place.Field.TYPES
                    )
                val request =
                    FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(request)
                    .addOnSuccessListener { response ->
                        val place =
                            response.place
                        var returnedAddress: Address? = null
                        var addressesRecent: List<Address?>? = null

                        val geocoder =
                            Geocoder(mContext, Locale.getDefault())
                        try {
                            addressesRecent =
                                geocoder.getFromLocation(
                                    place.latLng!!.latitude,
                                    place.latLng!!.longitude, 5
                                )

                        } catch (e: IOException) {
                        }
                        val token = preferencesManager.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
                        if (addressesRecent != null && addressesRecent.size >0){
                            client.SaveRecentLocation(
                                "Bearer $token", place.id,
                                addressesRecent?.get(0)!!.subAdminArea, addressesRecent?.get(0)!!.adminArea,
                                addressesRecent?.get(0)!!.countryName, item.address.toString()
                            )!!.enqueue(object : Callback<SaveLocationResponsePojo?> {
                                override fun onResponse(
                                    call: Call<SaveLocationResponsePojo?>,
                                    response: Response<SaveLocationResponsePojo?>
                                ) {
                                    // Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }

                                override fun onFailure(
                                    call: Call<SaveLocationResponsePojo?>,
                                    t: Throwable
                                ) {
                                    Log.d("response", t.stackTrace.toString())
                                }
                            })
                        }
                        clickListener!!.click(place, mResultList!![adapterPosition].address.toString())

                    }.addOnFailureListener { exception ->
                        if (exception is ApiException) {
                            Toast.makeText(mContext, exception.message + "", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            }

            if (v.id == R.id.iv_fav) {
                if (favClick.equals("first")) {
                    favClick ="second"

                    val placeId = item.placeId.toString()
                    val placeFields =
                        Arrays.asList(
                            Place.Field.ID,
                            Place.Field.NAME,
                            Place.Field.LAT_LNG,
                            Place.Field.ADDRESS
                        )
                    val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                    placesClient.fetchPlace(request)
                        .addOnSuccessListener { response ->
                            var returnedAddress: Address? = null
                            var addresses: List<Address?>? = null

                            val place = response.place
                            val geocoder =
                                Geocoder(mContext, Locale.getDefault())
                            try {
                                addresses =
                                    geocoder.getFromLocation(
                                        place.latLng!!.latitude,
                                        place.latLng!!.longitude, 1
                                    )

                            } catch (e: IOException) {
                            }
                            val token =
                                preferencesManager.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
                            client.SaveLocation(
                                "Bearer $token",
                                place.id,
                                addresses?.get(0)?.subAdminArea,
                                addresses?.get(0)?.adminArea,
                                item.address.toString(),
                                addresses?.get(0)?.countryName





                            )!!.enqueue(object : Callback<SaveLocationResponsePojo?> {
                                override fun onResponse(
                                    call: Call<SaveLocationResponsePojo?>,
                                    response: Response<SaveLocationResponsePojo?>
                                ) {
                                    if (response.code() == 200) {
                                        favClick = "first"
                                        iv_fav.setBackgroundResource(R.drawable.ic_fav_checked)
                                        Toast.makeText(
                                            mContext,
                                            "Location saved successfully !!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }else if (response.code() == 422||response.code() ==400 ) {
                                        val gson = GsonBuilder().create()
                                        var pojo: ValidationResponse? = ValidationResponse()
                                        try {
                                            pojo = gson.fromJson(
                                                response.errorBody()!!.string(),
                                                ValidationResponse::class.java
                                            )
                                            Toast.makeText(mContext, pojo.message, Toast.LENGTH_LONG).show()


                                        } catch (exception: IOException) {
                                        }

                                    } else {
                                        Toast.makeText(
                                            mContext,
                                            "Internal server error",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<SaveLocationResponsePojo?>,
                                    t: Throwable
                                ) {
                                    Log.d("response", t.stackTrace.toString())
                                }
                            })
                        }.addOnFailureListener { exception ->
                            if (exception is ApiException) {
                                Toast.makeText(mContext, exception.message + "", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }


                }
            }
        }

        init {
            area = itemView.findViewById(R.id.place_area)
            address = itemView.findViewById(R.id.place_address)
            ivEdit = itemView.findViewById(R.id.ivEdit)
            mRoW = itemView.findViewById(R.id.place_item)
            iv_fav = itemView.findViewById(R.id.iv_fav)
            itemView.setOnClickListener(this)
            iv_fav.setOnClickListener(this)
            mRoW.setOnClickListener(this)
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    inner class PlaceAutocomplete internal constructor(
        var placeId: CharSequence,
        var area: CharSequence,
        var address: CharSequence

    ) {
        override fun toString(): String {
            return area.toString()
        }

    }

    companion object {
        private const val TAG = "PlacesAutoAdapter"
    }

    init {
        STYLE_BOLD = StyleSpan(Typeface.BOLD)
        STYLE_NORMAL = StyleSpan(Typeface.NORMAL)
        placesClient = Places.createClient(mContext)
        PreferencesManager.initializeInstance(mContext)
        preferencesManager = PreferencesManager.instance!!
    }
}
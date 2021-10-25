package com.fairfareindia.ui.intercity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fairfareindia.R
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse

class CitySelectionDialog(context: Context?) : Dialog(context!!) {
    var title: String? = null
    var mAdapter: CitySelectionAdapter? = null
    var mList: ArrayList<GetAllowCityResponse.CitiesItem>? = null
    var initialList: ArrayList<GetAllowCityResponse.CitiesItem>? = null
    var mListener: SelectionDialogInterface? = null
    private var rlSearch: RelativeLayout? = null
    private var edtSearch: EditText? = null
    private var recyclerView: RecyclerView? = null
    private var txtTitle: TextView? = null

    open interface SelectionDialogInterface {
        fun onItemSelected(model: GetAllowCityResponse.CitiesItem?)
    }

    constructor(context: Context?, title: String?, list: ArrayList<GetAllowCityResponse.CitiesItem>?, listener: SelectionDialogInterface?) : this(context) {
        this.title = title
        mList = list
        mListener = listener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_city_selection)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.BOTTOM)
        init()
    }

    private fun init() {
        initialList = mList
        rlSearch = findViewById(R.id.rl_search)
        edtSearch = findViewById(R.id.edt_search)
        txtTitle = findViewById(R.id.txt_title)
        recyclerView = findViewById(R.id.recycler_view)

        //Method calling
        setListener()
        setUpRecyclerView()
        setData()
    }

    private fun setListener() {
        edtSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (charSequence.length > 1) {
                    val sortedList: ArrayList<GetAllowCityResponse.CitiesItem> = ArrayList<GetAllowCityResponse.CitiesItem>();
                    for (model in initialList!!) {
                        if (model.name != null && model.name?.toUpperCase()
                                ?.contains(charSequence.toString().toUpperCase())!!
                        ) {
                            sortedList.add(model)
                        }
                    }
                    mList = sortedList
                    mAdapter?.updateAdapter(mList)
                } else {
                    mList = initialList
                    mAdapter?.updateAdapter(mList)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        /* imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/
    }

    /**
     * FUNCTIONS
     */
    private fun setData() {
        if (title != null) {
            txtTitle?.text = title
        }
        if (mList!!.size > 6) {
            rlSearch?.visibility = View.VISIBLE
        } else {
            rlSearch?.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {
        mAdapter = CitySelectionAdapter(context, mList, object :
            CitySelectionAdapter.SelectionAdapterInterface {
            override fun onItemSelected(model: GetAllowCityResponse.CitiesItem?) {
                mListener?.onItemSelected(model)
            }
        })
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = mAdapter
    }

}
package com.fairfareindia.ui.drawer.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.fairfareindia.R

class Review : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_review, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        return rootView
    }


    private fun initView() {
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "Fair Fare Review"
    }
}
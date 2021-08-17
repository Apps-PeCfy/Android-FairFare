package com.fairfareindia.ui.home

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fairfareindia.R

class TextItemViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val textView: TextView
    fun bind(text: String?) {
        textView.text = text
    }

    init {
        textView = itemView.findViewById<View>(R.id.list_item) as TextView
    }
}
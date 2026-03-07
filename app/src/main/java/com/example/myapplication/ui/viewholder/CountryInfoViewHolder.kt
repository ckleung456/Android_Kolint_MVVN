package com.example.myapplication.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.CountryInfoItem

open class CountryInfoViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun onBind(itemInfo: CountryInfoItem) {}
}
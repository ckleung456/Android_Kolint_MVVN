package com.example.myapplication.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.domain.CountryItem

open class CountryItemViewHolder(
    itemView: View
): RecyclerView.ViewHolder(itemView) {
    open fun onBind(data: CountryItem) { }
}
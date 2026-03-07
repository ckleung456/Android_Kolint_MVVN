package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemImageBinding
import com.example.myapplication.databinding.ItemTextBinding
import com.example.myapplication.model.CountryInfoItem
import com.example.myapplication.ui.utils.CountryInfoDiffCallback
import com.example.myapplication.ui.viewholder.CountryInfoDetailViewHolder
import com.example.myapplication.ui.viewholder.CountryInfoFlagViewHolder
import com.example.myapplication.ui.viewholder.CountryInfoViewHolder

class CountryInfoAdapter : RecyclerView.Adapter<CountryInfoViewHolder>() {
    var items = listOf<CountryInfoItem>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(
                CountryInfoDiffCallback(
                    oldList = field,
                    newList = value
                )
            )
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryInfoViewHolder =
        LayoutInflater.from(parent.context).let {
            when (viewType) {
                R.layout.item_image -> CountryInfoFlagViewHolder(
                    binding = ItemImageBinding.inflate(it, parent, false)
                )
                else -> CountryInfoDetailViewHolder(
                    binding = ItemTextBinding.inflate(it, parent, false)
                )
            }
        }

    override fun onBindViewHolder(holder: CountryInfoViewHolder, position: Int) =
        holder.onBind(itemInfo = items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].itemType.type
}
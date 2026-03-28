package com.example.myapplication.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.model.domain.CountryInfoItem

class CountryInfoDiffCallback constructor(
    private val oldList: List<CountryInfoItem>,
    private val newList: List<CountryInfoItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}
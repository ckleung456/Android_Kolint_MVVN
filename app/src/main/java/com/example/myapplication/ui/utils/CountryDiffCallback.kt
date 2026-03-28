package com.example.myapplication.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.model.domain.CountryItem

class CountryDiffCallback(
    val oldList: List<CountryItem>,
    val newList: List<CountryItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].type == newList[newItemPosition].type

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when (oldItem) {
            is CountryItem.CountrySortLetter -> oldItem.letter == (newItem as CountryItem.CountrySortLetter).letter
            is CountryItem.CountryView -> oldItem.country.name == (newItem as CountryItem.CountryView).country.name && oldItem.country.region == newItem.country.region
                    && oldItem.country.capital == newItem.country.capital
        }
    }
}
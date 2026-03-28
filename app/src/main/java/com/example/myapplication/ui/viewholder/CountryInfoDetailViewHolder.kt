package com.example.myapplication.ui.viewholder

import android.content.Context
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemTextBinding
import com.example.myapplication.model.domain.CountryInfoItem

class CountryInfoDetailViewHolder(
    private val binding: ItemTextBinding
) : CountryInfoViewHolder(itemView = binding.root) {
    companion object {
        private const val NAME_TEXT_SIZE = 24f
    }

    private val context: Context = itemView.context


    override fun onBind(itemInfo: CountryInfoItem) {
        binding.txtInfo.text = when (itemInfo) {
            is CountryInfoItem.Name -> context.getString(R.string.country_name, itemInfo.name ?: "")
            is CountryInfoItem.Region -> context.getString(R.string.country_region, itemInfo.region)
            is CountryInfoItem.Capital -> context.getString(
                R.string.country_capital,
                itemInfo.capital
            )
            is CountryInfoItem.Code -> context.getString(R.string.country_code, itemInfo.code)
            is CountryInfoItem.Currency -> context.getString(
                R.string.currency_format,
                itemInfo.currency?.name ?: "",
                itemInfo.currency?.code ?: "",
                itemInfo.currency?.symbol ?: ""
            )
            is CountryInfoItem.Language -> context.getString(
                R.string.language_format,
                itemInfo.language?.name ?: "",
                itemInfo.language?.code ?: ""
            )
            else -> ""
        }
        if (itemInfo is CountryInfoItem.Name) {
            binding.txtInfo.textSize = NAME_TEXT_SIZE
        }
    }
}
package com.example.myapplication.ui.viewholder

import android.content.Context
import com.example.core.model.countries.Country
import com.example.myapplication.model.domain.CountryItem
import com.example.core.utils.setOnThrottleClickListener
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemCountryBinding

class CountryViewHolder(
    private val binding: ItemCountryBinding,
    private val onClick: (Country) -> Unit
) : CountryItemViewHolder(binding.root) {
    private val context: Context = itemView.context
    private var country: Country? = null

    init {
        itemView.setOnThrottleClickListener {
            country?.let { country ->
                onClick.invoke(country)
            }
        }
    }

    override fun onBind(data: CountryItem) {
        if (data is CountryItem.CountryView) {
            country = data.country
            binding.apply {
                txtCountryRegion.text = context.getString(
                    R.string.country_regin_format,
                    country?.name?.trim() ?: "",
                    country?.region?.trim() ?: ""
                )
                txtCountryCode.text = country?.code?.trim() ?: ""
                txtCountryCapital.text = country?.capital?.trim() ?: ""
            }
        }
    }
}
package com.example.myapplication.ui.viewholder

import com.example.core.utils.loadSvgImage
import com.example.myapplication.databinding.ItemImageBinding
import com.example.myapplication.model.domain.CountryInfoItem

class CountryInfoFlagViewHolder(
    private val binding: ItemImageBinding
) : CountryInfoViewHolder(itemView = binding.root) {
    override fun onBind(itemInfo: CountryInfoItem) {
        if (itemInfo is CountryInfoItem.Flag) {
            itemInfo.flagUrl?.let {
                binding.imgRound.loadSvgImage(imageUri = it)
            }
        }
    }
}
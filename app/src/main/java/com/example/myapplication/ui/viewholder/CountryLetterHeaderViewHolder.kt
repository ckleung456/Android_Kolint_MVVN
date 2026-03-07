package com.example.myapplication.ui.viewholder

import android.view.Gravity
import com.example.myapplication.databinding.ItemTextBinding
import com.example.myapplication.model.CountryItem

class CountryLetterHeaderViewHolder constructor(
    private val binding: ItemTextBinding
): CountryItemViewHolder(itemView = binding.root) {
    init {
        binding.txtInfo.gravity = Gravity.START
    }
    override fun onBind(data: CountryItem) {
        if (data is CountryItem.CountrySortLetter) {
            binding.txtInfo.text = data.letter
        }
    }
}
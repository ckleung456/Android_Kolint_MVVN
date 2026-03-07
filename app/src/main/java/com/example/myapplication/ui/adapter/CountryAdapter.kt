package com.example.myapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.core.model.countries.Country
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemCountryBinding
import com.example.myapplication.databinding.ItemTextBinding
import com.example.myapplication.model.CountryItem
import com.example.myapplication.ui.utils.CountryDiffCallback
import com.example.myapplication.ui.viewholder.CountryItemViewHolder
import com.example.myapplication.ui.viewholder.CountryLetterHeaderViewHolder
import com.example.myapplication.ui.viewholder.CountryViewHolder
import java.util.Locale

class CountryAdapter(
    private val onClick: (Country) -> Unit = {}
) : RecyclerView.Adapter<CountryItemViewHolder>(), Filterable {

    private var originalList = mutableListOf<CountryItem>()

    var items = listOf<CountryItem>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(
                CountryDiffCallback(
                    oldList = field,
                    newList = value
                )
            )
            if (originalList.isEmpty()) {
                originalList.addAll(value)
            }
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryItemViewHolder =
        when (viewType) {
            R.layout.item_country  -> CountryViewHolder(
                binding = ItemCountryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClick = onClick
            )
            R.layout.item_text -> CountryLetterHeaderViewHolder(
                binding = ItemTextBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> CountryItemViewHolder(ItemTextBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root)
        }


    override fun onBindViewHolder(holder: CountryItemViewHolder, position: Int) =
        holder.onBind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int = items[position].type.type

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchText = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""

            val filteredList = if (searchText.isBlank()) {
                originalList
            } else {
                items.filter {
                    if (it is CountryItem.CountryView) {
                        it.country.name?.lowercase(Locale.getDefault())?.contains(searchText) == true
                    } else false
                }
            }

            return FilterResults().apply {
                values = filteredList
                count = filteredList.size
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(
            constraint: CharSequence?,
            results: FilterResults?
        ) {
            results?.values?.let {
                items = it as List<CountryItem>
                notifyDataSetChanged()
            }
        }
    }
}


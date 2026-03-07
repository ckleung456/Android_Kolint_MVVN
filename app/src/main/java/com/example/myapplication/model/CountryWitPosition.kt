package com.example.myapplication.model

import android.os.Parcelable
import com.example.myapplication.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountryWitPosition(
    val countries: List<CountryItem>,
    val position: Int
) : Parcelable

sealed class CountryItem(val type: CountriesType): Parcelable {
    @Parcelize
    data class CountrySortLetter(
        val letter: String
    ): CountryItem(CountriesType.SORT_LETTER)

    @Parcelize
    data class CountryView(
        val country: com.example.core.model.countries.Country
    ): CountryItem(CountriesType.COUNTRY)
}

enum class CountriesType(val type: Int) {
    SORT_LETTER(R.layout.item_text),
    COUNTRY(R.layout.item_country)
}
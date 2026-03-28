package com.example.myapplication.model.domain

import android.os.Parcelable
import com.example.core.model.countries.CountryCurrency
import com.example.core.model.countries.CountryLanguage
import com.example.myapplication.R
import kotlinx.parcelize.Parcelize

sealed class CountryInfoItem(
    val itemType: CountryInfoItemType
) : Parcelable {
    @Parcelize
    data class Flag(
        val flagUrl: String?
    ) : CountryInfoItem(itemType = CountryInfoItemType.FLAG)

    @Parcelize
    data class Name(
        val name: String?
    ) : CountryInfoItem(itemType = CountryInfoItemType.NAME)

    @Parcelize
    data class Region(
        val region: String?
    ) : CountryInfoItem(itemType = CountryInfoItemType.REGION)

    @Parcelize
    data class Capital(
        val capital: String?
    ) : CountryInfoItem(itemType = CountryInfoItemType.CAPITAL)

    @Parcelize
    data class Code(
        val code: String?
    ) : CountryInfoItem(itemType = CountryInfoItemType.CODE)

    @Parcelize
    data class Currency(
        val currency: CountryCurrency?
    ) : CountryInfoItem(itemType = CountryInfoItemType.CURRENCY)

    @Parcelize
    data class Language(
        val language: CountryLanguage?
    ) : CountryInfoItem(itemType = CountryInfoItemType.LANGUAGE)
}

@Parcelize
enum class CountryInfoItemType(val type: Int) : Parcelable {
    FLAG(R.layout.item_image),
    NAME(R.layout.item_text),
    REGION(R.layout.item_text),
    CAPITAL(R.layout.item_text),
    CODE(R.layout.item_text),
    CURRENCY(R.layout.item_text),
    LANGUAGE(R.layout.item_text)
}
package com.example.core.model.countries

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    val capital: String?,
    val code: String?,
    val currency: CountryCurrency?,
    val flag: String?,
    val language: CountryLanguage,
    val name: String?,
    val region: String?
) : Parcelable


@Parcelize
data class CountryCurrency(
    val code: String?,
    val name: String?,
    val symbol: String?
) : Parcelable

@Parcelize
data class CountryLanguage(
    val code: String?,
    val name: String?
) : Parcelable
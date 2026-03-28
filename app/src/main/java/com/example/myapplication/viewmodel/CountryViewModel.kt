package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.core.model.countries.Country
import com.example.myapplication.model.domain.CountryInfoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val TAG: String = "CountryViewModel"
        const val ARGUMENT_COUNTRY = "countryDetail"
    }

    private val _country = savedStateHandle.getLiveData<Country>(ARGUMENT_COUNTRY)
    val countryInfo: LiveData<List<CountryInfoItem>> = _country.map {
        listOf(
            CountryInfoItem.Flag(flagUrl = it.flag),
            CountryInfoItem.Name(name = it.name),
            CountryInfoItem.Region(region = it.region),
            CountryInfoItem.Code(code = it.code),
            CountryInfoItem.Capital(capital = it.capital),
            CountryInfoItem.Currency(currency = it.currency),
            CountryInfoItem.Language(language = it.language)
        )
    }
}
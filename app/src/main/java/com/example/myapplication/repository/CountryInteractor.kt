package com.example.myapplication.repository

import com.example.core.repository.network.TestAPIs
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CountryInteractor @Inject constructor(
    private val apis: TestAPIs
) {
    fun getCountries() = apis.fetchCountriesAPI()
}
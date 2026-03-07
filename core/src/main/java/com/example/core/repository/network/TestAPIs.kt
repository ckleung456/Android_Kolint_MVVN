package com.example.core.repository.network

import com.example.core.model.countries.Country
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface TestAPIs {
    @GET("peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json")
    fun fetchCountriesAPI(): Flow<List<Country>>
}
package com.example.myapplication.usecase

import com.example.core.usecase.FlowUseCase
import com.example.myapplication.model.CountryItem
import com.example.myapplication.repository.CountryInteractor
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class GetCountriesUseCase @Inject constructor(
    private val interactor: CountryInteractor
) : FlowUseCase<Unit, List<CountryItem>>() {
    override suspend fun getFlow(input: Unit): Flow<List<CountryItem>> = interactor
        .getCountries()
        .map { countries ->
            val grouped = countries.sortedBy {
                it.name
            }.groupBy { it.name?.first()?.uppercaseChar() }
            grouped.filter { it.key != null }.flatMap { (letter, countries) ->
                listOf(
                    CountryItem.CountrySortLetter(letter = letter.toString())
                ) + countries.map { country ->
                    CountryItem.CountryView(country = country)
                }
            }
        }
        .flowOn(Dispatchers.IO)
}
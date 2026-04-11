package com.example.myapplication.usecase

import com.example.core.model.countries.Country
import com.example.core.usecase.FlowUseCase
import com.example.core.usecase.UseCaseOutputWithStatus
import com.example.myapplication.model.domain.CountryItem
import com.example.myapplication.repository.CountryRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetCountriesUseCase @Inject constructor(
    private val interactor: CountryRepository
) : FlowUseCase<Unit, List<Country>, List<CountryItem>>() {
    override suspend fun getFlow(input: Unit): Flow<List<Country>> = interactor
        .getCountries()

    override suspend fun List<Country>.onSucceedResult(): UseCaseOutputWithStatus.Success<List<CountryItem>> =
        this.let {
            val grouped = sortedBy {
                it.name
            }.groupBy { it.name?.first()?.uppercaseChar() }
            UseCaseOutputWithStatus.Success(
                result = grouped.filter { it.key != null }.flatMap { (letter, countries) ->
                    listOf(
                        CountryItem.CountrySortLetter(letter = letter.toString())
                    ) + countries.map { country ->
                        CountryItem.CountryView(country = country)
                    }
                }
            )
        }
}
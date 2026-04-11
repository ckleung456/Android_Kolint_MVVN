package com.example.myapplication.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.repository.network.RetrofitException
import com.example.core.usecase.UseCaseOutputWithStatus
import com.example.myapplication.R
import com.example.myapplication.model.domain.CountryItem
import com.example.myapplication.model.domain.CountryUiState
import com.example.myapplication.usecase.GetCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {
    companion object {
        private val TAG: String = CountriesViewModel::class.java.name

        @VisibleForTesting
        internal val EXTRAS_COUNTRIES = "$TAG.EXTRAS_COUNTRIES"

        @VisibleForTesting
        internal val EXTRAS_LAST_POSITION = "$TAG.EXTRAS_LAST_POSITION"

        private const val DEFAULT_STOP_TIME = 5000L
    }

    private val countries = savedStateHandle.get<List<CountryItem>>(EXTRAS_COUNTRIES)
    private val position = savedStateHandle.get<Int>(EXTRAS_LAST_POSITION)

    private val _uiState = Channel<CountryUiState>( Channel.BUFFERED)
    internal val uiState: StateFlow<CountryUiState> = _uiState
        .receiveAsFlow()
        .onStart {
            loadCountries()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(DEFAULT_STOP_TIME),
            initialValue = CountryUiState.Default
        )

    fun loadCountries(isHardRefresh: Boolean = false) {
        with(viewModelScope) {
            if (isHardRefresh || countries.isNullOrEmpty()) {
                launch(Dispatchers.IO) {
                    getCountriesUseCase
                        .invoke(input = Unit)
                        .collect { state ->
                            launch(Dispatchers.Main) {
                                when (state) {
                                    is UseCaseOutputWithStatus.Progress -> _uiState.send(CountryUiState.Loading)
                                    is UseCaseOutputWithStatus.Failed -> _uiState.send(
                                        CountryUiState.Failure(
                                            errorResId = when (state.error.getKind()) {
                                                RetrofitException.Kind.NETWORK -> R.string.error_network
                                                else -> R.string.error_server
                                            }
                                        )
                                    )
                                    is UseCaseOutputWithStatus.Success -> {
                                        savedStateHandle[EXTRAS_COUNTRIES] = state.result
                                        savedStateHandle[EXTRAS_LAST_POSITION] = 0
                                        _uiState.send(
                                            CountryUiState.Success(
                                                countries = state.result,
                                                position = 0
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
            } else {
                launch(Dispatchers.Main) {
                    _uiState.send(
                        CountryUiState.Success(
                            countries = countries,
                            position = position ?: 0
                        )
                    )
                }
            }
        }
    }

    fun saveLastPosition(position: Int) {
        savedStateHandle[EXTRAS_LAST_POSITION] = position
    }
}
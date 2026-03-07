package com.example.myapplication.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.repository.network.RetrofitException
import com.example.core.usecase.UseCaseOutputWithStatus
import com.example.core.utils.Event
import com.example.core.utils.fireEvent
import com.example.myapplication.R
import com.example.myapplication.model.CountryItem
import com.example.myapplication.model.CountryWitPosition
import com.example.myapplication.usecase.GetCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {
    companion object {
        private val TAG: String = CountriesViewModel::class.java.name

        @VisibleForTesting
        internal val EXTRAS_COUNTRIES = "$TAG.EXTRAS_COUNTRIES"

        @VisibleForTesting
        internal val EXTRAS_LAST_POSITION = "$TAG.EXTRAS_LAST_POSITION"
    }

    private val _countries = savedStateHandle.getLiveData<List<CountryItem>>(EXTRAS_COUNTRIES)
    private val _position = savedStateHandle.getLiveData<Int>(EXTRAS_LAST_POSITION)
    val countriesWithPosition: LiveData<CountryWitPosition> =
        MediatorLiveData<CountryWitPosition>().apply {
            fun update() {
                val countriesList = _countries.value ?: return
                val position = _position.value ?: 0

                value = CountryWitPosition(
                    countries = countriesList,
                    position = if (position > countriesList.size) countriesList.size - 1 else position
                )
            }

            addSource(_countries) { update() }
            addSource(_position) { update() }
        }

    private val _inProgress = MutableLiveData<Event<Boolean>>()
    val inProgress: LiveData<Event<Boolean>> = _inProgress

    private val _onError = MutableLiveData<Event<Int>>()
    val onError: LiveData<Event<Int>> = _onError

    init {
        viewModelScope.launch {
            getCountriesUseCase
                .invoke(input = Unit) { state ->
                    when (state) {
                        is UseCaseOutputWithStatus.Progress -> _inProgress.fireEvent(true)
                        is UseCaseOutputWithStatus.Failed -> state.error.let { error ->
                            _inProgress.fireEvent(false)
                            _onError.fireEvent(
                                when (error.getKind()) {
                                    RetrofitException.Kind.NETWORK -> R.string.error_network
                                    else -> R.string.error_server
                                }
                            )
                        }
                        is UseCaseOutputWithStatus.Success -> {
                            _inProgress.fireEvent(false)
                            _countries.value = state.result
                        }
                    }
                }
        }
    }

    fun saveLastPosition(position: Int) {
        savedStateHandle[EXTRAS_LAST_POSITION] = position
    }
}
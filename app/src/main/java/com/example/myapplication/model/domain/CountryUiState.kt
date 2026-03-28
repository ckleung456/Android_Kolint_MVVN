package com.example.myapplication.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface CountryUiState : Parcelable {
    @Parcelize
    object Default : CountryUiState


    @Parcelize
    object Loading : CountryUiState

    @Parcelize
    data class Success(
        val countries: List<CountryItem>,
        val position: Int
    ) : CountryUiState

    @Parcelize
    data class Failure(val errorResId: Int) : CountryUiState
}
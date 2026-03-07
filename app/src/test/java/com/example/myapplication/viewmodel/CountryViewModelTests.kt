package com.example.myapplication.viewmodel

import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.core.model.countries.Country
import com.example.core.model.countries.CountryCurrency
import com.example.core.model.countries.CountryLanguage
import com.example.myapplication.model.CountryInfoItem
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class CountryViewModelTests {
    companion object {
        private const val FAKE_FLAG = "flag"
        private const val FAKE_NAME = "name"
        private const val FAKE_REGION = "region"
        private const val FAKE_CODE = "code"
        private const val FAKE_CAPITAL = "capital"
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var savedStateHandleMock: SavedStateHandle

    @MockK
    lateinit var countryMock: Country

    @MockK
    lateinit var currencyMock: CountryCurrency

    @MockK
    lateinit var languageMock: CountryLanguage

    @MockK
    lateinit var looperMock: Looper

    private val countryLiveData = MutableLiveData<Country>()
    private var underTests: CountryViewModel? = null

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        mockkStatic(Looper::class)
        every {
            Looper.myLooper()
        } returns looperMock
        every {
            Looper.getMainLooper()
        } returns looperMock
        every {
            savedStateHandleMock.getLiveData<Country>(CountryViewModel.ARGUMENT_COUNTRY)
        } returns countryLiveData
        underTests = CountryViewModel(
            savedStateHandle = savedStateHandleMock
        )
    }

    @After
    fun `tear down`() {
        underTests = null
        unmockkAll()
    }

    @Test
    fun `country info`() {
        //GIVEN
        every {
            countryMock.flag
        } returns FAKE_FLAG
        every {
            countryMock.name
        } returns FAKE_NAME
        every {
            countryMock.region
        } returns FAKE_REGION
        every {
            countryMock.code
        } returns FAKE_CODE
        every {
            countryMock.capital
        } returns FAKE_CAPITAL
        every {
            countryMock.currency
        } returns currencyMock
        every {
            countryMock.language
        } returns languageMock

        //WHEN
        underTests?.countryInfo?.observeForever { }
        countryLiveData.value = countryMock

        //THEN
        assertEquals(
            listOf(
                CountryInfoItem.Flag(flagUrl = FAKE_FLAG),
                CountryInfoItem.Name(name = FAKE_NAME),
                CountryInfoItem.Region(region = FAKE_REGION),
                CountryInfoItem.Code(code = FAKE_CODE),
                CountryInfoItem.Capital(capital = FAKE_CAPITAL),
                CountryInfoItem.Currency(currency = currencyMock),
                CountryInfoItem.Language(language = languageMock)
            ),
            underTests?.countryInfo?.value
        )
    }
}
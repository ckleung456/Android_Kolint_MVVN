package com.example.myapplication.viewmodel

import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.core.model.countries.Country
import com.example.core.repository.network.RetrofitException
import com.example.core.usecase.UseCaseOutputWithStatus
import com.example.myapplication.CoroutineTestRule
import com.example.myapplication.R
import com.example.myapplication.model.domain.CountryWitPosition
import com.example.myapplication.usecase.GetCountriesUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class CountriesViewModelsTests {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    lateinit var savedStateHandleMock: SavedStateHandle

    @MockK
    lateinit var getCountriesUseCaseMock: GetCountriesUseCase

    @MockK
    lateinit var countryMock: Country

    @MockK
    lateinit var retrofitExceptionMock: RetrofitException

    @MockK
    lateinit var looperMock: Looper

    private val countries = MutableLiveData<List<Country>>()
    private val position = MutableLiveData<Int>()
    private var underTests: CountriesViewModel? = null

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        mockkStatic(Looper::class)
        every {
            savedStateHandleMock.getLiveData<List<Country>>(CountriesViewModel.EXTRAS_COUNTRIES)
        } returns countries
        every {
            savedStateHandleMock.getLiveData<Int>(CountriesViewModel.EXTRAS_LAST_POSITION)
        } returns position
        every {
            Looper.myLooper()
        } returns looperMock
        every {
            Looper.getMainLooper()
        } returns looperMock
    }

    @After
    fun `tear down`() {
        underTests = null
        unmockkAll()
    }

    @Test
    fun `init and load countries successfully`() {
        //WHEN
        val methodFnSlot = slot<(UseCaseOutputWithStatus<List<Country>>) -> Unit>()
        coEvery {
            getCountriesUseCaseMock
                .invoke(
                    input = Unit,
                    onResultFn = capture(methodFnSlot)
                )
        } answers {
            secondArg<(UseCaseOutputWithStatus<List<Country>>) -> Unit>().invoke(
                UseCaseOutputWithStatus.Success(
                    result = listOf(
                        countryMock,
                        countryMock,
                        countryMock
                    )
                )
            )
        }

        //WHEN
        underTests = CountriesViewModel(
            savedStateHandle = savedStateHandleMock,
            getCountriesUseCase = getCountriesUseCaseMock
        )
        underTests!!.inProgress.observeForever { }
        underTests!!.countriesWithPosition.observeForever { }

        //THEN
        assertEquals(
            listOf(countryMock, countryMock, countryMock),
            countries.value,
        )
        assertEquals(
            false,
            underTests!!.inProgress.value?.peekContent()
        )
        assertEquals(
            CountryWitPosition(
                countries = listOf(countryMock, countryMock, countryMock),
                position = 0
            ),
            underTests!!.countriesWithPosition.value
        )

        //WHEN
        position.value = 1

        //THEN
        assertEquals(
            CountryWitPosition(
                countries = listOf(countryMock, countryMock, countryMock),
                position = 1
            ),
            underTests!!.countriesWithPosition.value
        )
    }

    @Test
    fun `init and loading countries`() {
        //WHEN
        val methodFnSlot = slot<(UseCaseOutputWithStatus<List<Country>>) -> Unit>()
        coEvery {
            getCountriesUseCaseMock
                .invoke(
                    input = Unit,
                    onResultFn = capture(methodFnSlot)
                )
        } answers {
            secondArg<(UseCaseOutputWithStatus<List<Country>>) -> Unit>().invoke(
                UseCaseOutputWithStatus.Progress()
            )
        }

        //WHEN
        underTests = CountriesViewModel(
            savedStateHandle = savedStateHandleMock,
            getCountriesUseCase = getCountriesUseCaseMock
        )
        underTests!!.inProgress.observeForever { }

        //THEN
        assertEquals(
            true,
            underTests!!.inProgress.value?.peekContent()
        )
    }

    @Test
    fun `init and load countires failed`() {
        //WHEN
        val methodFnSlot = slot<(UseCaseOutputWithStatus<List<Country>>) -> Unit>()
        coEvery {
            getCountriesUseCaseMock
                .invoke(
                    input = Unit,
                    onResultFn = capture(methodFnSlot)
                )
        } answers {
            secondArg<(UseCaseOutputWithStatus<List<Country>>) -> Unit>().invoke(
                UseCaseOutputWithStatus.Failed(error = retrofitExceptionMock)
            )
        }
        every {
            retrofitExceptionMock.getKind()
        } returns RetrofitException.Kind.NETWORK

        //WHEN
        underTests = CountriesViewModel(
            savedStateHandle = savedStateHandleMock,
            getCountriesUseCase = getCountriesUseCaseMock
        )
        underTests!!.inProgress.observeForever { }
        underTests!!.onError.observeForever { }

        //THEN
        assertEquals(
            false,
            underTests!!.inProgress.value?.peekContent()
        )
        assertEquals(
            R.string.error_network,
            underTests!!.onError.value?.peekContent()
        )
    }
}
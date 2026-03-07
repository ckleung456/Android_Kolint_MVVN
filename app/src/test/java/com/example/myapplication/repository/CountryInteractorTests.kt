package com.example.myapplication.repository

import com.example.core.model.countries.Country
import com.example.core.repository.network.RetrofitException
import com.example.core.repository.network.TestAPIs
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CountryInteractorTests {
    @MockK
    lateinit var apisMock: TestAPIs

    @MockK
    lateinit var countryMock: Country

    @MockK
    lateinit var retrofitExceptionMock: RetrofitException

    private var underTests: CountryInteractor? = null

    @Before
    fun `set up`() {
        MockKAnnotations.init(this)
        underTests = CountryInteractor(
            apis = apisMock
        )
    }

    @After
    fun `tear down`() {
        underTests = null
        unmockkAll()
    }

    @Test
    fun `get countries successfully`() = runTest {
        //GIVEN
        val exceptedList = listOf(countryMock)
        every {
            apisMock.fetchCountriesAPI()
        } returns flowOf(exceptedList)

        //WHEN
        val result = mutableListOf<Country>()
        underTests!!.getCountries().onEach {
            result.addAll(it)
        }.collect()

        //THEN
        assertEquals(exceptedList, result)
    }

    @Test
    fun `get countries failed`() = runTest {
        //GIVEN
        every {
            apisMock.fetchCountriesAPI()
        } returns flow {
            throw retrofitExceptionMock
        }

        //WHEN
        var resultError: Throwable? = null
        underTests!!.getCountries()
            .catch { error ->
                resultError = error
            }.collect()

        //THEN
        assertEquals(retrofitExceptionMock, resultError)
    }
}
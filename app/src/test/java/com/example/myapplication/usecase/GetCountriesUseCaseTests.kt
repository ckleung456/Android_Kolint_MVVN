package com.example.myapplication.usecase

import com.example.core.model.countries.Country
import com.example.core.repository.network.RetrofitException
import com.example.core.usecase.UseCaseOutputWithStatus
import com.example.myapplication.CoroutineTestRule
import com.example.myapplication.repository.CountryInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCountriesUseCaseTests {
    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @MockK
    lateinit var interactorMock: CountryInteractor

    @MockK
    lateinit var onResultFnMock: (UseCaseOutputWithStatus<List<Country>>) -> Unit

    @MockK
    lateinit var countryMock: Country

    @MockK
    lateinit var retrofitExceptionMock: RetrofitException

    private var underTests: GetCountriesUseCase? = null

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        underTests = GetCountriesUseCase(
            interactor = interactorMock
        )
    }

    @After
    fun `tear down`() {
        underTests = null
        unmockkAll()
    }

    @Test
    fun `invoke successfully`() = runTest {
        //GIVEN
        val exceptedList = listOf(countryMock)
        every {
            interactorMock.getCountries()
        } returns flowOf(exceptedList)

        //WHEN
        underTests?.invoke(
            input = Unit,
            onResultFn = onResultFnMock
        )

        //THEN
        verify {
            onResultFnMock.invoke(
                UseCaseOutputWithStatus.Progress()
            )
            onResultFnMock.invoke(
                UseCaseOutputWithStatus.Success(result = exceptedList)
            )
        }
    }

    @Test
    fun `invoke failed`() = runTest {
        //GIVEN
        every {
            interactorMock.getCountries()
        } returns flow {
            throw retrofitExceptionMock
        }

        //WHEN
        underTests?.invoke(
            input = Unit,
            onResultFn = onResultFnMock
        )

        //THEN
        verify {
            onResultFnMock.invoke(
                UseCaseOutputWithStatus.Progress()
            )
            onResultFnMock.invoke(
                UseCaseOutputWithStatus.Failed(error = retrofitExceptionMock)
            )
        }
    }
}
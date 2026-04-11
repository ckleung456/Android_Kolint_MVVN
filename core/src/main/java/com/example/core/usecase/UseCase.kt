package com.example.core.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CoroutineUseCase<in INPUT, out RESULT> {
    suspend operator fun invoke(
        input: INPUT,
        onResultFn: (RESULT) -> Unit = {}
    )
}

abstract class FlowUseCase<in INPUT, INTERMEDIATE, out RESULT>() {
    /**
     * Implement this in use case
     */
    protected abstract suspend fun getFlow(
        input: INPUT
    ): Flow<INTERMEDIATE>

    /**
     * Implement this method if you need more logic to handle your error and results different RESULT type
     */
    protected open suspend fun errorResult(error: Throwable): RESULT? = null

    protected abstract suspend fun INTERMEDIATE.onSucceedResult(): UseCaseOutputWithStatus.Success<RESULT>

    operator fun invoke(
        input: INPUT
    ): Flow<UseCaseOutputWithStatus<RESULT>> = flow {
        emit(UseCaseOutputWithStatus.Progress())
        try {
            getFlow(input = input)
                .collect {
                    emit(it.onSucceedResult())
                }
        } catch (ex: Exception) {
            UseCaseOutputWithStatus.Failed(
                error = ex as? com.example.core.repository.network.RetrofitException
                    ?: com.example.core.repository.network.RetrofitException.unexpectedError(
                        exception = ex
                    ),
                failedResult = errorResult(error = ex)
            )
        }
    }

    /**
     * Call this in view model
     */
//    suspend operator fun invoke(
//        input: INPUT,
//        onResultFn: (UseCaseOutputWithStatus<RESULT>) -> Unit
//    ) = getFlow(input)
//        .onStart {
//            onResultFn(UseCaseOutputWithStatus.Progress())
//        }
//        .onEach { result ->
//            onResultFn(UseCaseOutputWithStatus.Success(result = result))
//        }
//        .catch { e ->
//            onResultFn(
//                UseCaseOutputWithStatus.Failed(
//                    error = if (e is com.example.core.repository.network.RetrofitException) e else com.example.core.repository.network.RetrofitException.unexpectedError(
//                        exception = e
//                    ),
//                    failedResult = errorResult(error = e)
//                )
//            )
//        }
//        .flowOn(downStreamThread())
//        .collect()
}

sealed class UseCaseOutputWithStatus<out RESULT> {
    data class Progress<out RESULT>(val data: Any? = null) : UseCaseOutputWithStatus<RESULT>()
    data class Success<out RESULT>(val result: RESULT) : UseCaseOutputWithStatus<RESULT>()
    data class Failed<out RESULT>(
        val error: com.example.core.repository.network.RetrofitException,
        val failedResult: RESULT? = null
    ) :
        UseCaseOutputWithStatus<RESULT>()
}
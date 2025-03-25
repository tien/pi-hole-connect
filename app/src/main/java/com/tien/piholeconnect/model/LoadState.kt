package com.tien.piholeconnect.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

typealias UnitLoadState = LoadState<Unit>

sealed interface LoadState<out T> {
    val data: T?

    data class Idle<T>(override val data: T? = null) : LoadState<T>

    data class Success<T>(override val data: T) : LoadState<T>

    data class Loading<T>(override val data: T? = null) : LoadState<T>

    data class Failure<T>(val throwable: Throwable, override val data: T? = null) : LoadState<T>

    companion object {
        val Idle = Idle<Unit>()

        val Success = Success(Unit)

        val Loading = Loading<Unit>()

        fun Failure(throwable: Throwable) = Failure<Unit>(throwable)
    }
}

fun <T> LoadState<T>.asSuccess(data: T): LoadState<T> {
    return LoadState.Success(data)
}

fun <T> LoadState<T>.asLoading(): LoadState<T> {
    return LoadState.Loading(this.data)
}

fun <T> LoadState<T>.asFailure(throwable: Throwable): LoadState<T> {
    return LoadState.Failure(throwable, this.data)
}

fun <T> Flow<T>.asLoadState(): Flow<LoadState<T>> {
    return this.map { LoadState.Success(it) as LoadState<T> }
        .onStart { emit(LoadState.Loading()) }
        .catch { emit(LoadState.Failure(it)) }
}

fun MutableStateFlow<UnitLoadState>.run(
    scope: CoroutineScope,
    block: suspend CoroutineScope.() -> Unit,
) {
    value = UnitLoadState.Loading
    scope.launch {
        try {
            block()
            value = UnitLoadState.Success
        } catch (error: Throwable) {
            value = UnitLoadState.Failure(error)
        }
    }
}

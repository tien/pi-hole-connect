package com.tien.piholeconnect.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface LoadState<out T> {
    val data: T?

    object Idle : LoadState<Nothing> {
        override val data = null
    }

    data class Success<T>(override val data: T) : LoadState<T>

    data class Loading<T>(override val data: T? = null) : LoadState<T>

    data class Failure<T>(val throwable: Throwable, override val data: T? = null) : LoadState<T>
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

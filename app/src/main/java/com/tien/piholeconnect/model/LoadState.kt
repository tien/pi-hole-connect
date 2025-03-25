package com.tien.piholeconnect.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface LoadState<out T> {
    val data: T?

    val count: Int

    object Idle : LoadState<Nothing> {
        override val data = null
        override val count = 0
    }

    data class Success<T>(override val data: T, override val count: Int = 0) : LoadState<T>

    data class Loading<T>(override val data: T? = null, override val count: Int = 0) : LoadState<T>

    data class Failure<T>(
        val throwable: Throwable,
        override val data: T? = null,
        override val count: Int = 0,
    ) : LoadState<T>
}

fun <T> LoadState<T>.asSuccess(data: T, count: Int = this.count): LoadState<T> {
    return LoadState.Success(data, count)
}

fun <T> LoadState<T>.asLoading(count: Int = this.count): LoadState<T> {
    return LoadState.Loading(this.data, count)
}

fun <T> LoadState<T>.asFailure(throwable: Throwable, count: Int = this.count): LoadState<T> {
    return LoadState.Failure(throwable, this.data, count)
}

fun <T> Flow<T>.asLoadState(count: Int = 0): Flow<LoadState<T>> {
    return this.map { LoadState.Success(it, count) as LoadState<T> }
        .onStart { emit(LoadState.Loading(count = count)) }
        .catch { emit(LoadState.Failure(it, count = count)) }
}

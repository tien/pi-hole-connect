package com.tien.piholeconnect.model

sealed class AsyncState<in T> {
    data object Idle : AsyncState<Any>()
    data object Pending : AsyncState<Any>()
    data class Settled<T>(val result: Result<T>) : AsyncState<T>()
}

package com.tien.piholeconnect.model

sealed class AsyncState<in T> {
    object Idle : AsyncState<Any>()
    object Pending : AsyncState<Any>()
    data class Settled<T>(val result: Result<T>) : AsyncState<T>()
}

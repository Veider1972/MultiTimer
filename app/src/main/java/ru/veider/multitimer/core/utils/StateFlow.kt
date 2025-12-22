package ru.veider.multitimer.core.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <T> stateFlow(
    initialValue: T,
    onValueChange: (T) -> Unit
): MutableStateFlow<T> = object : MutableStateFlow<T> {

    private val state: MutableStateFlow<T> = MutableStateFlow(initialValue)

    override val replayCache: List<T> get() = state.replayCache
    override val subscriptionCount: StateFlow<Int> get() = state.subscriptionCount
    override var value: T
        get() = state.value
        set(value) {
            if (state.value != value) {
                state.value = value
                onValueChange(value)
            }
        }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() = state.resetReplayCache()

    override fun tryEmit(value: T): Boolean = state.tryEmit(value)

    override suspend fun emit(value: T) { state.emit(value)}

    override fun compareAndSet(expect: T, update: T): Boolean = state.compareAndSet(expect, update)

    override suspend fun collect(collector: FlowCollector<T>): Nothing = state.collect(collector)

}
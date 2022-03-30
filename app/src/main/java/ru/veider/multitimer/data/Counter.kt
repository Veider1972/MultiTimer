package ru.veider.multitimer.data

import java.io.Serializable
import kotlin.jvm.Synchronized

data class Counter(
    val id: Int,
    var currentProgress: Int,
    var maxProgress: Int,
    var pausedAt: Long,
    var timeOfStart: Long,
    var state: CounterState,
    var title: String
) {

    constructor(id:Int) : this(id, 0, 0, 0, 0, CounterState.FINISHED, "")

    override fun toString(): String {
        return String.format("id=%d, time=%d, state=%s", id, currentProgress, state)
    }

}
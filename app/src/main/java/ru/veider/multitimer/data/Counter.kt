package ru.veider.multitimer.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Counter(
    val id: Int,
    var currentProgress: Int,
    var maxProgress: Int,
    var startTime: Long,
    var state: CounterState,
    var title: String
) : Parcelable, Cloneable {

    constructor(id:Int) : this(id, 0,  0, 0, CounterState.FINISHED, "")

    override fun toString(): String {
        return String.format("id=%d, time=%d, state=%s", id, currentProgress, state)
    }

}
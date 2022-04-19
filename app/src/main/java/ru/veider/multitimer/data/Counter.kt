package ru.veider.multitimer.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.veider.multitimer.const.CounterState
import java.io.Serializable


data class Counter(
    val id: Int,
    var currentProgress: Int,
    var maxProgress: Int,
    var startTime: Long,
    var state: CounterState,
    var title: String
) : Serializable {

    constructor(id:Int) : this(id, 0,  0, 0, CounterState.FINISHED, "")

    override fun toString(): String {
        return String.format("id=%d, time=%d, state=%s", id, currentProgress, state)
    }

}
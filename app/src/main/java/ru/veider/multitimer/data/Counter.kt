package ru.veider.multitimer.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import ru.veider.multitimer.const.CounterState
import java.io.Serializable

@Keep
data class Counter(
    @SerializedName("id") val id: Int,
    @SerializedName("currentProgress") var currentProgress: Int,
    @SerializedName("maxProgress") var maxProgress: Int,
    @SerializedName("startTime") var startTime: Long,
    @SerializedName("state") var state: CounterState,
    @SerializedName("title") var title: String
): Serializable {

    constructor(id:Int) : this(id, 0,  0, 0, CounterState.FINISHED, "")

}
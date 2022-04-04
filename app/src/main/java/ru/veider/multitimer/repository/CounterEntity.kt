package ru.veider.multitimer.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.veider.multitimer.data.CounterState

@Entity
data class CounterEntity (
    @PrimaryKey(autoGenerate = true)
    var key: Int=0,
    var id : Int = 0,
    var currentProgress: Int = 0,
    var maxProgress: Int = 0,
    var startTime: Long = 0,
    var state: Int = 0,
    var title: String = ""
)
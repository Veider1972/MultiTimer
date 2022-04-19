package ru.veider.multitimer.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.lang.StringBuilder
import kotlin.collections.ArrayList

class Counters : ArrayList<Counter>() {

    private val maxID get() = if (size > 0) last().id else 0

    fun delByID(id: Int) {
        removeAt(getIndexByID(id))
    }

    fun getIndexByID(id: Int): Int {
        for (i in 0..size) {
            if (id == this[i].id) return i
        }
        return -1
    }

    fun new(): Counter {
        val newID = maxID + 1
        val newCounter = Counter(newID)
        add(newCounter)
        return newCounter
    }

    fun swap(fromPosition: Int, toPosition: Int): Boolean {
        val fromCounter = this[fromPosition].copy()
        val toCounter = this[toPosition].copy()
        this[fromPosition] = toCounter
        this[toPosition] = fromCounter
        return true
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in 0..size-1) {
            sb.append("(${this[i]})")
            if ((size > 1) && (i < size - 1)) sb.append(",  ")
        }
        return sb.toString()
    }
}
package ru.veider.multitimer.data

import java.io.Serializable
import java.util.*

class Counters : LinkedList<Counter>(), Serializable {

    fun getByID(id: Int): Counter {
        return get(getIndexByID(id))
    }

    fun delByID(id: Int) {
        removeAt(getIndexByID(id))
    }


    fun getIndexByID(id: Int): Int {
        for (i in 0..this.size) {
            if (id == this[i].id) return i
        }
        return -1
    }


    fun new(): Counter {
        val newID = maxID + 1
        val newCounter = Counter(newID)
        this.add(newCounter)
        return newCounter
    }

    fun update(counter: Counter) {
        set(getIndexByID(counter.id), counter)
    }

    private val maxID: Int = if (size > 0) last().id else 0

    fun swap(fromPosition: Int, toPosition: Int): Boolean {
        val fromCounter = this[fromPosition]
        val toCounter = this[toPosition]
        if (toPosition > fromPosition) {
            removeAt(toPosition)
            removeAt(fromPosition)
            add(fromPosition, toCounter)
            add(toPosition, fromCounter)
        } else {
            removeAt(fromPosition)
            removeAt(toPosition)
            add(toPosition, fromCounter)
            add(fromPosition, toCounter)
        }
        return true
    }
}
package ru.veider.multitimer.data

import kotlin.collections.ArrayList

class Counters: ArrayList<Counter>() {

    private val maxID get() = if (size > 0) last().id else 0

    fun getByID(id: Int): Counter {
        return get(getIndexByID(id))
    }

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

    fun update(counter: Counter) {
        set(getIndexByID(counter.id), counter)
    }



    fun swap(fromPosition: Int, toPosition: Int): Boolean {
        val fromCounter = this[fromPosition].copy()
        val toCounter = this[toPosition].copy()
        this[fromPosition] = toCounter
        this[toPosition] = fromCounter
//        if (toPosition > fromPosition) {
//            removeAt(toPosition)
//            removeAt(fromPosition)
//            add(fromPosition, toCounter)
//            add(toPosition, fromCounter)
//        } else {
//            removeAt(fromPosition)
//            removeAt(toPosition)
//            add(toPosition, fromCounter)
//            add(fromPosition, toCounter)
//        }
        return true
    }

}
package ru.veider.multitimer.repository

import ru.veider.multitimer.data.Counter

interface CountersRepository {

    suspend fun getAll(): List<Counter>
    suspend fun get(id: Int): Counter?

    suspend fun upsert(counter: Counter)

    suspend fun upsert(counters: List<Counter>)

    suspend fun delete(id: Int)

    suspend fun deleteAll()
}
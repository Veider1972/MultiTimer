package ru.veider.multitimer.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CounterEntity::class],version=1, exportSchema = false)
abstract class CountersDataBase:RoomDatabase() {
    abstract fun countersDao(): CountersDao
}
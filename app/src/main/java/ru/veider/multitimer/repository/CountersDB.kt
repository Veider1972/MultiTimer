package ru.veider.multitimer.repository

import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.RoomDatabase

@Keep
@Database(entities = [CounterEntity::class], exportSchema = true, version = 1, autoMigrations = [])
abstract class CountersDB : RoomDatabase() {
    abstract fun dao(): CountersDao
}
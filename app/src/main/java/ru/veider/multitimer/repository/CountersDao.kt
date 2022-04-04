package ru.veider.multitimer.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.veider.multitimer.data.CounterState

@Dao
interface CountersDao {
    @Query("SELECT * FROM CounterEntity")
    fun getAll(): List<CounterEntity>

    @Query( "UPDATE CounterEntity SET currentProgress=:currentProgress, maxProgress=:maxProgress, startTime=:startTime, state=:state, title=:title  WHERE id=:id")
    fun updateCounter(id: Int, currentProgress:Int, maxProgress:Int, startTime:Long, state:Int, title:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCounter(entity: CounterEntity)

    @Query("DELETE FROM CounterEntity WHERE id = :id")
    fun deleteCounter(id: Int)

    @Query("DELETE FROM CounterEntity")
    fun deleteAllCounter()
}
package ru.veider.multitimer.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import ru.veider.multitimer.data.Counter

@Dao
interface CountersDao {
    @Query("SELECT * FROM CounterEntity")
    suspend fun getAll(): List<CounterEntity>

    @Query("SELECT * FROM CounterEntity WHERE id=:id")
    suspend fun get(id: Int): CounterEntity?

    @Insert
    suspend fun insert(counter: CounterEntity)

    @Query("UPDATE CounterEntity SET currentProgress=:currentProgress, maxProgress=:maxProgress, startTime=:startTime, state=:state, title=:title WHERE id=:id ")
    suspend fun update(id: Int, currentProgress: Int, maxProgress: Int, startTime: Long, state: Int, title: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(counter: CounterEntity){
        if (get(counter.id) == null)
            insert(counter)
        else
            update(counter.id, counter.currentProgress, counter.maxProgress, counter.startTime, counter.state, counter.title)
    }

    @Upsert
    suspend fun upsert(counters: List<CounterEntity>)

    @Query("DELETE FROM CounterEntity WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM CounterEntity")
    suspend fun deleteAll()
}
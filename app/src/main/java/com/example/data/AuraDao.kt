package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuraDao {
    @Query("SELECT * FROM user_baseline WHERE uid = 1")
    fun getUserBaseline(): Flow<UserBaseline?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBaseline(baseline: UserBaseline)

    @Query("SELECT * FROM cycle_tracker ORDER BY startDate DESC")
    fun getAllCycles(): Flow<List<CycleTracker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: CycleTracker)

    @Query("SELECT * FROM cycle_tracker ORDER BY startDate DESC LIMIT 1")
    suspend fun getCurrentCycle(): CycleTracker?

    @Query("SELECT * FROM cycle_tracker ORDER BY startDate DESC LIMIT 1")
    fun getCurrentCycleFlow(): Flow<CycleTracker?>

    @Query("SELECT * FROM daily_log WHERE cycleId = :cycleId ORDER BY date ASC")
    fun getLogsForCycle(cycleId: String): Flow<List<DailyLog>>
    
    @Query("SELECT * FROM daily_log ORDER BY date DESC LIMIT 30")
    fun getRecentLogs(): Flow<List<DailyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLog)
}

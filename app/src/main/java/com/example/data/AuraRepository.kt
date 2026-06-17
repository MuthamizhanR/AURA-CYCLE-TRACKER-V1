package com.example.data

import kotlinx.coroutines.flow.Flow

class AuraRepository(private val dao: AuraDao) {
    val userBaseline = dao.getUserBaseline()
    val allCycles = dao.getAllCycles()
    val currentCycleFlow = dao.getCurrentCycleFlow()
    val recentLogs = dao.getRecentLogs()

    suspend fun saveBaseline(baseline: UserBaseline) {
        dao.insertUserBaseline(baseline)
    }

    suspend fun startNewCycle(): CycleTracker {
        val newCycle = CycleTracker()
        dao.insertCycle(newCycle)
        return newCycle
    }

    suspend fun logDailySymptom(log: DailyLog) {
        dao.insertDailyLog(log)
    }

    suspend fun getCurrentCycleId(): String? {
        return dao.getCurrentCycle()?.cycleId
    }
}

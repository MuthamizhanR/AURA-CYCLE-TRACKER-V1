package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cycle_tracker")
data class CycleTracker(
    @PrimaryKey val cycleId: String = UUID.randomUUID().toString(),
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isAnovulatoryFlag: Boolean = false
)

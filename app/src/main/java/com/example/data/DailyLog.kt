package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "daily_log",
    foreignKeys = [
        ForeignKey(
            entity = CycleTracker::class,
            parentColumns = arrayOf("cycleId"),
            childColumns = arrayOf("cycleId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("cycleId")]
)
data class DailyLog(
    @PrimaryKey val logId: String = UUID.randomUUID().toString(),
    val cycleId: String,
    val date: Long = System.currentTimeMillis(),
    val tier1Energy: Int, // 1-10
    val tier1Cravings: String, // NONE, SUGAR, SALT, CARBS
    val tier1Skin: String, // CLEAR, ACNE, ACANTHOSIS
    val tier1Mood: String, // STABLE, IRRITABLE, FATIGUED
    val tier2Bbt: Float?, // null if not tracked
    val tier2LhStrip: String? // null, NEGATIVE, PEAK
)

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_baseline")
data class UserBaseline(
    @PrimaryKey val uid: Int = 1,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val waistCm: Float,
    val partnerEmail: String = "",
    val appMode: String = "SINGLE"
)

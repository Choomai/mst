package com.baolong.mst

import androidx.room.PrimaryKey
import java.time.LocalTime

data class TimetableEvent(
    val content: String,
    val weekday: Int,
    val time: LocalTime,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
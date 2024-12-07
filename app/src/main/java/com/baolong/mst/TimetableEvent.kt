package com.baolong.mst

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "timetable_events")
data class TimetableEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "weekday") val weekday: Int,
    @ColumnInfo(name = "time") val time: LocalTime
)
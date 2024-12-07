package com.baolong.mst

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(entities = [TimetableEvent::class, Note::class, Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun timetableDao(): TimetableDao
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable_events")
    fun getAllEvents(): List<TimetableEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: TimetableEvent)

    @Update
    fun updateEvent(vararg event: TimetableEvent)

    @Delete
    fun deleteEvent(event: TimetableEvent)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Update
    fun updateNote(vararg note: Note)

    @Delete
    fun deleteNote(note: Note)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task)

    @Update
    fun updateTask(vararg task: Task)

    @Delete
    fun deleteTask(task: Task)
}
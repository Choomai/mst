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
import androidx.room.TypeConverters
import androidx.room.Update

@Database(entities = [TimetableEvent::class, Note::class, Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
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

        fun destroyInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable_events")
    suspend fun getAllEvents(): List<TimetableEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TimetableEvent)

    @Update
    suspend fun updateEvent(vararg event: TimetableEvent)

    @Delete
    suspend fun deleteEvent(event: TimetableEvent)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(vararg note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(vararg task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}
package com.baolong.mst

import android.content.Context
import com.google.gson.Gson

class NotesViewModel(private val context: Context) {
    companion object {
        private const val PREF_KEY_TASKS = "tasks"
    }

    fun saveTasks(tasks: List<Task>) {
        val sharedPref = context.getSharedPreferences("tasks_pref", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(Companion.PREF_KEY_TASKS, Gson().toJson(tasks))
            apply()
        }
    }

    fun loadTasks(): List<Task> {
        val sharedPref = context.getSharedPreferences("tasks_pref", Context.MODE_PRIVATE)
        val tasksJson = sharedPref.getString(Companion.PREF_KEY_TASKS, "") ?: ""
        return try {
            Gson().fromJson(tasksJson, Array<Task>::class.java).toList()
        } catch (exception: Exception) {
            listOf(Task("Hoàn thành bài tập", "Môn Toán", false))
        }
    }
}
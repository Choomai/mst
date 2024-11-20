package com.baolong.mst

import android.content.Context
import com.google.gson.Gson

object TaskManager {
    private const val PREF_KEY_TASKS = "tasks"

    fun saveTasks(context: Context, tasks: List<Task>) {
        val sharedPref = context.getSharedPreferences("tasks_pref", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(PREF_KEY_TASKS, Gson().toJson(tasks))
            apply()
        }
    }

    fun loadTasks(context: Context): List<Task> {
        val sharedPref = context.getSharedPreferences("tasks_pref", Context.MODE_PRIVATE)
        val tasksJson = sharedPref.getString(PREF_KEY_TASKS, "") ?: ""
        return try {
            Gson().fromJson(tasksJson, Array<Task>::class.java).toList()
        } catch (exception: Exception) {
            listOf(
                Task("note 1", "content", false),
                Task("note 2", "lorem ipsum", true),
                Task("note", "lorem ipsum", false),
                Task("note 22", "lorem ipsum", true)
            )
        }
    }
}
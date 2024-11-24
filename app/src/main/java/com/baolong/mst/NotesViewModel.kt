package com.baolong.mst

import android.content.Context
import com.google.gson.Gson

class NotesViewModel(private val context: Context) {
    companion object {
        private const val PREF_KEY_TASKS = "notes"
    }

    fun saveNotes(notes: List<Note>) {
        val sharedPref = context.getSharedPreferences("notes_pref", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(Companion.PREF_KEY_TASKS, Gson().toJson(notes))
            apply()
        }
    }

    fun loadNotes(): List<Note> {
        val sharedPref = context.getSharedPreferences("notes_pref", Context.MODE_PRIVATE)
        val notesJson = sharedPref.getString(Companion.PREF_KEY_TASKS, "") ?: ""
        return try {
            Gson().fromJson(notesJson, Array<Note>::class.java).toList()
        } catch (exception: Exception) {
            listOf(Note("Công thức diện tích hình vuông", "a^2"))
        }
    }
}
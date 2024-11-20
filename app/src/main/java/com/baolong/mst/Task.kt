package com.baolong.mst

data class Task (
    val name: String,
    val content: String,
    var completed: Boolean = true
)
package com.baolong.mst

data class Alarm (
    val label: String,
    val time: String,
    val repeat: List<String>
)
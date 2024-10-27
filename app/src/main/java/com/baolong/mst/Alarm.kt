package com.baolong.mst

import kotlinx.serialization.Serializable

@Serializable
data class Alarm (
    val label: String,
    val time: String,
    val days: List<String>
)
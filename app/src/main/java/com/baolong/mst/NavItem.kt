package com.baolong.mst

data class NavItem(
    val title: String,
    val route: String,
    val selectedIconId: Int,
    val unselectedIconId: Int,
    var unread: Boolean = false,
    val badgeCount: Int? = null
)

package com.baolong.mst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.baolong.mst.ui.theme.MSTTheme

data class NavItem(
    val title: String,
    val route: String,
    val selectedIconId: Int,
    val unselectedIconId: Int,
    var unread: Boolean = false,
    val badgeCount: Int? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MSTTheme {
                val navBarItems = listOf(
                    NavItem(
                        title = "Công việc",
                        route = "tasks",
                        selectedIconId = R.drawable.baseline_task_24,
                        unselectedIconId = R.drawable.outline_task_24
                    ),
                    NavItem(
                        title = "Ghi chú",
                        route = "notes",
                        selectedIconId = R.drawable.baseline_sticky_note_2_24,
                        unselectedIconId = R.drawable.outline_sticky_note_2_24
                    ),
                    NavItem(
                        title = "Tập trung",
                        route = "focus",
                        selectedIconId = R.drawable.baseline_nightlight_24,
                        unselectedIconId = R.drawable.outline_nightlight_24
                    ),
                    NavItem(
                        title = "Cài đặt",
                        route = "settings",
                        selectedIconId = R.drawable.baseline_settings_24,
                        unselectedIconId = R.drawable.outline_settings_24
                    )
                )
                var selectedItemIndex by rememberSaveable { mutableIntStateOf(value = 0) }
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_24),
                                contentDescription = "Add action"
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            navBarItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItemIndex == index,
                                    onClick = {
                                        selectedItemIndex = index
                                        navController.navigate(item.route)
                                    },
                                    label = { Text(text = item.title) },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (item.badgeCount != null) {
                                                    Badge { Text(text = item.badgeCount.toString()) }
                                                } else if (item.unread){
                                                    Badge()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(if (index == selectedItemIndex) item.selectedIconId else item.unselectedIconId),
                                                contentDescription = item.title
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = navBarItems[0].route
                    ) {
                        composable(navBarItems[0].route) { TasksScreen() }
                        composable(navBarItems[1].route) { }
                        composable(navBarItems[2].route) { }
                        composable(navBarItems[3].route) { }
                    }
                }
            }
        }
    }
}

@Composable
fun TasksScreen() {
    val viewModel = TasksViewModel(LocalContext.current)
    val tasks = viewModel.loadTasks().toMutableStateList()

    LazyColumn {
        items(tasks) { task ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val textStyle = if (task.completed) {
                            TextStyle(
                                textDecoration = TextDecoration.LineThrough,
                                color = Color.Gray
                            )
                        } else TextStyle.Default

                        Text(
                            text = task.name,
                            fontSize = 20.sp,
                            style = textStyle
                        )
                        Text(
                            text = task.content,
                            style = textStyle
                        )
                        Text(text = if (task.completed) "Task done!" else "Task not completed")
                    }
                    Checkbox(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        checked = task.completed,
                        onCheckedChange = {
                            tasks[tasks.indexOf(task)] = task.copy(completed = it)
                            viewModel.saveTasks(tasks.toList())
                        }
                    )
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.saveTasks(tasks.toList()) }
    }
}
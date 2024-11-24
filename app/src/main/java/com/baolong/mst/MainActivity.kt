package com.baolong.mst

import android.app.AlertDialog
import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MSTTheme {
                val navBarItems = listOf(
                    NavItem(
                        title = "Nhiệm vụ",
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

                val openDialog = remember { mutableStateOf(false) }
                var inputTitle by remember { mutableStateOf("") }
                var inputTitleValid by remember { mutableStateOf(false) }
                var inputContent by remember { mutableStateOf("") }
                var inputContentValid by remember { mutableStateOf(false) }

                val tasksViewModel = TasksViewModel(LocalContext.current)
                val tasks = tasksViewModel.loadTasks().toMutableStateList()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { openDialog.value = true }
                        ) { Icon(imageVector = Icons.Default.Add, contentDescription = "Add action") }
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
                                                } else if (item.unread){ Badge() }
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
                        composable(navBarItems[0].route) {
                            TasksScreen(tasksViewModel, tasks)
                            if (openDialog.value) { CreateDialog(navBarItems[0].route, openDialog) }
                        }
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
fun TasksScreen(viewModel: TasksViewModel, tasks: SnapshotStateList<Task>) {
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
                        Text(text = if (task.completed) "Đã hoàn thành nhiệm vụ!" else "Nhiệm vụ chưa hoàn thành")
                    }
                    Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                        IconButton(
                            onClick = {
                                tasks.remove(task)
                                viewModel.saveTasks(tasks.toList())
                            }
                        ) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete") }
                        Checkbox(
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
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.saveTasks(tasks.toList()) }
    }
}

@Composable
fun NotesScreen(viewModel: NotesViewModel, notes: SnapshotStateList<Task>) {
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
                        Text(text = if (task.completed) "Đã hoàn thành nhiệm vụ!" else "Nhiệm vụ chưa hoàn thành")
                    }
                    Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                        IconButton(
                            onClick = {
                                tasks.remove(task)
                                viewModel.saveTasks(tasks.toList())
                            }
                        ) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete") }
                        Checkbox(
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
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.saveTasks(tasks.toList()) }
    }
}

@Composable
fun CreateDialog(routeName: String, state: MutableState<Boolean>) {
    when (routeName) {
        "tasks" -> AlertDialog(
            title = { Text(text = "Nhiệm vụ mới") },
            text = {
                Column {
                    Text(text = "Test")
                }
            },
            confirmButton = {
                Button(onClick = { state.value = false }) { Text(text = "Thêm vào") }
            },
            dismissButton = {
                Button(onClick = { state.value = false }) { Text(text = "Hủy bỏ") }
            },
            onDismissRequest = { state.value = false },
        )
        "notes" -> AlertDialog(
            title = { Text(text = "Ghi chú mới") },
            text = {
                Column {
                    Text(text = "Test")
                }
            },
            confirmButton = {
                Button(onClick = { state.value = false }) { Text(text = "Thêm vào") }
            },
            dismissButton = {
                Button(onClick = { state.value = false }) { Text(text = "Hủy bỏ") }
            },
            onDismissRequest = { state.value = false },
        )
    }
}
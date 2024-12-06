package com.baolong.mst

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.OutlinedTextField
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
                title = "Thời gian biểu",
                route = "timetable",
                selectedIconId = R.drawable.baseline_today_24,
                unselectedIconId = R.drawable.outline_today_24
            ),
            NavItem(
                title = "Cài đặt",
                route = "settings",
                selectedIconId = R.drawable.baseline_settings_24,
                unselectedIconId = R.drawable.outline_settings_24
            )
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        setContent {
            MSTTheme {
                var selectedItemIndex by rememberSaveable { mutableIntStateOf(value = 0) }
                val navController = rememberNavController()

                val openBasicDialog = remember { mutableStateOf(false) }
                val openTimetableDialog = remember { mutableStateOf(false) }

                val tasksViewModel = TasksViewModel(LocalContext.current)
                val tasks = tasksViewModel.loadTasks().toMutableStateList()
                val notesViewModel = NotesViewModel(LocalContext.current)
                val notes = notesViewModel.loadNotes().toMutableStateList()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { openBasicDialog.value = true }
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
                            if (openBasicDialog.value) { CreateBasicDialog(navBarItems[0].route, tasks = tasks, state = openBasicDialog) }
                        }
                        composable(navBarItems[1].route) {
                            NotesScreen(notesViewModel, notes)
                            if (openBasicDialog.value) { CreateBasicDialog(navBarItems[1].route, notes = notes, state = openBasicDialog) }
                        }
                        composable(navBarItems[2].route) {
                            TimetableScreen()
                            if (openTimetableDialog.value) { CreateTimetableDialog(state = openTimetableDialog) }
                        }
                        composable(navBarItems[3].route) { SettingsScreen() }
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
fun NotesScreen(viewModel: NotesViewModel, notes: SnapshotStateList<Note>) {
    LazyColumn {
        items(notes) { note ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = note.title,
                            fontSize = 20.sp,
                        )
                        Text(text = note.content)
                    }
                    Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                        IconButton(
                            onClick = {
                                notes.remove(note)
                                viewModel.saveNotes(notes.toList())
                            }
                        ) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete") }
                    }
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.saveNotes(notes.toList()) }
    }
}

@Composable
fun TimetableScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("In development!.")
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("In development!.")
    }
}

@Composable
fun CreateBasicDialog(
    routeName: String,
    tasks: SnapshotStateList<Task>? = null,
    notes: SnapshotStateList<Note>? = null,
    state: MutableState<Boolean>
) {
    var inputTitle by remember { mutableStateOf("") }
    var inputTitleValid by remember { mutableStateOf(false) }
    var inputContent by remember { mutableStateOf("") }
    var inputContentValid by remember { mutableStateOf(false) }

    fun resetInput() {
        inputTitle = ""
        inputTitleValid = false
        inputContent = ""
        inputContentValid = false
    }

    @Composable
    fun DismissButton() {
        Button(onClick = {
            resetInput()
            state.value = false
        }) { Text(text = "Hủy bỏ") }
    }

    when (routeName) {
        "tasks" -> AlertDialog(
            title = { Text(text = "Nhiệm vụ mới") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = {
                            inputTitle = it
                            inputTitleValid = it.isNotEmpty()
                        },
                        label = { Text(text = "Nhiệm vụ") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputContent,
                        onValueChange = {
                            inputContent = it
                            inputContentValid = it.isNotEmpty()
                        },
                        label = { Text(text = "Nội dung") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        tasks?.add(Task(inputTitle, inputContent))
                        resetInput()
                        state.value = false
                    },
                    enabled = inputTitleValid && inputContentValid
                ) { Text(text = "Thêm vào") }
            },
            dismissButton = { DismissButton() },
            onDismissRequest = { resetInput() },
        )
        "notes" -> AlertDialog(
            title = { Text(text = "Ghi chú mới") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = {
                            inputTitle = it
                            inputTitleValid = it.isNotEmpty()
                        },
                        label = { Text(text = "Tiêu đề") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputContent,
                        onValueChange = {
                            inputContent = it
                            inputContentValid = it.isNotEmpty()
                        },
                        label = { Text(text = "Nội dung") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notes?.add(Note(inputTitle, inputContent))
                        resetInput()
                        state.value = false
                    },
                    enabled = inputTitleValid && inputContentValid
                ) { Text(text = "Thêm vào") }
            },
            dismissButton = { DismissButton() },
            onDismissRequest = { resetInput() },
        )
    }
}

@Composable
fun CreateTimetableDialog(state: MutableState<Boolean>) {
    var inputTitle by remember { mutableStateOf("") }
    var inputTitleValid by remember { mutableStateOf(false) }
    var inputContent by remember { mutableStateOf("") }
    var inputContentValid by remember { mutableStateOf(false) }

    fun resetInput() {
        inputTitle = ""
        inputTitleValid = false
        inputContent = ""
        inputContentValid = false
    }

    AlertDialog(
        title = { Text("Thêm lịch trình") },
        text = {
            Column() {
                // TODO: Add time and weekday input
            }
        },
        confirmButton = {
            Button(onClick = {
                // TODO: handle and register with AlarmManager
                resetInput()
                state.value = false
            }) { Text("Thêm vào") }
        },
        dismissButton = {
            Button(onClick = {
                resetInput()
                state.value = false
            }) { Text("Hủy bỏ") }
        },
        onDismissRequest = { resetInput() }
    )
}
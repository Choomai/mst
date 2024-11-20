package com.baolong.mst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.baolong.mst.ui.theme.MSTTheme

fun randomStr(len: Int): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
    return (1..len).map{ charset.random() }.joinToString("")
}

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
                        title = "Tasks",
                        route = "tasks",
                        selectedIconId = R.drawable.baseline_task_24,
                        unselectedIconId = R.drawable.outline_task_24
                    ),
                    NavItem(
                        title = "Notes",
                        route = "notes",
                        selectedIconId = R.drawable.baseline_sticky_note_2_24,
                        unselectedIconId = R.drawable.outline_sticky_note_2_24
                    ),
                    NavItem(
                        title = "Focus",
                        route = "focus",
                        selectedIconId = R.drawable.baseline_nightlight_24,
                        unselectedIconId = R.drawable.outline_nightlight_24
                    ),
                    NavItem(
                        title = "Settings",
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
                        composable(navBarItems[0].route) { NotesScreen() }
                        composable(navBarItems[1].route) { NotesScreen() }
                        composable(navBarItems[2].route) { NotesScreen() }
                        composable(navBarItems[3].route) { NotesScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun NotesScreen() {
    val testNotes = remember { mutableStateListOf(
        Note("note 1", "content", false),
        Note("note 2", "lorem ipsum", true),
        Note("note", "lorem ipsum", false),
        Note("note 22", "lorem ipsum", true)
    ) }
    LazyColumn {
        items(testNotes) { note ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(4.dp, 8.dp)
            ) {
                Column {
                    Text(text = note.title, style = MaterialTheme.typography.headlineLarge)
                    Text(text = note.content)
                    Text(text = if (note.done) "Task done!" else "Task not completed")
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    MSTTheme {
//    }
//}
package com.baolong.mst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.baolong.mst.ui.theme.MSTTheme

fun randomStr(len: Int): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
    return (1..len).map{ charset.random() }.joinToString("")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MSTTheme {

            }
        }
    }
}

@Composable
fun NotesScreen() {
    val testNotes = listOf(
        Note("note 1", "content", false),
        Note("note 2", "lorem ipsum", true),
        Note("note", "lorem ipsum", false),
        Note("note 22", "lorem ipsum", true)
    )
    LazyColumn {
        items(testNotes) { note ->
            ListNote(note)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = "Add action"
            )
        }
    }
}

@Composable
fun ListNote(item: Note) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = item.title, style = MaterialTheme.typography.headlineLarge)
            Text(text = item.content)
            Text(text = item.done.toString())
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MSTTheme {
        NotesScreen()
    }
}
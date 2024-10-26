package com.baolong.mst

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.baolong.mst.ui.theme.MSTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stuff = mutableListOf(
            Alarm("WAKE UP BITCH", "12:00 AM", listOf("Sat", "Sun")),
            Alarm("Okay okay", "1:00 PM", listOf("Mon", "Tue"))
        )
        val createdAlarmToast = Toast.makeText(this, "Alarm created!", Toast.LENGTH_SHORT)

        setContent {
            MSTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        items(stuff) { ListStuff(it) }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    FloatingActionButton(
                        onClick = {
                            createdAlarmToast.show()
                            val newAlarm = Alarm("New Alarm", "12:00 AM", listOf("Mon", "Tue"))
                            stuff.add(element = newAlarm)
                        },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_alarm_24),
                            contentDescription = "Add new alarm"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListStuff(item: Alarm) {
    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.baseline_alarm_24),
                contentDescription = "Airplane mf",
                modifier = Modifier.width(72.dp).height(72.dp).padding()
            )
            Column {
                Text(text = item.label)
                Text(text = item.time)
                Text(text = "Repeat: " + item.days.joinToString(separator = ", "))
            }
        }
    }
}
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import kotlinx.serialization.json.Json
import com.baolong.mst.ui.theme.MSTTheme

fun randomStr(len: Int): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
    return (1..len).map{ charset.random() }.joinToString("")
}

/*class AlarmViewModel: ViewModel() {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    private fun loadAlarms() : List<Alarm> {
        val serializedAlarms = sharedPreferences.getString("alarms", null)
        // Deserialize the serialized list, allow null
        return if (serializedAlarms != null) {
            Json.decodeFromString(serializedAlarms)
        } else { stateListOf(Alarm("New Alarm", "08:00 AM", listOf("Mon", "Tue"))) }
    }
    private fun saveAlarms(serialized: String) {
        with (sharedPreferences.edit()) {
            putString("alarms", serialized)
            apply()
        }
    }

    private val _alarms = mutableStateOf(loadAlarms())
    val alarms: State<MutableList<Alarm>> = _alarms

    fun addAlarm(newAlarm: Alarm) {
        _alarms.value.add(newAlarm)
        saveAlarms(Json.encodeToString(_alarms.value))
    }

    fun removeAlarm(alarm: Alarm) {
        _alarms.value.remove(alarm)
        saveAlarms(Json.encodeToString(_alarms.value))
    }
}*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MSTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn (
                        modifier = Modifier.padding(innerPadding)
                    ) { }
                    Box(modifier = Modifier.fillMaxSize()) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_24),
                                contentDescription = "Add action"
                            )
                        }
                    }
                }
            }
        }
    }
}

/*
@Composable
fun ListAlarm(item: Alarm, alarms: MutableList<Alarm>) {
    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.baseline_alarm_24),
                contentDescription = "Alarm",
                modifier = Modifier.width(48.dp).height(48.dp)
            )
            Column {
                Text(text = item.label)
                Text(text = item.time)
                Text(text = "Repeat: " + item.days.joinToString(separator = ", "))
            }
            Button(
                onClick = { alarms.remove(item) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_highlight_off_24),
                    contentDescription = "Delete alarm"
                )
            }
        }
    }
}*/

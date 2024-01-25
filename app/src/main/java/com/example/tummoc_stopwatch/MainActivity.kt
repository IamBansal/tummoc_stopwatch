package com.example.tummoc_stopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tummoc_stopwatch.ui.theme.Tummoc_stopwatchTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tummoc_stopwatchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StopwatchApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchApp() {
    var isRunning by remember { mutableStateOf(false) }
    var showDrop by remember { mutableStateOf(false) }
    var selectedTimeUnit by remember { mutableStateOf(TimeUnit.SECONDS) }

    var startTime by remember { mutableStateOf(0L) }
    var elapsedTime by remember { mutableStateOf(0L) }

    var countdownValue by remember { mutableStateOf("00:00:00") }
    var countdownMillis by remember { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        TopAppBar(title = { Text(text = "Stopwatch", style = MaterialTheme.typography.headlineMedium,) })

        Spacer(modifier = Modifier.height(80.dp))

        if(countdownValue == "00:00:00:00" || countdownValue == "00:00:00"){
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFF90C1B9), CircleShape)
                    .padding(16.dp)
                    .clickable {
                        showDrop = !showDrop
                    },
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Text(
                        text = if(selectedTimeUnit == TimeUnit.SECONDS) "SECONDS" else "MILLISECONDS",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFf576871)
                    )
                    Icon(imageVector = if(showDrop) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = "drop")
                }

                DropdownMenu(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    expanded = showDrop,
                    onDismissRequest = { showDrop = !showDrop },
                ) {
                    DropdownMenuItem(onClick = {
                        selectedTimeUnit = TimeUnit.SECONDS
                        showDrop = !showDrop
                    }, text = { Text(text = "SECONDS") })
                    DropdownMenuItem(onClick = {
                        selectedTimeUnit = TimeUnit.MILLISECONDS
                        showDrop = !showDrop
                    }, text = { Text(text = "MILLISECONDS") })
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        } else {
            Spacer(modifier = Modifier.height(140.dp))
        }
        Clock(countdownValue = countdownValue)
        Spacer(modifier = Modifier.height(80.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if(isRunning) {isRunning = false}
                else { coroutineScope.launch {
                    isRunning = true
                    startTime = System.currentTimeMillis() - elapsedTime
                    while (isRunning) {
                        delay(10)
                        if (selectedTimeUnit == TimeUnit.SECONDS) {
                            elapsedTime = System.currentTimeMillis() - startTime
                            countdownMillis = 0L
                            countdownValue = formatTime(elapsedTime / 1000)
                        } else {
                            elapsedTime = System.currentTimeMillis() - startTime
                            countdownMillis = elapsedTime
                            countdownValue = formatTimeMillis(elapsedTime)
                        }
                    }
                } }
            }) {
                Text(text = if(isRunning) "PAUSE" else "START")
            }

            Button(onClick = {
                countdownValue = if (selectedTimeUnit == TimeUnit.SECONDS) "00:00:00" else "00:00:00:00"
                isRunning = false
                elapsedTime = 0L
                countdownMillis = 0L
            }) {
                Text(text = "RESET")
            }
        }
    }
}

@Composable
fun Clock(countdownValue: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, Color(0xFF90C1B9), CircleShape)
            .background(Color(0xFFd2e4e1))
            .padding(20.dp)
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = countdownValue,
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFf576871)
        )
    }
}

fun formatTime(timeInSeconds: Long): String {
    val hours = timeInSeconds / 360
    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun formatTimeMillis(timeInMillis: Long): String {
    val hours = timeInMillis / 3600000
    val minutes = (timeInMillis % 3600000) / 60000
    val seconds = (timeInMillis % 60000) / 1000
    val milliseconds = timeInMillis % 1000
    return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds)
}

enum class TimeUnit {
    SECONDS,
    MILLISECONDS
}

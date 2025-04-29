package com.example.moodplant.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodplant.R

class MoodPickerScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoodPickerUI { mood ->
                saveMoodAndNavigate(mood)
            }
        }

        // 🌟 Schedule notification reminder every 1 hour
        scheduleMoodReminder()

        // 🌟 Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }

    private fun scheduleMoodReminder() {
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intervalMillis = 60 * 60 * 1000L // 1 hour

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }

    private fun saveMoodAndNavigate(mood: String) {
        val prefs = getSharedPreferences("MoodPlantPrefs", Context.MODE_PRIVATE)
        val existingHistory = prefs.getString("moodHistory", "")
        val newHistory = if (existingHistory.isNullOrEmpty()) mood else "$existingHistory,$mood"

        prefs.edit()
            .putString("moodHistory", newHistory)
            .putString("mood", mood)
            .apply()

        MediaPlayer.create(this, R.raw.plant_sound)?.apply {
            start()
            setOnCompletionListener { release() }
        }


        val intent = Intent(this, PlantDisplayScreen::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun MoodPickerUI(onMoodSelected: (String) -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E7))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "How are you feeling today?",
            fontSize = 26.sp,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        MoodButton("😊 Happy") { onMoodSelected("happy") }
        Spacer(modifier = Modifier.height(12.dp))
        MoodButton("😡 Angry") { onMoodSelected("angry") }
        Spacer(modifier = Modifier.height(12.dp))
        MoodButton("😴 Tired") { onMoodSelected("tired") }
        Spacer(modifier = Modifier.height(12.dp))
        MoodButton("🤩 Excited") { onMoodSelected("excited") }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { context.startActivity(Intent(context, WeeklyResultScreen::class.java)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF), contentColor = Color.White)
        ) {
            Text("🌱 View Weekly Plant", fontSize = 18.sp)
        }
    }
}

@Composable
fun MoodButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF), contentColor = Color.White)
    ) {
        Text(text, fontSize = 18.sp)
    }
}

package com.example.moodplant.ui.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodplant.R

class WeeklyResultScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("MoodPlantPrefs", Context.MODE_PRIVATE)
        val moodHistoryString = prefs.getString("moodHistory", "") ?: ""

        val moodList = if (moodHistoryString.isNotEmpty()) {
            moodHistoryString.split(",")
        } else {
            emptyList()
        }

        val moodCounts = moodList.groupingBy { it }.eachCount()
        val maxCount = moodCounts.maxOfOrNull { it.value } ?: 0
        val mostFrequentMoods = moodList.distinct().filter { mood ->
            moodCounts[mood] == maxCount
        }

        Log.d("WeeklyDebug", "Mood History String: $moodHistoryString")
        Log.d("WeeklyDebug", "Mood List: $moodList")
        Log.d("WeeklyDebug", "Mood Counts: $moodCounts")

        setContent {
            WeeklyResultUI(
                moods = mostFrequentMoods,
                onStartNewWeek = {
                    prefs.edit()
                        .remove("moodHistory")
                        .remove("mood")
                        .apply()
                    finish()
                },
                onBackToHome = {
                    finish()
                }
            )
        }
    }
}

@Composable
fun WeeklyResultUI(
    moods: List<String>,
    onStartNewWeek: () -> Unit,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E7))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Weekly Mood Plant!",
                    fontSize = 26.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(moods.size) { index ->
                val mood = moods[index]

                var startAnimation by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (startAnimation) 1f else 0f,
                    animationSpec = tween(durationMillis = 800)
                )

                LaunchedEffect(Unit) {
                    startAnimation = true
                }

                Image(
                    painter = painterResource(id = moodToPlantImage(mood)),
                    contentDescription = "Mood Plant",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .padding(vertical = 4.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF),
                        contentColor = Color.White
                    )
                ) {
                    Text("🔙 Back to Home", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onStartNewWeek,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF),
                        contentColor = Color.White
                    )
                ) {
                    Text("🌱 Start New Week", fontSize = 18.sp)
                }
            }
        }
    }
}

fun moodToPlantImage(mood: String): Int {
    return when (mood) {
        "happy" -> R.drawable.plant_happy
        "angry" -> R.drawable.plant_angry
        "tired" -> R.drawable.plant_tired
        "excited" -> R.drawable.plant_excited
        else -> R.drawable.plant_happy
    }
}

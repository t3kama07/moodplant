package com.example.moodplant.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodplant.R

class PlantDisplayScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("MoodPlantPrefs", Context.MODE_PRIVATE)
        val mood = prefs.getString("mood", null) ?: "happy"

        setContent {
            PlantDisplayUI(mood)
        }
    }
}

@Composable
fun PlantDisplayUI(mood: String) {
    val plantImage = when (mood) {
        "happy" -> R.drawable.plant_happy
        "angry" -> R.drawable.plant_angry
        "tired" -> R.drawable.plant_tired
        "excited" -> R.drawable.plant_excited
        else -> R.drawable.plant_happy
    }

    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) { startAnimation = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E7))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = plantImage),
            contentDescription = "Plant Mood",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(250.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                val intent = Intent(context, MoodPickerScreen::class.java)
                context.startActivity(intent)
                (context as? ComponentActivity)?.finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF), contentColor = Color.White)
        ) {
            Text("🔙 Back to Home", fontSize = 18.sp)
        }
    }
}

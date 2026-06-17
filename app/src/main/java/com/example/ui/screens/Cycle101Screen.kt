package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

enum class EducationTopic {
    CYCLE_BASICS, PCOS_DECODER
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Cycle101Screen() {
    var selectedTopic by remember { mutableStateOf(EducationTopic.CYCLE_BASICS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            "Education Hub", 
            style = MaterialTheme.typography.headlineLarge, 
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Empower yourself to be the best supportive partner by understanding the science.", 
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Topic Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTopic == EducationTopic.CYCLE_BASICS,
                onClick = { selectedTopic = EducationTopic.CYCLE_BASICS },
                label = { Text("Cycle Basics") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
            )
            FilterChip(
                selected = selectedTopic == EducationTopic.PCOS_DECODER,
                onClick = { selectedTopic = EducationTopic.PCOS_DECODER },
                label = { Text("PCOS Decoder") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated Content Area
        AnimatedContent(
            targetState = selectedTopic,
            transitionSpec = {
                val duration = 400
                if (targetState > initialState) {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() with slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() with slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "Topic Transition"
        ) { topic ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (topic) {
                    EducationTopic.CYCLE_BASICS -> CycleBasicsCard()
                    EducationTopic.PCOS_DECODER -> PcosDecoderCard()
                }
            }
        }
    }
}

@Composable
fun CycleBasicsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Cycle & Hormone Flow", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "The cycle is driven by two main hormones: Estrogen and Progesterone. Notice how they peak at different times, shifting her energy and mood.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            HormoneCurveChart()
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem("Estrogen (Energy)", Color(0xFFEC4899)) // PinkPrimary
                LegendItem("Progesterone (Rest)", Color(0xFFFDBA74)) // MascotOrange
            }
        }
    }
}

@Composable
fun PcosDecoderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧬", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "The PCOS Decoder", 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Insulin Resistance (IR) is common in PCOS. Her body has to work 10x harder to process carbohydrates right now, which is why the fatigue is real. It's not laziness; it's a biological crash.\n\n" +
                "Action: Offer protein-rich snacks when she reports sugar cravings. Keep the pressure low.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun HormoneCurveChart() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFDF2F8)) // PinkLight
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Draw grid lines
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(0f, h / 2),
                end = Offset(w, h / 2),
                strokeWidth = 2f
            )

            // Estrogen Curve (Peaks around day 12-14)
            val estrogenPath = Path()
            estrogenPath.moveTo(0f, h * 0.9f)
            for (i in 0..100) {
                val x = w * (i / 100f)
                val normalizedX = (i / 100f) * Math.PI * 2
                // A pulse that peaks at ~35% of the width
                val y = h * 0.9f - (Math.exp(-Math.pow((i - 35.0) / 15.0, 2.0)) * h * 0.8f).toFloat()
                if (i == 0) estrogenPath.moveTo(x, y) else estrogenPath.lineTo(x, y)
            }
            
            // Progesterone Curve (Peaks around day 21)
            val progesteronePath = Path()
            progesteronePath.moveTo(0f, h * 0.9f)
            for (i in 0..100) {
                val x = w * (i / 100f)
                // A broader peak around 70% of the width
                val y = h * 0.9f - (Math.exp(-Math.pow((i - 70.0) / 20.0, 2.0)) * h * 0.6f).toFloat()
                if (i == 0) progesteronePath.moveTo(x, y) else progesteronePath.lineTo(x, y)
            }

            drawPath(
                path = estrogenPath,
                color = Color(0xFFEC4899), // Pink Primary
                style = Stroke(
                    width = 8f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawPath(
                path = progesteronePath,
                color = Color(0xFFFDBA74), // Mascot Orange
                style = Stroke(
                    width = 8f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            
            // Phase markers
            drawLine(color = Color.Gray.copy(alpha = 0.3f), start = Offset(w * 0.45f, 0f), end = Offset(w * 0.45f, h), strokeWidth = 2f)
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


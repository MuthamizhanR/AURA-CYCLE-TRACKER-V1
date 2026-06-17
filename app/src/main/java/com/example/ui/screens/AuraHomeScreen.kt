package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AuraUiState
import com.example.engine.CyclePhase
import com.example.ui.components.CanvasBunny
import com.example.ui.theme.*

@Composable
fun AuraHomeScreen(uiState: AuraUiState, onSettingsClick: () -> Unit, onLogClick: () -> Unit) {
    val phaseStr = when (uiState.currentPhase) {
        CyclePhase.FOLLICULAR -> "follicular"
        CyclePhase.OVULATORY -> "ovulation"
        CyclePhase.LUTEAL -> "luteal"
        CyclePhase.MENSTRUAL -> "menstrual"
        else -> "follicular"
    }

    val mood = when (phaseStr) {
        "follicular" -> "energetic"
        "ovulation" -> "glowing"
        "luteal" -> "cozy"
        "menstrual" -> "resting"
        else -> "energetic"
    }

    val accent = when (phaseStr) {
        "follicular" -> GoldFollicular
        "ovulation" -> MintOvulation
        "luteal" -> LavenderLuteal
        else -> RoseMenstrual
    }

    val tint = when (phaseStr) {
        "follicular" -> TintFollicular
        "ovulation" -> TintOvulation
        "luteal" -> TintLuteal
        else -> TintMenstrual
    }

    val label = when (phaseStr) {
        "follicular" -> "Follicular"
        "ovulation" -> "Ovulation"
        "luteal" -> "Luteal"
        "menstrual" -> "Menstrual"
        else -> "Phase"
    }

    // Default to day 1 if null, or calculate
    val dayLabel = "Day ${uiState.currentCycle?.startDate?.let { (System.currentTimeMillis() - it) / (1000 * 60 * 60 * 24).toInt() + 1 } ?: 1}"
    val greeting = when(phaseStr) {
        "follicular" -> "feeling fresh today"
        "ovulation" -> "main character energy"
        "luteal" -> "taking it slow today"
        "menstrual" -> "be extra gentle today"
        else -> "how are you?"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PorcelainBg)
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🐰 cozycycle",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PlumText
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Current phase →",
            fontSize = 12.sp,
            color = PlumSoft
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Follicular", "Ovulation", "Luteal", "Menstrual").forEach { p ->
                val isSelected = p.equals(label, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) accent else Color.White)
                        .border(1.dp, if (isSelected) accent else Color(0xFFF4E3E9), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        p,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) PlumText else PlumSoft
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(tint),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CanvasBunny(
                    phase = phaseStr,
                    mood = mood,
                    modifier = Modifier.size(240.dp),
                    accentColor = accent
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Hi there 🌷",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlumText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$label · $dayLabel · $greeting",
                    fontSize = 14.sp,
                    color = PlumSoft
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onLogClick,
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Daily check-in", color = if (phaseStr == "follicular") PlumText else Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Month Long Data Visualization
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CleanWhite)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Cycle Data Visualizer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PlumText)
                Spacer(modifier = Modifier.height(16.dp))
                
                val projection = uiState.monthProjection
                val chunks = projection.chunked(7)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    chunks.forEach { rowDays ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            rowDays.forEach { day ->
                                val color = when (day.projectedPhase) {
                                    CyclePhase.MENSTRUAL -> RoseMenstrual
                                    CyclePhase.FOLLICULAR -> GoldFollicular
                                    CyclePhase.OVULATORY -> MintOvulation
                                    CyclePhase.LUTEAL -> LavenderLuteal
                                    else -> Color.LightGray
                                }
                                val isToday = day.dayOfCycle == 1 
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(color.copy(alpha = if (isToday) 1f else 0.35f))
                                        .border(
                                            width = if (isToday) 2.dp else 0.dp,
                                            color = if (isToday) PlumText else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${day.dayOfCycle}", 
                                        fontSize = 12.sp, 
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = PlumText
                                    )
                                }
                            }
                            if (rowDays.size < 7) {
                                repeat(7 - rowDays.size) {
                                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    HomeScreenLegendItem("Menstrual", RoseMenstrual)
                    HomeScreenLegendItem("Follicular", GoldFollicular)
                    HomeScreenLegendItem("Ovulatory", MintOvulation)
                    HomeScreenLegendItem("Luteal", LavenderLuteal)
                }
            }
        }
    }
}

@Composable
fun HomeScreenLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = PlumText)
    }
}

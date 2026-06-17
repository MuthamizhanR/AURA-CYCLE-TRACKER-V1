package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AuraUiState
import com.example.engine.CyclePhase
import com.example.engine.DailyProjection
import com.example.ui.components.BunnyMascot

@Composable
fun PartnerDashboardScreen(uiState: AuraUiState, onSettingsClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BunnyMascot(
                    phase = uiState.currentPhase,
                    modifier = Modifier.size(160.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Month-Horizon Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        item {
            MonthCalendar(uiState.monthProjection, uiState.currentPhase)
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            TacticalBriefing(uiState.currentPhase)
        }
    }
}

@Composable
fun MonthCalendar(projection: List<DailyProjection>, currentPhase: CyclePhase) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.CleanWhite)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("30-Day Projection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.PlumText)
            Spacer(modifier = Modifier.height(16.dp))
            
            val chunks = projection.chunked(7)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                chunks.forEach { rowDays ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        rowDays.forEach { day ->
                            val color = when (day.projectedPhase) {
                                CyclePhase.MENSTRUAL -> com.example.ui.theme.RoseMenstrual
                                CyclePhase.FOLLICULAR -> com.example.ui.theme.GoldFollicular
                                CyclePhase.OVULATORY -> com.example.ui.theme.MintOvulation
                                CyclePhase.LUTEAL -> com.example.ui.theme.LavenderLuteal
                                else -> Color.LightGray
                            }
                            // Highlight "Today" (day 1 for demo purposes)
                            val isToday = day.dayOfCycle == 1 
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(alpha = if (isToday) 1f else 0.35f))
                                    .border(
                                        width = if (isToday) 2.dp else 0.dp,
                                        color = if (isToday) com.example.ui.theme.PlumText else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${day.dayOfCycle}", 
                                    fontSize = 12.sp, 
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = com.example.ui.theme.PlumText
                                )
                            }
                        }
                        // Fill empty cells if row < 7
                        if (rowDays.size < 7) {
                            repeat(7 - rowDays.size) {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LegendDot("Menstrual", com.example.ui.theme.RoseMenstrual)
                LegendDot("Follicular", com.example.ui.theme.GoldFollicular)
                LegendDot("Ovulatory", com.example.ui.theme.MintOvulation)
                LegendDot("Luteal", com.example.ui.theme.LavenderLuteal)
            }
        }
    }
}

@Composable
fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TacticalBriefing(phase: CyclePhase) {
    val phaseName = when(phase) {
        CyclePhase.MENSTRUAL -> "Phase 1: Menstrual (Deep Rest Window)"
        CyclePhase.FOLLICULAR -> "Phase 2: Follicular (Brainpower & Momentum)"
        CyclePhase.OVULATORY -> "Phase 3: Ovulatory (Peak Glow)"
        CyclePhase.LUTEAL -> "Phase 4: Luteal (Metabolic Shift & Protection)"
        else -> "Phase Unknown"
    }

    val dos = when(phase) {
        CyclePhase.MENSTRUAL -> listOf(
            "Take over 100% of high-effort physical household chores (laundry, grocery runs) without being asked.",
            "Prepare warm, iron-rich meals (stews, dark leafy greens, or dark chocolate) to replenish blood loss.",
            "Keep physical heating pads charged and available."
        )
        CyclePhase.FOLLICULAR -> listOf(
            "Plan creative, highly interactive date nights, try new restaurants, or book outdoor activities.",
            "Engage in deep, complex conversations about future plans or shared projects.",
            "Match her outward social energy and support her goals."
        )
        CyclePhase.OVULATORY -> listOf(
            "Verbally validate and praise her work, style, and presence—her confidence is naturally high.",
            "Coordinate social gatherings, group activities, or public events."
        )
        CyclePhase.LUTEAL -> listOf(
            "Keep complex, slow-burning carbohydrates accessible (sweet potatoes, berries, oats) to stabilize her blood sugar.",
            "Turn down the household thermostat at night; rising progesterone naturally elevates baseline body temperature.",
            "Validate all expressions of anxiety or overwhelm as entirely real and biological."
        )
        else -> emptyList()
    }

    val donts = when(phase) {
        CyclePhase.MENSTRUAL -> listOf(
            "Do not schedule intensive social gatherings, crowded family events, or high-energy outings.",
            "Do not suggest strenuous workouts or long walks; prioritize pure, unstructured recovery time."
        )
        CyclePhase.FOLLICULAR -> listOf(
            "Do not box her into a stagnant, boring routine; this phase thrives on novelty and exploration."
        )
        CyclePhase.OVULATORY -> listOf(
            "Do not dismiss or minimize her communication bursts or high productivity; clear paths for her."
        )
        CyclePhase.LUTEAL -> listOf(
            "Do not take sudden mood shifts, irritability, or withdrawal personally.",
            "Do not bring home highly refined, white-sugar treats to prevent intense spike-and-crash cycles.",
            "Do not use logical debugging or say 'calm down' when she expresses overwhelm."
        )
        else -> emptyList()
    }

    Text(
        phaseName,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)) // Light green for DOs
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = "Do", tint = Color(0xFF689F38))
                Spacer(modifier = Modifier.width(8.dp))
                Text("What To Do", style = MaterialTheme.typography.titleMedium, color = Color(0xFF33691E), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            dos.forEach { 
                Text("• $it", color = Color(0xFF33691E), modifier = Modifier.padding(bottom = 8.dp), lineHeight = 20.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Light red for DONTs
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Close, contentDescription = "Don't", tint = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.width(8.dp))
                Text("What NOT To Do", style = MaterialTheme.typography.titleMedium, color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            donts.forEach { 
                Text("• $it", color = Color(0xFFB71C1C), modifier = Modifier.padding(bottom = 8.dp), lineHeight = 20.sp)
            }
        }
    }
}

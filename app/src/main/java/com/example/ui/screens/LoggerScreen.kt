package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AuraUiState
import com.example.engine.CyclePhase
import com.example.ui.components.CanvasBunny
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

data class LogCard(
    val id: String,
    val question: String,
    val options: List<String>
)

val CARDS = listOf(
    LogCard("battery", "How's your battery today?", listOf("🪫 Empty", "🔋 Low", "⚡ Good", "🚀 Full")),
    LogCard("cravings", "Craving something sweet?", listOf("👅 Not really", "🍫 Yes please")),
    LogCard("skin", "Any skin changes?", listOf("🔴 Breakout", "🌱 A few spots", "✨ Clear")),
    LogCard("mood", "Mood check", listOf("😡 Irritable", "😢 Low", "😐 Meh", "😊 Good"))
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoggerScreen(uiState: AuraUiState, onLogComplete: (Int, String, String, String, Float?, String?) -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    val answers = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    val phaseStr = when (uiState.currentPhase) {
        CyclePhase.FOLLICULAR -> "follicular"
        CyclePhase.OVULATORY -> "ovulation"
        CyclePhase.LUTEAL -> "luteal"
        CyclePhase.MENSTRUAL -> "menstrual"
        else -> "follicular"
    }
    
    val tint = when (phaseStr) {
        "follicular" -> TintFollicular
        "ovulation" -> TintOvulation
        "luteal" -> TintLuteal
        else -> TintMenstrual
    }

    val done = currentIndex >= CARDS.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PorcelainBg)
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Text(
            "Today's check-in",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PlumText
        )
        Text(
            if (done) "All done for today" else "${currentIndex + 1} of ${CARDS.size} · swipe or tap",
            fontSize = 12.sp,
            color = PlumSoft
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(tint),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                // Done state
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CanvasBunny(
                        phase = phaseStr,
                        mood = "celebrating",
                        celebrating = true,
                        modifier = Modifier.size(160.dp),
                        accentColor = MintOvulation // generic celebratory accent
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Logged! 🎉", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PlumText)
                    Text("The bunny says thanks", fontSize = 12.sp, color = PlumSoft)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        answers.chunked(2).forEach { chunk ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                chunk.forEach { answer ->
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFFF0D8DF), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(answer, fontSize = 12.sp, color = PlumText)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            var e = 5
                            var c = "NONE"
                            var m = "STABLE"
                            val s = "CLEAR"
                            
                            answers.forEach { ans ->
                                if (ans.contains("Empty")) e = 1
                                if (ans.contains("Full")) e = 10
                                if (ans.contains("Yes please")) c = "SUGAR"
                                if (ans.contains("Irritable")) m = "IRRITABLE"
                            }
                            
                            onLogComplete(e, c, s, m, null, null)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PetalPrimary)
                    ) {
                        Text("Save & Close", color = PlumText, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Stack of cards
                val visibleCards = CARDS.drop(currentIndex).take(2).reversed()
                
                visibleCards.forEachIndexed { i, card ->
                    val isTop = card.id == CARDS[currentIndex].id
                    
                    val animatedOffsetX by animateFloatAsState(
                        targetValue = if (isTop) offsetX else 0f,
                        animationSpec = tween(if (offsetX == 0f) 300 else 0), label = "offsetX"
                    )
                    
                    val scale by animateFloatAsState(
                        targetValue = if (isTop) 1f else 0.94f,
                        animationSpec = tween(300), label = "scale"
                    )
                    
                    val yOffset by animateFloatAsState(
                        targetValue = if (isTop) 0f else 40f,
                        animationSpec = tween(300), label = "yOffset"
                    )

                    val rotation = animatedOffsetX / 20f

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .graphicsLayer {
                                translationX = animatedOffsetX
                                translationY = yOffset
                                scaleX = scale
                                scaleY = scale
                                rotationZ = if (isTop) rotation else 0f
                            }
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .then(
                                if (isTop) {
                                    Modifier.pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragEnd = {
                                                if (offsetX > 300f) {
                                                    // Swiped right (last option)
                                                    answers.add(card.options.last())
                                                    offsetX = 1000f
                                                    coroutineScope.launch { delay(200); currentIndex++; offsetX = 0f }
                                                } else if (offsetX < -300f) {
                                                    // Swiped left (first option)
                                                    answers.add(card.options.first())
                                                    offsetX = -1000f
                                                    coroutineScope.launch { delay(200); currentIndex++; offsetX = 0f }
                                                } else {
                                                    offsetX = 0f
                                                }
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                offsetX += dragAmount.x
                                            }
                                        )
                                    }
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (isTop) {
                                val rightOverlay = (offsetX / 300f).coerceIn(0f, 1f)
                                val leftOverlay = (-offsetX / 300f).coerceIn(0f, 1f)
                                
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    if (rightOverlay > 0) {
                                        Icon(Icons.Rounded.Check, contentDescription = "Yes", tint = MintOvulation, modifier = Modifier.size(32.dp).align(Alignment.TopStart).alpha(rightOverlay))
                                    }
                                    if (leftOverlay > 0) {
                                        Icon(Icons.Rounded.Close, contentDescription = "No", tint = RoseMenstrual, modifier = Modifier.size(32.dp).align(Alignment.TopEnd).alpha(leftOverlay))
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                card.question,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlumText,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                card.options.forEachIndexed { optIndex, opt ->
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFFF0D8DF), RoundedCornerShape(24.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(opt, fontSize = 14.sp, color = PlumText)
                                    }
                                }
                            }
                            
                            if (isTop) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = { 
                                        answers.add(card.options.first())
                                        offsetX = -1000f
                                        coroutineScope.launch { delay(200); currentIndex++; offsetX = 0f }
                                    }) { Text("Nope", color = PlumSoft) }
                                    
                                    TextButton(onClick = { 
                                        answers.add(card.options.last())
                                        offsetX = 1000f
                                        coroutineScope.launch { delay(200); currentIndex++; offsetX = 0f }
                                    }) { Text("Yes", color = PlumSoft) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

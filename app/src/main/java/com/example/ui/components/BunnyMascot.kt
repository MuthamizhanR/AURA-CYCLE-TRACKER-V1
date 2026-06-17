package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.engine.CyclePhase

@Composable
fun BunnyMascot(
    phase: CyclePhase,
    modifier: Modifier = Modifier
) {
    val animationResId = when (phase) {
        CyclePhase.FOLLICULAR, CyclePhase.OVULATORY -> R.raw.bunny_hopping
        CyclePhase.LUTEAL -> R.raw.bunny_eating
        CyclePhase.MENSTRUAL -> R.raw.bunny_sleeping
        else -> R.raw.bunny_resting
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResId))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

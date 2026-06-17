package com.example.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AuraRepository
import com.example.data.CycleTracker
import com.example.data.DailyLog
import com.example.data.UserBaseline
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DailyProjection(
    val dayOfCycle: Int,
    val projectedPhase: CyclePhase,
    val confidenceScore: Int,
    val metabolicState: String
)

data class AuraUiState(
    val baseline: UserBaseline? = null,
    val currentCycle: CycleTracker? = null,
    val recentLogs: List<DailyLog> = emptyList(),
    val currentPhase: CyclePhase = CyclePhase.UNKNOWN,
    val partnerVibeMessage: String = "No data yet",
    val auraColor: Long = 0xFFF8BBD0, // Pink/pastel orange
    val confidence: Int = 0,
    val monthProjection: List<DailyProjection> = emptyList()
)

enum class CyclePhase {
    FOLLICULAR, OVULATORY, LUTEAL, MENSTRUAL, UNKNOWN
}

class AuraViewModel(private val repository: AuraRepository) : ViewModel() {

    val uiState: StateFlow<AuraUiState> = combine(
        repository.userBaseline,
        repository.currentCycleFlow,
        repository.recentLogs
    ) { baseline, cycle, logs ->
        calculateAuraState(baseline, cycle, logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuraUiState())

    private fun calculateAuraState(baseline: UserBaseline?, cycle: CycleTracker?, logs: List<DailyLog>): AuraUiState {
        if (baseline == null || cycle == null) return AuraUiState(baseline, cycle, logs)

        // Baseline stats
        val whtR = if (baseline.heightCm > 0) baseline.waistCm / baseline.heightCm else 0f
        val hasHyperinsulinemiaRisk = whtR > 0.5f

        // Scientific Engine Logic
        var currentPhase = CyclePhase.UNKNOWN
        var partnerMsg = "Your body is building momentum today. Lean into that energy! 🐰🌸"
        var color = 0xFFF472B6 // MascotPink
        var conf = 10

        val cycleLengthMs = System.currentTimeMillis() - cycle.startDate
        val days = (cycleLengthMs / (1000 * 60 * 60 * 24)).toInt()

        val monthProjection = (1..30).map { day ->
            val phase = when {
                day in 1..5 -> CyclePhase.MENSTRUAL
                day in 6..13 -> CyclePhase.FOLLICULAR
                day in 14..16 -> CyclePhase.OVULATORY
                day in 17..28 -> CyclePhase.LUTEAL
                else -> CyclePhase.MENSTRUAL
            }
            val meta = if (phase == CyclePhase.LUTEAL && hasHyperinsulinemiaRisk) "Insulin Crash Warning" else "Normal"
            DailyProjection(day, phase, if (logs.isNotEmpty()) 90 else 50, meta)
        }

        // Very basic mock Bayesian fallback since we need to map Tier1 to Clinical Phase
        if (logs.isNotEmpty()) {
            val lastLog = logs.first() // Assuming ordered DESC
            if (lastLog.tier2LhStrip == "PEAK") {
                currentPhase = CyclePhase.OVULATORY
                partnerMsg = "You are glowing from the inside out today. 🐰✨"
                color = 0xFFFDBA74 // MascotOrange
                conf = 95
            } else if (lastLog.tier1Mood == "IRRITABLE" || lastLog.tier1Cravings == "SUGAR") {
                 currentPhase = CyclePhase.LUTEAL
                 partnerMsg = "Your body is working hard behind the scenes. It's okay to slow down. 🐰🍫"
                 color = 0xFF93C5FD // Soft blue/purple for cozy vibe
                 conf = if (hasHyperinsulinemiaRisk) 85 else 60
                 if (hasHyperinsulinemiaRisk && lastLog.tier1Cravings == "SUGAR") {
                     partnerMsg = "Heavy sugar cravings reported. Partner action: prep low-GI snacks, high metabolic fatigue. 🐰🍫"
                 }
            } else if (days < 5) {
                currentPhase = CyclePhase.MENSTRUAL
                partnerMsg = "Resting under a fluffy pink blanket. 🐰💤"
                color = 0xFFFCA5A5 // Reddish
                conf = 90
            } else {
                currentPhase = CyclePhase.FOLLICULAR
                partnerMsg = "Your body is building momentum today. Lean into that energy! 🐰🌸"
                color = 0xFFF472B6 // MascotPink
                conf = 50
            }
        }

        return AuraUiState(
            baseline = baseline,
            currentCycle = cycle,
            recentLogs = logs,
            currentPhase = currentPhase,
            partnerVibeMessage = partnerMsg,
            auraColor = color,
            confidence = conf,
            monthProjection = monthProjection
        )
    }

    fun updateAppMode(newMode: String) {
        viewModelScope.launch {
            val current = uiState.value.baseline
            if (current != null) {
                repository.saveBaseline(current.copy(appMode = newMode))
            }
        }
    }

    fun updatePartnerEmail(email: String) {
        viewModelScope.launch {
            val current = uiState.value.baseline
            if (current != null) {
                repository.saveBaseline(current.copy(partnerEmail = email))
            }
        }
    }

    fun saveBaseline(age: Int, height: Float, weight: Float, waist: Float, email: String, appMode: String) {
        viewModelScope.launch {
            repository.saveBaseline(UserBaseline(1, age, height, weight, waist, email, appMode))
            var cycleId = repository.getCurrentCycleId()
            if (cycleId == null) {
                repository.startNewCycle()
            }
        }
    }

    fun logDaily(energy: Int, craves: String, skin: String, mood: String, bbt: Float?, lh: String?) {
        viewModelScope.launch {
            val cycleId = repository.getCurrentCycleId() ?: repository.startNewCycle().cycleId
            repository.logDailySymptom(DailyLog(
                cycleId = cycleId,
                tier1Energy = energy,
                tier1Cravings = craves,
                tier1Skin = skin,
                tier1Mood = mood,
                tier2Bbt = bbt,
                tier2LhStrip = lh
            ))
            
            // "P2P PUSH": In a real app this encrypts via Tink and uses Gmail API.
            // Based on user prompt: "The Partner 'Hubby' Dashboard: Background Fetch... The partner's UI updates"
            // Since this is embedded in one app to comply with "embedded in one app", we just store it in Room 
            // and the partner tab will read `uiState.partnerVibeMessage`.
        }
    }
}

class AuraViewModelFactory(private val repository: AuraRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

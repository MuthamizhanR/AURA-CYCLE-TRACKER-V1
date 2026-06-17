package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.engine.AuraUiState

@Composable
fun ClinicalExportScreen(uiState: AuraUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = "Clinical PDF",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Clinical Export",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("6-Month Summary", style = MaterialTheme.typography.titleMedium)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Total Logs Available: ${uiState.recentLogs.size}")
                Text("Baseline Waist/Height Risk: ${if (uiState.baseline?.let { it.heightCm > 0 && it.waistCm / it.heightCm > 0.5f } == true) "High (Possible Hyperinsulinemia)" else "Normal"}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("The algorithm predicts cycle phases using Rotterdam Criteria weights based on your Tier 1 inputs.")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { /* Generate PDF Intent */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Generate PDF Report")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("Data Management", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { /* Backup to Drive */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Backup to Google Drive")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { /* Restore from Cloud */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Restore from Cloud Vault")
        }
    }
}

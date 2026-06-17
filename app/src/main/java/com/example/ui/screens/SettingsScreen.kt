package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.engine.AuraViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AuraViewModel, onNavigateBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appMode = uiState.baseline?.appMode ?: "SINGLE"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text("App Mode", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = appMode == "SINGLE",
                    onClick = { viewModel.updateAppMode("SINGLE") },
                    label = { Text("Single") }
                )
                FilterChip(
                    selected = appMode == "COUPLED",
                    onClick = { viewModel.updateAppMode("COUPLED") },
                    label = { Text("Coupled") }
                )
                FilterChip(
                    selected = appMode == "PARTNER",
                    onClick = { viewModel.updateAppMode("PARTNER") },
                    label = { Text("Partner") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            if (appMode == "COUPLED") {
                Text("Partner Sync", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                var emailInput by remember { mutableStateOf(uiState.baseline?.partnerEmail ?: "") }
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Partner's Email ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.updatePartnerEmail(emailInput) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Partner Email")
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(32.dp))
            }

            Text("Data Management", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            var googleSignStatus by remember { mutableStateOf("Not Connected") }

            OutlinedButton(
                onClick = { 
                    googleSignStatus = "Requires google-services.json & Web Client ID"
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Connect Google Account for Backup ($googleSignStatus)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Restore logic */ },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Restore from Cloud Vault")
            }
        }
    }
}

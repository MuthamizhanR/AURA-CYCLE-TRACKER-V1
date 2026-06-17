package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onComplete: (Int, Float, Float, Float, String, String) -> Unit) {
    var age by remember { mutableStateOf("25") }
    var height by remember { mutableStateOf("165.0") }
    var weight by remember { mutableStateOf("65.0") }
    var waist by remember { mutableStateOf("80.0") }
    var partnerEmail by remember { mutableStateOf("") }
    var appMode by remember { mutableStateOf("SINGLE") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Aura", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Let's establish your clinical baseline.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("How will you be using this app?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = appMode == "SINGLE",
                onClick = { appMode = "SINGLE" },
                label = { Text("Single") }
            )
            FilterChip(
                selected = appMode == "COUPLED",
                onClick = { appMode = "COUPLED" },
                label = { Text("Coupled") }
            )
            FilterChip(
                selected = appMode == "PARTNER",
                onClick = { appMode = "PARTNER" },
                label = { Text("Partner") }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (appMode != "PARTNER") {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = waist,
                onValueChange = { waist = it },
                label = { Text("Waist Circumference (cm) - Optional") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (appMode != "SINGLE") {
            OutlinedTextField(
                value = partnerEmail,
                onValueChange = { partnerEmail = it },
                label = { Text(if (appMode == "PARTNER") "Your Email" else "Partner's Email (For P2P Sync)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                onComplete(
                    age.toIntOrNull() ?: 25,
                    height.toFloatOrNull() ?: 165f,
                    weight.toFloatOrNull() ?: 65f,
                    waist.toFloatOrNull() ?: 80f,
                    partnerEmail,
                    appMode
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Initialize")
        }
    }
}

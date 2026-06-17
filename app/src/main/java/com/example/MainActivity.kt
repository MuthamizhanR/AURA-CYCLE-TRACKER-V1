package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AuraDatabase
import com.example.data.AuraRepository
import com.example.engine.AuraViewModel
import com.example.engine.AuraViewModelFactory
import com.example.ui.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val db = AuraDatabase.getDatabase(this)
        val repository = AuraRepository(db.auraDao())
        
        setContent {
            val viewModel: AuraViewModel = viewModel(factory = AuraViewModelFactory(repository))
            MyApplicationTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

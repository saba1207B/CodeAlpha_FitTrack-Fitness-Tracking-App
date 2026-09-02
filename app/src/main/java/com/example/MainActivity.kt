package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.fittrack.navigation.FitTrackApp
import com.example.fittrack.viewmodel.FitTrackViewModel
import com.example.fittrack.viewmodel.FitTrackViewModelFactory
import com.example.ui.theme.FitTrackTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FitTrackViewModel by viewModels {
        FitTrackViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FitTrackApp(viewModel = viewModel)
                }
            }
        }
    }
}


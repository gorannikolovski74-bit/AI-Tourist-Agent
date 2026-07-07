package com.goran.aitouristagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.goran.aitouristagent.ui.theme.AiTouristAgentTheme
import com.goran.aitouristagent.ui.trips.TripsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiTouristAgentTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TripsScreen()
                }
            }
        }
    }
}

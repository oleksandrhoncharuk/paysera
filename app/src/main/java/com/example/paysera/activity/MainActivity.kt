package com.example.paysera.activity

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.paysera_compose_ui.screen.MainScreen
import com.example.paysera_compose_ui.theme.PayseraTheme
import com.example.paysera_core.fetch_rates_service.CurrencyForegroundService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PayseraTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainScreen()
        }

        // Start the foreground service
        val serviceIntent = Intent(this, CurrencyForegroundService::class.java)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
          startForegroundService(serviceIntent)
        } else {
          startService(serviceIntent)
        }
      }
    }
  }
}
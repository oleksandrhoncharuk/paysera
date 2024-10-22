package com.example.paysera_compose_ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.paysera_core.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
  val isLoading = remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    // Simulate loading delay
    delay(10000)
    // Call your function to get currency exchange rates
    viewModel.getCurrencyExchangeRatesFromDatabase()
    // Set loading to false after the data is fetched
    isLoading.value = false
  }

  // Show loading indicator if data is still loading
  if (isLoading.value) {
    CircularProgressIndicator()
  } else {
    Scaffold(
      topBar = { TopBar() }
    ) { paddingValues ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(start = 15.dp)
      ) {
        CurrencyExchangeContent(viewModel)
      }
    }
  }
}

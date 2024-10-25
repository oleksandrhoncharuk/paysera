package com.example.paysera_compose_ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.paysera_core.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
  val isLoading = viewModel.exchangeRatesLoading.observeAsState(true)

  LaunchedEffect(Unit) {
    // Call function to get currency exchange rates
    viewModel.getCurrencyExchangeRatesFromDatabase()
  }

  // Show loading indicator if data is still loading
  if (isLoading.value) {
    CircularProgressIndicator()
  } else {
    CurrencyExchangeContent(viewModel)
  }
}

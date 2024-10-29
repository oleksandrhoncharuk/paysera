package com.example.paysera_compose_ui.screen

import android.annotation.SuppressLint
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.paysera_core.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
  val isLoading = viewModel.exchangeRatesLoading.observeAsState(true)

  // Show loading indicator if data is still loading
  if (isLoading.value) {
    CircularProgressIndicator()
  } else {
    CurrencyExchangeContent(viewModel)
  }
}

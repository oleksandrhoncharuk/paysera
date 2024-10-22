package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.mapToCurrency
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_core.viewmodel.MainViewModel

@Composable
fun SubmitButton(
  viewModel: MainViewModel,
  currencyState: CurrencyState
) {
  Box( // Wrap the Button in a Box to allow alignment
    modifier = Modifier.fillMaxSize() // Fill the available space
  ) {
    Button(
      onClick = {
        viewModel.submitCurrencyExchange(
          sellCurrency = currencyState.mapToCurrency(),
          sellAmount = currencyState.sellAmount.toDoubleOrNull() ?: 0.0,
          receiveCurrencyName = currencyState.currencyReceiveName,
        )
      },
      modifier = Modifier
        .align(Alignment.BottomCenter) // Align button at the bottom center within the Box
        .padding(16.dp)
        .fillMaxWidth()
        .height(60.dp)
    ) {
      Text(
        text = stringResource(R.string.submit_button_text),
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}
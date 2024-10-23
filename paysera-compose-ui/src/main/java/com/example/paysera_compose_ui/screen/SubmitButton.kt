package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
  var showDialog by remember { mutableStateOf(false) }

  val isLoading by viewModel.isLoadingAfterSubmit.observeAsState(false)

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
        showDialog = true
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

    if (showDialog) {
      AlertDialog(
        onDismissRequest = {
          // Dismiss the dialog when the user taps outside the dialog or the back button
          showDialog = false
        },
        title = {
          Text(text = stringResource(R.string.dialog_title))
        },
        text = {
          Text(
            text = stringResource(
              R.string.dialog_message,
              currencyState.sellAmount,
              currencyState.currencySellName,
              currencyState.receiveAmount,
              currencyState.currencyReceiveName,
              currencyState.fee
            )
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showDialog = false
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(60.dp)
          ) {
            Text(text = stringResource(R.string.dialog_done))
          }
        }
      )
    }
  }
}
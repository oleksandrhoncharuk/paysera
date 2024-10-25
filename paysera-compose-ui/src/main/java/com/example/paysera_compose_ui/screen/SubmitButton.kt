package com.example.paysera_compose_ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.mapToCurrency
import com.example.paysera_compose_ui.mapToCurrencyDataItem
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_compose_ui.model.isNullOrEmpty
import com.example.paysera_core.viewmodel.MainViewModel

@Composable
fun SubmitButton(
  viewModel: MainViewModel,
  currencyState: CurrencyState?
) {
  if (currencyState.isNullOrEmpty()) return
  var showDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val toastMessage = stringResource(R.string.similar_currency_operation_text)

  val isLoading by viewModel.isLoadingAfterSubmit.observeAsState(false)
  val sellCurrencyItem = currencyState?.sellStateItem
  val receiveCurrencyItem = currencyState?.receiveStateItem

  Box {
    if (isLoading) {
      // Center the CircularProgressIndicator
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0x80000000), shape = RoundedCornerShape(4.dp)) // Optional: dim background
          .wrapContentSize(Alignment.Center)
      ) {
        CircularProgressIndicator()
      }
    }
    Button(
      onClick = {
        if (sellCurrencyItem?.currencyName.equals(receiveCurrencyItem?.currencyName, ignoreCase = true)) {
          Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
          return@Button
        }
        viewModel.submitCurrencyExchange(
          sellCurrency = sellCurrencyItem?.mapToCurrencyDataItem(),
          receiveCurrency = receiveCurrencyItem?.mapToCurrencyDataItem(),
        )
        if (!isLoading) {
          showDialog = true
        }
      },
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .height(60.dp)
        .imePadding()
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
              sellCurrencyItem!!.operationalAmount,
              sellCurrencyItem.currencyName,
              receiveCurrencyItem!!.operationalAmount,
              receiveCurrencyItem.currencyName,
              sellCurrencyItem.fee
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
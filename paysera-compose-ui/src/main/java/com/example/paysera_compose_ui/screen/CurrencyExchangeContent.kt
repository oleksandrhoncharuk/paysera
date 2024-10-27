package com.example.paysera_compose_ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.map
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.mapToCurrencyDataItem
import com.example.paysera_compose_ui.mapToCurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_core.viewmodel.MainViewModel

@Composable
fun CurrencyExchangeContent(viewModel: MainViewModel) {
  val currencyState = viewModel.currencyDataLiveData.map { it.mapToCurrencyState() }.observeAsState()
  val isLoading = viewModel.isLoadingAfterSubmit.observeAsState(false)
  val showDialog = rememberSaveable { mutableStateOf(false) }

  val context = LocalContext.current
  val toastMessage = stringResource(R.string.similar_currency_operation_text)

  val updateReceiveCurrency: (CurrencyStateItem?) -> Unit = { receiveCurrencyStateItem ->
    receiveCurrencyStateItem?.let {
      viewModel.updateReceiveCurrency(receiveCurrencyStateItem.mapToCurrencyDataItem())
    }
  }

  val updateSellCurrency: (CurrencyStateItem?) -> Unit = { sellCurrencyStateItem ->
    sellCurrencyStateItem?.let {
      viewModel.updateSellCurrency(sellCurrencyStateItem.mapToCurrencyDataItem())
    }
  }

  val submitClick: () -> Unit = { ->
    currencyState.value?.let {
      if (it.sellStateItem?.currencyName.equals(it.receiveStateItem?.currencyName, ignoreCase = true)) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        return@let
      }
      viewModel.submitCurrencyExchange(
        sellCurrency = it.sellStateItem?.mapToCurrencyDataItem(),
        receiveCurrency = it.receiveStateItem?.mapToCurrencyDataItem())
      if (!isLoading.value) {
        showDialog.value = true
      }
    }
  }

  if (currencyState.value == null || currencyState.value?.sellStateItem == null || currencyState.value?.receiveStateItem == null) {
    CircularProgressIndicator()
  } else {
    Scaffold(
      topBar = { TopBar() },
      bottomBar = {
        SubmitButton(submitClick)
      }
    ) { paddingValues ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(start = 15.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .imePadding()
        ) {
          PayseraTitle(
            title = stringResource(R.string.balance_title),
            modifier = Modifier.padding(top = 15.dp)
          )

          BalanceRow(currencyState.value)

          PayseraTitle(
            title = stringResource(R.string.exchange_title),
            modifier = Modifier.padding(top = 25.dp, bottom = 15.dp)
          )

          CurrencyExchangeSellItem(currencyState.value!!, updateSellCurrency)

          HorizontalDivider(
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
          )

          CurrencyExchangeReceiveItem(currencyState.value!!, updateReceiveCurrency)

          if (showDialog.value) {
            AlertDialog(
              onDismissRequest = {
                // Dismiss the dialog when the user taps outside the dialog or the back button
                showDialog.value = false
              },
              title = {
                Text(text = stringResource(R.string.dialog_title))
              },
              text = {
                Text(
                  text = stringResource(
                    R.string.dialog_message,
                    currencyState.value!!.sellStateItem!!.operationalAmount,
                    currencyState.value!!.sellStateItem!!.currencyName,
                    currencyState.value!!.receiveStateItem!!.operationalAmount,
                    currencyState.value!!.receiveStateItem!!.currencyName,
                    currencyState.value!!.sellStateItem!!.fee
                  )
                )
              },
              confirmButton = {
                Button(
                  onClick = {
                    showDialog.value = false
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
    }
  }
}

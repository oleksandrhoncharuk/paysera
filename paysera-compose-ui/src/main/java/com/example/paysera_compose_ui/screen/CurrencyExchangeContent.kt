package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_core.viewmodel.MainViewModel

@Composable
fun CurrencyExchangeContent(viewModel: MainViewModel) {
  val balance by viewModel.currencyBalanceDBLiveData.observeAsState()
  val currencyList by viewModel.currencyNames.observeAsState()
  val receiveAmount by viewModel.receivedAmountOfSelectedCurrency.observeAsState()
  var currencyState by remember { mutableStateOf(CurrencyState()) }

  val currencyStateUpdate = { sellState: CurrencyState ->
    currencyState = sellState
    viewModel.loadReceiveAmountOfSelectedCurrency(
      currencyState.currencySellName,
      currencyState.currencyReceiveName,
      currencyState.sellAmount.toDoubleOrNull() ?: 0.0
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
  ) {
    PayseraTitle(
      title = stringResource(R.string.balance_title),
      modifier = Modifier.padding(top = 15.dp)
    )

    BalanceRow(balance)

    PayseraTitle(
      title = stringResource(R.string.exchange_title),
      modifier = Modifier.padding(top = 25.dp, bottom = 15.dp)
    )

//    CurrencyExchangeItem(isSell = true, operationAmount = 100.00, currency = "USD")
    CurrencyExchangeSellItem(currencyState, currencyList, currencyStateUpdate)
    HorizontalDivider(
      color = Color.LightGray,
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
    )
//    CurrencyExchangeItem(isSell = false, operationAmount = 100.00, currency = "EUR")
    CurrencyExchangeReceiveItem(currencyState, currencyList, receiveAmount, currencyStateUpdate)
  }

  SubmitButton(viewModel, currencyState)
}


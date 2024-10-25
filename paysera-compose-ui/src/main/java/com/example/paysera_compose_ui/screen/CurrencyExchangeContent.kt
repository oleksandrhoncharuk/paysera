package com.example.paysera_compose_ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.map
import com.example.paysera_compose_ui.R
import com.example.paysera_compose_ui.mapToCurrency
import com.example.paysera_compose_ui.mapToCurrencyDataItem
import com.example.paysera_compose_ui.mapToCurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_compose_ui.model.CurrencyStateItemSaver
import com.example.paysera_core.viewmodel.MainViewModel

@Composable
fun CurrencyExchangeContent(viewModel: MainViewModel) {
  val currencyState = viewModel.currencyDataLiveData.map { it.mapToCurrencyState() }.observeAsState()
//  val balance by viewModel.currencyBalanceDBLiveData.observeAsState()
//  val receiveAmount by viewModel.receivedAmountOfSelectedCurrency.observeAsState()
//  val sellCurrencyAmountLeft by viewModel.sellCurrencyAmountLeft.observeAsState()
//  val fee by viewModel.sellCurrencyFee.observeAsState()

//  val currencyList = balance?.map { it.currencyName } ?: emptyList()

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

  var currencyStateItem by rememberSaveable(stateSaver = CurrencyStateItemSaver) {
    mutableStateOf(CurrencyStateItem())
  }

//  sellCurrencyAmountLeft?.let { amountLeft ->
//    currencyStateItem = currencyStateItem.copy(
//      currencyAmount = amountLeft
//    )
//  }
//
//  receiveAmount?.let {
//    currencyStateItem = currencyStateItem.copy(
//      receiveAmount = it
//    )
//  }
//
//  fee?.let {
//    currencyStateItem = currencyStateItem.copy(
//      fee = it
//    )
//  }

//  val currencyStateUpdate = { newState: CurrencyStateItem ->
//    currencyStateItem = newState
//
//    viewModel.updateAmountOfSelectedCurrencies(
//      newState.currencyName,
//      newState.currencyReceiveName,
//      newState.operationalAmount.toDoubleOrNull() ?: 0.0
//    )
//  }

  if (currencyState.value == null || currencyState.value?.sellStateItem == null || currencyState.value?.receiveStateItem == null) {
    CircularProgressIndicator()
  } else {
    Scaffold(
      topBar = { TopBar() },
      bottomBar = {
        SubmitButton(
          viewModel,
          currencyState.value
        )
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
        }
      }
    }
  }
}

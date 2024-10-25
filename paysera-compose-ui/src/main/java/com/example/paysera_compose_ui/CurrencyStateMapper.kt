package com.example.paysera_compose_ui

import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_core.model.CurrencyData
import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_database.model.Currency

fun Currency.mapToCurrencyState() = CurrencyStateItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  currencyAmount = amount,
  operationalAmount = operationalAmount,
  exchangeCount = exchangeCount
)

fun CurrencyStateItem.mapToCurrency() = Currency(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  amount = currencyAmount,
  operationalAmount = operationalAmount,
  exchangeCount = exchangeCount
)

fun CurrencyDataItem.mapToCurrencyState() = CurrencyStateItem(
  currencyName = currencyName?: "",
  exchangeRate = exchangeRate?: 0.0,
  currencyAmount = operationalAmount?: 0.0,
  exchangeCount = exchangeCount?: 0
)

fun CurrencyStateItem.mapToSellCurrencyDataItem() = CurrencyDataItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  operationalAmount = currencyAmount,
  amount = currencyAmount,
  exchangeCount = exchangeCount
)

fun CurrencyStateItem.mapToCurrencyDataItem() = CurrencyDataItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  operationalAmount = operationalAmount,
  amount = currencyAmount,
  exchangeCount = exchangeCount
)

fun CurrencyData.mapToCurrencyState() = CurrencyState(
  balance = balance ?: emptyMap(),
  sellStateItem = sellCurrency?.mapToCurrencyState(),
  receiveStateItem = receiveCurrency?.mapToCurrencyState()
)

fun CurrencyState.mapToCurrencyData() = CurrencyData(
  balance = balance,
  sellCurrency = sellStateItem?.mapToCurrencyDataItem(),
  receiveCurrency = receiveStateItem?.mapToCurrencyDataItem()
)
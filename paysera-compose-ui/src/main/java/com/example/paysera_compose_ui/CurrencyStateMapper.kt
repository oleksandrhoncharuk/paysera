package com.example.paysera_compose_ui

import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_core.model.CurrencyData
import com.example.paysera_core.model.CurrencyDataItem

fun CurrencyDataItem.mapToCurrencyState() = CurrencyStateItem(
  currencyName = currencyName?: "",
  exchangeRate = exchangeRate?: 0.0,
  currencyAmount = amount?: 0.0,
  operationalAmount = operationalAmount.toString(),
  exchangeCount = exchangeCount?: 0,
  fee = fee
)

fun CurrencyStateItem.mapToCurrencyDataItem() = CurrencyDataItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  operationalAmount = operationalAmount.toDoubleOrNull() ?: 0.0,
  amount = currencyAmount,
  exchangeCount = exchangeCount
)

fun List<CurrencyDataItem>?.mapToCurrencyStateList() = this?.map { it.mapToCurrencyState() }

fun CurrencyData.mapToCurrencyState() = CurrencyState(
  balance = balance ?: emptyMap(),
  sellStateItem = sellCurrency?.mapToCurrencyState(),
  receiveStateItem = receiveCurrency?.mapToCurrencyState()
)
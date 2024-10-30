package com.example.paysera_compose_ui

import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_core.model.CurrencyData
import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_core.repository.mapper.roundAmount

internal fun CurrencyDataItem.mapToCurrencyState() = CurrencyStateItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  currencyAmount = amount ?: 0.00,
  operationalAmount = operationalAmount.roundAmount(),
  exchangeCount = exchangeCount?: 0,
  fee = fee
)

internal fun CurrencyStateItem.mapToCurrencyDataItem() = CurrencyDataItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  operationalAmount = operationalAmount.toDoubleOrNull() ?: 0.0,
  amount = currencyAmount,
  exchangeCount = exchangeCount
)

internal fun CurrencyData.mapToCurrencyState() = CurrencyState(
  balance = balance ?: emptyMap(),
  sellStateItem = sellCurrency?.mapToCurrencyState(),
  receiveStateItem = receiveCurrency?.mapToCurrencyState()
)

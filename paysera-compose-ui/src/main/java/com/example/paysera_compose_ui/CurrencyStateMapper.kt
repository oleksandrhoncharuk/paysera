package com.example.paysera_compose_ui

import com.example.paysera_compose_ui.model.CurrencyState
import com.example.paysera_database.model.Currency

fun Currency.mapToCurrencyState() = CurrencyState(
  currencySellName = currencyName,
  exchangeRate = exchangeRate,
  currencyAmount = amount,
  exchangeCount = exchangeCount
)

fun CurrencyState.mapToCurrency() = Currency(
  currencyName = currencySellName,
  exchangeRate = exchangeRate,
  amount = currencyAmount,
  exchangeCount = exchangeCount
)
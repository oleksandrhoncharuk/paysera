package com.example.paysera_core

import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_database.model.Currency

fun Currency.mapToCurrencyDataItem() = CurrencyDataItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  operationalAmount = operationalAmount,
  amount = amount,
  exchangeCount = exchangeCount
)

fun CurrencyDataItem.mapToCurrency() = Currency(
  currencyName = currencyName ?: "",
  exchangeRate = exchangeRate ?: 0.0,
  amount = amount ?: 0.0,
  operationalAmount = operationalAmount ?: 0.0,
  exchangeCount = exchangeCount ?: 0
)

fun List<Currency>.mapToCurrencyDataItemList() = map { it.mapToCurrencyDataItem() }



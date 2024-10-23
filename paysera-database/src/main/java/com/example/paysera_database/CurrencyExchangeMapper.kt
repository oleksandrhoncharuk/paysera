package com.example.paysera_database

import com.example.paysera_database.model.Currency
import com.example.paysera_database.model.CurrencyExchange

fun CurrencyExchange.mapToCurrency(): Currency {
  return if (this.currencyName == "EUR") {
    Currency(
      currencyName = currencyName,
      exchangeRate = 1.0,
      amount = amount,
      exchangeCount = exchangeCount
    )
  } else {
    Currency(
      currencyName = currencyName,
      exchangeRate = exchangeRate,
      amount = amount,
      exchangeCount = exchangeCount
    )
  }
}

fun Currency.mapToDomain() = CurrencyExchange(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  amount = amount,
  exchangeCount = exchangeCount
)

fun List<CurrencyExchange>.mapToCurrencyList() = map { it.mapToCurrency() }

fun Currency.isFeeFree() = exchangeCount < 5
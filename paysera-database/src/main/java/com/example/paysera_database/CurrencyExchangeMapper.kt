package com.example.paysera_database

import com.example.paysera_database.model.Currency
import com.example.paysera_database.model.CurrencyExchange

fun CurrencyExchange.mapToCurrency() = Currency(
    currencyName = currencyName,
    exchangeRate = exchangeRate,
    amount = amount,
    operationalAmount = 0.0,
    exchangeCount = exchangeCount
  )

fun Currency.mapToDomain() = CurrencyExchange(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  amount = amount,
  exchangeCount = exchangeCount
)

fun CurrencyExchange.updateWithCurrency(currency: Currency): CurrencyExchange {
  return this.copy(
    exchangeRate = currency.exchangeRate,
    amount = if (currency.amount == 0.0) amount else currency.amount,
    exchangeCount = if (currency.exchangeCount == 0) exchangeCount else currency.exchangeCount
  )
}

fun List<CurrencyExchange>.mapToCurrencyList() = map { it.mapToCurrency() }

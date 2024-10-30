package com.example.paysera_core.repository.mapper

import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_database.model.Currency
import java.util.Locale

internal fun Currency.mapToCurrencyDataItem(
  updatedOperationalAmount: Double? = null,
  updatedExchangeCount: Int? = null,
) = CurrencyDataItem(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  operationalAmount = updatedOperationalAmount ?: operationalAmount,
  amount = amount,
  exchangeCount = updatedExchangeCount ?: exchangeCount,
  fee = getFee(updatedExchangeCount ?: exchangeCount, updatedOperationalAmount)
)

internal fun CurrencyDataItem.mapToCurrency() = Currency(
  currencyName = currencyName,
  exchangeRate = exchangeRate,
  amount = amount ?: 0.0,
  operationalAmount = operationalAmount ?: 0.0,
  exchangeCount = exchangeCount ?: 0
)

internal fun List<Currency>.mapToCurrencyDataItemList() = map { it.mapToCurrencyDataItem() }

internal fun getFee(exchangeCount: Int, operationalAmount: Double?): Double {
  if (operationalAmount == null) {
    return 0.0
  }
  if (exchangeCount > 5) {
    return operationalAmount * FEE_MULTIPLIER
  }
  return 0.0
}

fun Double?.roundAmount(): String {
  if (this == null) return "0.00"
  return String.format(Locale.US,"%.2f", this)
}

private const val FEE_MULTIPLIER = 0.007

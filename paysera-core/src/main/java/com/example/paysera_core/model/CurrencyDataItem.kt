package com.example.paysera_core.model

/**
 * CurrencyDataItem is a data class that holds the information about receive or sell currency data.
 *
 * @param currencyName the name of the currency
 * @param exchangeRate the exchange rate of the currency
 * @param operationalAmount the operational amount of the currency
 * @param amount the amount of the currency
 * @param exchangeCount the exchange count of the currency
 */
data class CurrencyDataItem(
  val currencyName: String? = null,
  val exchangeRate: Double? = null,
  val operationalAmount: Double? = null,
  val amount: Double? = null,
  val exchangeCount: Int? = null
)

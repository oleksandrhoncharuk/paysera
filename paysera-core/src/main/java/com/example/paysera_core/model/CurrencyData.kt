package com.example.paysera_core.model

/**
 * CurrencyData is a data class that holds the information about the exchange data.
 *
 * @param balance the balance of the user
 * @param sellCurrency the sell currency data
 * @param receiveCurrency the receive currency data
 */
data class CurrencyData(
  val balance: Map<String, Double?>? = null,
  val sellCurrency: CurrencyDataItem? = null,
  val receiveCurrency: CurrencyDataItem? = null
)

/**
 * CurrencyDataItem is a data class that holds the information about receive or sell currency data.
 *
 * @param currencyName the name of the currency, default value `EUR`
 * @param exchangeRate the exchange rate of the currency default value `1.0`
 * @param operationalAmount the operational amount of the currency
 * @param amount the amount of the currency
 * @param exchangeCount the exchange count of the currency
 */
data class CurrencyDataItem(
  val currencyName: String = "EUR",
  val exchangeRate: Double = 1.0,
  val operationalAmount: Double? = null,
  val amount: Double? = null,
  val exchangeCount: Int? = null,
  val fee: Double = 0.0
)

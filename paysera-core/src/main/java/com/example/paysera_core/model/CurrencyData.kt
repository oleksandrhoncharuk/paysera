package com.example.paysera_core.model

/**
 * CurrencyData is a data class that holds the information about the exchange data.
 *
 * @param balance the balance of the user
 * @param sellCurrency the sell currency data
 * @param receiveCurrency the receive currency data
 */
data class CurrencyData(
  val balance: Map<String?, Double?>? = null,
  val sellCurrency: CurrencyDataItem? = null,
  val receiveCurrency: CurrencyDataItem? = null
)
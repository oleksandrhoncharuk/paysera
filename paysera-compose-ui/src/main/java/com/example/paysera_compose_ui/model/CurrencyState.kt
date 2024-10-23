package com.example.paysera_compose_ui.model

/**
 * CurrencyState is a data class that holds the state of the currency exchange for compose.
 *
 * @param currencySellName the name of the currency that user want to sell
 * @#param currencyReceiveName the name of the currency that user want to receive
 * @param exchangeRate the exchange rate of the currency
 * @param currencyAmount the amount of the currency that user have
 * @param sellAmount the amount of currency that user want sell, stored in String as this is the input field
 * @param isSell the flag that indicates if the user is selling the currency
 * @param receiveAmount the amount of currency that user will receive
 * @param exchangeCount the count of the exchange that was made with this currency
 * @param fee the fee that is applied to the exchange
 */
data class CurrencyState(
  val currencySellName: String = "EUR",
  val currencyReceiveName: String = "USD",
  val exchangeRate: Double = 1.0,
  val currencyAmount: Double = 0.0,
  val sellAmount: String = "0.0",
  val isSell: Boolean = true,
  val receiveAmount: Double = 0.0,
  val exchangeCount: Int = 0,
  val fee: Double = 0.0
)

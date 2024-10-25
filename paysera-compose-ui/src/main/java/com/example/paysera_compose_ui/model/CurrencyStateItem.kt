package com.example.paysera_compose_ui.model

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

/**
 * CurrencyStateItem is a data class that holds the state of sell and receive currency information for compose.
 *
 * @param currencyName the name of the currency that user want to sell
 * @param exchangeRate the exchange rate of the currency
 * @param currencyAmount the amount of the currency that user have
 * @param operationalAmount the amount of currency that user want sell or buy
 * @param isSell the flag that indicates if the user is selling the currency
 * @param exchangeCount the count of the exchange that was made with this currency
 * @param fee the fee that is applied to the exchange
 */
data class CurrencyStateItem(
  val currencyName: String = "EUR",
  val exchangeRate: Double = 1.0,
  val currencyAmount: Double = 0.0,
  val operationalAmount: Double = 0.0,
  val isSell: Boolean = true,
  val exchangeCount: Int = 0,
  val fee: Double = 0.0
)

// Custom Saver for CurrencyState
val CurrencyStateItemSaver: Saver<CurrencyStateItem, Any> = listSaver(
  save = { state ->
    listOf(
      state.currencyName,
      state.exchangeRate,
      state.currencyAmount,
      state.operationalAmount,
      state.isSell,
      state.exchangeCount,
      state.fee
    )
  },
  restore = { restoredList ->
    CurrencyStateItem(
      currencyName = restoredList[0] as String,
      exchangeRate = restoredList[2] as Double,
      currencyAmount = restoredList[3] as Double,
      operationalAmount = restoredList[4] as Double,
      isSell = restoredList[5] as Boolean,
      exchangeCount = restoredList[7] as Int,
      fee = restoredList[8] as Double
    )
  }
)

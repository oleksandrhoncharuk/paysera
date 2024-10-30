package com.example.paysera_compose_ui.model

/**
 * CurrencyState is a data class that holds the state of the currency exchange for compose.
 *
 * @param balance the balance of the user <String> currency name to <Double?> amount
 * @param sellStateItem the state of the currency that user want to sell
 * @param receiveStateItem the state of the currency that user want to receive
 */
data class CurrencyState(
  val balance: Map<String, Double?>?,
  val sellStateItem: CurrencyStateItem?,
  val receiveStateItem: CurrencyStateItem?
)

fun CurrencyState?.isBalanceNullOrEmpty(): Boolean {
  if (this == null) return false
  return balance.isNullOrEmpty()
}

fun CurrencyState.getCurrenciesNamesList(): List<String> {
  if (balance.isNullOrEmpty()) return emptyList()
  return balance
    .toList()
    .map { (key, _) -> key ?: "" }
}

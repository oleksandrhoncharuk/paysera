package com.example.paysera_compose_ui.model

/**
 * CurrencyState is a data class that holds the state of the currency exchange for compose.
 *
 * @param balance the balance of the user
 * @param sellStateItem the state of the currency that user want to sell
 * @param receiveStateItem the state of the currency that user want to receive
 */
data class CurrencyState(
  val balance: Map<String?, Double?>?,
  val sellStateItem: CurrencyStateItem?,
  val receiveStateItem: CurrencyStateItem?
)

fun CurrencyState?.isBalanceNullOrEmpty(): Boolean {
  if (this == null) return false
  return balance.isNullOrEmpty()
}

fun CurrencyState.isSellStateItemNull(): Boolean {
  return sellStateItem == null
}

fun CurrencyState.isReceiveStateItemNull(): Boolean {
  return receiveStateItem == null
}

fun CurrencyState?.isNullOrEmpty(): Boolean {
  if (this == null) return true
  return balance.isNullOrEmpty() && sellStateItem == null && receiveStateItem == null
}

fun CurrencyState.getSortedBalanceList(): List<String> {
  if (balance.isNullOrEmpty()) return emptyList()
  return balance
    .toList()
    .sortedByDescending { (_, value) -> value }
    .map { (key, _) -> key ?: "" }
}
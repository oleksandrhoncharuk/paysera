package com.example.paysera_core.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_core.model.CurrencyData

internal fun combine(
  currencies: List<CurrencyDataItem>?,
  sellCurrency: CurrencyDataItem?,
  receiveCurrency: CurrencyDataItem?,
  updatedSellAmount: Double? = null,
  updatedReceiveAmount: Double? = null,
  fee: Double? = null,
): CurrencyData {

  val balance = currencies?.associate { it.currencyName to it.amount } ?: emptyMap()

  val sellCurrencyDataItem = sellCurrency?.let {
    CurrencyDataItem(
      currencyName = it.currencyName,
      exchangeRate = it.exchangeRate,
      operationalAmount = it.operationalAmount,
      amount = updatedSellAmount ?: it.amount,
      exchangeCount = it.exchangeCount,
      fee = fee ?: it.fee
    )
  }

  val receiveCurrencyDataItem = receiveCurrency?.let {
    CurrencyDataItem(
      currencyName = it.currencyName,
      exchangeRate = it.exchangeRate,
      operationalAmount = updatedReceiveAmount ?: it.operationalAmount,
      amount = it.amount,
      exchangeCount = it.exchangeCount
    )
  }

  return CurrencyData(
    balance = balance,
    sellCurrency = sellCurrencyDataItem,
    receiveCurrency = receiveCurrencyDataItem
  )
}



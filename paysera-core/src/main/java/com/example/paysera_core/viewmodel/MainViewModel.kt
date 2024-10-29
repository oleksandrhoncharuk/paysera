package com.example.paysera_core.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.paysera_core.mapToCurrency
import com.example.paysera_core.mapToCurrencyDataItem
import com.example.paysera_core.mapToCurrencyDataItemList
import com.example.paysera_core.model.CurrencyData
import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_core.repository.CurrencyExchangeRepository
import com.example.paysera_core.tools.combine
import com.example.paysera_database.model.roundAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: CurrencyExchangeRepository
) : ViewModel() {

  private val mCurrencyBalanceDBLiveData = repository.getCurrencyExchangeRatesFromDatabase()
    .asLiveData().map { it.mapToCurrencyDataItemList() }

  private val mSellCurrencyLiveData: MutableLiveData<CurrencyDataItem> = MutableLiveData()
  private val mReceiveCurrencyLiveData: MutableLiveData<CurrencyDataItem> = MutableLiveData()

  private val mExchangeRatesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
  val exchangeRatesLoading: LiveData<Boolean>
    get() = mExchangeRatesLoading

  private val mIsLoadingAfterSubmit: MutableLiveData<Boolean> = MutableLiveData(false)
  val isLoadingAfterSubmit: LiveData<Boolean>
    get() = mIsLoadingAfterSubmit

  private val mCurrencyDataLiveData = MediatorLiveData<CurrencyData>()
  val currencyDataLiveData: LiveData<CurrencyData>
    get() = mCurrencyDataLiveData

  init {
    mCurrencyDataLiveData.apply {
      var currencies: List<CurrencyDataItem>? = null
      var sellCurrency: CurrencyDataItem? = null
      var receiveCurrency: CurrencyDataItem? = null

      addSource(mCurrencyBalanceDBLiveData) { newCurrencies ->
        currencies = newCurrencies
        if (sellCurrency == null) sellCurrency = newCurrencies.first()
        if (receiveCurrency == null) receiveCurrency = newCurrencies.first()

        value = combine(currencies, sellCurrency, receiveCurrency)
      }

      addSource(mReceiveCurrencyLiveData) { newReceiveCurrency ->
        receiveCurrency = newReceiveCurrency
        value = combine(currencies, sellCurrency, receiveCurrency)
      }

      addSource(mSellCurrencyLiveData) { newSellCurrency ->
        sellCurrency = newSellCurrency
        value = combine(currencies, newSellCurrency, sellCurrency)
      }
    }
  }

  private fun updateSellCurrencyDataItemFromDB(newSellCurrencyItem: CurrencyDataItem, newReceiveCurrencyItem: CurrencyDataItem?) {
    viewModelScope.launch {
      val sellCurrencyFromDB = getCurrencyByName(newSellCurrencyItem.currencyName)
      val updatedSellCurrency = sellCurrencyFromDB?.let {
        newSellCurrencyItem.copy(
          amount = sellCurrencyFromDB.amount,
          exchangeRate = sellCurrencyFromDB.exchangeRate,
          exchangeCount = sellCurrencyFromDB.exchangeCount,
          fee = sellCurrencyFromDB.fee
        )

      } ?: newSellCurrencyItem
      mSellCurrencyLiveData.value = updatedSellCurrency
      updateReceiveCurrencyAmount(updatedSellCurrency, newReceiveCurrencyItem)
    }
  }

  fun updateReceiveCurrency(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem) {
    viewModelScope.launch {
      updateReceiveCurrencyAmount(sellCurrency, receiveCurrency)
    }
  }

  fun updateSellCurrency(sellCurrency: CurrencyDataItem, receiveCurrency: CurrencyDataItem?) {
    updateSellCurrencyDataItemFromDB(sellCurrency, receiveCurrency)
  }

  private suspend fun updateReceiveCurrencyAmount(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?) {
    val sellCurrencyFromDB = sellCurrency?.currencyName?.let { getCurrencyByName(it) }
    val receiveCurrencyFromDB = receiveCurrency?.currencyName?.let { getCurrencyByName(it) }

    val updatedReceiveAmount = receiveCurrencyExchangeAmount(
      sellCurrency?.copy(amount = sellCurrencyFromDB?.amount.roundAmount()),
      receiveCurrency?.copy(
        amount = receiveCurrencyFromDB?.amount.roundAmount(),
        exchangeRate = receiveCurrencyFromDB?.exchangeRate ?: receiveCurrency.exchangeRate,
        exchangeCount = receiveCurrencyFromDB?.exchangeCount,
        fee = receiveCurrencyFromDB?.fee ?: receiveCurrency.fee
      )
    ).roundAmount()
    mReceiveCurrencyLiveData.value = receiveCurrency?.copy(operationalAmount = updatedReceiveAmount)
  }

  fun submitCurrencyExchange(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?) {
    if (sellCurrency == null || receiveCurrency == null) return
    viewModelScope.launch {
      mIsLoadingAfterSubmit.value = true
      if (isOperationPossible(sellCurrency, receiveCurrency)) {
        val sellCurrencyAmountLeft = sellCurrency.amount!! - sellCurrency.operationalAmount!!
        val receiveCurrencyAmount = receiveCurrencyExchangeAmount(
          sellCurrency,
          receiveCurrency
        )
        Log.d("MainViewModel", "receiveCurrencyExchangeAmount result: $receiveCurrencyAmount")

        Log.d("MainViewModel", "start updateCurrencyExchange for ${sellCurrency.currencyName}")

        val updatedSellCurrency = sellCurrency.copy(
          amount = sellCurrencyAmountLeft,
          exchangeCount = sellCurrency.exchangeCount!! + 1
        )
        updateCurrencyExchange(updatedSellCurrency)

        val updatedReceiveCurrency = receiveCurrency.copy(
          amount = (receiveCurrency.amount ?: 0.0) + receiveCurrencyAmount
        )
        updateCurrencyExchange(updatedReceiveCurrency)

        updateFeeFor(sellCurrency, sellCurrency.operationalAmount)

        mSellCurrencyLiveData.value = updatedSellCurrency
        mReceiveCurrencyLiveData.value = updatedReceiveCurrency
      }
    }
    mIsLoadingAfterSubmit.value = false
  }

  private fun isOperationPossible(sellCurrency: CurrencyDataItem, receiveCurrency: CurrencyDataItem): Boolean {
    if (isCurrencyDataFieldsNotValid(sellCurrency) || isCurrencyDataFieldsNotValid(receiveCurrency)) return false
    return sellCurrency.amount!! >= sellCurrency.operationalAmount!!
  }

  private fun isCurrencyDataFieldsNotValid(currencyData: CurrencyDataItem): Boolean {
    val listOfCurrencyDataFields = listOf(
      currencyData.currencyName,
      currencyData.exchangeRate,
      currencyData.operationalAmount,
      currencyData.amount,
      currencyData.exchangeCount
    )
    return listOfCurrencyDataFields.all { it == null }
  }

  private fun receiveCurrencyAmountFromEuro(currencyToBuy: CurrencyDataItem?, amountToSell: Double): Double {
    if (currencyToBuy?.exchangeRate == null) return 0.00
    return amountToSell * currencyToBuy.exchangeRate
  }

  private fun convertToEuro(currency: CurrencyDataItem?): Double {
    if (currency?.operationalAmount == null) return 0.00
    val currencyRate = currency.exchangeRate
    return currency.operationalAmount / currencyRate
  }

  private fun receiveCurrencyExchangeAmount(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?): Double {
    Log.d("MainViewModel", "receiveCurrencyExchangeAmount")
    if (sellCurrency == null || receiveCurrency == null) return 0.00
    val euroAmountOfCurrencyToSell = convertToEuro(sellCurrency)
    return receiveCurrencyAmountFromEuro(receiveCurrency, euroAmountOfCurrencyToSell)
  }

  private suspend fun updateFeeFor(sellCurrency: CurrencyDataItem?, soldAmount: Double) {
    sellCurrency?.let {
      val fee = repository.getSoldCurrencyFee(sellCurrency.currencyName, soldAmount)
      mSellCurrencyLiveData.value = sellCurrency.copy(fee = fee)
    }
  }

  private suspend fun getCurrencyByName(currencyName: String): CurrencyDataItem? {
    val currencyByName = repository.getCurrencyByName(currencyName) ?: return null
    return currencyByName.mapToCurrencyDataItem()
  }

  private suspend fun updateCurrencyExchange(currency: CurrencyDataItem?) {
    Log.d("MainViewModel", "updateCurrencyExchange currency is not null ${currency != null}")
    Log.d("MainViewModel", "updateCurrencyExchange for ${currency?.currencyName}")
    Log.d("MainViewModel", "updateCurrencyExchange Currency exchange count ${currency?.exchangeCount}")
    if (currency == null) return
    repository.updateCurrencyExchangeFrom(currency.mapToCurrency())
  }
}
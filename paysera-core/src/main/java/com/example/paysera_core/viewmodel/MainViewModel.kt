package com.example.paysera_core.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

  private val mCurrencyBalanceDBLiveData: MutableLiveData<List<CurrencyDataItem>> = MutableLiveData()
  val currencyBalanceDBLiveData: LiveData<List<CurrencyDataItem>>
    get() = mCurrencyBalanceDBLiveData

  private val mExchangeRatesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
  val exchangeRatesLoading: LiveData<Boolean>
    get() = mExchangeRatesLoading

  private val receiveCurrencyLiveData: MutableLiveData<CurrencyDataItem> = MutableLiveData()
  private val sellCurrencyLiveData: MutableLiveData<CurrencyDataItem> = MutableLiveData()

  private val mReceivedAmountOfSelectedCurrency: MutableLiveData<Double> = MutableLiveData()
  val receivedAmountOfSelectedCurrency: LiveData<Double>
    get() = mReceivedAmountOfSelectedCurrency

  private val mSellCurrencyAmountLeft: MutableLiveData<Double> = MutableLiveData()
  val sellCurrencyAmountLeft: LiveData<Double>
    get() = mSellCurrencyAmountLeft

  private val mSellCurrencyFee: MutableLiveData<Double> = MutableLiveData()
  val sellCurrencyFee: LiveData<Double>
    get() = mSellCurrencyFee

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

      addSource(sellCurrencyLiveData) { newSellCurrency ->
        sellCurrency = newSellCurrency
        value = combine(currencies, sellCurrency, receiveCurrency)
      }

      addSource(receiveCurrencyLiveData) { newReceiveCurrency ->
        receiveCurrency = newReceiveCurrency
        value = combine(currencies, sellCurrency, receiveCurrency)
      }
    }
  }

  fun getCurrencyExchangeRatesFromDatabase() {
    mExchangeRatesLoading.value = true
    viewModelScope.launch {
      updateCurrencyExchangeRatesFromDatabase()
    }
  }

  fun updateReceiveCurrency(receiveCurrency: CurrencyDataItem) {
    receiveCurrencyLiveData.value = receiveCurrency
  }

  fun updateSellCurrency(sellCurrency: CurrencyDataItem) {
    sellCurrencyLiveData.value = sellCurrency
  }

  fun updateAmountOfSelectedCurrencies(sellCurrency: CurrencyDataItem, receiveCurrency: CurrencyDataItem) {
    if (isCurrencyDataFieldsNotValid(sellCurrency) || isCurrencyDataFieldsNotValid(receiveCurrency)) return
    viewModelScope.launch {
      val sellAmountLeft = getCurrencyByName(sellCurrency.currencyName!!)?.amount ?: 0.00
      mSellCurrencyAmountLeft.value = sellAmountLeft.roundAmount()
      val receive = receiveCurrencyExchangeAmount(sellCurrency, receiveCurrency)
      mReceivedAmountOfSelectedCurrency.value = receive.roundAmount()
    }
  }

  fun submitCurrencyExchange(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?) {
    if (sellCurrency == null || receiveCurrency == null) return
    viewModelScope.launch {
      Log.d("MainViewModel", "submitCurrencyExchange")
      Log.d("MainViewModel",
        "Currency to sell: ${sellCurrency.currencyName}, " +
            "amount: ${sellCurrency.operationalAmount}, receive currency: ${receiveCurrency.currencyName}, " +
            "exchange count: ${sellCurrency.exchangeCount}")
      mIsLoadingAfterSubmit.value = true
      if (isOperationPossible(sellCurrency, receiveCurrency)) {
        val sellCurrencyAmountLeft = sellCurrency.amount!! - sellCurrency.operationalAmount!!
        Log.d("MainViewModel", "${sellCurrency.currencyName} amount left: $sellCurrencyAmountLeft")
        val receiveCurrencyAmount = receiveCurrencyExchangeAmount(
          sellCurrency,
          receiveCurrency
        )
        Log.d("MainViewModel", "receiveCurrencyExchangeAmount result: $receiveCurrencyAmount")

        Log.d("MainViewModel", "start updateCurrencyExchange for ${sellCurrency.currencyName}")

        updateCurrencyExchange(
          sellCurrency.copy(
            amount = sellCurrencyAmountLeft,
            exchangeCount = sellCurrency.exchangeCount!! + 1
          )
        )
        Log.d("MainViewModel", "start updateCurrencyExchange for ${receiveCurrency.currencyName}")
        updateCurrencyExchange(
          receiveCurrency.copy(
            amount = receiveCurrency.amount!! + receiveCurrencyAmount
          )
        )
        Log.d("MainViewModel", "getting fee for ${sellCurrency.currencyName}")
        getFeeForSoldCurrency(sellCurrency.currencyName!!, sellCurrency.operationalAmount)
        Log.d("MainViewModel", "updateCurrencyExchangeRatesFromDatabase")
        updateCurrencyExchangeRatesFromDatabase()
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

  private suspend fun updateCurrencyExchangeRatesFromDatabase() {
    val currencyRates = repository.getCurrencyListFromDatabase()
    mCurrencyBalanceDBLiveData.value = currencyRates.mapToCurrencyDataItemList().let { currencyDataItems ->
      currencyDataItems.map { it.copy(amount = it.amount.roundAmount()) }
        .sortedWith(comparator = compareByDescending<CurrencyDataItem> { it.amount }
          .thenBy { it.currencyName })
    }
    mExchangeRatesLoading.value = false
  }

  private suspend fun receiveCurrencyAmountFromEuro(currencyNameToBuy: String?, amountToSell: Double): Double {
    if (currencyNameToBuy == null) return 0.00
    val currencyRate = getCurrencyRateByName(currencyNameToBuy)
    return amountToSell * currencyRate
  }

  private suspend fun convertToEuro(currencyName: String?, amount: Double?): Double {
    if (currencyName == null || amount == null) return 0.00
    val currencyRate = getCurrencyRateByName(currencyName)
    return amount / currencyRate
  }

  private suspend fun getCurrencyRateByName(currencyName: String): Double {
    return getCurrencyByName(currencyName)?.exchangeRate ?: 0.00
  }

  private suspend fun receiveCurrencyExchangeAmount(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?): Double {
    Log.d("MainViewModel", "receiveCurrencyExchangeAmount")
    if (sellCurrency == null || receiveCurrency == null) return 0.00
    val euroAmountOfCurrencyToSell = convertToEuro(sellCurrency.currencyName, sellCurrency.operationalAmount)
    return receiveCurrencyAmountFromEuro(receiveCurrency.currencyName, euroAmountOfCurrencyToSell)
  }

  private suspend fun getFeeForSoldCurrency(currencyName: String, soldAmount: Double) {
    val fee = repository.getSoldCurrencyFee(currencyName, soldAmount)
    mSellCurrencyFee.value = fee
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
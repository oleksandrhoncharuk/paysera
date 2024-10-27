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

  private val currencyBalanceDBLiveData: MutableLiveData<List<CurrencyDataItem>> = MutableLiveData()
  private val sellCurrencyLiveData: MutableLiveData<CurrencyDataItem> = MutableLiveData()
  private val sellCurrencyFee: MutableLiveData<Double> = MutableLiveData()
  private val receiveCurrencyLiveData: MutableLiveData<CurrencyDataItem> = MutableLiveData()

  private val mSellCurrencyAmountLeft: MutableLiveData<Double> = MutableLiveData()
  val sellCurrencyAmountLeft: LiveData<Double>
    get() = mSellCurrencyAmountLeft

  private val mReceivedAmountOfSelectedCurrency: MutableLiveData<Double> = MutableLiveData()
  val receivedAmountOfSelectedCurrency: LiveData<Double>
    get() = mReceivedAmountOfSelectedCurrency

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

      addSource(currencyBalanceDBLiveData) { newCurrencies ->
        currencies = newCurrencies
        if (sellCurrency == null) sellCurrency = newCurrencies.first()
        if (receiveCurrency == null) receiveCurrency = newCurrencies.first()
        value = combine(currencies, sellCurrency, receiveCurrency)
      }

      addSource(receiveCurrencyLiveData) { newReceiveCurrency ->
        receiveCurrency = newReceiveCurrency
        value = combine(currencies, sellCurrency, receiveCurrency)
      }

      addSource(sellCurrencyLiveData) { newSellCurrency ->
        sellCurrency = newSellCurrency
        updateReceiveCurrencyAmount(newSellCurrency, receiveCurrency)
        value = combine(currencies, sellCurrency, receiveCurrency)
      }


      addSource(mSellCurrencyAmountLeft) { newSellAmountLeft ->
        value = combine(currencies, sellCurrency, receiveCurrency, updatedSellAmount = newSellAmountLeft)
      }

      addSource(mReceivedAmountOfSelectedCurrency) { newReceiveAmount ->
        value = combine(currencies, sellCurrency, receiveCurrency, updatedReceiveAmount = newReceiveAmount)
      }

      addSource(sellCurrencyFee) { newFee ->
        value = combine(currencies, sellCurrency, receiveCurrency, fee = newFee)
      }
    }
  }

  private fun updateCurrencyDataItemFromDB(newCurrencyItem: CurrencyDataItem) {
    viewModelScope.launch {
      if (newCurrencyItem.currencyName == null) return@launch
      val sellCurrencyFromDB = getCurrencyByName(newCurrencyItem.currencyName)
      if (sellCurrencyFromDB == null) {
        sellCurrencyLiveData.value = newCurrencyItem
        return@launch
      }
      sellCurrencyLiveData.value = newCurrencyItem.copy(
        exchangeRate = sellCurrencyFromDB.exchangeRate,
        exchangeCount = sellCurrencyFromDB.exchangeCount,
        fee = sellCurrencyFromDB.fee
      )
    }
  }

  fun getCurrencyExchangeRatesFromDatabase() {
    mExchangeRatesLoading.value = true
    viewModelScope.launch {
      updateCurrencyExchangeRatesFromDatabase()
    }
  }

  fun updateReceiveCurrency(receiveCurrency: CurrencyDataItem) {
    viewModelScope.launch {
      if (receiveCurrency.currencyName == null) return@launch
      updateReceiveCurrencyAmount(sellCurrencyLiveData.value!!, receiveCurrency)
    }
  }

  fun updateSellCurrency(sellCurrency: CurrencyDataItem) {
    updateCurrencyDataItemFromDB(sellCurrency)
  }

  private fun updateAmountOfSelectedCurrencies(currenciesData: Pair<CurrencyDataItem?, CurrencyDataItem?>) {
    val (sellCurrency, receiveCurrency) = currenciesData
    if (sellCurrency == null || receiveCurrency == null || isCurrencyDataFieldsNotValid(sellCurrency) || isCurrencyDataFieldsNotValid(
        receiveCurrency
      )
    ) return
    viewModelScope.launch {
      val sellAmountLeft = getCurrencyByName(sellCurrency.currencyName!!)?.amount ?: 0.00
      sellCurrencyLiveData.value = sellCurrency.copy(amount = sellAmountLeft.roundAmount())
      mSellCurrencyAmountLeft.value = sellAmountLeft.roundAmount()

      val receive = receiveCurrencyExchangeAmount(sellCurrency, receiveCurrency)
      receiveCurrencyLiveData.value = receiveCurrency.copy(operationalAmount = receive.roundAmount())
      mReceivedAmountOfSelectedCurrency.value = receive.roundAmount()
    }
  }

  private fun updateReceiveCurrencyAmount(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?) {
    val sellCurrencyName = sellCurrency?.currencyName

    receiveCurrency?.let {
      viewModelScope.launch {
        val sellAmountLeft = sellCurrencyName?.let { getCurrencyByName(it)?.amount } ?: 0.00
        val receive = receiveCurrencyExchangeAmount(sellCurrency?.copy(amount = sellAmountLeft.roundAmount()), receiveCurrency)
        receiveCurrencyLiveData.value = receiveCurrency.copy(operationalAmount = receive.roundAmount())
      }
    }
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
        sellCurrencyLiveData.value = updatedSellCurrency

        val updatedReceiveCurrency = receiveCurrency.copy(
          amount = receiveCurrency.amount!! + receiveCurrencyAmount
        )
        updateCurrencyExchange(updatedReceiveCurrency)
        receiveCurrencyLiveData.value = updatedReceiveCurrency

        updateFeeFor(sellCurrency, sellCurrency.operationalAmount)
        Log.d("MainViewModel", "updateCurrencyExchangeRatesFromDatabase")
      }
    }
    mIsLoadingAfterSubmit.value = false
  }

  override fun onCleared() {
    super.onCleared()
//    updateCurrentCurrencySelection.removeObserver(updateCurrentCurrencySelectionObserver)
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
    val currencies = repository.getCurrenciesFromDatabase()
    currencyBalanceDBLiveData.value = currencies.mapToCurrencyDataItemList().let { currencyDataItems ->
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

  private suspend fun convertToEuro(currencyName: String?, operationalAmount: Double?): Double {
    if (currencyName == null || operationalAmount == null) return 0.00
    val currencyRate = getCurrencyRateByName(currencyName)
    return operationalAmount / currencyRate
  }

  private suspend fun getCurrencyRateByName(currencyName: String): Double {
    return getCurrencyItemFromDBBy(currencyName)?.exchangeRate ?: 0.00
  }

  private suspend fun getCurrencyItemFromDBBy(currencyName: String?): CurrencyDataItem? {
    if (currencyName == null) return null
    return getCurrencyByName(currencyName)
  }

  private suspend fun receiveCurrencyExchangeAmount(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?): Double {
    Log.d("MainViewModel", "receiveCurrencyExchangeAmount")
    if (sellCurrency == null || receiveCurrency == null) return 0.00
    val euroAmountOfCurrencyToSell = convertToEuro(sellCurrency.currencyName, sellCurrency.operationalAmount)
    return receiveCurrencyAmountFromEuro(receiveCurrency.currencyName, euroAmountOfCurrencyToSell)
  }

  private suspend fun updateFeeFor(sellCurrency: CurrencyDataItem?, soldAmount: Double) {
    sellCurrency?.let {
      if (sellCurrency.currencyName == null) return
      val fee = repository.getSoldCurrencyFee(sellCurrency.currencyName, soldAmount)
      sellCurrencyLiveData.value = sellCurrency.copy(fee = fee)
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
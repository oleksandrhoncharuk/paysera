package com.example.paysera_core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.paysera_core.repository.mapper.mapToCurrency
import com.example.paysera_core.repository.mapper.mapToCurrencyDataItem
import com.example.paysera_core.repository.mapper.mapToCurrencyDataItemList
import com.example.paysera_core.model.CurrencyData
import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_core.repository.CurrencyExchangeRepository
import com.example.paysera_core.tools.combine
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
  private val mSubmitError: MutableLiveData<Boolean> = MutableLiveData()
  val submitError: LiveData<Boolean>
    get() = mSubmitError

  private val mExchangeRatesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
  val exchangeRatesLoading: LiveData<Boolean>
    get() = mExchangeRatesLoading

  private val mShowSubmitDialog: MutableLiveData<Boolean> = MutableLiveData(false)
  val showSubmitDialog: LiveData<Boolean>
    get() = mShowSubmitDialog

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

      addSource(mSellCurrencyLiveData) { newSellCurrency ->
        sellCurrency = newSellCurrency
        value = combine(currencies, newSellCurrency, sellCurrency)
      }

      addSource(mReceiveCurrencyLiveData) { newReceiveCurrency ->
        receiveCurrency = newReceiveCurrency
        value = combine(currencies, sellCurrency, receiveCurrency)
      }
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

  fun dismissDialog() {
    mShowSubmitDialog.value = false
  }

  fun submitCurrencyExchange(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?) {
    if (sellCurrency == null || receiveCurrency == null) {
      mShowSubmitDialog.postValue(false)
      mSubmitError.postValue(true)
      return
    }
    viewModelScope.launch {
      mShowSubmitDialog.postValue(false)
      mSubmitError.postValue(false)
      try {
        val sellCurrencyFromDB = repository.getCurrencyByName(sellCurrency.currencyName)?.mapToCurrencyDataItem(
          updatedOperationalAmount = sellCurrency.operationalAmount
        )
        val receiveCurrencyFromDB = repository.getCurrencyByName(receiveCurrency.currencyName)?.mapToCurrencyDataItem(
          updatedOperationalAmount = receiveCurrency.operationalAmount
        )
        if (isOperationPossible(sellCurrencyFromDB, receiveCurrencyFromDB)) {
          val updatedSellCurrency = sellCurrencyFromDB?.copy(
            amount = calculateAmountThatLeft(sellCurrencyFromDB),
            exchangeCount = (sellCurrency.exchangeCount ?: 0) + 1
          )

          updateCurrencyExchange(updatedSellCurrency)

          val updatedReceiveCurrency = receiveCurrencyFromDB?.copy(
            amount = (receiveCurrencyFromDB.amount ?: 0.0) + (receiveCurrencyFromDB.operationalAmount ?: 0.0),
          )

          updateCurrencyExchange(updatedReceiveCurrency)

          mSellCurrencyLiveData.value = updatedSellCurrency!!
          mReceiveCurrencyLiveData.value = updatedReceiveCurrency!!
        } else {
          mSubmitError.postValue(true)
        }
      } finally {
        if (mSubmitError.value == false) {
          mShowSubmitDialog.postValue(true)
        }
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

  private suspend fun updateReceiveCurrencyAmount(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?) {
    val sellCurrencyFromDB = sellCurrency?.currencyName?.let { getCurrencyByName(it) }
    val receiveCurrencyFromDB = receiveCurrency?.currencyName?.let { getCurrencyByName(it) }

    val updatedReceiveAmount = receiveCurrencyExchangeAmount(
      sellCurrency?.copy(amount = sellCurrencyFromDB?.amount),
      receiveCurrency?.copy(
        amount = receiveCurrencyFromDB?.amount,
        exchangeRate = receiveCurrencyFromDB?.exchangeRate ?: receiveCurrency.exchangeRate,
        exchangeCount = receiveCurrencyFromDB?.exchangeCount,
        fee = receiveCurrencyFromDB?.fee ?: receiveCurrency.fee
      )
    )
    mReceiveCurrencyLiveData.value = receiveCurrency?.copy(operationalAmount = updatedReceiveAmount)
  }

  fun isCurrenciesSame(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?): Boolean {
    if (sellCurrency == null || receiveCurrency == null) return false
    return sellCurrency.currencyName.equals(receiveCurrency.currencyName, ignoreCase = true)
  }

  private fun calculateAmountThatLeft(currency: CurrencyDataItem?) =
    currency?.let {
      if (it.amount == null || it.operationalAmount == null) return@let 0.0
      it.amount - (it.operationalAmount + it.fee)
    }

  private fun isOperationPossible(sellCurrency: CurrencyDataItem?, receiveCurrency: CurrencyDataItem?): Boolean {
    if (isCurrencyDataFieldsNotValid(sellCurrency) && isCurrencyDataFieldsNotValid(receiveCurrency)) return false
    return sellCurrency?.amount!! >= (sellCurrency.operationalAmount!! + sellCurrency.fee)
  }

  private fun isCurrencyDataFieldsNotValid(currencyData: CurrencyDataItem?): Boolean {
    if (currencyData == null) return true
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
    if (sellCurrency == null || receiveCurrency == null) return 0.00
    val euroAmountOfCurrencyToSell = convertToEuro(sellCurrency)
    return receiveCurrencyAmountFromEuro(receiveCurrency, euroAmountOfCurrencyToSell)
  }

  private suspend fun getCurrencyByName(currencyName: String): CurrencyDataItem? {
    val currencyByName = repository.getCurrencyByName(currencyName) ?: return null
    return currencyByName.mapToCurrencyDataItem()
  }

  private suspend fun updateCurrencyExchange(currency: CurrencyDataItem?) {
    if (currency == null) return
    repository.updateCurrencyExchangeFrom(currency.mapToCurrency())
  }
}
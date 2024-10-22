package com.example.paysera_core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.paysera_core.repository.CurrencyExchangeRepository
import com.example.paysera_database.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: CurrencyExchangeRepository
) : ViewModel() {

  private val mCurrencyBalanceDBLiveData: MutableLiveData<List<Currency>> = MutableLiveData()
  val currencyBalanceDBLiveData: LiveData<List<Currency>>
    get() = mCurrencyBalanceDBLiveData

  val currencyNames: LiveData<List<String>>
    get() = mCurrencyBalanceDBLiveData.map { currencyList -> currencyList.map { it.currencyName } }

  private val mReceivedAmountOfSelectedCurrency: MutableLiveData<Double> = MutableLiveData()
  val receivedAmountOfSelectedCurrency: LiveData<Double>
    get() = mReceivedAmountOfSelectedCurrency

  fun getCurrencyExchangeRatesFromDatabase() {
    viewModelScope.launch {
      mCurrencyBalanceDBLiveData.value = repository.getCurrencyListFromDatabase()
    }
  }

  suspend fun receiveCurrencyAmountFromEuro(currencyNameToBuy: String, amountToSell: Double): Double {
    val currencyRate = getCurrencyRateByName(currencyNameToBuy)
    return amountToSell * currencyRate
  }

  suspend fun convertToEuro(currencyName: String, amount: Double): Double {
    val currencyRate = getCurrencyRateByName(currencyName)
    return amount / currencyRate
  }

  suspend fun getCurrencyRateByName(currencyName: String): Double {
    return repository.currencyRatesFlow.filterNotNull().map { it.rates[currencyName] }.single() ?: 0.0
  }

  suspend fun receiveCurrencyExchangeAmount(currencyNameToSell: String, currencyNameToBuy: String, amountToSell: Double): Double {
    val euroAmountOfCurrencyToSell = convertToEuro(currencyNameToSell, amountToSell)
    return receiveCurrencyAmountFromEuro(currencyNameToBuy, euroAmountOfCurrencyToSell)
  }

  fun loadReceiveAmountOfSelectedCurrency(currencyNameToSell: String, currencyNameToBuy: String, amount: Double) {
    viewModelScope.launch {
      val receive = receiveCurrencyExchangeAmount(currencyNameToSell, currencyNameToBuy, amount)
      mReceivedAmountOfSelectedCurrency.value = receive
    }
  }

  fun submitCurrencyExchange(sellCurrency: Currency, sellAmount: Double, receiveCurrencyName: String) {
    viewModelScope.launch {
      if (sellCurrency.amount >= sellAmount) {
        val sellCurrencyAmount = sellCurrency.amount - sellAmount
        val receiveCurrencyAmount = receiveCurrencyExchangeAmount(
          sellCurrency.currencyName,
          receiveCurrencyName,
          sellAmount
        )

        val receiveCurrency = getCurrencyByName(receiveCurrencyName)
        updateCurrencyExchange(sellCurrency.copy(amount = sellCurrencyAmount))
        updateCurrencyExchange(
          receiveCurrency?.copy(
            amount = receiveCurrency.amount + receiveCurrencyAmount,
            exchangeCount = receiveCurrency.exchangeCount + 1
          )
        )
      }
    }
  }

  private suspend fun getCurrencyByName(currencyName: String): Currency? {
    return repository.getCurrencyByName(currencyName)
  }

  private suspend fun updateCurrencyExchange(currency: Currency?) {
    if (currency == null) return
    repository.updateCurrency(currency)
  }
}
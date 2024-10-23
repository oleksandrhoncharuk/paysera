package com.example.paysera_core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.paysera_core.repository.CurrencyExchangeRepository
import com.example.paysera_database.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
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

  private val mSellCurrencyAmountLeft: MutableLiveData<Double> = MutableLiveData()
  val sellCurrencyAmountLeft: LiveData<Double>
    get() = mSellCurrencyAmountLeft

  private val mSellCurrencyFee: MutableLiveData<Double> = MutableLiveData()
  val sellCurrencyFee: LiveData<Double>
    get() = mSellCurrencyFee

  private val mIsLoadingAfterSubmit: MutableLiveData<Boolean> = MutableLiveData(false)
  val isLoadingAfterSubmit: LiveData<Boolean>
    get() = mIsLoadingAfterSubmit

  fun getCurrencyExchangeRatesFromDatabase() {
    viewModelScope.launch {
      val currencyRates = repository.getCurrencyListFromDatabase()
      mCurrencyBalanceDBLiveData.value = currencyRates
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
    return getCurrencyByName(currencyName)?.exchangeRate ?: 0.0
  }

  suspend fun receiveCurrencyExchangeAmount(currencyNameToSell: String, currencyNameToBuy: String, amountToSell: Double): Double {
    val euroAmountOfCurrencyToSell = convertToEuro(currencyNameToSell, amountToSell)
    return receiveCurrencyAmountFromEuro(currencyNameToBuy, euroAmountOfCurrencyToSell)
  }

  fun updateAmountOfSelectedCurrencies(currencyNameToSell: String, currencyNameToBuy: String, amount: Double) {
    viewModelScope.launch {
      val sellAmountLeft = getCurrencyByName(currencyNameToSell)?.amount ?: 0.0
      mSellCurrencyAmountLeft.value = sellAmountLeft
      val receive = receiveCurrencyExchangeAmount(currencyNameToSell, currencyNameToBuy, amount)
      mReceivedAmountOfSelectedCurrency.value = receive
    }
  }

  fun submitCurrencyExchange(sellCurrency: Currency, sellAmount: Double, receiveCurrencyName: String) {
    viewModelScope.launch {
      mIsLoadingAfterSubmit.value = true
      if (sellCurrency.amount >= sellAmount) {
        val sellCurrencyAmountLeft = sellCurrency.amount - sellAmount
        val receiveCurrencyAmount = receiveCurrencyExchangeAmount(
          sellCurrency.currencyName,
          receiveCurrencyName,
          sellAmount
        )

        val receiveCurrency = getCurrencyByName(receiveCurrencyName)
        updateCurrencyExchange(
          sellCurrency.copy(
            amount = sellCurrencyAmountLeft,
            exchangeCount = sellCurrency.exchangeCount + 1
          )
        )
        updateCurrencyExchange(
          receiveCurrency?.copy(
            amount = receiveCurrency.amount + receiveCurrencyAmount
          )
        )
        getCurrencyExchangeRatesFromDatabase()
      }
    }
    mIsLoadingAfterSubmit.value = false
  }

  fun getFeeForSoldCurrency(currencyName: String, soldAmount: Double) {
    viewModelScope.launch {
      val fee = repository.getSoldCurrencyFee(currencyName, soldAmount)
      mSellCurrencyFee.value = fee
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
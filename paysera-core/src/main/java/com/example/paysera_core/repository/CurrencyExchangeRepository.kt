package com.example.paysera_core.repository

import android.util.Log
import com.example.paysera_database.db.DatabaseRepositoryImpl
import com.example.paysera_database.isFeeFree
import com.example.paysera_database.mapToCurrency
import com.example.paysera_database.mapToCurrencyList
import com.example.paysera_database.mapToDomain
import com.example.paysera_database.model.Currency
import com.example.paysera_database.updateWithCurrency
import com.example.paysera_network.mapToCurrencyRate
import com.example.paysera_network.models.CurrencyRate
import com.example.paysera_network.service.CurrencyExchangeInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val FEE_VALUE = 0.007

class CurrencyExchangeRepository @Inject constructor(
  private val database: DatabaseRepositoryImpl,
  private val networkApi: CurrencyExchangeInterface
) {

  private fun getCurrencyExchangeRates(): Flow<CurrencyRate?> = flow {
    val response = networkApi.getCurrencyExchange()?.mapToCurrencyRate()
    emit(response)
  }.flowOn(Dispatchers.IO)


  suspend fun fetchCurrencyExchangeRates() {
    val currencyRates = getCurrencyExchangeRates()
    currencyRates.collect {
      updateDatabase(it)
    }
  }

  private suspend fun updateDatabase(currencyRates: CurrencyRate?) {
    if (currencyRates == null) return

    withContext(Dispatchers.IO) {
      val currencyList = currencyRates.rates.map { Currency(currencyName = it.key, exchangeRate = it.value) }
      currencyList.forEach {
        Log.d("CurrencyExchangeRepository", "Update databse from network: ${it.currencyName}")
        val currency = getCurrencyByName(it.currencyName)
        if (currency != null) {
          updateCurrencyExchangeFrom(it)
        } else {
          insertCurrency(it)
        }
      }
    }
  }

  fun getCurrencyExchangeRatesFromDatabase(): Flow<List<Currency>> {
    return database.currencyExchangeDao().getAllCurrenciesFlow()
      .map { currencyList ->
        currencyList.mapToCurrencyList().sortedByDescending { it.amount }
      }
      .distinctUntilChanged()
  }

  suspend fun getCurrenciesFromDatabase(): List<Currency> {
    return withContext(Dispatchers.IO) {
      val databaseList = database.currencyExchangeDao().getAllCurrencies()
      databaseList.mapToCurrencyList()
    }
  }

  suspend fun getCurrencyByName(currencyName: String): Currency? {
    return withContext(Dispatchers.IO) {
      database.currencyExchangeDao().getCurrencyByName(currencyName)?.mapToCurrency() ?: return@withContext null
    }
  }

  suspend fun getSoldCurrencyFee(currencyName: String, soldAmount: Double): Double {
    return withContext(Dispatchers.IO) {
      val currency = getCurrencyByName(currencyName) ?: return@withContext 0.0
      Log.d("CurrencyExchangeRepository", "exchange count: ${currency.exchangeCount}")
      return@withContext if (currency.isFeeFree()) {
        0.0
      } else {
        soldAmount * FEE_VALUE
      }
    }
  }

  suspend fun insertCurrency(currency: Currency) {
    withContext(Dispatchers.IO) {
      Log.d("CurrencyExchangeRepository", "Inserting currency: ${currency.currencyName}")
      database.currencyExchangeDao().insert(currency.mapToDomain())
    }
  }

  suspend fun updateCurrencyExchangeFrom(currency: Currency) {
    withContext(Dispatchers.IO) {
      Log.d("CurrencyExchangeRepository", "Update currency: ${currency.currencyName}")
      val domainCurrency = database.currencyExchangeDao().getCurrencyByName(currency.currencyName) ?: return@withContext
      val updatedCurrencyExchange = domainCurrency.updateWithCurrency(currency)
      database.currencyExchangeDao().update(updatedCurrencyExchange)
    }
  }

  suspend fun deleteCurrencyExchange(currency: Currency) {
    withContext(Dispatchers.IO) {
      database.currencyExchangeDao().delete(currency.mapToDomain())
    }
  }
}
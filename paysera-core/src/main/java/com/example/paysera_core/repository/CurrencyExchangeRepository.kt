package com.example.paysera_core.repository

import com.example.paysera_database.db.DatabaseRepositoryImpl
import com.example.paysera_database.mapToCurrency
import com.example.paysera_database.mapToCurrencyList
import com.example.paysera_database.mapToDomain
import com.example.paysera_database.model.Currency
import com.example.paysera_network.mapToCurrencyRate
import com.example.paysera_network.models.CurrencyRate
import com.example.paysera_network.service.CurrencyExchangeInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyExchangeRepository @Inject constructor(
  private val database: DatabaseRepositoryImpl,
  private val networkApi: CurrencyExchangeInterface
) {

  private val _currencyRatesFlow = MutableSharedFlow<CurrencyRate?>()
  val currencyRatesFlow: SharedFlow<CurrencyRate?> = _currencyRatesFlow.asSharedFlow()

  private fun getCurrencyExchangeRates(): Flow<CurrencyRate?> = flow {
    val response = networkApi.getCurrencyExchange()?.mapToCurrencyRate()
    emit(response)
  }.flowOn(Dispatchers.IO)


  suspend fun fetchCurrencyExchangeRates() {
    val currencyRates = getCurrencyExchangeRates()
    currencyRates.collect {
      _currencyRatesFlow.emit(it)
      updateDatabase(it)
    }
  }

  private suspend fun updateDatabase(currencyRates: CurrencyRate?) {
    if (currencyRates == null) return

    withContext(Dispatchers.Default) {
      val currencyList = currencyRates.rates.map { Currency(it.key, it.value) }
      currencyList.forEach {
        val currency = getCurrencyByName(it.currencyName)
        if (currency != null) {
          updateCurrency(it)
        } else {
          insertCurrency(it)
        }
      }
    }
  }

  suspend fun getCurrencyListFromDatabase(): List<Currency> {
    return withContext(Dispatchers.IO) {
      database.currencyExchangeDao().getAllCurrencies().mapToCurrencyList()
    }
  }

  suspend fun getCurrencyByName(currencyName: String): Currency? {
    return withContext(Dispatchers.IO) {
      database.currencyExchangeDao().getCurrencyByName(currencyName)?.mapToCurrency() ?: return@withContext null
    }
  }

  suspend fun insertCurrency(currency: Currency) {
    withContext(Dispatchers.IO) {
      database.currencyExchangeDao().insert(currency.mapToDomain())
    }
  }

  suspend fun updateCurrency(currency: Currency) {
    withContext(Dispatchers.IO) {
      database.currencyExchangeDao().update(currency.mapToDomain())
    }
  }

  suspend fun deleteCurrencyExchange(currency: Currency) {
    withContext(Dispatchers.IO) {
      database.currencyExchangeDao().delete(currency.mapToDomain())
    }
  }
}
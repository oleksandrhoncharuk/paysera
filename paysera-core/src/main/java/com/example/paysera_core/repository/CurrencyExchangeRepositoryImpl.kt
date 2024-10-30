package com.example.paysera_core.repository

import com.example.paysera_database.db.DatabaseRepository
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

interface CurrencyExchangeRepository {
  fun getCurrencyExchangeRatesFromDatabase(): Flow<List<Currency>>

  suspend fun updateCurrencyExchangeFrom(currency: Currency)

  suspend fun getCurrencyByName(currencyName: String): Currency?

  suspend fun fetchCurrencyExchangeRates()
}

class CurrencyExchangeRepositoryImpl @Inject constructor(
  private val database: DatabaseRepository,
  private val networkApi: CurrencyExchangeInterface
) : CurrencyExchangeRepository {

  override fun getCurrencyExchangeRatesFromDatabase(): Flow<List<Currency>> {
    return database.currencyExchangeDao().getAllCurrenciesFlow()
      .map { currencyList ->
        currencyList.mapToCurrencyList().sortedByDescending { it.amount }
      }
      .distinctUntilChanged()
  }

  override suspend fun updateCurrencyExchangeFrom(currency: Currency) {
    withContext(Dispatchers.IO) {
      val domainCurrency = database.currencyExchangeDao().getCurrencyByName(currency.currencyName) ?: return@withContext
      val updatedCurrencyExchange = domainCurrency.updateWithCurrency(currency)
      database.currencyExchangeDao().update(updatedCurrencyExchange)
    }
  }

  override suspend fun getCurrencyByName(currencyName: String): Currency? {
    return withContext(Dispatchers.IO) {
      database.currencyExchangeDao().getCurrencyByName(currencyName)?.mapToCurrency() ?: return@withContext null
    }
  }

  override suspend fun fetchCurrencyExchangeRates() {
    val currencyRates = getCurrencyExchangeRates()
    currencyRates.collect {
      updateDatabase(it)
    }
  }

  private fun getCurrencyExchangeRates(): Flow<CurrencyRate?> = flow {
    val response = networkApi.getCurrencyExchange()?.mapToCurrencyRate()
    emit(response)
  }.flowOn(Dispatchers.IO)

  private suspend fun updateDatabase(currencyRates: CurrencyRate?) {
    if (currencyRates == null) return

    withContext(Dispatchers.IO) {
      val currencyList = currencyRates.rates.map { Currency(currencyName = it.key, exchangeRate = it.value) }
      currencyList.forEach {
        val currency = getCurrencyByName(it.currencyName)
        if (currency != null) {
          updateCurrencyExchangeFrom(it)
        } else {
          insertCurrency(it)
        }
      }
    }
  }

  private suspend fun insertCurrency(currency: Currency) {
    withContext(Dispatchers.IO) {
      database.currencyExchangeDao().insert(currency.mapToDomain())
    }
  }
}
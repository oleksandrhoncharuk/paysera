package com.example.paysera_core.repository

import com.example.paysera_database.CurrencyExchangeDao
import com.example.paysera_database.db.DatabaseRepositoryImpl
import com.example.paysera_database.mapToCurrency
import com.example.paysera_database.model.CurrencyExchange
import com.example.paysera_network.mapToCurrencyRatesResponse
import com.example.paysera_network.models.CurrencyRate
import com.example.paysera_network.service.CurrencyExchangeInterface
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyExchangeRepositoryImplTest {

  private lateinit var repository: CurrencyExchangeRepositoryImpl
  private val database: DatabaseRepositoryImpl = mockk(relaxed = true)
  private val networkApi: CurrencyExchangeInterface = mockk(relaxed = true)
  private val mockDao: CurrencyExchangeDao = mockk(relaxed = true)

  @Before
  fun setUp() {
    coEvery { database.currencyExchangeDao() } returns mockDao
    repository = CurrencyExchangeRepositoryImpl(database, networkApi)
  }

  @Test
  fun `test getCurrencyExchangeRatesFromDatabase returns sorted list`() = runBlocking {
    // Arrange
    val currencyList = listOf(
      CurrencyExchange(1, "USD", 1.0, 0.0, 0),
      CurrencyExchange(2, "EUR", 0.9, 1000.0, 0)
    )

    // Mocking the database response
    every { mockDao.getAllCurrenciesFlow() } returns flowOf(currencyList)

    // Act
    val result = repository.getCurrencyExchangeRatesFromDatabase().last()

    // Assert
    assert(result[0].amount >= result[1].amount)
  }

  @Test
  fun `test updateCurrencyExchangeFrom updates currency in database`() = runBlocking {
    // Arrange
    val currency = CurrencyExchange(1, "ALL", 1.0, 0.0, 0)

    coEvery { mockDao.getCurrencyByName(currency.currencyName) } returns currency

    // Act
    repository.updateCurrencyExchangeFrom(currency.mapToCurrency())

    // Assert
    coVerify { mockDao.update(currency) } // Ensure update is called
  }

  @Test
  fun `test getCurrencyByName returns currency from database`() = runBlocking {
    // Arrange
    val currency = CurrencyExchange(1, "USD", 1.0, 0.0, 0)

    coEvery { mockDao.getCurrencyByName(currency.currencyName) } returns currency

    // Act
    val result = repository.getCurrencyByName("USD")

    // Assert
    assert(result == currency.mapToCurrency()) // Check if returned currency matches
  }

  @Test
  fun `test fetchCurrencyExchangeRates updates database`() = runBlocking {
    // Arrange
    val currencyRate = CurrencyRate("EUR", "", mapOf("USD" to 1.0, "EUR" to 0.9))
    val currency = CurrencyExchange(1, "USD", 1.0, 0.0, 0)
    val currency2 = CurrencyExchange(1, "EUR", 1.0, 0.0, 0)
//    val dao = mockk<CurrencyExchangeDao>(relaxed = true)
//    every { database.currencyExchangeDao() } returns dao

    coEvery { mockDao.getCurrencyByName(currency.currencyName) } returns currency
    coEvery { mockDao.getCurrencyByName(currency2.currencyName) } returns currency2
    coEvery { networkApi.getCurrencyExchange() } returns currencyRate.mapToCurrencyRatesResponse()

    // Act
    repository.fetchCurrencyExchangeRates()

    // Assert
    coVerify(exactly = 2) { mockDao.update(any()) }
  }
}

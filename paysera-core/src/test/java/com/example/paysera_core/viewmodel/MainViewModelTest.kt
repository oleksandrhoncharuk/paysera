package com.example.paysera_core.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_core.repository.CurrencyExchangeRepository
import com.example.paysera_core.repository.mapper.mapToCurrency
import com.example.paysera_core.test_tools.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var repository: CurrencyExchangeRepository
  private lateinit var observerSubmitError: Observer<Boolean>
  private lateinit var viewModel: MainViewModel

  @Before
  fun setup() {
    Dispatchers.setMain(Dispatchers.Unconfined)
    repository = mockk(relaxed = true)
    observerSubmitError = mockk(relaxed = true)
    viewModel = MainViewModel(repository)
    viewModel.submitError.observeForever(observerSubmitError)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset to the original Main dispatcher
  }

  @Test
  fun `test submitCurrencyExchange with null values`() {
    viewModel.submitCurrencyExchange(null, null)

    verify { observerSubmitError.onChanged(true) }
    assert(viewModel.showSubmitDialog.value == false)
  }

  @Test
  fun `test submitCurrencyExchange with valid currencies`() = runTest {
    val sellCurrency = CurrencyDataItem(
      currencyName = "USD",
      exchangeRate = 1.2,
      operationalAmount = 100.0,
      amount = 1000.0,
      exchangeCount = 0,
      fee = 0.0
    )
    val receiveCurrency = CurrencyDataItem(
      currencyName = "EUR",
      exchangeRate = 1.0,
      operationalAmount = 0.0,
      amount = 0.0,
      exchangeCount = 0,
      fee = 0.0
    )

    // Mock repository behavior
    coEvery { repository.getCurrencyByName("USD") } returns sellCurrency.mapToCurrency()
    coEvery { repository.getCurrencyByName("EUR") } returns receiveCurrency.mapToCurrency()

    viewModel.submitCurrencyExchange(sellCurrency, receiveCurrency)

    val submitError = viewModel.submitError.getOrAwaitValue()
    val showSubmitDialog = viewModel.showSubmitDialog.getOrAwaitValue()

    assert(!submitError)
    assert(showSubmitDialog) // Dialog should be shown after success
  }

  @Test
  fun `test updateSellCurrency updates LiveData correctly`() {
    val sell = CurrencyDataItem(
      currencyName = "EUR",
      exchangeRate = 1.0,
      operationalAmount = 50.0,
      amount = 0.0,
      exchangeCount = 0,
      fee = 0.0
    )

    val receive = CurrencyDataItem(
      currencyName = "USD",
      exchangeRate = 1.2,
      operationalAmount = 100.0,
      amount = 1000.0,
      exchangeCount = 0,
      fee = 0.0
    )

    coEvery { repository.getCurrencyByName("EUR") } returns sell.mapToCurrency()
    coEvery { repository.getCurrencyByName("USD") } returns receive.mapToCurrency()

    viewModel.updateSellCurrency(sell, receive)

    val sellCurrencyResult = viewModel.currencyDataLiveData.getOrAwaitValue(30).sellCurrency
    val receiveCurrencyResult = viewModel.currencyDataLiveData.getOrAwaitValue(30).receiveCurrency

    // Verify that the LiveData has been updated
    assert(sellCurrencyResult == sell)
    assert(receiveCurrencyResult == receive.calculateOperationalAmountForReceive(sellCurrencyResult!!))
  }


  @Test
  fun `test dismissDialog hides the submit dialog`() {
    viewModel.dismissDialog()

    val showSubmitDialog = viewModel.showSubmitDialog.getOrAwaitValue()
    assert(!showSubmitDialog)
  }


  private fun CurrencyDataItem.calculateOperationalAmountForReceive(sellCurrency: CurrencyDataItem): CurrencyDataItem {
    val op = sellCurrency.operationalAmount ?: 0.0
    val rate = this.exchangeRate

    return this.copy(
      operationalAmount = op * rate
    )
  }
}

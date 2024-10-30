package com.example.paysera_compose_ui

import com.example.paysera_compose_ui.model.CurrencyStateItem
import com.example.paysera_core.model.CurrencyData
import com.example.paysera_core.model.CurrencyDataItem
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test

class CurrencyMappingTest {

  @Test
  fun `CurrencyDataItem maps to CurrencyStateItem correctly`() {
    val currencyDataItem = CurrencyDataItem(
      currencyName = "USD",
      exchangeRate = 1.2,
      amount = 100.0,
      operationalAmount = 50.0,
      exchangeCount = 5,
      fee = 0.01
    )

    val currencyStateItem = currencyDataItem.mapToCurrencyState()

    assertEquals("USD", currencyStateItem.currencyName)
    assertEquals(1.2, currencyStateItem.exchangeRate, 0.0)
    assertEquals("50.00", currencyStateItem.operationalAmount)
    assertEquals(100.0, currencyStateItem.currencyAmount, 0.0)
    assertEquals(5, currencyStateItem.exchangeCount)
    assertEquals(0.01, currencyStateItem.fee, 0.0)
  }

  @Test
  fun `CurrencyStateItem maps to CurrencyDataItem correctly`() {
    val currencyStateItem = CurrencyStateItem(
      currencyName = "EUR",
      exchangeRate = 1.1,
      currencyAmount = 200.0,
      operationalAmount = "20.50",
      exchangeCount = 3,
      fee = 0.02
    )

    val currencyDataItem = currencyStateItem.mapToCurrencyDataItem()

    assertEquals("EUR", currencyDataItem.currencyName)
    assertEquals(1.1, currencyDataItem.exchangeRate, 0.0)
    assertNotNull(currencyDataItem.amount)
    assertEquals(200.0, currencyDataItem.amount!!, 0.0)
    assertNotNull(currencyDataItem.operationalAmount)
    assertEquals(20.50, currencyDataItem.operationalAmount!!, 0.0)
    assertEquals(3, currencyDataItem.exchangeCount)
  }

  @Test
  fun `CurrencyData maps to CurrencyState correctly`() {
    val currencyData = CurrencyData(
      balance = mapOf("USD" to 100.0, "EUR" to 200.0),
      sellCurrency = CurrencyDataItem(
        currencyName = "USD",
        exchangeRate = 1.2,
        amount = 100.0,
        operationalAmount = 50.0,
        exchangeCount = 2,
        fee = 0.01
      ),
      receiveCurrency = CurrencyDataItem(
        currencyName = "EUR",
        exchangeRate = 0.9,
        amount = 200.0,
        operationalAmount = 30.0,
        exchangeCount = 1,
        fee = 0.02
      )
    )

    val currencyState = currencyData.mapToCurrencyState()

    assertEquals(mapOf("USD" to 100.0, "EUR" to 200.0), currencyState.balance)
    assertEquals("USD", currencyState.sellStateItem?.currencyName)
    assertEquals("EUR", currencyState.receiveStateItem?.currencyName)
    assertEquals("50.00", currencyState.sellStateItem?.operationalAmount)
    assertEquals("30.00", currencyState.receiveStateItem?.operationalAmount)
  }
}
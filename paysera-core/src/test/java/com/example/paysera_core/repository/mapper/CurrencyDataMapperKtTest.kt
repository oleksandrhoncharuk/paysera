package com.example.paysera_core.repository.mapper

import com.example.paysera_core.model.CurrencyDataItem
import com.example.paysera_database.model.Currency
import org.junit.jupiter.api.Assertions.*
import org.junit.Test

class CurrencyDataMapperKtTest {
  @Test
  fun `mapToCurrencyDataItem maps correctly with default values`() {
    val currency = Currency(
      currencyName = "USD",
      exchangeRate = 1.2,
      operationalAmount = 100.0,
      amount = 200.0,
      exchangeCount = 3
    )

    val result = currency.mapToCurrencyDataItem()

    assertEquals("USD", result.currencyName)
    assertEquals(1.2, result.exchangeRate, 0.001)
    assertNotNull(result.operationalAmount)
    assertEquals(100.0, result.operationalAmount!!, 0.001)
    assertNotNull(result.amount)
    assertEquals(200.0, result.amount!!, 0.001)
    assertEquals(3, result.exchangeCount)
    assertEquals(0.0, result.fee, 0.001) // since exchangeCount <= 5
  }

  @Test
  fun `mapToCurrencyDataItem maps correctly with updated values`() {
    val currency = Currency(
      currencyName = "USD",
      exchangeRate = 1.2,
      operationalAmount = 100.0,
      amount = 200.0,
      exchangeCount = 3
    )

    val result = currency.mapToCurrencyDataItem(updatedOperationalAmount = 150.0, updatedExchangeCount = 7)

    assertEquals("USD", result.currencyName)
    assertEquals(1.2, result.exchangeRate, 0.001)
    assertNotNull(result.operationalAmount)
    assertEquals(150.0, result.operationalAmount!!, 0.001)
    assertNotNull(result.amount)
    assertEquals(200.0, result.amount!!, 0.001)
    assertEquals(7, result.exchangeCount)
    assertEquals(1.05, result.fee, 0.001) // since exchangeCount > 5, fee applies
  }

  @Test
  fun `mapToCurrency maps back to Currency with defaults`() {
    val currencyDataItem = CurrencyDataItem(
      currencyName = "USD",
      exchangeRate = 1.2,
      operationalAmount = null,
      amount = null,
      exchangeCount = null,
      fee = 0.0
    )

    val result = currencyDataItem.mapToCurrency()

    assertEquals("USD", result.currencyName)
    assertEquals(1.2, result.exchangeRate, 0.001)
    assertEquals(0.0, result.operationalAmount, 0.001) // default when null
    assertEquals(0.0, result.amount, 0.001) // default when null
    assertEquals(0, result.exchangeCount) // default when null
  }

  @Test
  fun `mapToCurrencyDataItemList maps list correctly`() {
    val currencies = listOf(
      Currency("USD", 1.2, 100.0, 200.0, 3),
      Currency("EUR", 1.0, 80.0, 150.0, 2)
    )

    val result = currencies.mapToCurrencyDataItemList()

    assertEquals(2, result.size)
    assertEquals("USD", result[0].currencyName)
    assertEquals("EUR", result[1].currencyName)
  }

  @Test
  fun `getFee returns correct fee based on exchangeCount and operationalAmount`() {
    val fee1 = getFee(6, 100.0)
    assertEquals(0.7, fee1, 0.001) // exchangeCount > 5, fee applies

    val fee2 = getFee(4, 100.0)
    assertEquals(0.0, fee2, 0.001) // exchangeCount <= 5, no fee

    val fee3 = getFee(6, null)
    assertEquals(0.0, fee3, 0.001) // operationalAmount is null, no fee
  }

  @Test
  fun `roundAmount returns correctly formatted string`() {
    val value = 123.456
    assertEquals("123.46", value.roundAmount()) // rounded to two decimals

    val nullValue: Double? = null
    assertEquals("0.00", nullValue.roundAmount()) // default for null
  }
}
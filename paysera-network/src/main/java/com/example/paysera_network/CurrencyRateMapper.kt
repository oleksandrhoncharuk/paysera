package com.example.paysera_network

import com.example.paysera_network.models.CurrencyRate
import com.example.paysera_network.models.CurrencyRatesResponse

fun CurrencyRatesResponse.mapToCurrencyRate() = CurrencyRate(
  base = base,
  date = date,
  rates = rates
)

fun CurrencyRate.getExchangeRate(currency: String) = rates[currency] ?: 0.0

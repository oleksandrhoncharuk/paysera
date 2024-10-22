package com.example.paysera_network.service

import com.example.paysera_network.models.CurrencyRatesResponse
import retrofit2.http.GET

interface CurrencyExchangeInterface {
  @GET("currency-exchange-rates")
  suspend fun getCurrencyExchange(): CurrencyRatesResponse?
}
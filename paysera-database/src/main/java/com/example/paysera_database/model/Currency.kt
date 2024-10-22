package com.example.paysera_database.model

data class Currency(
  val currencyName: String = "EUR",
  val exchangeRate: Double = 1.0,
  val amount: Double = 0.0,
  val exchangeCount: Int = 0
)
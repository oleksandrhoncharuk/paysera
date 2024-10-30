package com.example.paysera_database.model

data class Currency(
  val currencyName: String,
  val exchangeRate: Double,
  val amount: Double = 0.0,
  val operationalAmount: Double = 0.0,
  val exchangeCount: Int = 0
)
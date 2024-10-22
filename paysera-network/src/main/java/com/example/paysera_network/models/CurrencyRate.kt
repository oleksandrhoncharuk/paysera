package com.example.paysera_network.models

data class CurrencyRate(
  val base: String,
  val date: String,
  val rates: Map<String, Double>
)
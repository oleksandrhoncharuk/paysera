package com.example.paysera_database.model

import java.math.RoundingMode

data class Currency(
  val currencyName: String,
  val exchangeRate: Double,
  val amount: Double = 0.0,
  val operationalAmount: Double = 0.0,
  val exchangeCount: Int = 0
)

fun Double?.roundAmount(): Double {
  if (this == null) return 0.0
  return this.toBigDecimal()
    .setScale(2, RoundingMode.HALF_EVEN)
    .toDouble()
}
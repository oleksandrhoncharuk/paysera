package com.example.paysera_database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_exchange_table")
data class CurrencyExchange(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val currencyName: String,
  val exchangeRate: Double,
  val amount: Double,
  val exchangeCount: Int
)
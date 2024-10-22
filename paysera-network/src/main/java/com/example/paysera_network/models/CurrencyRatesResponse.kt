package com.example.paysera_network.models

import com.google.gson.annotations.SerializedName

data class CurrencyRatesResponse(
  @SerializedName("base")
  val base: String,

  @SerializedName("date")
  val date: String,

  @SerializedName("rates")
  val rates: Map<String, Double>
)
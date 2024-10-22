package com.example.paysera_network.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService private constructor() {
  private val retrofit: Retrofit

  init {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    val client: OkHttpClient.Builder = OkHttpClient.Builder()
      .addInterceptor(interceptor)
    retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(client.build())
      .build()
  }

  val currencyExchangeAPI: CurrencyExchangeInterface
    get() = retrofit.create(CurrencyExchangeInterface::class.java)

  companion object {
    private var service: NetworkService? = null
    const val BASE_URL = "https://developers.paysera.com/tasks/api/"
    val instance: NetworkService?
      get() {
        if (service == null) service = NetworkService()
        return service
      }
  }
}
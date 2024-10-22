package com.example.paysera_network.di

import com.example.paysera_network.service.CurrencyExchangeInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  private const val BASE_URL = "https://developers.paysera.com/tasks/api/"

  @Provides
  @Singleton
  fun provideCurrencyExchangeInterface(): CurrencyExchangeInterface {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    val client: OkHttpClient.Builder = OkHttpClient.Builder()
      .addInterceptor(interceptor)
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(client.build())
      .build()
      .create(CurrencyExchangeInterface::class.java)
  }
}
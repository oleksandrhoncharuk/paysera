package com.example.paysera_core.repository.di

import com.example.paysera_core.repository.CurrencyExchangeRepository
import com.example.paysera_core.repository.CurrencyExchangeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindCurrencyExchangeRepository(
    impl: CurrencyExchangeRepositoryImpl
  ): CurrencyExchangeRepository
}

package com.example.paysera_database.db

import com.example.paysera_database.CurrencyExchangeDao

interface DatabaseRepository {

  fun currencyExchangeDao(): CurrencyExchangeDao
}
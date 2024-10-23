package com.example.paysera_database.db

import android.content.Context
import com.example.paysera_database.db.CurrencyDatabase.DatabaseProvider.getDatabase

class DatabaseRepositoryImpl(context: Context) : DatabaseRepository {
  private var database: CurrencyDatabase? = null

  init {
    database = getDatabase(context)
  }
  override fun currencyExchangeDao() = database!!.currencyExchangeDao()
}
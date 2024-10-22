package com.example.paysera_database.db

import android.content.Context
import com.example.paysera_database.db.CurrencyDatabase.DatabaseProvider.getDatabase
import kotlinx.coroutines.CoroutineScope

class DatabaseRepositoryImpl(context: Context, scope: CoroutineScope) : DatabaseRepository {
  private var database: CurrencyDatabase? = null

  init {
    database = getDatabase(context, scope)
  }
  override fun currencyExchangeDao() = database!!.currencyExchangeDao()
}
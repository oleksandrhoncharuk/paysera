package com.example.paysera_database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.paysera_database.CurrencyExchangeDao
import com.example.paysera_database.model.CurrencyExchange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CurrencyExchange::class], version = 2, exportSchema = false)
abstract class CurrencyDatabase : RoomDatabase() {
  abstract fun currencyExchangeDao(): CurrencyExchangeDao

  companion object DatabaseProvider {
    @Volatile
    private var INSTANCE: CurrencyDatabase? = null

    private const val DATABASE_NAME = "currency_exchange_db"

    fun getDatabase(context: Context): CurrencyDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CurrencyDatabase::class.java,
          DATABASE_NAME
        )
          .createFromAsset("databases/currency_exchange_db.db")
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
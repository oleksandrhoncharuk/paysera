package com.example.paysera_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.paysera_database.model.CurrencyExchange
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyExchangeDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(currencyExchange: CurrencyExchange)

  @Update
  suspend fun update(currencyExchange: CurrencyExchange)

  @Delete
  suspend fun delete(currencyExchange: CurrencyExchange)

  @Query("DELETE FROM currency_exchange_table")
  suspend fun deleteAll()

  @Query("SELECT * FROM currency_exchange_table")
  suspend fun getAllCurrencies(): List<CurrencyExchange>

  @Query("SELECT * FROM currency_exchange_table")
  fun getAllCurrenciesFlow(): Flow<List<CurrencyExchange>>

  @Query("SELECT * FROM currency_exchange_table WHERE currencyName = :currencyName")
  suspend fun getCurrencyByName(currencyName: String): CurrencyExchange?
}
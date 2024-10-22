package com.example.paysera_database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.paysera_database.CurrencyExchangeDao
import com.example.paysera_database.model.CurrencyExchange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [CurrencyExchange::class], version = 2, exportSchema = false)
abstract class CurrencyDatabase : RoomDatabase() {
  abstract fun currencyExchangeDao(): CurrencyExchangeDao

  companion object DatabaseProvider {
    @Volatile
    private var INSTANCE: CurrencyDatabase? = null

    private const val DATABASE_NAME = "currency_exchange_db"

    fun getDatabase(context: Context, scope: CoroutineScope): CurrencyDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CurrencyDatabase::class.java,
          DATABASE_NAME
        )
          .addCallback(CurrencyDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class CurrencyDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch {
          populateDatabase(database.currencyExchangeDao())
        }
      }
    }

    suspend fun populateDatabase(currencyDao: CurrencyExchangeDao) {
      // Delete all content here if needed.
      currencyDao.deleteAll()

      // Add default values to the database
      val defaultCurrencies = listOf(
        CurrencyExchange(currencyName = "AED", amount = 0.0, exchangeRate = 4.147043, exchangeCount = 0),
        CurrencyExchange(currencyName = "AFN", amount = 0.0, exchangeRate = 118.466773, exchangeCount = 0),
        CurrencyExchange(currencyName = "ALL", amount = 0.0, exchangeRate = 120.73174, exchangeCount = 0),
        CurrencyExchange(currencyName = "AMD", amount = 0.0, exchangeRate = 545.483468, exchangeCount = 0),
        CurrencyExchange(currencyName = "ANG", amount = 0.0, exchangeRate = 2.035477, exchangeCount = 0),
        CurrencyExchange(currencyName = "AOA", amount = 0.0, exchangeRate = 623.962579, exchangeCount = 0),
        CurrencyExchange(currencyName = "ARS", amount = 0.0, exchangeRate = 116.396925, exchangeCount = 0),
        CurrencyExchange(currencyName = "AUD", amount = 0.0, exchangeRate = 1.57676, exchangeCount = 0),
        CurrencyExchange(currencyName = "AWG", amount = 0.0, exchangeRate = 2.032821, exchangeCount = 0),
        CurrencyExchange(currencyName = "AZN", amount = 0.0, exchangeRate = 1.895163, exchangeCount = 0),
        CurrencyExchange(currencyName = "BAM", amount = 0.0, exchangeRate = 1.951459, exchangeCount = 0),
        CurrencyExchange(currencyName = "BBD", amount = 0.0, exchangeRate = 2.280333, exchangeCount = 0),
        CurrencyExchange(currencyName = "BDT", amount = 0.0, exchangeRate = 96.872638, exchangeCount = 0),
        CurrencyExchange(currencyName = "BGN", amount = 0.0, exchangeRate = 1.952581, exchangeCount = 0),
        CurrencyExchange(currencyName = "BHD", amount = 0.0, exchangeRate = 0.425669, exchangeCount = 0),
        CurrencyExchange(currencyName = "BIF", amount = 0.0, exchangeRate = 2253.093736, exchangeCount = 0),
        CurrencyExchange(currencyName = "BMD", amount = 0.0, exchangeRate = 1.129031, exchangeCount = 0),
        CurrencyExchange(currencyName = "BND", amount = 0.0, exchangeRate = 1.530499, exchangeCount = 0),
        CurrencyExchange(currencyName = "BOB", amount = 0.0, exchangeRate = 7.798334, exchangeCount = 0),
        CurrencyExchange(currencyName = "BRL", amount = 0.0, exchangeRate = 6.445617, exchangeCount = 0),
        CurrencyExchange(currencyName = "BSD", amount = 0.0, exchangeRate = 1.129391, exchangeCount = 0),
        CurrencyExchange(currencyName = "BTC", amount = 0.0, exchangeRate = 2.6156179e-5, exchangeCount = 0),
        CurrencyExchange(currencyName = "BTN", amount = 0.0, exchangeRate = 83.913403, exchangeCount = 0),
        CurrencyExchange(currencyName = "BWP", amount = 0.0, exchangeRate = 13.318607, exchangeCount = 0),
        CurrencyExchange(currencyName = "BYN", amount = 0.0, exchangeRate = 2.918863, exchangeCount = 0),
        CurrencyExchange(currencyName = "BYR", amount = 0.0, exchangeRate = 22129.014412, exchangeCount = 0),
        CurrencyExchange(currencyName = "BZD", amount = 0.0, exchangeRate = 2.276542, exchangeCount = 0),
        CurrencyExchange(currencyName = "CAD", amount = 0.0, exchangeRate = 1.445555, exchangeCount = 0),
        CurrencyExchange(currencyName = "CDF", amount = 0.0, exchangeRate = 2263.707335, exchangeCount = 0),
        CurrencyExchange(currencyName = "CHF", amount = 0.0, exchangeRate = 1.037789, exchangeCount = 0),
        CurrencyExchange(currencyName = "CLF", amount = 0.0, exchangeRate = 0.03431, exchangeCount = 0),
        CurrencyExchange(currencyName = "CLP", amount = 0.0, exchangeRate = 946.71533, exchangeCount = 0),
        CurrencyExchange(currencyName = "CNY", amount = 0.0, exchangeRate = 7.198254, exchangeCount = 0),
        CurrencyExchange(currencyName = "COP", amount = 0.0, exchangeRate = 4548.494719, exchangeCount = 0),
        CurrencyExchange(currencyName = "CRC", amount = 0.0, exchangeRate = 725.029021, exchangeCount = 0),
        CurrencyExchange(currencyName = "CUC", amount = 0.0, exchangeRate = 1.129031, exchangeCount = 0),
        CurrencyExchange(currencyName = "CUP", amount = 0.0, exchangeRate = 29.919331, exchangeCount = 0),
        CurrencyExchange(currencyName = "CVE", amount = 0.0, exchangeRate = 110.017623, exchangeCount = 0),
        CurrencyExchange(currencyName = "CZK", amount = 0.0, exchangeRate = 24.656241, exchangeCount = 0),
        CurrencyExchange(currencyName = "DJF", amount = 0.0, exchangeRate = 201.063247, exchangeCount = 0),
        CurrencyExchange(currencyName = "DKK", amount = 0.0, exchangeRate = 7.438803, exchangeCount = 0),
        CurrencyExchange(currencyName = "DOP", amount = 0.0, exchangeRate = 64.669308, exchangeCount = 0),
        CurrencyExchange(currencyName = "DZD", amount = 0.0, exchangeRate = 157.117122, exchangeCount = 0),
        CurrencyExchange(currencyName = "EGP", amount = 0.0, exchangeRate = 17.742178, exchangeCount = 0),
        CurrencyExchange(currencyName = "ERN", amount = 0.0, exchangeRate = 16.935558, exchangeCount = 0),
        CurrencyExchange(currencyName = "ETB", amount = 0.0, exchangeRate = 56.010148, exchangeCount = 0),
        CurrencyExchange(currencyName = "EUR", amount = 1000.0, exchangeRate = 1.0, exchangeCount = 0),
        CurrencyExchange(currencyName = "FJD", amount = 0.0, exchangeRate = 2.399167, exchangeCount = 0),
        CurrencyExchange(currencyName = "FKP", amount = 0.0, exchangeRate = 0.851718, exchangeCount = 0),
        CurrencyExchange(currencyName = "GBP", amount = 0.0, exchangeRate = 0.835342, exchangeCount = 0),
        CurrencyExchange(currencyName = "GEL", amount = 0.0, exchangeRate = 3.494348, exchangeCount = 0),
        CurrencyExchange(currencyName = "GGP", amount = 0.0, exchangeRate = 0.851718, exchangeCount = 0),
        CurrencyExchange(currencyName = "GHS", amount = 0.0, exchangeRate = 6.97385, exchangeCount = 0),
        CurrencyExchange(currencyName = "GIP", amount = 0.0, exchangeRate = 0.851718, exchangeCount = 0),
        CurrencyExchange(currencyName = "GMD", amount = 0.0, exchangeRate = 59.609858, exchangeCount = 0),
        CurrencyExchange(currencyName = "GNF", amount = 0.0, exchangeRate = 10416.455146, exchangeCount = 0),
        CurrencyExchange(currencyName = "GTQ", amount = 0.0, exchangeRate = 8.718696, exchangeCount = 0),
        CurrencyExchange(currencyName = "GYD", amount = 0.0, exchangeRate = 236.28081, exchangeCount = 0),
        CurrencyExchange(currencyName = "HKD", amount = 0.0, exchangeRate = 8.806501, exchangeCount = 0),
        CurrencyExchange(currencyName = "HNL", amount = 0.0, exchangeRate = 27.726393, exchangeCount = 0),
        CurrencyExchange(currencyName = "HRK", amount = 0.0, exchangeRate = 7.522063, exchangeCount = 0),
        CurrencyExchange(currencyName = "HTG", amount = 0.0, exchangeRate = 115.219987, exchangeCount = 0),
        CurrencyExchange(currencyName = "HUF", amount = 0.0, exchangeRate = 363.074072, exchangeCount = 0),
        CurrencyExchange(currencyName = "IDR", amount = 0.0, exchangeRate = 16256.0756, exchangeCount = 0),
        CurrencyExchange(currencyName = "ILS", amount = 0.0, exchangeRate = 3.521099, exchangeCount = 0),
        CurrencyExchange(currencyName = "IMP", amount = 0.0, exchangeRate = 0.851718, exchangeCount = 0),
        CurrencyExchange(currencyName = "INR", amount = 0.0, exchangeRate = 84.06711, exchangeCount = 0),
        CurrencyExchange(currencyName = "IQD", amount = 0.0, exchangeRate = 1648.197205, exchangeCount = 0),
        CurrencyExchange(currencyName = "IRR", amount = 0.0, exchangeRate = 47701.574046, exchangeCount = 0),
        CurrencyExchange(currencyName = "ISK", amount = 0.0, exchangeRate = 146.819264, exchangeCount = 0),
        CurrencyExchange(currencyName = "JEP", amount = 0.0, exchangeRate = 0.851718, exchangeCount = 0),
        CurrencyExchange(currencyName = "JMD", amount = 0.0, exchangeRate = 173.825768, exchangeCount = 0),
        CurrencyExchange(currencyName = "JOD", amount = 0.0, exchangeRate = 0.800527, exchangeCount = 0),
        CurrencyExchange(currencyName = "JPY", amount = 0.0, exchangeRate = 130.869977, exchangeCount = 0),
        CurrencyExchange(currencyName = "KES", amount = 0.0, exchangeRate = 127.80595, exchangeCount = 0),
        CurrencyExchange(currencyName = "KGS", amount = 0.0, exchangeRate = 95.749402, exchangeCount = 0),
        CurrencyExchange(currencyName = "KHR", amount = 0.0, exchangeRate = 4602.273972, exchangeCount = 0),
        CurrencyExchange(currencyName = "KMF", amount = 0.0, exchangeRate = 490.335222, exchangeCount = 0),
        CurrencyExchange(currencyName = "KPW", amount = 0.0, exchangeRate = 1016.128125, exchangeCount = 0),
        CurrencyExchange(currencyName = "KRW", amount = 0.0, exchangeRate = 1357.965315, exchangeCount = 0),
        CurrencyExchange(currencyName = "KWD", amount = 0.0, exchangeRate = 0.341826, exchangeCount = 0),
        CurrencyExchange(currencyName = "KYD", amount = 0.0, exchangeRate = 0.941205, exchangeCount = 0),
        CurrencyExchange(currencyName = "KZT", amount = 0.0, exchangeRate = 491.795077, exchangeCount = 0),
        CurrencyExchange(currencyName = "LAK", amount = 0.0, exchangeRate = 12682.938295, exchangeCount = 0),
        CurrencyExchange(currencyName = "LBP", amount = 0.0, exchangeRate = 1707.874932, exchangeCount = 0),
        CurrencyExchange(currencyName = "LKR", amount = 0.0, exchangeRate = 228.134954, exchangeCount = 0),
        CurrencyExchange(currencyName = "LRD", amount = 0.0, exchangeRate = 164.950073, exchangeCount = 0),
        CurrencyExchange(currencyName = "LSL", amount = 0.0, exchangeRate = 17.929034, exchangeCount = 0),
        CurrencyExchange(currencyName = "LTL", amount = 0.0, exchangeRate = 3.333736, exchangeCount = 0),
        CurrencyExchange(currencyName = "LVL", amount = 0.0, exchangeRate = 0.682939, exchangeCount = 0),
        CurrencyExchange(currencyName = "LYD", amount = 0.0, exchangeRate = 5.197651, exchangeCount = 0),
        CurrencyExchange(currencyName = "MAD", amount = 0.0, exchangeRate = 10.455783, exchangeCount = 0),
        CurrencyExchange(currencyName = "MDL", amount = 0.0, exchangeRate = 20.15945, exchangeCount = 0),
        CurrencyExchange(currencyName = "MGA", amount = 0.0, exchangeRate = 4487.678598, exchangeCount = 0),
        CurrencyExchange(currencyName = "MKD", amount = 0.0, exchangeRate = 61.477311, exchangeCount = 0),
        CurrencyExchange(currencyName = "MMK", amount = 0.0, exchangeRate = 2008.074357, exchangeCount = 0),
        CurrencyExchange(currencyName = "MNT", amount = 0.0, exchangeRate = 3227.205877, exchangeCount = 0),
        CurrencyExchange(currencyName = "MOP", amount = 0.0, exchangeRate = 9.066072, exchangeCount = 0),
        CurrencyExchange(currencyName = "MRO", amount = 0.0, exchangeRate = 403.063997, exchangeCount = 0),
        CurrencyExchange(currencyName = "MUR", amount = 0.0, exchangeRate = 49.508135, exchangeCount = 0),
        CurrencyExchange(currencyName = "MVR", amount = 0.0, exchangeRate = 17.443978, exchangeCount = 0),
        CurrencyExchange(currencyName = "MWK", amount = 0.0, exchangeRate = 922.043265, exchangeCount = 0),
        CurrencyExchange(currencyName = "MXN", amount = 0.0, exchangeRate = 23.403578, exchangeCount = 0),
        CurrencyExchange(currencyName = "MYR", amount = 0.0, exchangeRate = 4.756044, exchangeCount = 0),
        CurrencyExchange(currencyName = "MZN", amount = 0.0, exchangeRate = 72.066134, exchangeCount = 0),
        CurrencyExchange(currencyName = "NAD", amount = 0.0, exchangeRate = 17.934686, exchangeCount = 0),
        CurrencyExchange(currencyName = "NGN", amount = 0.0, exchangeRate = 466.062578, exchangeCount = 0),
        CurrencyExchange(currencyName = "NIO", amount = 0.0, exchangeRate = 39.986153, exchangeCount = 0),
        CurrencyExchange(currencyName = "NOK", amount = 0.0, exchangeRate = 10.054905, exchangeCount = 0),
        CurrencyExchange(currencyName = "NPR", amount = 0.0, exchangeRate = 134.270657, exchangeCount = 0),
        CurrencyExchange(currencyName = "NZD", amount = 0.0, exchangeRate = 1.675177, exchangeCount = 0),
        CurrencyExchange(currencyName = "OMR", amount = 0.0, exchangeRate = 0.43408, exchangeCount = 0),
        CurrencyExchange(currencyName = "PAB", amount = 0.0, exchangeRate = 1.129371, exchangeCount = 0),
        CurrencyExchange(currencyName = "PEN", amount = 0.0, exchangeRate = 4.47123, exchangeCount = 0),
        CurrencyExchange(currencyName = "PGK", amount = 0.0, exchangeRate = 4.016376, exchangeCount = 0),
        CurrencyExchange(currencyName = "PHP", amount = 0.0, exchangeRate = 57.778153, exchangeCount = 0),
        CurrencyExchange(currencyName = "PKR", amount = 0.0, exchangeRate = 199.649788, exchangeCount = 0),
        CurrencyExchange(currencyName = "PLN", amount = 0.0, exchangeRate = 4.581315, exchangeCount = 0),
        CurrencyExchange(currencyName = "PYG", amount = 0.0, exchangeRate = 7790.689469, exchangeCount = 0),
        CurrencyExchange(currencyName = "QAR", amount = 0.0, exchangeRate = 4.110825, exchangeCount = 0),
        CurrencyExchange(currencyName = "RON", amount = 0.0, exchangeRate = 4.946966, exchangeCount = 0),
        CurrencyExchange(currencyName = "RSD", amount = 0.0, exchangeRate = 117.605521, exchangeCount = 0),
        CurrencyExchange(currencyName = "RUB", amount = 0.0, exchangeRate = 86.49171, exchangeCount = 0),
        CurrencyExchange(currencyName = "RWF", amount = 0.0, exchangeRate = 1171.766392, exchangeCount = 0),
        CurrencyExchange(currencyName = "SAR", amount = 0.0, exchangeRate = 4.240189, exchangeCount = 0),
        CurrencyExchange(currencyName = "SBD", amount = 0.0, exchangeRate = 9.123559, exchangeCount = 0),
        CurrencyExchange(currencyName = "SCR", amount = 0.0, exchangeRate = 15.83372, exchangeCount = 0),
        CurrencyExchange(currencyName = "SDG", amount = 0.0, exchangeRate = 493.951724, exchangeCount = 0),
        CurrencyExchange(currencyName = "SEK", amount = 0.0, exchangeRate = 10.33977, exchangeCount = 0),
        CurrencyExchange(currencyName = "SGD", amount = 0.0, exchangeRate = 1.53581, exchangeCount = 0),
        CurrencyExchange(currencyName = "SHP", amount = 0.0, exchangeRate = 1.555125, exchangeCount = 0),
        CurrencyExchange(currencyName = "SLL", amount = 0.0, exchangeRate = 13694.978594, exchangeCount = 0),
        CurrencyExchange(currencyName = "SOS", amount = 0.0, exchangeRate = 644.838251, exchangeCount = 0),
        CurrencyExchange(currencyName = "SRD", amount = 0.0, exchangeRate = 43.722409, exchangeCount = 0),
        CurrencyExchange(currencyName = "STD", amount = 0.0, exchangeRate = 23381.935232, exchangeCount = 0),
        CurrencyExchange(currencyName = "SVC", amount = 0.0, exchangeRate = 9.883725, exchangeCount = 0),
        CurrencyExchange(currencyName = "SYP", amount = 0.0, exchangeRate = 2832.004325, exchangeCount = 0),
        CurrencyExchange(currencyName = "SZL", amount = 0.0, exchangeRate = 17.929034, exchangeCount = 0),
        CurrencyExchange(currencyName = "THB", amount = 0.0, exchangeRate = 38.082421, exchangeCount = 0),
        CurrencyExchange(currencyName = "TJS", amount = 0.0, exchangeRate = 12.815692, exchangeCount = 0),
        CurrencyExchange(currencyName = "TMT", amount = 0.0, exchangeRate = 3.951608, exchangeCount = 0),
        CurrencyExchange(currencyName = "TND", amount = 0.0, exchangeRate = 3.517283, exchangeCount = 0),
        CurrencyExchange(currencyName = "TOP", amount = 0.0, exchangeRate = 2.697688, exchangeCount = 0),
        CurrencyExchange(currencyName = "TRY", amount = 0.0, exchangeRate = 10.969617, exchangeCount = 0),
        CurrencyExchange(currencyName = "TTD", amount = 0.0, exchangeRate = 7.667869, exchangeCount = 0),
        CurrencyExchange(currencyName = "TWD", amount = 0.0, exchangeRate = 35.111356, exchangeCount = 0),
        CurrencyExchange(currencyName = "TZS", amount = 0.0, exchangeRate = 2827.261743, exchangeCount = 0),
        CurrencyExchange(currencyName = "UAH", amount = 0.0, exchangeRate = 31.018778, exchangeCount = 0),
        CurrencyExchange(currencyName = "UGX", amount = 0.0, exchangeRate = 3998.045715, exchangeCount = 0),
        CurrencyExchange(currencyName = "USD", amount = 0.0, exchangeRate = 1.129031, exchangeCount = 0),
        CurrencyExchange(currencyName = "UYU", amount = 0.0, exchangeRate = 50.399435, exchangeCount = 0),
        CurrencyExchange(currencyName = "UZS", amount = 0.0, exchangeRate = 12218.550541, exchangeCount = 0),
        CurrencyExchange(currencyName = "VEF", amount = 0.0, exchangeRate = 241421024074.42, exchangeCount = 0),
        CurrencyExchange(currencyName = "VND", amount = 0.0, exchangeRate = 25691.672829, exchangeCount = 0),
        CurrencyExchange(currencyName = "VUV", amount = 0.0, exchangeRate = 127.865795, exchangeCount = 0),
        CurrencyExchange(currencyName = "WST", amount = 0.0, exchangeRate = 2.935675, exchangeCount = 0),
        CurrencyExchange(currencyName = "XAF", amount = 0.0, exchangeRate = 654.502727, exchangeCount = 0),
        CurrencyExchange(currencyName = "XAG", amount = 0.0, exchangeRate = 0.050046, exchangeCount = 0),
        CurrencyExchange(currencyName = "XAU", amount = 0.0, exchangeRate = 0.000626, exchangeCount = 0),
        CurrencyExchange(currencyName = "XCD", amount = 0.0, exchangeRate = 3.051263, exchangeCount = 0),
        CurrencyExchange(currencyName = "XDR", amount = 0.0, exchangeRate = 0.808703, exchangeCount = 0),
        CurrencyExchange(currencyName = "XOF", amount = 0.0, exchangeRate = 654.502727, exchangeCount = 0),
        CurrencyExchange(currencyName = "XPF", amount = 0.0, exchangeRate = 119.169197, exchangeCount = 0),
        CurrencyExchange(currencyName = "YER", amount = 0.0, exchangeRate = 282.540438, exchangeCount = 0),
        CurrencyExchange(currencyName = "ZAR", amount = 0.0, exchangeRate = 18.00901, exchangeCount = 0),
        CurrencyExchange(currencyName = "ZMK", amount = 0.0, exchangeRate = 10162.625635, exchangeCount = 0),
        CurrencyExchange(currencyName = "ZMW", amount = 0.0, exchangeRate = 18.934429, exchangeCount = 0),
        CurrencyExchange(currencyName = "ZWL", amount = 0.0, exchangeRate = 363.547633, exchangeCount = 0)
      )

      defaultCurrencies.forEach { currency ->
        currencyDao.insert(currency)
      }
    }
  }
}
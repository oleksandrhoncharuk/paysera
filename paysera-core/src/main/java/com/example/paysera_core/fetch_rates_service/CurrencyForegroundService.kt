package com.example.paysera_core.fetch_rates_service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.paysera_core.repository.CurrencyExchangeRepository
import com.example.paysera_core.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyForegroundService: Service() {

  @Inject
  lateinit var repository: CurrencyExchangeRepository

  private val serviceJob = Job()
  private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

  override fun onCreate() {
    super.onCreate()

    // Start the service in the foreground immediately
    startForegroundServiceWithNotification()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // Start periodic network call using repository
    serviceScope.launch {
      while (true) {
//        repository.fetchCurrencyExchangeRates()
//        // Do something with the rates
//        delay(5000) // 5 seconds delay
      }
    }

    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    serviceJob.cancel()
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  // Start the foreground notification
  private fun startForegroundServiceWithNotification() {
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Currency Updates")
      .setContentText("Service running in the background")
      .setSmallIcon(R.drawable.notification_icon)
      .build()

    startForeground(NOTIFICATION_ID, notification)
  }

  companion object {
    const val CHANNEL_ID = "currency_service_channel"
    const val NOTIFICATION_ID = 1
  }
}
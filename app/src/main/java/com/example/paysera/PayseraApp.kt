package com.example.paysera

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.paysera_core.fetch_rates_service.CurrencyForegroundService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PayseraApp : Application() {
  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CurrencyForegroundService.CHANNEL_ID,
        "Currency Service",
        NotificationManager.IMPORTANCE_LOW
      )
      val manager = getSystemService(NotificationManager::class.java)
      manager?.createNotificationChannel(channel)
    }
  }
}
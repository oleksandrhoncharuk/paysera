package com.example.paysera_core.test_tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
fun <T> LiveData<T>.getOrAwaitValue(time: Long = 5, timeUnit: TimeUnit = TimeUnit.SECONDS): T {
  var data: T? = null
  val latch = CountDownLatch(1)

  val observer = object: Observer<T> {
    override fun onChanged(value: T) {
      data = value
      latch.countDown()
      this@getOrAwaitValue.removeObserver(this)
    }
  }

  this.observeForever(observer)

  try {
    if (!latch.await(time, timeUnit)) {
      throw TimeoutException("LiveData value was never set.")
    }
  } finally {
    this.removeObserver(observer)
  }

  @Suppress("UNCHECKED_CAST")
  return data as T
}

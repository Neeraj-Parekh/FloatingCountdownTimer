package xyz.tberghuis.floatingtimer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    setupCrashHandler()
  }

  private fun createNotificationChannel() {
    val notificationChannel = NotificationChannel(
      NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL_DISPLAY, NotificationManager.IMPORTANCE_DEFAULT
    )
    // channel description???
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(notificationChannel)
  }
  
  private fun setupCrashHandler() {
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      Log.e("FloatingTimer", "Uncaught exception in thread ${thread.name}", throwable)
      logd("CRASH: ${throwable.message}")
      throwable.printStackTrace()
      
      // Let the default handler continue (will crash the app)
      defaultHandler?.uncaughtException(thread, throwable)
    }
  }
}
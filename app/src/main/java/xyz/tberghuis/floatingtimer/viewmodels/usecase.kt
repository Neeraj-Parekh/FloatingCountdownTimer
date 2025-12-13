package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application

// Premium is disabled - app is now free
suspend fun shouldShowPremiumDialogMultipleTimers(application: Application): Boolean {
  return false
}
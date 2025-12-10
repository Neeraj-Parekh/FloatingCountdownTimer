package xyz.tberghuis.floatingtimer.data

import android.content.Context
import android.media.RingtoneManager
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import xyz.tberghuis.floatingtimer.BuildConfig
import xyz.tberghuis.floatingtimer.DEFAULT_HALO_COLOR
import xyz.tberghuis.floatingtimer.logd

val Context.dataStore by preferencesDataStore(
  name = "user_preferences",
)

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
  val vibrationFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("vibration")] ?: true
  }

  suspend fun updateVibration(vibration: Boolean) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("vibration")] = vibration
    }
  }

  val soundFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("sound")] ?: true
  }

  suspend fun updateSound(sound: Boolean) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("sound")] = sound
    }
  }

  val haloColourPurchasedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("halo_colour_purchased")] ?: BuildConfig.DEFAULT_PURCHASED
  }

  suspend fun updateHaloColourPurchased(purchased: Boolean) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("halo_colour_purchased")] = purchased
    }
  }

  val haloColourFlow: Flow<Color> = dataStore.data.map { preferences ->
    val haloColourString = preferences[stringPreferencesKey("halo_colour")]
    val haloColor = if (haloColourString == null)
      DEFAULT_HALO_COLOR
    else
      Color(haloColourString.toULong())
    logd("haloColourFlow halocolor $haloColor")
    haloColor
  }

  suspend fun updateHaloColour(color: Color) {
    logd("updateHaloColour")
    val haloColourString = color.value.toString()
    dataStore.edit { preferences ->
      preferences[stringPreferencesKey("halo_colour")] = haloColourString
    }
  }

  suspend fun resetHaloColour() {
    dataStore.edit { preferences ->
      preferences.remove(stringPreferencesKey("halo_colour"))
    }
  }

  val bubbleScaleFlow: Flow<Float> = dataStore.data.map { preferences ->
    preferences[floatPreferencesKey("bubble_scale")] ?: 0f
  }

  suspend fun updateBubbleScale(scale: Float) {
    dataStore.edit { preferences ->
      preferences[floatPreferencesKey("bubble_scale")] = scale
    }
  }

  suspend fun resetBubbleScale() {
    dataStore.edit { preferences ->
      preferences.remove(floatPreferencesKey("bubble_scale"))
    }
  }

  val alarmRingtoneUriFlow: Flow<String?> = dataStore.data.map { preferences ->
    var uri = preferences[stringPreferencesKey("alarm_ringtone_uri")]
    if (uri == null) {
      uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString()
    }
    uri
  }.distinctUntilChanged()

  suspend fun updateAlarmRingtoneUri(uri: String) {
    dataStore.edit { preferences ->
      preferences[stringPreferencesKey("alarm_ringtone_uri")] = uri
    }
  }

  val customSoundNameFlow: Flow<String?> = dataStore.data.map { preferences ->
    preferences[stringPreferencesKey("custom_sound_name")]
  }

  suspend fun updateCustomSoundName(name: String?) {
    dataStore.edit { preferences ->
      if (name == null) {
        preferences.remove(stringPreferencesKey("custom_sound_name"))
      } else {
        preferences[stringPreferencesKey("custom_sound_name")] = name
      }
    }
  }

  val loopingFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("looping")] ?: true
  }

  suspend fun updateLooping(looping: Boolean) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("looping")] = looping
    }
  }

  val autoStartFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("autoStart")] ?: false
  }

  suspend fun updateAutoStart(autoStart: Boolean) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("autoStart")] = autoStart
    }
  }

  companion object {
    @Volatile
    private var instance: PreferencesRepository? = null
    fun getInstance(context: Context) =
      instance ?: synchronized(this) {
        instance ?: PreferencesRepository(context.dataStore).also { instance = it }
      }
  }

  val flashEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("flash_enabled")] ?: false
  }

  suspend fun updateFlashEnabled(enabled: Boolean) {
    dataStore.edit { preferences ->
        preferences[booleanPreferencesKey("flash_enabled")] = enabled
    }
  }

  val flashColorFlow: Flow<Color> = dataStore.data.map { preferences ->
    val colorString = preferences[stringPreferencesKey("flash_color")]
    if (colorString == null) Color.Red else Color(colorString.toULong())
  }

  suspend fun updateFlashColor(color: Color) {
    dataStore.edit { preferences ->
      preferences[stringPreferencesKey("flash_color")] = color.value.toString()
    }
  }

  val visualStyleFlow: Flow<TimerVisualStyle> = dataStore.data.map { preferences ->
    val styleName = preferences[stringPreferencesKey("visual_style")]
    if (styleName != null) {
        try {
            TimerVisualStyle.valueOf(styleName)
        } catch (e: IllegalArgumentException) {
            TimerVisualStyle.DEFAULT
        }
    } else {
        TimerVisualStyle.DEFAULT
    }
  }

  suspend fun updateVisualStyle(style: TimerVisualStyle) {
    dataStore.edit { preferences ->
      preferences[stringPreferencesKey("visual_style")] = style.name
    }
  }

  val secondaryColorFlow: Flow<Color> = dataStore.data.map { preferences ->
    val colorString = preferences[stringPreferencesKey("secondary_color")]
    if (colorString == null) Color.Cyan else Color(colorString.toULong())
  }

  suspend fun updateSecondaryColor(color: Color) {
    dataStore.edit { preferences ->
      preferences[stringPreferencesKey("secondary_color")] = color.value.toString()
    }
  }

  val audioLoopingFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("audio_looping")] ?: true // Default to true
  }

  suspend fun updateAudioLooping(loop: Boolean) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("audio_looping")] = loop
    }
  }

  // AOD
  val aodEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("aod_enabled")] ?: false
  }

  suspend fun updateAodEnabled(enabled: Boolean) {
    dataStore.edit { preferences ->
        preferences[booleanPreferencesKey("aod_enabled")] = enabled
    }
  }

  // Theme: "system", "light", "dark"
  val themeModeFlow: Flow<String> = dataStore.data.map { preferences ->
    preferences[stringPreferencesKey("theme_mode")] ?: "system"
  }

  suspend fun updateThemeMode(mode: String) {
    dataStore.edit { preferences ->
        preferences[stringPreferencesKey("theme_mode")] = mode
    }
  }
  
  // Audio Masking
  val audioMaskingEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("audio_masking_enabled")] ?: false
  }

  suspend fun updateAudioMaskingEnabled(enabled: Boolean) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("audio_masking_enabled")] = enabled
    }
  }
}

val Context.preferencesRepository: PreferencesRepository
  get() = PreferencesRepository.getInstance(this)
package xyz.tberghuis.floatingtimer.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.logd

class SoundManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playSound(soundResId: Int, looping: Boolean = false) {
        stop() // Stop existing
        try {
            mediaPlayer = MediaPlayer.create(context, soundResId)
            setupMediaPlayer(looping)
        } catch (e: Exception) {
            logd("SoundManager error: ${e.message}")
        }
    }

    fun playFile(file: java.io.File, looping: Boolean = false) {
        stop()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                prepare()
            }
            setupMediaPlayer(looping)
        } catch (e: Exception) {
             logd("SoundManager file error: ${e.message}")
        }
    }
    
    private fun setupMediaPlayer(looping: Boolean) {
        mediaPlayer?.apply {
            isLooping = looping
            setOnCompletionListener { 
                if (!looping) release() 
            }
            start()
        }
    }
    
    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            logd("SoundManager stop error: ${e.message}")
        }
    }
}

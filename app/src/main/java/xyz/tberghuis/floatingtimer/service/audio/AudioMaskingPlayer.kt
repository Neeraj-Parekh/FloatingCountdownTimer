package xyz.tberghuis.floatingtimer.service.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import xyz.tberghuis.floatingtimer.logd
import kotlin.random.Random

import android.content.Context

class AudioMaskingPlayer(private val context: Context) {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private val sampleRate = 44100

    fun playWhiteNoise() {
        stop() // Ensure clean state
        isPlaying = true
        
        Thread {
            try {
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                
                audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(bufferSize)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    AudioTrack(
                        android.media.AudioManager.STREAM_MUSIC,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize,
                        AudioTrack.MODE_STREAM
                    )
                }

                audioTrack?.play()

                val buffer = ShortArray(bufferSize)
                val random = Random.Default

                while (isPlaying) {
                    for (i in buffer.indices) {
                        // Generate white noise: random values between -32768 and 32767
                        buffer[i] = (random.nextInt(65536) - 32768).toShort()
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                logd("AudioMaskingPlayer error: ${e.message}")
            } finally {
                release()
            }
        }.start()
    }

    fun stop() {
        isPlaying = false
        try {
            if (audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack?.stop()
            }
        } catch (e: Exception) {
            logd("Error stopping AudioMaskingPlayer: ${e.message}")
        }
    }
    
    private fun release() {
        try {
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            logd("Error releasing AudioMaskingPlayer: ${e.message}")
        }
    }
}

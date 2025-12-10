package xyz.tberghuis.floatingtimer.service.audio

import android.content.Context
import android.net.Uri
import java.io.File
import xyz.tberghuis.floatingtimer.logd

class CustomSoundManager(private val context: Context) {

    private val customSoundsDir = File(context.getExternalFilesDir(null), "custom_sounds")

    init {
        if (!customSoundsDir.exists()) {
            customSoundsDir.mkdirs()
        }
    }

    fun saveCustomSound(uri: Uri, fileName: String): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputFile = File(customSoundsDir, fileName)
            inputStream?.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            logd("Custom sound saved to ${outputFile.absolutePath}")
            outputFile
        } catch (e: Exception) {
            logd("Error saving custom sound: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun getCustomSounds(): List<File> {
        return customSoundsDir.listFiles()?.toList() ?: emptyList()
    }
    
    fun getSoundFile(name: String): File? {
        val file = File(customSoundsDir, name)
        return if (file.exists()) file else null
    }

    fun deleteCustomSound(name: String): Boolean {
         val file = File(customSoundsDir, name)
         return if (file.exists()) file.delete() else false
    }
}

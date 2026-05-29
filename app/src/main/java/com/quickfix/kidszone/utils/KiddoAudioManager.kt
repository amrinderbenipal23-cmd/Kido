package com.quickfix.kidszone.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KiddoAudioManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var backgroundPlayer: ExoPlayer? = null
    private var sfxPlayer: MediaPlayer? = null

    fun playBackgroundMusic(assetPath: String) {
        stopBackgroundMusic()
        backgroundPlayer = ExoPlayer.Builder(context).build().apply {
            val uri = android.net.Uri.parse("asset:///$assetPath")
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            prepare()
            play()
        }
    }

    fun stopBackgroundMusic() {
        backgroundPlayer?.release()
        backgroundPlayer = null
    }

    fun pauseBackgroundMusic() {
        backgroundPlayer?.pause()
    }

    fun resumeBackgroundMusic() {
        backgroundPlayer?.play()
    }

    fun setBackgroundMusicVolume(volume: Float) {
        backgroundPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    fun playSuccessSound() {
        playTone(1000f, 300)
    }

    fun playClickSound() {
        vibrate(30)
    }

    fun playWrongSound() {
        vibrate(200)
    }

    fun vibrate(durationMs: Long) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val manager = context.getSystemService(VibratorManager::class.java)
                manager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) { }
    }

    private fun playTone(frequency: Float, durationMs: Int) {
        // In production, replace with actual sound assets
        vibrate(50)
    }

    fun release() {
        backgroundPlayer?.release()
        backgroundPlayer = null
        sfxPlayer?.release()
        sfxPlayer = null
    }
}

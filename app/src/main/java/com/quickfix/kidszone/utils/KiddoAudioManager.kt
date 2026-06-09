package com.quickfix.kidszone.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.quickfix.kidszone.R
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central audio manager for sound effects and looping background music.
 *
 * - Short SFX (click / correct / wrong / reward) are played via [SoundPool].
 * - Background music is played via a looping [MediaPlayer].
 *
 * Playback respects the user's preferences: SFX obey "Sound Effects" and
 * music obeys "Background Music" from [SettingsDataStore]. The flags are
 * observed reactively so toggling a setting takes effect immediately.
 */
@Singleton
class KiddoAudioManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsDataStore: SettingsDataStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Volatile private var soundEnabled: Boolean = true
    @Volatile private var musicEnabled: Boolean = true

    private val soundPool: SoundPool
    private val soundIds = mutableMapOf<Int, Int>()

    private var musicPlayer: MediaPlayer? = null
    private var musicShouldPlay = false

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attrs)
            .build()
        loadSounds()
        observeSettings()
    }

    private fun loadSounds() {
        soundIds[R.raw.sfx_click] = soundPool.load(context, R.raw.sfx_click, 1)
        soundIds[R.raw.sfx_correct] = soundPool.load(context, R.raw.sfx_correct, 1)
        soundIds[R.raw.sfx_wrong] = soundPool.load(context, R.raw.sfx_wrong, 1)
        soundIds[R.raw.sfx_reward] = soundPool.load(context, R.raw.sfx_reward, 1)
    }

    private fun observeSettings() {
        settingsDataStore.soundEnabled
            .onEach { soundEnabled = it }
            .launchIn(scope)
        settingsDataStore.musicEnabled
            .onEach { enabled ->
                musicEnabled = enabled
                if (!enabled) {
                    musicPlayer?.let { if (it.isPlaying) it.pause() }
                } else if (musicShouldPlay) {
                    startBackgroundMusic()
                }
            }
            .launchIn(scope)
    }

    // ── Sound effects ──────────────────────────────────────────────────────

    private fun play(resId: Int, rate: Float = 1f, volume: Float = 1f) {
        if (!soundEnabled) return
        val id = soundIds[resId] ?: return
        soundPool.play(id, volume, volume, 1, 0, rate)
    }

    fun playClickSound() {
        play(R.raw.sfx_click, volume = 0.7f)
        vibrate(20)
    }

    fun playSuccessSound() = play(R.raw.sfx_correct)

    fun playWrongSound() {
        play(R.raw.sfx_wrong)
        vibrate(120)
    }

    fun playRewardSound() = play(R.raw.sfx_reward)

    // ── Background music ───────────────────────────────────────────────────

    fun startBackgroundMusic() {
        musicShouldPlay = true
        if (!musicEnabled) return
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(context, R.raw.bg_music)?.apply {
                isLooping = true
                setVolume(0.35f, 0.35f)
            }
        }
        musicPlayer?.let { if (!it.isPlaying) it.start() }
    }

    fun pauseBackgroundMusic() {
        musicPlayer?.let { if (it.isPlaying) it.pause() }
    }

    fun resumeBackgroundMusic() {
        if (musicShouldPlay && musicEnabled) {
            val player = musicPlayer
            if (player != null) {
                if (!player.isPlaying) player.start()
            } else {
                startBackgroundMusic()
            }
        }
    }

    fun stopBackgroundMusic() {
        musicShouldPlay = false
        musicPlayer?.release()
        musicPlayer = null
    }

    fun setBackgroundMusicVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        musicPlayer?.setVolume(v, v)
    }

    // ── Haptics ────────────────────────────────────────────────────────────

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

    fun release() {
        stopBackgroundMusic()
        soundPool.release()
    }
}

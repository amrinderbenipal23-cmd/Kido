package com.quickfix.kidszone

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.navigation.KiddoNavGraph
import com.quickfix.kidszone.ui.LocalAdsEnabled
import com.quickfix.kidszone.ui.LocalAppLanguage
import com.quickfix.kidszone.ui.theme.KidsZoneTheme
import com.quickfix.kidszone.utils.KiddoAudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var audioManager: KiddoAudioManager
    @Inject lateinit var settings: SettingsDataStore

    private var resumeAtMs: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            ),
        )
        setContent {
            KidsZoneTheme {
                val adsEnabled by settings.adsEnabled.collectAsState(initial = true)
                val language by settings.language.collectAsState(initial = "en")
                CompositionLocalProvider(
                    LocalAdsEnabled provides adsEnabled,
                    LocalAppLanguage provides language,
                ) {
                    Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                        val navController = rememberNavController()
                        KiddoNavGraph(navController = navController)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeAtMs = SystemClock.elapsedRealtime()
        audioManager.startBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        val elapsedSeconds = (SystemClock.elapsedRealtime() - resumeAtMs) / 1000L
        if (elapsedSeconds > 0L) {
            lifecycleScope.launch { settings.addScreenTimeSeconds(elapsedSeconds) }
        }
        audioManager.pauseBackgroundMusic()
    }
}

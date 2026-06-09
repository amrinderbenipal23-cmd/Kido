package com.quickfix.kidszone.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.quickfix.kidszone.ui.LocalAdsEnabled
import com.quickfix.kidszone.utils.AdManager

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    // Respect the parent "Show Ads" setting — render nothing when disabled.
    if (!LocalAdsEnabled.current) return
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdManager.getBannerAdUnitId()
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}

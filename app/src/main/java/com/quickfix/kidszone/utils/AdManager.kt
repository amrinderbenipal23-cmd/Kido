package com.quickfix.kidszone.utils

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// COPPA-compliant Ad Manager — treats all users as children (age <= 12)
object AdManager {

    // Test ad unit IDs — replace with real IDs before publishing
    private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private val _interstitialReady = MutableStateFlow(false)
    val interstitialReady: StateFlow<Boolean> = _interstitialReady

    private val _rewardedReady = MutableStateFlow(false)
    val rewardedReady: StateFlow<Boolean> = _rewardedReady

    fun initialize(context: Context) {
        // Configure for child safety (COPPA)
        val requestConfiguration = RequestConfiguration.Builder()
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(context)
        preloadInterstitial(context)
        preloadRewarded(context)
    }

    fun preloadInterstitial(context: Context) {
        val adRequest = buildChildSafeAdRequest()
        InterstitialAd.load(context, INTERSTITIAL_AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    _interstitialReady.value = true
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    _interstitialReady.value = false
                }
            })
    }

    fun preloadRewarded(context: Context) {
        val adRequest = buildChildSafeAdRequest()
        RewardedAd.load(context, REWARDED_AD_UNIT_ID, adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    _rewardedReady.value = true
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    _rewardedReady.value = false
                }
            })
    }

    fun showInterstitial(
        activity: android.app.Activity,
        onDismissed: () -> Unit,
    ) {
        val ad = interstitialAd ?: run { onDismissed(); return }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                _interstitialReady.value = false
                onDismissed()
                preloadInterstitial(activity)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                _interstitialReady.value = false
                onDismissed()
                preloadInterstitial(activity)
            }
        }
        ad.show(activity)
    }

    fun showRewarded(
        activity: android.app.Activity,
        onRewarded: (Int) -> Unit,
        onDismissed: () -> Unit,
    ) {
        val ad = rewardedAd ?: run { onDismissed(); return }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                _rewardedReady.value = false
                onDismissed()
                preloadRewarded(activity)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                _rewardedReady.value = false
                onDismissed()
                preloadRewarded(activity)
            }
        }
        ad.show(activity) { reward -> onRewarded(reward.amount) }
    }

    private fun buildChildSafeAdRequest(): AdRequest = AdRequest.Builder().build()

    fun getBannerAdUnitId(): String = BANNER_AD_UNIT_ID
}

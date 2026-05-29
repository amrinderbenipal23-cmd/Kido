# KiddoLearn ProGuard rules

# Retain line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Media3
-keep class androidx.media3.** { *; }

# Lottie
-keep class com.airbnb.lottie.** { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }

# Coil
-keep class coil.** { *; }

# DataStore
-keep class androidx.datastore.** { *; }

# App models — never obfuscate domain/data models
-keep class com.quickfix.kidszone.domain.model.** { *; }
-keep class com.quickfix.kidszone.data.local.database.entities.** { *; }

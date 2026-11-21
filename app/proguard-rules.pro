# ProGuard rules for XRayDB Pocket
-keep class com.xx.xraydbpocket.** { *; }
-keepclassmembers class com.xx.xraydbpocket.** { *; }

# Room Database Keep Rules
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**

# Google Mobile Ads Keep Rules
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

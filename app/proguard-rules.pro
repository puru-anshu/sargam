# Optimizations
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Glide rules
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Make Crashlytics reports more informative
-keepattributes SourceFile,LineNumberTable

# Don't break support libraries
-keep class android.support.v7.widget.SearchView { *; }
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**
-keep class android.support.v7.preference.PreferenceCategoryCompat { *; }
-keep class android.support.v7.preference.ColorPreference { *; }
-keep class android.support.v7.preference.IntListPreference { *; }
-keep class android.support.v7.preference.IntDropDownPreference { *; }

# Used to animate values of an Observable with an ObjectAnimator
# (since it calls setters by name using reflection)
-keepnames class * extends android.databinding.BaseObservable { *; }

# GSON rules
# Don't obfuscate instance field names for GSON
-keepnames class com.marverenic.music.model.** { *; }
# From https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

## Retrolambda specific rules ##
# as per official recommendation: https://github.com/evant/gradle-retrolambda#proguard
-dontwarn java.lang.invoke.*

## Retrofit + Retrolambda ##
-dontwarn retrofit2.Platform$Java8

## OkHttp ##
-dontwarn okio.**

## RxJava ##
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# ActiveAndroid
-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class * extends com.activeandroid.Model
-keep class * extends com.activeandroid.serializer.TypeSerializer

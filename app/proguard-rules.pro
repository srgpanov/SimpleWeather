-dontoptimize
-dontobfuscate

# Prints some helpful hints, always add this option
-verbose

-keepattributes SourceFile,LineNumberTable,Exceptions,InnerClasses,Signature,Deprecated,*Annotation*,EnclosingMethod


# add all known-to-be-safely-shrinkable classes to the beginning of line below
-keep class !com.android.support.**,!com.google.android.**,** { *; }


# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**


# Retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
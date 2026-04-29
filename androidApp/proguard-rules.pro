# Kotlin Serialization — keep generated serializer classes and reflection metadata
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class **$$serializer { *; }

# Ktor — uses internal coroutine machinery R8 can aggressively strip
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }

# OkHttp / Okio — suppress warnings from internal APIs
-dontwarn okhttp3.**
-dontwarn okio.**

# MLKit Text Recognition — accessed reflectively at runtime
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Kermit logging — suppress warnings from expect/actual internals
-dontwarn co.touchlab.kermit.**

# BuildKonfig — generated config object must survive shrinking
-keep class io.capistudio.deckamushi.BuildKonfig { *; }
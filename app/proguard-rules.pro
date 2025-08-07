# uz.alien.dictup.model ichidagi barcha klasslar saqlansin
-keep class uz.alien.dictup.data.remote.retrofit.dto.** { *; }

# Keep Room entities
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.Entity
-keep class androidx.room.Entity

# Keep entities and DAOs
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep annotations
-keep @androidx.room.* class * { *; }

# Keep model classes
-keep class uz.alien.dictup.data.local.room.entity.** { *; }

# keep kotlin data classes
-keepclassmembers class * {
    <init>(...);
}

# GSON TypeToken uchun generik tiplarda ishlashi uchun kerak:
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.JsonSerializer

# Hilt uchun (agar ishlatilayotgan bo‘lsa)
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
-dontwarn dagger.hilt.**

# ViewModel lar
-keep class * extends androidx.lifecycle.ViewModel { *; }

# WorkManager (Agar Background task ishlatilsa)
-keep class * extends androidx.work.Worker { *; }
-keep class androidx.work.* { *; }

# Retrofit (ixtiyoriy, lekin foydali)
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

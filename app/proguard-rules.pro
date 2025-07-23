# uz.alien.dictup.model ichidagi barcha klasslar saqlansin
-keep class uz.alien.dictup.archive.model.** { *; }

# GSON TypeToken uchun generik tiplarda ishlashi uchun kerak:
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.JsonSerializer

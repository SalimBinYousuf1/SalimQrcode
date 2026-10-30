-keepattributes *Annotation*
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public void clearAllTables();
}
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**
-keep class com.google.mlkit.vision.barcode.** { *; }
-dontwarn com.google.mlkit.vision.barcode.**

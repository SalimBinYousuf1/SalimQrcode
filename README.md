# Salim QR

Salim is a native, offline-first Android application for scanning and generating QR codes and barcodes.

## Key Features
- **100% Offline & Private:** Zero `INTERNET` permission. All processing, ML inference, decoding, generation, and storage remain strictly on-device.
- **Fast Multiformat Scanning:** Powered by CameraX with bundled ML Kit Barcode Scanning and ZXing fallback decoders.
- **Comprehensive Code Creator:** Generates codes for Websites, Plain Text, Email, SMS, Wi-Fi, Phone, vCard Contacts, Geolocation, Calendar Events, UPI Payments, and 1D Barcodes.
- **Scannability Self-Check:** Built-in verification engine ensures generated codes have sufficient contrast and readability before sharing or printing.
- **Glassmorphic Design:** Material 3 design system with custom Poppins typography, soft gradients, and floating tab navigation.
- **History & Management:** SQLite Room persistence with instant search, filtering, and swipe-to-delete with undo.

## Permissions
- `android.permission.CAMERA`: Required strictly for live camera viewfinder scanning.
- `android.permission.VIBRATE`: Provides haptic feedback upon successful scan.
- `android.permission.USE_BIOMETRIC`: Optional app lock protection.
- `android.permission.WRITE_EXTERNAL_STORAGE` (maxSdkVersion=28): Legacy support for saving generated images to device gallery. On modern Android versions (API 29+), MediaStore is used without requiring storage permissions.

## Build Instructions
```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

# Technical and Design Decisions

1. **Typeface**: Bundled Google Font 'Poppins' (.ttf) locally in `app/src/main/res/font/poppins.ttf` using `font-util` CLI, ensuring zero network requirement at runtime.
2. **Offline Architecture**: Pure offline application with zero `android.permission.INTERNET`. Scans, generation, decoding, safety analysis, and export occur locally on-device.
3. **Decoders**: Hybrid scanner utilizing ML Kit Barcode Scanning bundled model with a built-in ZXing MultiFormatReader fallback for 100% offline coverage across all Android devices.
4. **Encoding Engine**: ZXing Core 3.5.3 handles QR Code generation as well as Code 128, EAN-13, UPC-A, Aztec, PDF-417, etc., with scannability self-check and custom styling (rounded dots, custom eyes, gradients).
5. **Storage**: Local persistence using Room SQLite database (`CodeRecord` table) and Jetpack DataStore Preferences for user settings.
6. **Design Tokens**: Standardized in `DesignSpec.kt` based on the 390x844 reference frame and UI design boards.

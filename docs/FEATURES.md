# Features Inventory

| Section | Feature / Element | Implementation File | Status |
|---|---|---|---|
| 3.1 | Decorative QR Watermark | `com.salim.qr.ui.components.SalimWatermark.kt` | Pass |
| 3.1 | Glass Back Button (44dp, radius 14dp) | `com.salim.qr.ui.components.GlassBackButton.kt` | Pass |
| 3.1 | Floating Glass Bottom Tab Bar (64dp, radius 24dp) | `com.salim.qr.ui.components.SalimBottomBar.kt` | Pass |
| 3.1 | Primary Gradient Button (56dp, radius 28dp) | `com.salim.qr.ui.components.SalimButton.kt` | Pass |
| 3.2 | Squircle App Icon & Splash Branding | `res/drawable/ic_salim_logo.png`, `ic_launcher.xml` | Pass |
| 3.3 | Onboarding Pager (3 pages, real copy, 3D art, dock button) | `com.salim.qr.ui.screens.onboarding.OnboardingScreen.kt` | Pass |
| 3.4 | Scan Screen (Wordmark, Camera flip 52dp, Scan frame 232dp, animated scan line) | `com.salim.qr.ui.screens.scan.ScanScreen.kt` | Pass |
| 3.4 | Scan Extra Controls (Torch, Gallery decoder, Zoom slider) | `com.salim.qr.ui.screens.scan.ScanScreen.kt` | Pass |
| 3.5 | Create QR Destination List (11 glossy types, 76dp cards) | `com.salim.qr.ui.screens.create.CreateDestinationScreen.kt` | Pass |
| 3.6 | Create Forms (Type-specific fields, live validation, auto-prefix) | `com.salim.qr.ui.screens.create.CreateFormScreen.kt` | Pass |
| 3.7 | Result Modal (340dp glass card, real QR, Share, Save, Style) | `com.salim.qr.ui.screens.create.ResultModal.kt` | Pass |
| 3.8 | Recent History (69dp glass cards, date/time, search, swipe-delete red trash, undo) | `com.salim.qr.ui.screens.recent.RecentScreen.kt` | Pass |
| 3.9 | Scan Result Bottom Sheet (Parsed fields, safety warning banner, direct intents) | `com.salim.qr.ui.screens.scan.ScanResultBottomSheet.kt` | Pass |
| 3.10 | Settings Screen (Grouped glass cards, DataStore toggles, offline privacy) | `com.salim.qr.ui.screens.settings.SettingsScreen.kt` | Pass |
| 5.0 | Link Safety Engine (Local HTTPS, IP, shortener, blocked schemes) | `com.salim.qr.domain.safety.LinkSafetyChecker.kt` | Pass |
| 5.0 | Encoding & Scannability Checker (ZXing generation & self-check) | `com.salim.qr.domain.encoding.BarcodeEncoder.kt` | Pass |
| 9.0 | Room SQLite Persistence (`CodeRecord`, DAO, DB) | `com.salim.qr.data.db.SalimDatabase.kt` | Pass |

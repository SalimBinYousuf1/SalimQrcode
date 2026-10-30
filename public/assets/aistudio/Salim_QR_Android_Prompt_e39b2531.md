# Salim QR: Native Android QR and Barcode Scanner + Creator, Complete Build Prompt

Attach the three design images together with this prompt. Build a complete, shippable native Android app named **Salim** (wordmark "Salim" + "QR"), application ID and namespace **`com.salim.qr`**, whose UI matches the design exactly. It scans and creates QR codes and barcodes, keeps a history, and has a full Settings screen. It is a real, offline, production-ready product, not a prototype.

**How to read this prompt.** Assume you cannot see the images. Sections 3 and 4 are a complete written inventory of every screen and are your pixel specification. If you can see the images, treat them as authoritative and report every place where they disagree with the text. Values marked **(verify)** are a best reading of low-resolution design boards and are accurate to about ±4dp. If the owner provides `docs/DESIGN_SPEC_OVERRIDES.md` (exact numbers exported from the design file), those numbers override everything written here. Anything this prompt leaves open, decide in the most complete real-world way and record in `DECISIONS.md`.

---

## 0. Non-negotiable rules

1. Deliver a complete Gradle project that builds with `./gradlew assembleRelease` and `./gradlew bundleRelease` and runs on physical devices.
2. **Identity:** the product name everywhere is **Salim** (launcher label, splash, wordmark, About, notifications, strings, README, store text). The application ID, namespace, and every source package is **`com.salim.qr`** (`com.salim.qr.ui`, `com.salim.qr.data`, and so on), including test and androidTest packages. The words **`com.example`**, **`example`**, and the design's sample brand **`MrQR`** must not appear anywhere in the repository, resources, manifest, ProGuard files, generated files, or documentation (the only allowed exception is a real RFC 2606 domain used inside a unit test fixture in the test source set).
3. **No mock, demo, prototype, placeholder, or sample content, anywhere.** Forbidden in source and resources: lorem ipsum, `TODO`, `FIXME`, stub functions, empty click handlers, "coming soon", commented-out code, fake history rows, pre-filled codes, hard-coded URLs, names, or dates. The sample content in the design (Instagram URL, "drownbyuynn", the dates, the Lorem ipsum paragraphs, the example QR images) is design reference only and never ships. A fresh install has an empty history with designed empty states. All onboarding copy is real, written for this app, and stored in `strings.xml`.
4. Everything is real and functional: scanning from the live camera and from images, creating every supported code type, exporting and sharing, history, search, swipe to delete, settings that change behavior, app lock, backup, shortcuts, tile, widget, and accessibility.
5. 100% native: Kotlin and Jetpack Compose. No WebView, Flutter, React Native, or legacy XML screens.
6. **Offline and private:** no `INTERNET` permission, no account, no analytics, no ads, no crash-reporting SDKs, no trackers. Scanned content never leaves the device unless the user taps an action that hands it to another app (open link, share, send). Link safety checks run locally.
7. All user-visible text lives in `strings.xml`, all colors, dimensions, and type styles live in the theme and design tokens, and every number in section 3 lives in one `DesignSpec.kt`.
8. **Layout safety is a hard requirement (section 6):** on any device size, nothing overlaps, jumbles, overcrowds, clips, or goes outside the margins or safe areas, except the designed decorative watermark in the design.
9. **Pixel-perfect is the requirement:** the layout, sizes, type, colors, radii, glass, and shadows match sections 3 and 4, proven by the protocol in section 12 and the audit-fix loop in section 13, not by eye.
10. Do not stop, summarize, or ask questions until every section is fully implemented. Never leave a stub. Run the audit-fix loop until it is clean.

---

## 1. Project setup

- Application ID and namespace `com.salim.qr`, app name **Salim**, version `1.0.0`, version code `1`.
- **Min SDK 24** (so older and low-end phones worldwide are covered), target and compile SDK latest stable. Gradle Kotlin DSL, version catalog, KSP, latest stable dependency versions pinned.
- Single-activity, Jetpack Compose, Navigation Compose, edge-to-edge, predictive back.
- MVVM, unidirectional data flow, packages `data`, `domain`, `ui`, `core`. Hilt, Coroutines and Flow, `StateFlow` UI state. No business logic in composables.
- Libraries: **CameraX** (preview, analysis, torch, zoom, focus), **ML Kit Barcode Scanning bundled model** (offline, does not depend on Google Play services) with a ZXing-based fallback decoder for devices where it fails, **ZXing core** for encoding and for self-verification of generated codes, Room (history, with FTS), DataStore, WorkManager (retention cleanup), Glance (widget), Biometric. Verify each library's current API in its docs.
- Release build with R8 and resource shrinking, ProGuard rules for the libraries used. Release signing from a git-ignored `keystore.properties`. No secrets in the repo.
- Adaptive launcher icon (section 3.2) with themed monochrome layer, SplashScreen API. `allowBackup="false"` with explicit `dataExtractionRules`. RTL layouts mirror. README (build, signing, permissions explained) and a `LICENSES` file. All bundled native libraries compatible with 16 KB memory pages.
- Permissions, each requested only at the moment its feature is used, with a friendly in-app rationale, and every feature degrades gracefully when denied: `CAMERA` (scanning), `VIBRATE`, `USE_BIOMETRIC`, `POST_NOTIFICATIONS` (only if a feature needs it), and `WRITE_EXTERNAL_STORAGE` with `android:maxSdkVersion="28"` only for saving images on old Android. Saving uses `MediaStore` on Android 10 and above with no permission. Picking images uses the Photo Picker or the Storage Access Framework, contacts use the system picker intents, never broad storage or contacts permissions.

---

## 2. Brand and design system

- **Typeface:** Poppins (OFL, bundle with its license) for everything: titles SemiBold or Bold, labels Medium, body Regular. Confirm against the description in section 3 and record the choice in `DECISIONS.md`. Support font scaling up to 200% without clipping (section 6).
- **Wordmark:** "Salim" in navy `#1E2347` semibold followed by "QR" in indigo `#5B6CF0` bold, about 14sp, at the top left of the main screens.
- **Color tokens (verify):** indigo `#5B6CF0` (titles, active tab, links), indigo gradient for primary buttons from `#6B73F0` to `#8B7CF6`, navy `#1E2347` (headings), body gray `#6F7391`, light gray-indigo `#B9BDE0` (inactive tab icons and labels), danger red `#FF6B6B` (delete), glass white at 60 to 70% with a 1dp white border at about 70% and an indigo shadow at about 10%. Type tile tints: Website blue `#6FA8FF` to `#4F7CF0`, Text orange `#FFB070` to `#F58A4B`, Email cyan `#6ED3E8` to `#38B7D8`, SMS pink `#FF8FB8` to `#F2609A`, Wi-Fi violet `#C58BFF` to `#9B5CF0`.
- **Background:** a soft full-bleed gradient under the system bars: light pink-lavender (`#F3DDF0`) glow at the top left blending into pale lavender (`#E9E6F7`) in the middle and a lavender-blue (`#D6D8F5`) glow at the bottom right. Static.
- **Glass:** cards and the tab bar are translucent white, with real background blur (about 20dp) on API 31 and above and a tuned translucent fallback below, soft borders, soft shadows. Text and icons are drawn above the glass, never blurred, and always meet WCAG AA contrast.
- **Dark theme:** the same layout with a deep indigo-navy gradient (`#14172E` to `#1E2147`), dark glass cards, the same indigo accents and gradient buttons, AA contrast. Follows the system by default with a manual override. Build and verify the light theme against the design first.
- **Motion:** spring physics, 250 to 350ms transitions, the scan line sweeps smoothly, the result modal scales and fades in, list items animate on add and delete, haptics on a successful scan and on delete. Respect reduce-motion. Hold 60 fps.
- **Icons:** thin rounded outline icons drawn as `ImageVector`, original artwork in the style of the design. Illustrations (the 3D character and the folder stack in onboarding) are original artwork made for the app in the same soft 3D style (layered gradients, rounded forms, soft shadows), or an owner-supplied asset if one is provided, and are never traced from the images.

---

## 3. Exact visual specification (written inventory of the design images)

Reference frame: a 390 × 844 dp phone, edge to edge. Never draw the fake phone frames or the fake 9:41 status bar. Use the real system bars with edge-to-edge insets. The big titles above the boards ("Scan and Create QR Code", "Welcome to MrQR Code", "Review and Manage your QR Code") are presentation headings and are not app screens.

### 3.1 Global elements

- **Decorative QR watermark:** a large pale-lavender QR pattern (`#C5C8F0` at about 60%, about 163 × 166dp) in the top-right corner of the Create flow screens and the Recent screen, partly bleeding off the top and right edges, drawn behind all content. Text over it must keep AA contrast. This is the only element allowed to extend past the screen edges.
- **Glass back button:** a 44dp rounded square (radius 14dp) glass button with an indigo left chevron, 20dp from the left edge and just below the status bar.
- **Bottom tab bar (floating glass):** four equal items, **Create QR**, **Scan QR**, **Recent**, **Settings**. A rounded glass bar about 64dp tall, radius 24dp, 8dp from the left and right edges, about 22dp above the bottom edge (plus the navigation inset), soft shadow. Each item stacks a 22dp outline icon (pencil-in-square, scan frame, clock, gear) over an 11sp label. The active item is indigo with a bold label, inactive items are `#B9BDE0`. Items are at least 48dp wide and tall touch targets.
- **Primary button:** full-width pill, indigo gradient, white 16sp medium text, height 56dp, radius 28dp, soft glow shadow.
- **Page margins:** 20dp left and right for lists and forms, 24dp for onboarding text.

### 3.2 App icon and splash

A squircle with an indigo-to-violet gradient (`#6B6BEE` to `#8E7CF6`), subtle inner glow, white scan-corner brackets with a faint QR pattern, and the word **Salim** in white bold, with a soft indigo glow shadow beneath it. The adaptive icon puts the brackets and the word in the foreground, with a monochrome layer. The splash shows the icon centered on the app gradient, and on the very first launch only it also shows "Welcome to" (navy bold) above "Salim QR Code" (indigo bold) for about 1.2 seconds.

### 3.3 Onboarding (three pages, first launch only, with Skip)

All three pages use the app gradient. "Skip" is a 12sp text button at the top right (24dp from the right, about 75dp from the top). A horizontal pager with swipe and the bottom dock button. Real copy is written in `strings.xml` (short, friendly, one sentence each, never Lorem ipsum).

1. **Page 1:** title "Create your / own QR Code" (two lines, 30sp bold navy, left aligned at 24dp, first line about 152dp from the top), a three-line gray body under it (13sp, `#6F7391`), and a soft 3D illustration of a smiling person with glasses and a beanie in the lower right, partly cropped by the right edge, standing above the dock. Dock label: **Explore more**.
2. **Page 2:** a large pale-blue QR graphic in the top left, partly cropped by the left and top edges, crossed by a purple-indigo translucent scan band; below the graphic, lower on the screen, the title "Scan / QR Code" (two lines) and the gray body; dock label **Next**.
3. **Page 3:** title "Review / and Manage" (two lines) and body at the top, with a soft 3D stack of folders (teal, indigo, and brown tabs) in the lower right; dock label **Get Started**.
4. **Bottom dock button** (all pages): an indigo-gradient rounded bar, about 72dp tall (verify), 16dp from the sides and bottom, radius 28dp. At the left, a three-part progress indicator: the current page is a white elongated dash (about 18 × 4dp) and the other pages are small 4dp dots at about 60% white. At the right, the label in white 12sp medium followed by a long thin arrow (about 36dp). Tapping advances, and on the last page it finishes onboarding and shows the camera permission explanation, then opens the Scan screen.

### 3.4 Scan QR screen (the default tab)

1. Wordmark at the top left (24dp from the left, about 89dp from the top of the full screen, or aligned under the status bar).
2. **Camera flip button** at the top right: a 52dp rounded-square glass button (radius 16dp, 20dp from the right) with a camera icon, switches between the back and front cameras.
3. **Title** "Scan QR Code" (30sp bold indigo) at 24dp from the left, vertical center about 163dp from the top, and the gray subtitle "Position the QR code within the frame." (13sp, `#8A8FA8`) about 38dp below its center.
4. **Scan frame**, centered horizontally, about 232 × 232dp, top about 309dp from the top: four indigo corner brackets (each about 29dp long, 4dp stroke, rounded caps, 8dp inner radius). The **live camera preview fills the frame area** (rounded 20dp corners), and a horizontal **scan line** (an indigo-to-violet translucent gradient band about 10dp thick with a soft glow, the frame's full width) sweeps up and down continuously. Pinch to zoom and tap to focus work inside the frame. Analysis uses exactly the region the user sees, so what is inside the frame is what is scanned. A setting offers a **Large viewfinder** (the frame grows to the available width) and a **Full-screen camera** style as alternatives, both keeping the title, subtitle, and tab bar readable.
5. **Extra controls** (not in the design, same style, placed in the free space between the frame and the tab bar so the design's layout is unchanged): a centered row of three round 48dp glass buttons, **Torch**, **Gallery** (scan a code from an image), and **Zoom** (a slider appears), with labels under them.
6. Tab bar with **Scan QR** active.
7. **States:** camera permission not granted (a friendly message inside the frame area with an "Allow camera" button and the Gallery button still working), camera unavailable, torch unavailable (button hidden), low light hint ("Try the torch"), multiple codes in view (a numbered chooser), and a continuous-scan mode with a running list.

### 3.5 Create QR: "Choose your destination"

1. Wordmark at the top left, the QR watermark at the top right.
2. **Headline** on two lines: "Choose" in indigo and "your Destination!" in navy, 30sp bold, left aligned at 24dp, first line center about 170dp from the top, line pitch about 42dp.
3. **Destination list:** glass cards 20dp from each edge (about 348dp wide at 390dp), about 76dp tall, radius 24dp, 16dp apart, starting about 259dp from the top, each with: a 52dp glossy rounded type tile at the left (12dp inside, a vertical gradient in the type's tint, a thin white inner border, a soft glossy highlight arc at the top, and a white glyph), the type name in 16sp medium navy 14dp to the right of the tile, and a thin indigo-gray chevron at the right (24dp from the card edge). In this order: **Website** (blue, window glyph), **Text** (orange, document glyph), **Email** (cyan, envelope glyph), **SMS** (pink, chat bubble glyph), **Wifi** (violet, wifi glyph). Below these five, in the same style, scrolling with the list: **Phone**, **Contact**, **Location**, **Event**, **Payment**, **Barcode**. The list scrolls and keeps bottom padding so nothing hides behind the tab bar.
4. Tab bar with **Create QR** active.

### 3.6 Create form (the "Website" example, same pattern for every type)

1. Glass back button, QR watermark top right.
2. **Headline** "Make my code" (navy) and "scan to **Website**" (the type name in indigo), 28 to 30sp bold, and a gray 11 to 12sp subtitle "Your QR Code will be generated automatically."
3. **Input card:** a white rounded card (radius 16dp) with a bold 13sp label ("Enter your website url" style, localized per field), and a large input area below it (multi-line, at least 140dp tall for text and website types) whose text is shown in link blue/indigo for URLs. The form scrolls above the keyboard (IME insets), and the focused field is never hidden.
4. **Primary button** "Generate QR Code" pinned above the keyboard or the bottom inset, enabled only when the input is valid. (It is not visible in the design only because the keyboard covers it.)
5. Field sets per type (all real, all validated in plain words): **Website** (URL, auto-adds https, paste button), **Text** (up to the QR capacity with a live capacity meter), **Email** (to, subject, body, builds `mailto:`), **SMS** (phone number with country-aware formatting, message, builds `SMSTO:`/`smsto:`), **Wifi** (network name, password with show/hide, security WPA/WPA2/WPA3, WEP, or none, hidden network switch, builds `WIFI:T:...;S:...;P:...;H:...;;` with correct escaping of `\ ; , : "`), **Phone** (`tel:`), **Contact** (vCard 3.0 fields, or filled from the system contact picker), **Location** (latitude and longitude typed, or "Use my location" with an optional location permission, builds `geo:`), **Event** (title, start, end, place, description, builds a `VEVENT`), **Payment** (a UPI payment address, name, amount, note, builds `upi://pay?...`, and an EPC/SEPA mode), **Barcode** (EAN-13 with check-digit computation, UPC-A, Code 128, Code 39, ITF, PDF417, Aztec, Data Matrix, each validated to its rules).
6. Drafts survive process death and the keyboard never covers the button.

### 3.7 Result modal: "Here's your code!"

1. The form screen dims and blurs behind a centered **modal card** (about 340dp wide, radius 28dp, a white-to-lavender gradient glass look, soft shadow), with a glass back button remaining at the top left.
2. Top: a glossy rounded-square tile (about 200dp, scaling with the screen up to 280dp, with scan-corner brackets in the corners) **that contains the real generated QR code** (or barcode). The design shows an icon in this slot only because it is a mock-up.
3. Heading "Here's your code!" (22sp bold indigo, the real copy fixes the design's grammar), a 13sp gray line below ("This is your unique QR code for another person to scan."), and two circular 48dp glass action buttons side by side with 11sp labels: **Share** and **Save**. Additional real actions in the same style under or beside them: **Copy image**, **Print**, **Customize** (opens the style editor), **Favorite**.
4. The code is saved to History automatically (a setting can turn this off).

### 3.8 Recent

1. Glass back button when opened from a type, QR watermark top right.
2. **Title** on two lines in the design's two-tone style: for a filtered list "Recent **Website**" (type in indigo) and "QR code" (navy), 28 to 30sp bold. For the unfiltered tab: "Recent" in indigo and "QR codes" in navy.
3. A segmented control (**All**, **Scanned**, **Created**) and horizontally scrolling type chips in the glass style, a search field, and a sort menu.
4. **List rows:** glass cards 16dp from the edges, about 69dp tall, radius 16dp, 11dp apart: a 28dp type icon tile at the left, the content in 13sp bold navy on one line with a middle ellipsis for long text, and under it the date and time in 10sp gray-indigo (for example "20.11.2022 12:03 PM", formatted by the user's locale and clock setting; the image is the reference for that style). The list scrolls under the tab bar with bottom padding.
5. **Swipe left to reveal a red delete button** (a 44dp rounded square, `#FF6B6B`, white trash icon, 16dp from the right), as in the design. Swiping fully deletes with an Undo snackbar. Tap opens the detail, long-press starts multi-select (delete, favorite, share, export). Empty and no-result states are designed.

### 3.9 Scan result and detail sheet (same visual language)

A glass bottom sheet (32dp top radius) shows: a type tile and the type name, the full content (selectable), parsed fields for structured types, and a primary gradient button with the type's main action. Secondary round actions: Copy, Share, Favorite, Save to history, Create a similar code. Per type: **Website** (open in the browser, with the safety banner in section 5), **Text** (copy, search the web, share), **Email** (open the mail composer pre-filled), **SMS** (open the messaging app pre-filled), **Phone** (dial), **Wi-Fi** (network name, security, password with reveal and copy, **Connect** using the platform API for the device version), **Contact** (add to contacts), **Event** (add to calendar), **Location** (open in a maps app), **Payment** (parsed fields, open in the payment app, with a caution banner), **Product barcode** (number, format, search by intent), **Other** (raw text with a hex view).

### 3.10 Settings (described in the same style)

Title "Settings" (indigo bold 30sp). Grouped glass cards with rows (a type-colored tile, a title, a subtitle, and a switch, a value, or a chevron). The groups and rows are in section 7.

---

## 4. Navigation and behavior

- Start destination is **Scan QR** (a setting can change it). The four tabs keep their own back stacks and states. Back from a tab returns to Scan, then exits. The Create flow is Create QR, a type form, then the result modal.
- Deep links and intents: a share target that accepts images and text (scan an image shared from another app, or turn shared text or a link into a QR), app shortcuts (Scan, Create, Recent), a Quick Settings tile that opens the scanner, and a Glance widget that opens the scanner.
- Opening a `WIFI:` or other code from outside is not hijacked, the app only handles its own share target.

---

## 5. Scanning, creating, and safety (all real)

**Scanning**
- All common formats: QR, Micro QR where the decoder supports it, Data Matrix, Aztec, PDF417, EAN-8, EAN-13, UPC-A, UPC-E, Code 128, Code 39, Code 93, ITF, Codabar. Formats can be enabled or disabled in Settings.
- Fast and reliable on low-end phones: adaptive resolution, autofocus and tap to focus, continuous focus, exposure handling, a torch toggle, zoom, inverted and mirrored codes, damaged codes, small codes, and multiple codes. Decode from images with the Photo Picker (including large images, handled without running out of memory).
- Feedback: a configurable beep, vibration, and an on-screen highlight of the detected code, plus a TalkBack and optional spoken announcement of the result (platform `TextToSpeech`) so users who cannot read the screen can use it.
- Continuous scan mode and batch mode collect many codes into a list that can be exported.
- Parsing for every type in 3.9, including the `WIFI:` format (with escaped characters), `MECARD`, vCard 2.1 to 4.0, `VEVENT`, `mailto:`, `smsto:`, `tel:`, `geo:`, `upi://`, EPC/SEPA, Bitcoin-style URIs, and plain URLs without a scheme.

**Link and content safety (local only, no network)**
- A banner for suspicious links: no HTTPS, IP-address hosts, punycode or look-alike (mixed script) domains, very long or heavily encoded URLs, URL shorteners (shown clearly as "shortened link"), non-HTTP schemes such as `intent:`, `javascript:`, and `file:` (blocked from opening), and payment or Wi-Fi codes with a caution note. The user can always copy the text instead of opening it. Auto-open is off by default and never applies to flagged links.
- Show the real destination host prominently before opening.
- Wi-Fi passwords copy with the platform's sensitive clipboard flag so they are hidden in the clipboard preview.

**Creating**
- Build the payload strings correctly for each type and encode with ZXing. A **style editor** offers: error correction level (L, M, Q, H, with guidance), module shape (squares, rounded, dots), eye (corner) style and color, foreground color or gradient, background color or transparency, quiet-zone size, a center logo or image (raising the error correction automatically and limiting the logo size), output size, and format.
- **Scannability self-check:** after every render, decode the generated image with the scanner and confirm the payload matches. Warn the user, in plain words, if the colors have too little contrast, the logo is too big, or the code is too dense for its size, and offer one-tap fixes.
- Export as PNG, JPEG, WebP, SVG, and PDF (SVG and PDF are true vector output), at user-chosen sizes up to at least 4096px. Save to the gallery (folder named Salim), share through the system share sheet, copy the image to the clipboard, and print through the platform print service.
- Static codes only. State plainly in the app's help that dynamic codes that can be edited later need a server, which this offline app deliberately does not have.

---

## 6. Layout safety: no overlap, no jumbling, no overcrowding, nothing out of the margins, on any device

This section is as binding as the pixel spec.

1. **Safe areas:** every screen draws edge to edge but keeps all content inside `WindowInsets.safeDrawing` (status bar, navigation bar, display cutout, IME) plus the 20 to 24dp page margins. Only the decorative watermark may bleed off the edges.
2. **Window size classes:** compact (under 600dp wide) uses the phone layout. On medium and expanded widths (tablets, foldables, landscape) content keeps a maximum width of about 560dp, centered, and Recent becomes a two-pane list and detail on expanded widths. The scan screen in landscape puts the frame on one side and the controls on the other.
3. **Sizing rules:** no fixed heights for content that holds text. Every card, button, and row grows with text, wraps to more lines, or ellipsizes according to a declared rule (titles wrap to 3 lines max, URLs and codes use a middle ellipsis, labels in the tab bar use one line with ellipsis and a capped scale of about 130%). Touch targets are at least 48dp. Elements never shrink below their minimum.
4. **Spacing system:** an 8dp grid, with the margins and gaps in section 3, and a minimum 8dp separation between any two interactive elements.
5. **Scroll containers:** any content that could exceed the screen is in a scrolling container with bottom padding equal to the tab bar height plus the inset, so nothing hides behind the bar or the keyboard.
6. **Extremes tested:** 320dp wide small phones, 360, 393, 412, and 480dp phones, short screens (568dp tall), tall 21:9 screens, tablets (600, 800, 1280dp), foldables folded and unfolded, landscape, font scale 0.85 to 2.0, the largest display size, RTL languages, a visible keyboard, one-handed and split-screen modes, and gesture and three-button navigation.
7. **Automated enforcement:** build a `LayoutSafetyRule` test utility that walks the Compose semantics tree for every screen in the matrix above and fails when two sibling text or interactive nodes overlap (apart from an allow-list of designed overlaps), when any node extends beyond the safe area, when a touch target is below 48dp, or when text is truncated where the spec says it must wrap. Add screenshot tests for each screen across the matrix. These must pass.

---

## 7. Settings (every item functional and persisted in DataStore)

- **General:** language (a picker showing each language in its own script, using the per-app language setting), theme (system, light, dark), start tab, haptics.
- **Scan:** beep, vibrate, spoken result, auto-open links (off by default), auto-copy, continuous scan, save scans to history, enabled formats, viewfinder style (frame, large, full screen), default camera, torch default, scan delay, duplicate-scan suppression time.
- **Create:** default error correction, default colors, default module and eye style, default size and format, remember the last type, save created codes to history, default logo.
- **History:** retention (forever, 7, 30, 90, 365 days, enforced by WorkManager), clear all with confirmation, export to CSV and JSON, import from JSON.
- **Privacy and security:** app lock with biometric or device credential and a timeout, hide content in the recents screen (`FLAG_SECURE`), link safety warnings on or off, a plain-language statement ("no network, no account, no analytics").
- **Accessibility:** text size, high-contrast mode, larger touch targets, spoken results, reduce motion.
- **Backup:** export and restore history and settings to a user-chosen file with optional password encryption (AES-256-GCM with a strong KDF).
- **Help and About:** short illustrated how-to cards (scan, create, Wi-Fi, share), the safety explanation, rate and share the app through intents, version, licenses (including the bundled libraries and fonts), the privacy statement, and a diagnostics export that contains no scanned content.

---

## 8. Easy for everyone

- Plain, short words in every label and message ("This link is not secure, be careful", never technical jargon). Icons always come with labels.
- A clear single main action per screen, large buttons, no hidden gestures required (swipe to delete also has a visible delete action in the row's menu and in multi-select).
- Spoken and sound feedback for people who cannot read well or have low vision, color is never the only signal, and everything works one-handed.
- Translations shipped for at least English, Hindi, Urdu, Arabic, Bengali, Spanish, French, Portuguese, Indonesian, and Swahili (each flagged in the README as needing native-speaker review), with full RTL support, locale-correct dates and numbers, and the ability to add any language by adding a strings file.
- Works fully offline on small, old, low-memory phones, with a small install size and light battery use.

---

## 9. Data layer (Room, no data loss)

Entities: `ScanRecord` and `CreatedCode` (or one `CodeRecord` with an origin field) with the id, origin (scanned or created), payload, format, parsed type, parsed fields, created timestamp, favorite flag, the style definition (for created codes), and an optional thumbnail path. FTS over the payload for search. Migrations are written and tested for every schema version. The thumbnail cache is bounded and cleaned. Everything survives process death and updates.

---

## 10. Quality bar

- Compose performance: stable keys, immutable UI models, hoisted state, no recomposition storms, preview and analysis on the right threads with backpressure, bitmaps recycled, no leaks.
- Reliability: camera lifecycle handled across rotation, split-screen, backgrounding, and another app taking the camera, with graceful recovery.
- Tests: unit tests for payload builders and parsers (including all escaping rules), safety heuristics, EAN check digits, retention, backup encryption, and Room migrations. Instrumented tests that decode generated codes back from images. Compose UI tests for the create flows, history swipe and undo, settings, and onboarding. Layout safety and screenshot tests from section 6. All pass.
- Lint clean, zero compiler warnings, no unused code or resources, `detekt` and `ktlint` configured, StrictMode on in debug.

---

## 11. Build order and completion protocol

Build order, finishing each step completely before the next: (1) setup, theme, `DesignSpec.kt`, Poppins, icons, gradient, glass components, tab bar, `LayoutSafetyRule`; (2) onboarding, splash, icon; (3) scanner with CameraX and decoding, frame, scan line, controls, permission states; (4) parsers, result sheet, safety checks, actions; (5) Create list, forms, payload builders; (6) renderer, style editor, self-check, exports, result modal; (7) Room, History, search, swipe delete, multi-select; (8) Settings, app lock, backup; (9) shortcuts, tile, widget, share target; (10) accessibility, translations, RTL, dark theme, tablets and foldables; (11) tests, pixel and layout protocols, R8 release build, signing, README.

- Write every file in full. Never write `...`, "rest unchanged", or "similar to above".
- Before coding, create `PROGRESS.md` with one checkbox per requirement here, numbered by section. Update it after each step. You are done only when every box is checked and the audit loop passes.
- The project compiles after every step. If you run out of room, stop at a clean file boundary, say which `PROGRESS.md` item you are on, and continue from it. Never shrink scope or stub a feature.
- Do not invent platform or library APIs. Read the current documentation for every call you are unsure of.

### Acceptance tests (on real devices)

| Feature | It must be true that |
| --- | --- |
| Identity | The package, namespace, and applicationId are `com.salim.qr`, the app is called Salim everywhere, and no `com.example`, `example`, or `MrQR` string exists in the repo |
| Scan | A printed QR, a screen QR, a damaged QR, an inverted QR, and EAN, Code 128, and Data Matrix barcodes all decode quickly on a low-end phone, and image scanning works |
| Create | Every type builds a correct payload that scans back identically with this app and with another scanner app, including Wi-Fi with special characters |
| Style | Colors, shapes, logo, and gradients render correctly and the self-check warns when a code would not scan |
| Export | PNG, JPEG, WebP, SVG, and PDF files open correctly, saving lands in the gallery without extra permission on Android 10 and above, and sharing and printing work |
| History | Scans and created codes appear with correct dates, search and filters work, swipe reveals the red delete button, undo restores, retention deletes on schedule |
| Safety | Look-alike domains, IP hosts, shorteners, non-HTTPS links, and dangerous schemes are flagged or blocked, and nothing opens automatically |
| Offline | The release build has no `INTERNET` permission and every feature works in airplane mode |
| Pixel match | At 390 × 844 dp the geometry tests pass within ±1dp and the screenshot diffs are clean for every screen in section 3 |
| Layout safety | The `LayoutSafetyRule` and screenshot matrix pass at every size, font scale, orientation, and language in section 6 with zero overlaps and zero clipping |
| Accessibility | TalkBack reads every screen sensibly, the spoken result works, targets are at least 48dp, contrast is AA in both themes |

---

## 12. Pixel-perfect protocol (required, works even if you cannot see the images)

1. **Single source of numbers:** put every measurement from section 3 (and any value in `docs/DESIGN_SPEC_OVERRIDES.md`, which wins when it exists) in `DesignSpec.kt` and in named tokens, each with a comment naming the spec line it came from. No magic numbers in composables.
2. **Reference frame:** lay out exactly at 390 × 844 dp. On other sizes keep the same margins and sizes, anchor the header at the top and the tab bar at the bottom, and let content flex under the rules in section 6.
3. **Geometry tests:** Compose UI tests at 390 × 844 dp assert the position, size, and padding of every element in section 3 within ±1dp of `DesignSpec`, and assert font sizes, weights, radii, and colors against the tokens. They use seeded data from a test-only database in the test source set. These must pass.
4. **Screenshot tests:** render each screen in a fixed, frozen state and compare against golden images with a small per-pixel tolerance. A golden is accepted only after the owner approves it against the design.
5. **Overlay tool (debug builds only, stripped from release):** loads an owner-supplied reference image from the device, draws it over the running screen with an opacity slider and a difference blend mode, with dp rulers and a grid. If you can see the reference, use it yourself. If you cannot, produce the tool and a `PIXEL_REVIEW.md` listing every element with its measured value so the owner can compare and send corrections, then apply each correction and repeat.
6. **Inventory check:** `FEATURES.md` has a row for every numbered item in section 3, with the file that draws it and the test that checks it. A missing row or element is a finding.
7. **Report honestly:** list in `PIXEL_REVIEW.md` every value still marked (verify) and every deviation, and never claim pixel-perfect unless the geometry tests pass and the owner has approved the golden images.

---

## 13. The audit-fix loop (required: run it until clean, then run it again)

Do not treat the build as finished when it compiles. Each pass is an **audit** that finds problems, then a **fix** of every problem, then a fresh audit from the top. Keep `AUDIT_LOG.md` with one entry per pass: the pass number, every finding (file, line, severity, evidence), the fix, and the re-check result. The loop ends only after **three consecutive full passes find zero issues**. There is no cap on the number of passes.

A pass covers all of the following:

1. **Forbidden content scan:** search the whole repo, including resources, manifests, Gradle files, ProGuard files, docs, and tests, for `com.example`, `example`, `MrQR`, `Lorem`, `ipsum`, `drownbyuynn`, `instagram`, `TODO`, `FIXME`, `mock`, `fake`, `dummy`, `sample`, `placeholder`, `coming soon`, `not implemented`, hard-coded URLs, names, dates, and user-facing strings, hard-coded colors and sizes outside the theme and `DesignSpec`, empty click lambdas, swallowed exceptions, unused code and resources. Test data is allowed only in the test source set.
2. **Identity check:** the applicationId, namespace, every package directory, the manifest, the Gradle settings, and the generated files all use `com.salim.qr`, and the visible name is Salim everywhere.
3. **Build and static analysis:** clean `assembleDebug`, `assembleRelease`, and `bundleRelease`, zero warnings, lint, `detekt`, and `ktlint` clean, R8 without missing-class warnings, and a check that the release APK has no `INTERNET` permission, no WebView, no debug menu, no reference images, and no analytics or ad library.
4. **Manifest and security review:** every component's `exported` value is justified, permissions are minimal and requested in context, no cleartext configuration, backups excluded, intent handling cannot be abused by a crafted code (no automatic opening, dangerous schemes blocked, inputs validated).
5. **Feature matrix:** `FEATURES.md` lists every feature and screen element with the implementing file, its test, and a pass or fail from a real run. A fail, a missing file, or no test is a finding.
6. **Automated tests:** all unit, instrumented, UI, geometry, screenshot, and layout-safety tests pass, including the round trip (create, render, decode, compare payload) for every code type and format.
7. **Real device runs:** install the release build and exercise every screen and feature with real data on a low-end phone, a large phone, a tablet, and a foldable, on API 24, a middle API, and the latest API. Include rotation, split-screen, dark theme, 200% font scale, TalkBack, RTL, airplane mode, camera permission denied, camera in use by another app, low storage, and a reboot.
8. **Scanning field test:** at least 100 varied real codes (small, large, glossy, curved, low light, on screens, inverted, damaged, many formats) with the recorded success rate, and fix the pipeline until it matches or beats common scanner apps.
9. **Resilience and performance:** process death and restoration on every screen, a monkey run of at least 20,000 events with no crash, ANR, or StrictMode violation, cold start under 1 second on a mid-range device, steady 60 fps scrolling with 10,000 history rows, stable memory, and no leaks.
10. **Accessibility and localization:** TalkBack, spoken results, 48dp targets (larger in the accessibility mode), AA contrast in both themes, every string externalized and translated for the listed languages, RTL mirrored, locale-correct formats.
11. **Pixel and layout check:** the section 12 tests and the section 6 `LayoutSafetyRule` matrix pass, and `PIXEL_REVIEW.md` has no unresolved owner correction.
12. **Data integrity:** export, wipe, restore round-trips with no loss, wrong passwords fail safely, corrupted files never damage existing data, and retention and deletion remove data and thumbnails from disk.

How to run the loop: fix the root cause of each finding and add a regression test for behavioral bugs. After a fix, rerun the whole pass. Never lower a standard, delete a test, or weaken a check to make a pass go green, and never mark a finding resolved without evidence in `AUDIT_LOG.md`. When three consecutive clean passes are done, write a final report in `AUDIT_LOG.md` with the pass count, each check's result, the known limitations (for example that dynamic QR codes need a server), and anything still needing the owner, such as approving the golden images and the native-speaker review of translations.

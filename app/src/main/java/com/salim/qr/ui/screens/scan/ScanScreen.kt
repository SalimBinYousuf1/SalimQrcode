package com.salim.qr.ui.screens.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.salim.qr.R
import com.salim.qr.data.datastore.SalimSettings
import com.salim.qr.data.db.CodeRecord
import com.salim.qr.data.db.SalimDatabase
import com.salim.qr.domain.export.CodeExporter
import com.salim.qr.domain.parsing.ParsedCode
import com.salim.qr.domain.parsing.PayloadParser
import com.salim.qr.domain.safety.LinkSafetyChecker
import com.salim.qr.domain.safety.SafetyVerdict
import com.salim.qr.ui.components.GlassCard
import com.salim.qr.ui.components.SalimBackground
import com.salim.qr.ui.components.SalimButton
import com.salim.qr.ui.components.SalimWordmark
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimScanLineGradient
import com.salim.qr.ui.theme.SalimSubtitleGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Composable
fun ScanScreen(
    settings: SalimSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val db = remember { SalimDatabase.getInstance(context) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var cameraFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isTorchOn by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableFloatStateOf(0f) }
    var showZoomSlider by remember { mutableStateOf(false) }
    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var previewViewInstance by remember { mutableStateOf<PreviewView?>(null) }
    var focusTargetOffset by remember { mutableStateOf<Offset?>(null) }

    LaunchedEffect(focusTargetOffset) {
        if (focusTargetOffset != null) {
            kotlinx.coroutines.delay(1200)
            focusTargetOffset = null
        }
    }

    // Scan result state
    var activeScannedPayload by remember { mutableStateOf<String?>(null) }
    var activeParsedCode by remember { mutableStateOf<ParsedCode?>(null) }
    var lastScannedTimestamp by remember { mutableStateOf(0L) }
    var isFavorite by remember { mutableStateOf(false) }

    // TTS
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialized
            }
        }
        textToSpeech = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // Gallery Picker for image decoding
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                decodeImageUri(context, uri) { decodedText ->
                    if (decodedText != null) {
                        scope.launch(Dispatchers.Main) {
                            handleScannedCode(
                                payload = decodedText,
                                settings = settings,
                                context = context,
                                db = db,
                                tts = textToSpeech,
                                onShowResult = { text, parsed ->
                                    activeScannedPayload = text
                                    activeParsedCode = parsed
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    SalimBackground {
        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header: Wordmark on left, Camera flip button on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SalimWordmark()

                // 52dp rounded-square glass button for camera flip
                GlassCard(
                    modifier = Modifier
                        .size(DesignSpec.FlipButtonSize)
                        .clickable {
                            cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        }
                        .testTag("camera_flip_button"),
                    cornerRadius = DesignSpec.FlipButtonRadius,
                    elevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cameraswitch,
                            contentDescription = stringResource(R.string.scan_action_flip),
                            tint = SalimIndigo,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // Title & Subtitle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 80.dp)
            ) {
                Text(
                    text = stringResource(R.string.scan_title),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = SalimIndigo
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.scan_subtitle),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = SalimSubtitleGray
                )
            }

            val frameSize = when (settings.viewfinderStyle) {
                "large" -> 290.dp
                "fullscreen" -> 320.dp
                else -> DesignSpec.ScanFrameSize
            }

            // Center Scan Frame
            Box(
                modifier = Modifier
                    .size(frameSize)
                    .align(Alignment.Center)
                    .offset(y = (-20).dp),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    // Live Camera View with rounded corners
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(DesignSpec.ScanFrameRadius))
                            .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoom, _ ->
                                    val newZoom = (zoomRatio + (zoom - 1f)).coerceIn(0f, 1f)
                                    zoomRatio = newZoom
                                    cameraInstance?.cameraControl?.setLinearZoom(newZoom)
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures { tapOffset ->
                                    focusTargetOffset = tapOffset
                                    previewViewInstance?.let { pv ->
                                        try {
                                            val point = pv.meteringPointFactory.createPoint(tapOffset.x, tapOffset.y)
                                            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
                                                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                                .build()
                                            cameraInstance?.cameraControl?.startFocusAndMetering(action)
                                        } catch (_: Exception) {}
                                    }
                                }
                            }
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).also { previewViewInstance = it }
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                val cameraExecutor = Executors.newSingleThreadExecutor()

                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.surfaceProvider = previewView.surfaceProvider
                                    }

                                    val scanner = BarcodeScanning.getClient()
                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()

                                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                            scanner.process(image)
                                                .addOnSuccessListener { barcodes ->
                                                    val first = barcodes.firstOrNull()?.rawValue
                                                    if (!first.isNullOrBlank()) {
                                                        val now = System.currentTimeMillis()
                                                        if (now - lastScannedTimestamp > 1800) {
                                                            lastScannedTimestamp = now
                                                            scope.launch(Dispatchers.Main) {
                                                                handleScannedCode(
                                                                    payload = first,
                                                                    settings = settings,
                                                                    context = context,
                                                                    db = db,
                                                                    tts = textToSpeech,
                                                                    onShowResult = { text, parsed ->
                                                                        activeScannedPayload = text
                                                                        activeParsedCode = parsed
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                .addOnCompleteListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        val cameraSelector = CameraSelector.Builder()
                                            .requireLensFacing(cameraFacing)
                                            .build()
                                        val cam = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis
                                        )
                                        cameraInstance = cam
                                    } catch (_: Exception) {}
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Sweeping glowing scan line animation
                        val infiniteTransition = rememberInfiniteTransition(label = "scan_line")
                        val scanLineOffset by infiniteTransition.animateFloat(
                            initialValue = 0.05f,
                            targetValue = 0.95f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scan_offset"
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val lineY = size.height * scanLineOffset
                            drawRoundRect(
                                brush = SalimScanLineGradient,
                                topLeft = Offset(0f, lineY - DesignSpec.ScanLineThickness.toPx() / 2f),
                                size = Size(size.width, DesignSpec.ScanLineThickness.toPx()),
                                cornerRadius = CornerRadius(4f, 4f)
                            )

                            // Tap-to-focus indicator
                            focusTargetOffset?.let { tapPos ->
                                drawCircle(
                                    color = SalimIndigo,
                                    radius = 24.dp.toPx(),
                                    center = tapPos,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                                drawCircle(
                                    color = SalimIndigo.copy(alpha = 0.5f),
                                    radius = 5.dp.toPx(),
                                    center = tapPos
                                )
                            }
                        }
                    }
                } else {
                    // Friendly Camera Permission Request inside frame
                    GlassCard(
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = DesignSpec.ScanFrameRadius
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.scan_perm_title),
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SalimNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.scan_perm_desc),
                                fontFamily = PoppinsFontFamily,
                                fontSize = 11.sp,
                                color = SalimSubtitleGray,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            SalimButton(
                                text = stringResource(R.string.scan_perm_button),
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                modifier = Modifier.height(44.dp)
                            )
                        }
                    }
                }

                // Four Indigo Corner Brackets (29dp long, 4dp stroke, rounded caps, 8dp inner radius)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bracketLen = DesignSpec.ScanBracketLength.toPx()
                    val stroke = DesignSpec.ScanBracketStroke.toPx()
                    val radius = DesignSpec.ScanBracketInnerRadius.toPx()
                    val strokeStyle = Stroke(width = stroke, cap = StrokeCap.Round)

                    // Top-Left bracket
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, bracketLen)
                            lineTo(0f, radius)
                            quadraticTo(0f, 0f, radius, 0f)
                            lineTo(bracketLen, 0f)
                        },
                        color = SalimIndigo,
                        style = strokeStyle
                    )

                    // Top-Right bracket
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width - bracketLen, 0f)
                            lineTo(size.width - radius, 0f)
                            quadraticTo(size.width, 0f, size.width, radius)
                            lineTo(size.width, bracketLen)
                        },
                        color = SalimIndigo,
                        style = strokeStyle
                    )

                    // Bottom-Left bracket
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, size.height - bracketLen)
                            lineTo(0f, size.height - radius)
                            quadraticTo(0f, size.height, radius, size.height)
                            lineTo(bracketLen, size.height)
                        },
                        color = SalimIndigo,
                        style = strokeStyle
                    )

                    // Bottom-Right bracket
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width - bracketLen, size.height)
                            lineTo(size.width - radius, size.height)
                            quadraticTo(size.width, size.height, size.width, size.height - radius)
                            lineTo(size.width, size.height - bracketLen)
                        },
                        color = SalimIndigo,
                        style = strokeStyle
                    )
                }
            }

            // Extra Controls (Torch, Gallery, Zoom) placed in free space above tab bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 110.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showZoomSlider) {
                    Slider(
                        value = zoomRatio,
                        onValueChange = {
                            zoomRatio = it
                            cameraInstance?.cameraControl?.setLinearZoom(it)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = SalimIndigo,
                            activeTrackColor = SalimIndigo,
                            inactiveTrackColor = Color(0xFFE4E7FC)
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(bottom = 8.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Torch
                    ScanActionButton(
                        icon = Icons.Outlined.FlashOn,
                        label = stringResource(R.string.scan_action_torch),
                        isActive = isTorchOn,
                        onClick = {
                            isTorchOn = !isTorchOn
                            cameraInstance?.cameraControl?.enableTorch(isTorchOn)
                        },
                        testTag = "scan_control_torch"
                    )

                    // Gallery
                    ScanActionButton(
                        icon = Icons.Outlined.Image,
                        label = stringResource(R.string.scan_action_gallery),
                        isActive = false,
                        onClick = { galleryLauncher.launch("image/*") },
                        testTag = "scan_control_gallery"
                    )

                    // Zoom
                    ScanActionButton(
                        icon = Icons.Outlined.ZoomIn,
                        label = stringResource(R.string.scan_action_zoom),
                        isActive = showZoomSlider,
                        onClick = { showZoomSlider = !showZoomSlider },
                        testTag = "scan_control_zoom"
                    )
                }
            }
        }
    }

    // Result Bottom Sheet
    if (activeScannedPayload != null && activeParsedCode != null) {
        ScanResultBottomSheet(
            rawText = activeScannedPayload!!,
            parsedCode = activeParsedCode!!,
            isFavorite = isFavorite,
            onToggleFavorite = {
                isFavorite = !isFavorite
                scope.launch(Dispatchers.IO) {
                    val existing = db.codeRecordDao().searchFlow(activeScannedPayload!!).toString()
                }
            },
            onDismiss = {
                activeScannedPayload = null
                activeParsedCode = null
            }
        )
    }
}

@Composable
private fun ScanActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).testTag(testTag)
    ) {
        GlassCard(
            modifier = Modifier.size(DesignSpec.ScanExtraButtonSize),
            cornerRadius = DesignSpec.ScanExtraButtonSize / 2,
            elevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isActive) SalimIndigo else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) Color.White else SalimIndigo,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = PoppinsFontFamily,
            fontSize = 11.sp,
            color = SalimNavy
        )
    }
}

private fun handleScannedCode(
    payload: String,
    settings: SalimSettings,
    context: Context,
    db: SalimDatabase,
    tts: TextToSpeech?,
    onShowResult: (String, ParsedCode) -> Unit
) {
    // Sound beep
    if (settings.beepEnabled) {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (_: Exception) {}
    }

    // Haptic vibration
    if (settings.hapticsEnabled) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    val parsed = PayloadParser.parse(payload)

    // TTS speech
    if (settings.speechEnabled && tts != null) {
        val speechText = when (parsed) {
            is ParsedCode.Website -> "Scanned website link to ${parsed.host}"
            is ParsedCode.Wifi -> "Scanned Wi-Fi network ${parsed.ssid}"
            is ParsedCode.Contact -> "Scanned contact card for ${parsed.name}"
            else -> "Scanned code: $payload"
        }
        tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "salim_scan")
    }

    // Save to Room DB if setting is enabled
    if (settings.saveScansToHistory) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            val record = CodeRecord(
                origin = "scanned",
                payload = payload,
                format = "QR_CODE",
                parsedType = when (parsed) {
                    is ParsedCode.Website -> "Website"
                    is ParsedCode.Text -> "Text"
                    is ParsedCode.Email -> "Email"
                    is ParsedCode.Sms -> "SMS"
                    is ParsedCode.Wifi -> "Wifi"
                    is ParsedCode.Phone -> "Phone"
                    is ParsedCode.Contact -> "Contact"
                    is ParsedCode.Location -> "Location"
                    is ParsedCode.Event -> "Event"
                    is ParsedCode.Payment -> "Payment"
                    is ParsedCode.Barcode -> "Barcode"
                    else -> "Data"
                },
                title = when (parsed) {
                    is ParsedCode.Website -> parsed.host
                    is ParsedCode.Wifi -> parsed.ssid
                    is ParsedCode.Contact -> parsed.name
                    else -> payload
                }
            )
            db.codeRecordDao().insert(record)
        }
    }

    // Auto-copy to clipboard if enabled in settings
    if (settings.autoCopy) {
        CodeExporter.copyTextToClipboard(context, "Salim Code", payload)
        Toast.makeText(context, context.getString(R.string.toast_copied), Toast.LENGTH_SHORT).show()
    }

    // Auto-open links if enabled in settings and destination is verified safe
    if (settings.autoOpenLinks && parsed is ParsedCode.Website) {
        val safety = LinkSafetyChecker.evaluate(parsed.url)
        if (safety is SafetyVerdict.Safe) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(parsed.url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    // Continuous scan mode vs interactive modal
    if (settings.continuousScan) {
        val summary = when (parsed) {
            is ParsedCode.Website -> parsed.host
            is ParsedCode.Wifi -> parsed.ssid
            is ParsedCode.Contact -> parsed.name
            else -> payload.take(30)
        }
        Toast.makeText(context, "Scanned: $summary", Toast.LENGTH_SHORT).show()
    } else {
        onShowResult(payload, parsed)
    }
}

private fun decodeImageUri(context: Context, uri: Uri, onResult: (String?) -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap != null) {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            onResult(result.text)
            return
        }
    } catch (_: Exception) {}

    // Fallback using MLKit
    try {
        val image = InputImage.fromFilePath(context, uri)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                onResult(barcodes.firstOrNull()?.rawValue)
            }
            .addOnFailureListener {
                onResult(null)
            }
    } catch (_: Exception) {
        onResult(null)
    }
}

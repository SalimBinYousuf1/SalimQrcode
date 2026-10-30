package com.salim.qr.ui.screens.create

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.print.PrintHelper
import com.salim.qr.R
import com.salim.qr.domain.encoding.BarcodeEncoder
import com.salim.qr.domain.encoding.EyeShape
import com.salim.qr.domain.encoding.ModuleShape
import com.salim.qr.domain.encoding.QrStyle
import com.salim.qr.domain.encoding.ScannabilityChecker
import com.salim.qr.domain.export.CodeExporter
import com.salim.qr.ui.components.GlassBackButton
import com.salim.qr.ui.components.GlassCard
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimDangerRed
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimSubtitleGray

@Composable
fun ResultModal(
    payload: String,
    title: String,
    typeTitle: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var qrStyle by remember {
        mutableStateOf(
            QrStyle(
                foregroundColor = 0xFF5B67EA.toInt(),
                moduleShape = ModuleShape.ROUNDED,
                eyeShape = EyeShape.ROUNDED
            )
        )
    }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showStyleDialog by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var scannabilityWarning by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(payload, qrStyle) {
        val bitmap = BarcodeEncoder.encode(payload = payload, style = qrStyle)
        val check = ScannabilityChecker.verify(bitmap, payload)
        scannabilityWarning = check.warningMessage
        qrBitmap = bitmap
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .statusBarsPadding()
            .testTag("result_modal_container")
    ) {
        // Back Button top left
        GlassBackButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = DesignSpec.BackButtonLeftMargin, top = 16.dp)
        )

        // Modal Card: 340dp wide, radius 28dp, white-to-lavender gradient glass look
        GlassCard(
            modifier = Modifier
                .width(DesignSpec.ResultModalWidth)
                .align(Alignment.Center)
                .padding(vertical = 24.dp),
            cornerRadius = DesignSpec.ResultModalRadius,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White, Color(0xFFF6F5FC))
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top: Glossy rounded tile (200dp) with scan brackets containing the real QR code
                Box(
                    modifier = Modifier
                        .size(DesignSpec.ResultCodeTileSize)
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "Generated Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Decorative corner brackets
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val bracketLen = 18.dp.toPx()
                        val stroke = 3.dp.toPx()
                        val capStyle = Stroke(width = stroke, cap = StrokeCap.Round)

                        // Top-Left
                        drawLine(SalimIndigo, Offset(0f, bracketLen), Offset(0f, 0f), stroke)
                        drawLine(SalimIndigo, Offset(0f, 0f), Offset(bracketLen, 0f), stroke)
                        // Top-Right
                        drawLine(SalimIndigo, Offset(size.width - bracketLen, 0f), Offset(size.width, 0f), stroke)
                        drawLine(SalimIndigo, Offset(size.width, 0f), Offset(size.width, bracketLen), stroke)
                        // Bottom-Left
                        drawLine(SalimIndigo, Offset(0f, size.height - bracketLen), Offset(0f, size.height), stroke)
                        drawLine(SalimIndigo, Offset(0f, size.height), Offset(bracketLen, size.height), stroke)
                        // Bottom-Right
                        drawLine(SalimIndigo, Offset(size.width - bracketLen, size.height), Offset(size.width, size.height), stroke)
                        drawLine(SalimIndigo, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), stroke)
                    }
                }

                if (scannabilityWarning != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = scannabilityWarning!!,
                        fontFamily = PoppinsFontFamily,
                        fontSize = 11.sp,
                        color = SalimDangerRed
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Heading "Here's your code!"
                Text(
                    text = stringResource(R.string.result_modal_title),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = SalimIndigo
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = stringResource(R.string.result_modal_subtitle),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = SalimSubtitleGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Two Circular 48dp Action Buttons (Share and Save)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ResultActionButton(
                        icon = Icons.Outlined.Share,
                        label = stringResource(R.string.action_share),
                        onClick = {
                            qrBitmap?.let { bmp ->
                                CodeExporter.shareBitmap(context, bmp, "Salim QR Code: $payload")
                            }
                        },
                        testTag = "result_action_share"
                    )

                    ResultActionButton(
                        icon = Icons.Outlined.Download,
                        label = stringResource(R.string.action_save),
                        onClick = {
                            qrBitmap?.let { bmp ->
                                val uri = CodeExporter.saveToGallery(context, bmp, title)
                                if (uri != null) {
                                    Toast.makeText(context, context.getString(R.string.toast_saved), Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        testTag = "result_action_save"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Secondary Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickIconButton(
                        icon = Icons.Outlined.ContentCopy,
                        label = "Copy",
                        onClick = {
                            CodeExporter.copyTextToClipboard(context, "Salim Code", payload)
                            Toast.makeText(context, context.getString(R.string.toast_copied), Toast.LENGTH_SHORT).show()
                        }
                    )
                    QuickIconButton(
                        icon = Icons.Outlined.Palette,
                        label = "Style",
                        onClick = { showStyleDialog = true }
                    )
                    QuickIconButton(
                        icon = Icons.Outlined.Print,
                        label = "Print",
                        onClick = {
                            qrBitmap?.let { bmp ->
                                val printHelper = PrintHelper(context)
                                printHelper.scaleMode = PrintHelper.SCALE_MODE_FIT
                                printHelper.printBitmap("Salim_QR_$title", bmp)
                            }
                        }
                    )
                    QuickIconButton(
                        icon = Icons.Outlined.PictureAsPdf,
                        label = "PDF",
                        onClick = {
                            qrBitmap?.let { bmp ->
                                CodeExporter.exportPdfFile(context, bmp, title, payload)
                            }
                        }
                    )
                    QuickIconButton(
                        icon = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        label = "Favorite",
                        tint = if (isFavorite) SalimDangerRed else SalimNavy,
                        onClick = { isFavorite = !isFavorite }
                    )
                }
            }
        }
    }

    if (showStyleDialog) {
        StyleEditorDialog(
            initialStyle = qrStyle,
            onSave = { newStyle ->
                qrStyle = newStyle
                showStyleDialog = false
            },
            onDismiss = { showStyleDialog = false }
        )
    }
}

@Composable
private fun ResultActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    GlassCard(
        modifier = Modifier
            .size(width = 76.dp, height = 70.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        cornerRadius = 18.dp,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = SalimIndigo,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = SalimNavy
            )
        }
    }
}

@Composable
private fun QuickIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color = SalimNavy,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4FB)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = PoppinsFontFamily,
            fontSize = 10.sp,
            color = SalimSubtitleGray
        )
    }
}

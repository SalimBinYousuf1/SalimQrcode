package com.salim.qr.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.salim.qr.domain.encoding.ModuleShape
import com.salim.qr.domain.encoding.QrStyle
import com.salim.qr.ui.components.SalimButton
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimSubtitleGray

@Composable
fun StyleEditorDialog(
    initialStyle: QrStyle,
    onSave: (QrStyle) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedShape by remember { mutableStateOf(initialStyle.moduleShape) }
    var selectedColor by remember { mutableStateOf(initialStyle.foregroundColor) }
    var selectedEc by remember { mutableStateOf(initialStyle.errorCorrection) }
    var withLogo by remember { mutableStateOf(initialStyle.withCenterLogo) }

    val colorOptions = listOf(
        0xFF1E2347.toInt(), // Navy
        0xFF5B6CF0.toInt(), // Indigo
        0xFF7C3AED.toInt(), // Violet
        0xFF0D9488.toInt(), // Teal
        0xFFBE185D.toInt()  // Rose
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("style_editor_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Customize QR Style",
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SalimNavy
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Module Shape Selector
                Text(
                    text = "Module Pattern",
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SalimNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModuleShape.values().forEach { shape ->
                        val isSelected = selectedShape == shape
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SalimIndigo else Color(0xFFF3F4FB))
                                .clickable { selectedShape = shape },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = shape.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontFamily = PoppinsFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else SalimNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color Selector
                Text(
                    text = "Color Theme",
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SalimNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorOptions.forEach { colorInt ->
                        val isSelected = selectedColor == colorInt
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorInt))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color(0xFFE0E7FF) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorInt }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Center Salim Brand Icon Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Embed Brand Icon",
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = SalimNavy
                        )
                        Text(
                            text = "Auto-enhances error correction",
                            fontFamily = PoppinsFontFamily,
                            fontSize = 11.sp,
                            color = SalimSubtitleGray
                        )
                    }
                    Switch(
                        checked = withLogo,
                        onCheckedChange = { withLogo = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SalimIndigo
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                SalimButton(
                    text = "Apply Changes",
                    onClick = {
                        onSave(
                            initialStyle.copy(
                                moduleShape = selectedShape,
                                foregroundColor = selectedColor,
                                errorCorrection = if (withLogo) ErrorCorrectionLevel.H else selectedEc,
                                withCenterLogo = withLogo
                            )
                        )
                    },
                    modifier = Modifier.height(48.dp)
                )
            }
        }
    }
}

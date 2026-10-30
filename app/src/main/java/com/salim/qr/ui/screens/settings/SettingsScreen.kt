package com.salim.qr.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.qr.R
import com.salim.qr.data.datastore.SalimSettings
import com.salim.qr.data.datastore.SettingsPreferences
import com.salim.qr.data.db.SalimDatabase
import com.salim.qr.ui.components.GlassCard
import com.salim.qr.ui.components.SalimBackground
import com.salim.qr.ui.components.SalimWordmark
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimDangerRed
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimSubtitleGray
import com.salim.qr.ui.theme.TypeBarcodeGradient
import com.salim.qr.ui.theme.TypeEmailGradient
import com.salim.qr.ui.theme.TypePaymentGradient
import com.salim.qr.ui.theme.TypePhoneGradient
import com.salim.qr.ui.theme.TypeTextGradient
import com.salim.qr.ui.theme.TypeWebsiteGradient
import com.salim.qr.ui.theme.TypeWifiGradient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settings: SalimSettings,
    preferences: SettingsPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { SalimDatabase.getInstance(context) }
    var showClearConfirm by remember { mutableStateOf(false) }

    SalimBackground {
        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Wordmark top left
                SalimWordmark(
                    modifier = Modifier
                        .padding(start = DesignSpec.WordmarkLeftMargin, top = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Title "Settings" (30sp bold indigo)
                Text(
                    text = stringResource(R.string.settings_title),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = SalimIndigo,
                    modifier = Modifier.padding(horizontal = DesignSpec.PageMarginOnboarding)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("settings_list"),
                    contentPadding = PaddingValues(
                        start = DesignSpec.PageMarginContent,
                        end = DesignSpec.PageMarginContent,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // General Group
                    item {
                        SettingsGroup(title = stringResource(R.string.settings_group_general)) {
                            // Theme Selector
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = stringResource(R.string.setting_theme),
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = SalimNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("system", "light", "dark").forEach { mode ->
                                        val isSelected = settings.themeMode == mode
                                        val label = when (mode) {
                                            "light" -> stringResource(R.string.setting_theme_light)
                                            "dark" -> stringResource(R.string.setting_theme_dark)
                                            else -> stringResource(R.string.setting_theme_system)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) SalimIndigo else Color(0xFFF3F4FB))
                                                .clickable { scope.launch { preferences.setThemeMode(mode) } },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontFamily = PoppinsFontFamily,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Color.White else SalimNavy
                                            )
                                        }
                                    }
                                }
                            }

                            SettingsToggleRow(
                                title = stringResource(R.string.setting_haptics),
                                subtitle = stringResource(R.string.setting_haptics_desc),
                                isChecked = settings.hapticsEnabled,
                                onCheckedChange = { scope.launch { preferences.setHapticsEnabled(it) } },
                                icon = Icons.Outlined.Fingerprint,
                                iconGradient = TypeWifiGradient
                            )
                        }
                    }

                    // Scan Group
                    item {
                        SettingsGroup(title = stringResource(R.string.settings_group_scan)) {
                            // Viewfinder Style
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = stringResource(R.string.setting_viewfinder),
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = SalimNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("frame", "large", "fullscreen").forEach { style ->
                                        val isSelected = settings.viewfinderStyle == style
                                        val label = when (style) {
                                            "large" -> "Large"
                                            "fullscreen" -> "Full"
                                            else -> "Standard"
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) SalimIndigo else Color(0xFFF3F4FB))
                                                .clickable { scope.launch { preferences.setViewfinderStyle(style) } },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontFamily = PoppinsFontFamily,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Color.White else SalimNavy
                                            )
                                        }
                                    }
                                }
                            }

                            SettingsToggleRow(
                                title = stringResource(R.string.setting_beep),
                                subtitle = stringResource(R.string.setting_beep_desc),
                                isChecked = settings.beepEnabled,
                                onCheckedChange = { scope.launch { preferences.setBeepEnabled(it) } },
                                icon = Icons.Outlined.VolumeUp,
                                iconGradient = TypeWebsiteGradient
                            )
                            SettingsToggleRow(
                                title = stringResource(R.string.setting_speech),
                                subtitle = stringResource(R.string.setting_speech_desc),
                                isChecked = settings.speechEnabled,
                                onCheckedChange = { scope.launch { preferences.setSpeechEnabled(it) } },
                                icon = Icons.Outlined.VolumeUp,
                                iconGradient = TypeEmailGradient
                            )
                            SettingsToggleRow(
                                title = stringResource(R.string.setting_auto_copy),
                                subtitle = stringResource(R.string.setting_auto_copy_desc),
                                isChecked = settings.autoCopy,
                                onCheckedChange = { scope.launch { preferences.setAutoCopy(it) } },
                                icon = Icons.Outlined.QrCodeScanner,
                                iconGradient = TypeTextGradient
                            )
                            SettingsToggleRow(
                                title = stringResource(R.string.setting_auto_open),
                                subtitle = stringResource(R.string.setting_auto_open_desc),
                                isChecked = settings.autoOpenLinks,
                                onCheckedChange = { scope.launch { preferences.setAutoOpenLinks(it) } },
                                icon = Icons.Outlined.QrCodeScanner,
                                iconGradient = TypeWebsiteGradient
                            )
                            SettingsToggleRow(
                                title = stringResource(R.string.setting_continuous_scan),
                                subtitle = stringResource(R.string.setting_continuous_scan_desc),
                                isChecked = settings.continuousScan,
                                onCheckedChange = { scope.launch { preferences.setContinuousScan(it) } },
                                icon = Icons.Outlined.QrCodeScanner,
                                iconGradient = TypeWifiGradient
                            )
                        }
                    }

                    // History Group
                    item {
                        SettingsGroup(title = stringResource(R.string.settings_group_history)) {
                            // Retention selector
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = stringResource(R.string.setting_retention),
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = SalimNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("forever", "30", "90").forEach { ret ->
                                        val isSelected = settings.retentionPeriod == ret
                                        val label = when (ret) {
                                            "30" -> "30 Days"
                                            "90" -> "90 Days"
                                            else -> "Forever"
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) SalimIndigo else Color(0xFFF3F4FB))
                                                .clickable {
                                                    scope.launch {
                                                        preferences.setRetentionPeriod(ret)
                                                        if (ret == "30") {
                                                            val cutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
                                                            db.codeRecordDao().deleteOlderThan(cutoff)
                                                        } else if (ret == "90") {
                                                            val cutoff = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
                                                            db.codeRecordDao().deleteOlderThan(cutoff)
                                                        }
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontFamily = PoppinsFontFamily,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Color.White else SalimNavy
                                            )
                                        }
                                    }
                                }
                            }

                            SettingsActionRow(
                                title = stringResource(R.string.setting_clear_history),
                                subtitle = "Permanently remove all scan and created records",
                                icon = Icons.Outlined.DeleteSweep,
                                iconGradient = Brush.verticalGradient(listOf(SalimDangerRed, Color(0xFFE53E3E))),
                                onClick = { showClearConfirm = true }
                            )
                            SettingsActionRow(
                                title = stringResource(R.string.setting_export_csv),
                                subtitle = "Save history records as CSV file",
                                icon = Icons.Outlined.Download,
                                iconGradient = TypePhoneGradient,
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        exportHistoryCsv(context, db)
                                    }
                                }
                            )
                            SettingsActionRow(
                                title = stringResource(R.string.setting_export_json),
                                subtitle = "Backup full records as JSON",
                                icon = Icons.Outlined.Download,
                                iconGradient = TypePaymentGradient,
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        exportHistoryJson(context, db)
                                    }
                                }
                            )
                        }
                    }

                    // Privacy & Security Group
                    item {
                        SettingsGroup(title = stringResource(R.string.settings_group_privacy)) {
                            SettingsToggleRow(
                                title = stringResource(R.string.setting_app_lock),
                                subtitle = stringResource(R.string.setting_app_lock_desc),
                                isChecked = settings.appLockEnabled,
                                onCheckedChange = { scope.launch { preferences.setAppLockEnabled(it) } },
                                icon = Icons.Outlined.Lock,
                                iconGradient = TypePaymentGradient
                            )
                            SettingsInfoRow(
                                title = stringResource(R.string.setting_privacy_statement),
                                description = stringResource(R.string.setting_privacy_statement_desc),
                                icon = Icons.Outlined.Security,
                                iconGradient = TypeBarcodeGradient
                            )
                        }
                    }

                    // About Group
                    item {
                        SettingsGroup(title = stringResource(R.string.settings_group_about)) {
                            SettingsInfoRow(
                                title = "Salim QR",
                                description = "Version 1.0.0 (Offline Build)\nNo network permissions, 100% private.",
                                icon = Icons.Outlined.Info,
                                iconGradient = TypeWebsiteGradient
                            )
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = {
                Text(
                    text = stringResource(R.string.clear_history_confirm),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = SalimNavy
                )
            },
            text = {
                Text(
                    text = "This will delete all saved scan and created QR codes permanently.",
                    fontFamily = PoppinsFontFamily,
                    color = SalimSubtitleGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            db.codeRecordDao().clearAll()
                        }
                        showClearConfirm = false
                        Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear All", color = SalimDangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = SalimNavy)
                }
            }
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = SalimNavy,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            elevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    iconGradient: Brush
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = SalimNavy
                )
                Text(
                    text = subtitle,
                    fontFamily = PoppinsFontFamily,
                    fontSize = 11.sp,
                    color = SalimSubtitleGray
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SalimIndigo
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconGradient: Brush,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = SalimNavy
            )
            Text(
                text = subtitle,
                fontFamily = PoppinsFontFamily,
                fontSize = 11.sp,
                color = SalimSubtitleGray
            )
        }
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    description: String,
    icon: ImageVector,
    iconGradient: Brush
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = SalimNavy
            )
            Text(
                text = description,
                fontFamily = PoppinsFontFamily,
                fontSize = 11.sp,
                color = SalimSubtitleGray,
                lineHeight = 16.sp
            )
        }
    }
}

private suspend fun exportHistoryCsv(context: Context, db: SalimDatabase) {
    val records = db.codeRecordDao().getAllFlow().first()
    val sb = StringBuilder()
    sb.append("ID,Origin,Type,Payload,Timestamp\n")
    for (r in records) {
        val safePayload = r.payload.replace("\"", "\"\"")
        sb.append("${r.id},\"${r.origin}\",\"${r.parsedType}\",\"$safePayload\",${r.timestamp}\n")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "Salim_QR_History_Export.csv")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Export History"))
}

private suspend fun exportHistoryJson(context: Context, db: SalimDatabase) {
    val records = db.codeRecordDao().getAllFlow().first()
    val jsonStr = com.salim.qr.domain.export.CodeExporter.serializeRecordsToJson(records)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_SUBJECT, "Salim_QR_Backup.json")
        putExtra(Intent.EXTRA_TEXT, jsonStr)
    }
    context.startActivity(Intent.createChooser(intent, "Export JSON Backup"))
}

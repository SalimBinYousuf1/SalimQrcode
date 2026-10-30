package com.salim.qr.ui.screens.scan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.qr.R
import com.salim.qr.domain.export.CodeExporter
import com.salim.qr.domain.parsing.ParsedCode
import com.salim.qr.domain.safety.LinkSafetyChecker
import com.salim.qr.domain.safety.SafetyVerdict
import com.salim.qr.ui.components.SalimButton
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimBodyGray
import com.salim.qr.ui.theme.SalimDangerRed
import com.salim.qr.ui.theme.SalimGlassWhite
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.TypeWebsiteGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultBottomSheet(
    rawText: String,
    parsedCode: ParsedCode,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val safetyVerdict = if (parsedCode is ParsedCode.Website) {
        LinkSafetyChecker.evaluate(parsedCode.url)
    } else SafetyVerdict.Safe

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = DesignSpec.BottomSheetRadius,
            topEnd = DesignSpec.BottomSheetRadius
        ),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .testTag("scan_result_sheet")
        ) {
            // Header with Type and Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TypeWebsiteGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when (parsedCode) {
                            is ParsedCode.Website -> stringResource(R.string.type_website)
                            is ParsedCode.Text -> stringResource(R.string.type_text)
                            is ParsedCode.Email -> stringResource(R.string.type_email)
                            is ParsedCode.Sms -> stringResource(R.string.type_sms)
                            is ParsedCode.Wifi -> stringResource(R.string.type_wifi)
                            is ParsedCode.Phone -> stringResource(R.string.type_phone)
                            is ParsedCode.Contact -> stringResource(R.string.type_contact)
                            is ParsedCode.Location -> stringResource(R.string.type_location)
                            is ParsedCode.Event -> stringResource(R.string.type_event)
                            is ParsedCode.Payment -> stringResource(R.string.type_payment)
                            is ParsedCode.Barcode -> stringResource(R.string.type_barcode)
                            is ParsedCode.Other -> "Data"
                        },
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SalimNavy
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4FB))
                        .clickable { onToggleFavorite() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) SalimDangerRed else SalimNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety Warning Banner if applicable
            if (safetyVerdict is SafetyVerdict.Warning) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF3CD),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFF856404),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = safetyVerdict.reasonTitle,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF856404)
                            )
                            Text(
                                text = safetyVerdict.reasonDesc,
                                fontFamily = PoppinsFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF856404)
                            )
                        }
                    }
                }
            } else if (safetyVerdict is SafetyVerdict.Blocked) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8D7DA),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Blocked",
                            tint = SalimDangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = safetyVerdict.reasonTitle,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SalimDangerRed
                            )
                            Text(
                                text = safetyVerdict.reasonDesc,
                                fontFamily = PoppinsFontFamily,
                                fontSize = 11.sp,
                                color = SalimDangerRed
                            )
                        }
                    }
                }
            }

            // Raw / Parsed Content Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF7F8FC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = when (parsedCode) {
                            is ParsedCode.Website -> parsedCode.url
                            is ParsedCode.Wifi -> "SSID: ${parsedCode.ssid}\nSecurity: ${parsedCode.security}\nPassword: ${parsedCode.password}"
                            is ParsedCode.Contact -> "${parsedCode.name}\n${parsedCode.phone}\n${parsedCode.email}"
                            is ParsedCode.Payment -> "Payee: ${parsedCode.name}\nUPI ID: ${parsedCode.vpa}\nAmount: ${parsedCode.amount}"
                            else -> rawText
                        },
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        color = SalimNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action Button
            if (safetyVerdict !is SafetyVerdict.Blocked) {
                val primaryLabel = when (parsedCode) {
                    is ParsedCode.Website -> "Open in Browser"
                    is ParsedCode.Wifi -> "Connect to Network"
                    is ParsedCode.Email -> "Send Email"
                    is ParsedCode.Sms -> "Send SMS"
                    is ParsedCode.Phone -> "Call Phone"
                    is ParsedCode.Contact -> "Add to Contacts"
                    is ParsedCode.Location -> "Open in Maps"
                    is ParsedCode.Event -> "Add to Calendar"
                    is ParsedCode.Payment -> "Pay with UPI"
                    else -> "Copy Content"
                }

                SalimButton(
                    text = primaryLabel,
                    onClick = {
                        handlePrimaryAction(context, parsedCode, rawText)
                    },
                    testTag = "action_primary_button"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary Quick Actions: Copy and Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val isSensitive = parsedCode is ParsedCode.Wifi
                            CodeExporter.copyTextToClipboard(context, "Salim Code", rawText, isSensitive)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy",
                        tint = SalimIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_copy),
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = SalimIndigo
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            CodeExporter.shareText(context, rawText)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = SalimIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_share),
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = SalimIndigo
                    )
                }
            }
        }
    }
}

private fun handlePrimaryAction(context: Context, parsed: ParsedCode, raw: String) {
    try {
        when (parsed) {
            is ParsedCode.Website -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(parsed.url))
                context.startActivity(intent)
            }
            is ParsedCode.Email -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${parsed.address}")).apply {
                    putExtra(Intent.EXTRA_SUBJECT, parsed.subject)
                    putExtra(Intent.EXTRA_TEXT, parsed.body)
                }
                context.startActivity(intent)
            }
            is ParsedCode.Sms -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${parsed.phoneNumber}")).apply {
                    putExtra("sms_body", parsed.message)
                }
                context.startActivity(intent)
            }
            is ParsedCode.Phone -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${parsed.phoneNumber}"))
                context.startActivity(intent)
            }
            is ParsedCode.Location -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${parsed.latitude},${parsed.longitude}?q=${parsed.latitude},${parsed.longitude}"))
                context.startActivity(intent)
            }
            is ParsedCode.Payment -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(raw))
                context.startActivity(intent)
            }
            is ParsedCode.Contact -> {
                val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
                    putExtra(ContactsContract.Intents.Insert.NAME, parsed.name)
                    putExtra(ContactsContract.Intents.Insert.PHONE, parsed.phone)
                    putExtra(ContactsContract.Intents.Insert.EMAIL, parsed.email)
                    putExtra(ContactsContract.Intents.Insert.COMPANY, parsed.organization)
                }
                context.startActivity(intent)
            }
            is ParsedCode.Event -> {
                val intent = Intent(Intent.ACTION_INSERT).apply {
                    data = CalendarContract.Events.CONTENT_URI
                    putExtra(CalendarContract.Events.TITLE, parsed.title)
                    putExtra(CalendarContract.Events.EVENT_LOCATION, parsed.location)
                    putExtra(CalendarContract.Events.DESCRIPTION, parsed.description)
                }
                context.startActivity(intent)
            }
            else -> {
                CodeExporter.copyTextToClipboard(context, "Salim Code", raw)
            }
        }
    } catch (_: Exception) {
        CodeExporter.copyTextToClipboard(context, "Salim Code", raw)
    }
}

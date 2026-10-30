package com.salim.qr.ui.screens.create

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.qr.R
import com.salim.qr.data.datastore.SalimSettings
import com.salim.qr.data.db.CodeRecord
import com.salim.qr.data.db.SalimDatabase
import com.salim.qr.ui.components.GlassBackButton
import com.salim.qr.ui.components.SalimBackground
import com.salim.qr.ui.components.SalimButton
import com.salim.qr.ui.components.SalimWatermark
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimBodyGray
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimSubtitleGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateFormScreen(
    type: DestinationType,
    settings: SalimSettings,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { SalimDatabase.getInstance(context) }
    val scrollState = rememberScrollState()

    // Form field states
    var websiteUrl by remember { mutableStateOf("") }
    var textMessage by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    var smsNumber by remember { mutableStateOf("") }
    var smsMessage by remember { mutableStateOf("") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var wifiSecurity by remember { mutableStateOf("WPA") }
    var wifiHidden by remember { mutableStateOf(false) }
    var wifiPasswordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactOrg by remember { mutableStateOf("") }
    var locationLat by remember { mutableStateOf("") }
    var locationLon by remember { mutableStateOf("") }
    var eventTitle by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var eventDesc by remember { mutableStateOf("") }
    var paymentVpa by remember { mutableStateOf("") }
    var paymentName by remember { mutableStateOf("") }
    var paymentAmount by remember { mutableStateOf("") }
    var barcodeValue by remember { mutableStateOf("") }
    var barcodeFormat by remember { mutableStateOf("CODE_128") }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIdx = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        if (nameIdx >= 0) contactName = it.getString(nameIdx) ?: ""
                        val idIdx = it.getColumnIndex(ContactsContract.Contacts._ID)
                        if (idIdx >= 0) {
                            val id = it.getString(idIdx)
                            val phoneCursor = context.contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(id),
                                null
                            )
                            phoneCursor?.use { pCur ->
                                if (pCur.moveToFirst()) {
                                    val pIdx = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    if (pIdx >= 0) contactPhone = pCur.getString(pIdx) ?: ""
                                }
                            }
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    // Result modal state
    var showResultModal by remember { mutableStateOf(false) }
    var generatedPayload by remember { mutableStateOf("") }
    var generatedTitle by remember { mutableStateOf("") }

    // Validation
    val isFormValid = when (type) {
        DestinationType.WEBSITE -> websiteUrl.trim().isNotBlank()
        DestinationType.TEXT -> textMessage.trim().isNotBlank()
        DestinationType.EMAIL -> emailAddress.trim().contains("@")
        DestinationType.SMS -> smsNumber.trim().isNotBlank()
        DestinationType.WIFI -> wifiSsid.trim().isNotBlank()
        DestinationType.PHONE -> phoneNumber.trim().isNotBlank()
        DestinationType.CONTACT -> contactName.trim().isNotBlank()
        DestinationType.LOCATION -> locationLat.trim().isNotBlank() && locationLon.trim().isNotBlank()
        DestinationType.EVENT -> eventTitle.trim().isNotBlank()
        DestinationType.PAYMENT -> paymentVpa.trim().contains("@")
        DestinationType.BARCODE -> barcodeValue.trim().isNotBlank()
    }

    SalimBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            // Top Right Decorative QR Watermark
            SalimWatermark(
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // Glass Back Button top left
            GlassBackButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = DesignSpec.BackButtonLeftMargin, top = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 76.dp)
                    .padding(horizontal = DesignSpec.PageMarginContent)
                    .verticalScroll(scrollState)
            ) {
                // Headline: "Make my code" and "scan to [Type]"
                Text(
                    text = stringResource(R.string.create_make_my_code),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = SalimNavy
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.create_scan_to) + " ",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = SalimNavy
                    )
                    Text(
                        text = type.title,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = SalimIndigo
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.create_auto_hint),
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = SalimSubtitleGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input Card Container
                Card(
                    shape = RoundedCornerShape(DesignSpec.FormCardRadius),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        when (type) {
                            DestinationType.WEBSITE -> {
                                WebsiteFormFields(
                                    url = websiteUrl,
                                    onUrlChange = { websiteUrl = it },
                                    onPaste = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                                        if (!text.isNullOrBlank()) websiteUrl = text
                                    }
                                )
                            }
                            DestinationType.TEXT -> {
                                TextFormFields(
                                    text = textMessage,
                                    onTextChange = { textMessage = it }
                                )
                            }
                            DestinationType.EMAIL -> {
                                EmailFormFields(
                                    to = emailAddress,
                                    subject = emailSubject,
                                    body = emailBody,
                                    onToChange = { emailAddress = it },
                                    onSubjectChange = { emailSubject = it },
                                    onBodyChange = { emailBody = it }
                                )
                            }
                            DestinationType.SMS -> {
                                SmsFormFields(
                                    number = smsNumber,
                                    message = smsMessage,
                                    onNumberChange = { smsNumber = it },
                                    onMessageChange = { smsMessage = it }
                                )
                            }
                            DestinationType.WIFI -> {
                                WifiFormFields(
                                    ssid = wifiSsid,
                                    password = wifiPassword,
                                    security = wifiSecurity,
                                    hidden = wifiHidden,
                                    passwordVisible = wifiPasswordVisible,
                                    onSsidChange = { wifiSsid = it },
                                    onPasswordChange = { wifiPassword = it },
                                    onSecurityChange = { wifiSecurity = it },
                                    onHiddenChange = { wifiHidden = it },
                                    onTogglePasswordVisibility = { wifiPasswordVisible = !wifiPasswordVisible }
                                )
                            }
                            DestinationType.PHONE -> {
                                StandardField(
                                    label = stringResource(R.string.field_phone_number),
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    keyboardType = KeyboardType.Phone
                                )
                            }
                            DestinationType.CONTACT -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.type_contact),
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = DesignSpec.FormLabelSize,
                                        color = SalimNavy
                                    )
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { contactPickerLauncher.launch(null) }
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Import Contact",
                                            fontFamily = PoppinsFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = SalimIndigo
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                StandardField(label = stringResource(R.string.field_contact_name), value = contactName, onValueChange = { contactName = it })
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = stringResource(R.string.field_contact_phone), value = contactPhone, onValueChange = { contactPhone = it }, keyboardType = KeyboardType.Phone)
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = stringResource(R.string.field_contact_email), value = contactEmail, onValueChange = { contactEmail = it }, keyboardType = KeyboardType.Email)
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = stringResource(R.string.field_contact_org), value = contactOrg, onValueChange = { contactOrg = it })
                            }
                            DestinationType.LOCATION -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.type_location),
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = DesignSpec.FormLabelSize,
                                        color = SalimNavy
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                StandardField(label = stringResource(R.string.field_location_lat), value = locationLat, onValueChange = { locationLat = it }, keyboardType = KeyboardType.Decimal)
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = stringResource(R.string.field_location_lon), value = locationLon, onValueChange = { locationLon = it }, keyboardType = KeyboardType.Decimal)
                            }
                            DestinationType.EVENT -> {
                                StandardField(label = stringResource(R.string.field_event_title), value = eventTitle, onValueChange = { eventTitle = it })
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = stringResource(R.string.field_event_location), value = eventLocation, onValueChange = { eventLocation = it })
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = "Description", value = eventDesc, onValueChange = { eventDesc = it })
                            }
                            DestinationType.PAYMENT -> {
                                StandardField(label = stringResource(R.string.field_payment_vpa), value = paymentVpa, onValueChange = { paymentVpa = it })
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = stringResource(R.string.field_payment_name), value = paymentName, onValueChange = { paymentName = it })
                                Spacer(modifier = Modifier.height(12.dp))
                                StandardField(label = stringResource(R.string.field_payment_amount), value = paymentAmount, onValueChange = { paymentAmount = it }, keyboardType = KeyboardType.Decimal)
                            }
                            DestinationType.BARCODE -> {
                                Text(
                                    text = "Barcode Format",
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = DesignSpec.FormLabelSize,
                                    color = SalimNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val formats = listOf("CODE_128", "EAN_13", "UPC_A", "EAN_8", "CODE_39", "PDF_417")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    formats.take(3).forEach { fmt ->
                                        val isSelected = barcodeFormat == fmt
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) SalimIndigo else Color(0xFFF3F4FB))
                                                .clickable { barcodeFormat = fmt },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = fmt.replace("_", " "),
                                                fontFamily = PoppinsFontFamily,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else SalimNavy
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    formats.drop(3).forEach { fmt ->
                                        val isSelected = barcodeFormat == fmt
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) SalimIndigo else Color(0xFFF3F4FB))
                                                .clickable { barcodeFormat = fmt },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = fmt.replace("_", " "),
                                                fontFamily = PoppinsFontFamily,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else SalimNavy
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                StandardField(
                                    label = stringResource(R.string.field_barcode_data),
                                    value = barcodeValue,
                                    onValueChange = { barcodeValue = it },
                                    keyboardType = if (barcodeFormat.startsWith("EAN") || barcodeFormat.startsWith("UPC")) KeyboardType.Number else KeyboardType.Text
                                )

                                // EAN-13 Check Digit Helper
                                if (barcodeFormat == "EAN_13" && barcodeValue.length == 12 && barcodeValue.all { it.isDigit() }) {
                                    val checkDigit = calculateEan13CheckDigit(barcodeValue)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEEF2FF))
                                            .clickable { barcodeValue += checkDigit }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Append check digit ($checkDigit) -> ${barcodeValue}$checkDigit",
                                            fontFamily = PoppinsFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = SalimIndigo
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary Button: "Generate QR Code"
                SalimButton(
                    text = stringResource(R.string.create_button_generate),
                    enabled = isFormValid,
                    onClick = {
                        val (payload, title) = buildPayload(
                            type = type,
                            url = websiteUrl,
                            text = textMessage,
                            email = emailAddress,
                            emailSub = emailSubject,
                            emailBody = emailBody,
                            smsNum = smsNumber,
                            smsMsg = smsMessage,
                            wifiS = wifiSsid,
                            wifiP = wifiPassword,
                            wifiSec = wifiSecurity,
                            wifiH = wifiHidden,
                            phone = phoneNumber,
                            name = contactName,
                            cPhone = contactPhone,
                            cEmail = contactEmail,
                            cOrg = contactOrg,
                            lat = locationLat,
                            lon = locationLon,
                            evTitle = eventTitle,
                            evLoc = eventLocation,
                            evDesc = eventDesc,
                            vpa = paymentVpa,
                            payee = paymentName,
                            amt = paymentAmount,
                            barcode = barcodeValue
                        )

                        generatedPayload = payload
                        generatedTitle = title

                        // Save to history automatically if enabled
                        if (settings.saveCreatedToHistory) {
                            scope.launch(Dispatchers.IO) {
                                db.codeRecordDao().insert(
                                    CodeRecord(
                                        origin = "created",
                                        payload = payload,
                                        format = if (type == DestinationType.BARCODE) barcodeFormat else "QR_CODE",
                                        parsedType = type.title,
                                        title = title
                                    )
                                )
                            }
                        }

                        showResultModal = true
                    },
                    testTag = "generate_qr_code_button"
                )

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    if (showResultModal) {
        ResultModal(
            payload = generatedPayload,
            title = generatedTitle,
            typeTitle = type.title,
            onBack = { showResultModal = false }
        )
    }
}

@Composable
private fun WebsiteFormFields(
    url: String,
    onUrlChange: (String) -> Unit,
    onPaste: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.field_website_url),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = DesignSpec.FormLabelSize,
                color = SalimNavy
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onPaste() }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentPaste,
                    contentDescription = "Paste",
                    tint = SalimIndigo,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Paste",
                    fontFamily = PoppinsFontFamily,
                    fontSize = 11.sp,
                    color = SalimIndigo
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            placeholder = { Text("https://", color = SalimSubtitleGray) },
            singleLine = false,
            minLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .height(DesignSpec.FormInputMinHeight)
                .testTag("input_website_url"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SalimIndigo,
                unfocusedTextColor = SalimNavy,
                focusedBorderColor = SalimIndigo,
                unfocusedBorderColor = Color(0xFFE4E7FC)
            )
        )
    }
}

@Composable
private fun TextFormFields(
    text: String,
    onTextChange: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.field_text_content),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = DesignSpec.FormLabelSize,
                color = SalimNavy
            )
            Text(
                text = "${text.length} / 1200",
                fontFamily = PoppinsFontFamily,
                fontSize = 11.sp,
                color = SalimSubtitleGray
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { if (it.length <= 1200) onTextChange(it) },
            singleLine = false,
            minLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .height(DesignSpec.FormInputMinHeight)
                .testTag("input_plain_text"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SalimNavy,
                unfocusedTextColor = SalimNavy,
                focusedBorderColor = SalimIndigo,
                unfocusedBorderColor = Color(0xFFE4E7FC)
            )
        )
    }
}

@Composable
private fun EmailFormFields(
    to: String,
    subject: String,
    body: String,
    onToChange: (String) -> Unit,
    onSubjectChange: (String) -> Unit,
    onBodyChange: (String) -> Unit
) {
    Column {
        StandardField(label = stringResource(R.string.field_email_address), value = to, onValueChange = onToChange, keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(12.dp))
        StandardField(label = stringResource(R.string.field_email_subject), value = subject, onValueChange = onSubjectChange)
        Spacer(modifier = Modifier.height(12.dp))
        StandardField(label = stringResource(R.string.field_email_body), value = body, onValueChange = onBodyChange, minLines = 3)
    }
}

@Composable
private fun SmsFormFields(
    number: String,
    message: String,
    onNumberChange: (String) -> Unit,
    onMessageChange: (String) -> Unit
) {
    Column {
        StandardField(label = stringResource(R.string.field_sms_number), value = number, onValueChange = onNumberChange, keyboardType = KeyboardType.Phone)
        Spacer(modifier = Modifier.height(12.dp))
        StandardField(label = stringResource(R.string.field_sms_message), value = message, onValueChange = onMessageChange, minLines = 3)
    }
}

@Composable
private fun WifiFormFields(
    ssid: String,
    password: String,
    security: String,
    hidden: Boolean,
    passwordVisible: Boolean,
    onSsidChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSecurityChange: (String) -> Unit,
    onHiddenChange: (Boolean) -> Unit,
    onTogglePasswordVisibility: () -> Unit
) {
    Column {
        StandardField(label = stringResource(R.string.field_wifi_ssid), value = ssid, onValueChange = onSsidChange)
        Spacer(modifier = Modifier.height(12.dp))

        // Security Selector
        Text(
            text = stringResource(R.string.field_wifi_security),
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = DesignSpec.FormLabelSize,
            color = SalimNavy
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("WPA", "WEP", "nopass").forEach { sec ->
                val isSelected = security == sec
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SalimIndigo else Color(0xFFF3F4FB))
                        .clickable { onSecurityChange(sec) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (sec == "nopass") "None" else sec,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else SalimNavy
                    )
                }
            }
        }

        if (security != "nopass") {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.field_wifi_password),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = DesignSpec.FormLabelSize,
                color = SalimNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Toggle password visibility",
                        tint = SalimSubtitleGray,
                        modifier = Modifier.clickable { onTogglePasswordVisibility() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SalimNavy,
                    unfocusedTextColor = SalimNavy,
                    focusedBorderColor = SalimIndigo,
                    unfocusedBorderColor = Color(0xFFE4E7FC)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.field_wifi_hidden),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = DesignSpec.FormLabelSize,
                color = SalimNavy
            )
            Switch(
                checked = hidden,
                onCheckedChange = onHiddenChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SalimIndigo
                )
            )
        }
    }
}

@Composable
private fun StandardField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = DesignSpec.FormLabelSize,
            color = SalimNavy
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = minLines == 1,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SalimNavy,
                unfocusedTextColor = SalimNavy,
                focusedBorderColor = SalimIndigo,
                unfocusedBorderColor = Color(0xFFE4E7FC)
            )
        )
    }
}

private fun buildPayload(
    type: DestinationType,
    url: String,
    text: String,
    email: String,
    emailSub: String,
    emailBody: String,
    smsNum: String,
    smsMsg: String,
    wifiS: String,
    wifiP: String,
    wifiSec: String,
    wifiH: Boolean,
    phone: String,
    name: String,
    cPhone: String,
    cEmail: String,
    cOrg: String,
    lat: String,
    lon: String,
    evTitle: String,
    evLoc: String,
    evDesc: String,
    vpa: String,
    payee: String,
    amt: String,
    barcode: String
): Pair<String, String> {
    return when (type) {
        DestinationType.WEBSITE -> {
            val fullUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
            Pair(fullUrl, fullUrl)
        }
        DestinationType.TEXT -> Pair(text, text.take(24))
        DestinationType.EMAIL -> {
            val mailto = "mailto:$email?subject=${android.net.Uri.encode(emailSub)}&body=${android.net.Uri.encode(emailBody)}"
            Pair(mailto, email)
        }
        DestinationType.SMS -> {
            val smsto = "smsto:$smsNum:$smsMsg"
            Pair(smsto, smsNum)
        }
        DestinationType.WIFI -> {
            val escapedSsid = escapeWifi(wifiS)
            val escapedPass = escapeWifi(wifiP)
            val wifiStr = "WIFI:T:$wifiSec;S:$escapedSsid;P:$escapedPass;H:$wifiH;;"
            Pair(wifiStr, wifiS)
        }
        DestinationType.PHONE -> Pair("tel:$phone", phone)
        DestinationType.CONTACT -> {
            val vcard = "BEGIN:VCARD\nVERSION:3.0\nFN:$name\nTEL:$cPhone\nEMAIL:$cEmail\nORG:$cOrg\nEND:VCARD"
            Pair(vcard, name)
        }
        DestinationType.LOCATION -> {
            val geo = "geo:$lat,$lon?q=$lat,$lon"
            Pair(geo, "$lat, $lon")
        }
        DestinationType.EVENT -> {
            val vevent = "BEGIN:VEVENT\nSUMMARY:$evTitle\nLOCATION:$evLoc\nDESCRIPTION:$evDesc\nEND:VEVENT"
            Pair(vevent, evTitle)
        }
        DestinationType.PAYMENT -> {
            val upi = "upi://pay?pa=$vpa&pn=${android.net.Uri.encode(payee)}&am=$amt"
            Pair(upi, payee.ifBlank { vpa })
        }
        DestinationType.BARCODE -> Pair(barcode, barcode)
    }
}

private fun escapeWifi(input: String): String {
    return input.replace("\\", "\\\\")
        .replace(";", "\\;")
        .replace(":", "\\:")
        .replace(",", "\\,")
        .replace("\"", "\\\"")
}

private fun calculateEan13CheckDigit(digits12: String): Int {
    var sum = 0
    for (i in 0 until 12) {
        val d = digits12[i].digitToInt()
        sum += if (i % 2 == 0) d else d * 3
    }
    val rem = sum % 10
    return if (rem == 0) 0 else 10 - rem
}

package com.salim.qr.ui.screens.create

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import com.salim.qr.ui.theme.TypeBarcodeGradient
import com.salim.qr.ui.theme.TypeContactGradient
import com.salim.qr.ui.theme.TypeEmailGradient
import com.salim.qr.ui.theme.TypeEventGradient
import com.salim.qr.ui.theme.TypeLocationGradient
import com.salim.qr.ui.theme.TypePaymentGradient
import com.salim.qr.ui.theme.TypePhoneGradient
import com.salim.qr.ui.theme.TypeSmsGradient
import com.salim.qr.ui.theme.TypeTextGradient
import com.salim.qr.ui.theme.TypeWebsiteGradient
import com.salim.qr.ui.theme.TypeWifiGradient

enum class DestinationType(
    val title: String,
    val icon: ImageVector,
    val gradient: Brush
) {
    WEBSITE("Website", Icons.Outlined.Language, TypeWebsiteGradient),
    TEXT("Text", Icons.Outlined.Description, TypeTextGradient),
    EMAIL("Email", Icons.Outlined.AlternateEmail, TypeEmailGradient),
    SMS("SMS", Icons.Outlined.Sms, TypeSmsGradient),
    WIFI("Wifi", Icons.Outlined.Wifi, TypeWifiGradient),
    PHONE("Phone", Icons.Outlined.Phone, TypePhoneGradient),
    CONTACT("Contact", Icons.Outlined.Person, TypeContactGradient),
    LOCATION("Location", Icons.Outlined.LocationOn, TypeLocationGradient),
    EVENT("Event", Icons.Outlined.CalendarMonth, TypeEventGradient),
    PAYMENT("Payment", Icons.Outlined.Payment, TypePaymentGradient),
    BARCODE("Barcode", Icons.Outlined.QrCode, TypeBarcodeGradient)
}

package com.salim.qr.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary brand colors from design specification
val SalimIndigo = Color(0xFF5B6CF0)
val SalimNavy = Color(0xFF1E2347)
val SalimBodyGray = Color(0xFF6F7391)
val SalimInactiveTab = Color(0xFFB9BDE0)
val SalimDangerRed = Color(0xFFFF6B6B)
val SalimSubtitleGray = Color(0xFF8A8FA8)
val SalimWatermarkColor = Color(0xFFC5C8F0)

// Glass tokens
val SalimGlassWhite = Color(0xB8FFFFFF)
val SalimGlassBorder = Color(0xB2FFFFFF)
val SalimGlassShadow = Color(0x1A5B6CF0)

// Background gradient points
val SalimBgGlowTopLeft = Color(0xFFF3DDF0)
val SalimBgMiddle = Color(0xFFE9E6F7)
val SalimBgGlowBottomRight = Color(0xFFD6D8F5)

// Dark theme background tokens
val SalimDarkBgTop = Color(0xFF14172E)
val SalimDarkBgBottom = Color(0xFF1E2147)
val SalimDarkGlass = Color(0x2EFFFFFF)
val SalimDarkGlassBorder = Color(0x3DFFFFFF)

// Primary button gradient: #6B73F0 to #8B7CF6
val SalimPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF6B73F0), Color(0xFF8B7CF6))
)

// Scan line gradient: #6B73F0 to #9B5CF0
val SalimScanLineGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0x005B6CF0),
        Color(0xCC6B73F0),
        Color(0xFF9B5CF0),
        Color(0xCC6B73F0),
        Color(0x005B6CF0)
    )
)

// Destination type tile gradients
val TypeWebsiteGradient = Brush.verticalGradient(listOf(Color(0xFF6FA8FF), Color(0xFF4F7CF0)))
val TypeTextGradient = Brush.verticalGradient(listOf(Color(0xFFFFB070), Color(0xFFF58A4B)))
val TypeEmailGradient = Brush.verticalGradient(listOf(Color(0xFF6ED3E8), Color(0xFF38B7D8)))
val TypeSmsGradient = Brush.verticalGradient(listOf(Color(0xFFFF8FB8), Color(0xFFF2609A)))
val TypeWifiGradient = Brush.verticalGradient(listOf(Color(0xFFC58BFF), Color(0xFF9B5CF0)))
val TypePhoneGradient = Brush.verticalGradient(listOf(Color(0xFF50E3C2), Color(0xFF10B981)))
val TypeContactGradient = Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
val TypeLocationGradient = Brush.verticalGradient(listOf(Color(0xFFEC4899), Color(0xFFBE185D)))
val TypeEventGradient = Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)))
val TypePaymentGradient = Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)))
val TypeBarcodeGradient = Brush.verticalGradient(listOf(Color(0xFF64748B), Color(0xFF334155)))

package com.salim.qr.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Single source of truth for all exact dimensions, sizes, and spacings specified in Section 3
 * of the Salim QR Design Specification.
 */
object DesignSpec {
    // Section 3: Reference Frame
    val ReferenceScreenWidth: Dp = 390.dp
    val ReferenceScreenHeight: Dp = 844.dp
    val MaxContentWidthTablet: Dp = 560.dp

    // Section 3.1: Global elements
    // "Decorative QR watermark: large pale-lavender QR pattern (#C5C8F0 at about 60%, about 163 x 166dp)"
    val WatermarkWidth: Dp = 163.dp
    val WatermarkHeight: Dp = 166.dp
    const val WatermarkAlpha: Float = 0.60f

    // "Glass back button: a 44dp rounded square (radius 14dp) glass button with an indigo left chevron"
    val BackButtonSize: Dp = 44.dp
    val BackButtonRadius: Dp = 14.dp
    val BackButtonLeftMargin: Dp = 20.dp

    // "Bottom tab bar: four equal items... about 64dp tall, radius 24dp, 8dp from left and right edges, about 22dp above bottom edge"
    val BottomBarHeight: Dp = 64.dp
    val BottomBarCornerRadius: Dp = 24.dp
    val BottomBarHorizontalMargin: Dp = 8.dp
    val BottomBarBottomMargin: Dp = 22.dp
    val TabIconSize: Dp = 22.dp
    val TabLabelSize: TextUnit = 11.sp
    val TabMinTouchTarget: Dp = 48.dp

    // "Primary button: full-width pill, indigo gradient, white 16sp medium text, height 56dp, radius 28dp"
    val PrimaryButtonHeight: Dp = 56.dp
    val PrimaryButtonRadius: Dp = 28.dp
    val PrimaryButtonTextSize: TextUnit = 16.sp

    // "Page margins: 20dp left and right for lists and forms, 24dp for onboarding text"
    val PageMarginContent: Dp = 20.dp
    val PageMarginOnboarding: Dp = 24.dp
    val GridSpacing8: Dp = 8.dp
    val GridSpacing16: Dp = 16.dp

    // Section 3.2: Wordmark
    // "Salim in navy #1E2347 semibold followed by QR in indigo #5B6CF0 bold, about 14sp"
    val WordmarkSize: TextUnit = 14.sp
    val WordmarkLeftMargin: Dp = 24.dp

    // Section 3.3: Onboarding
    val OnboardingSkipTopMargin: Dp = 75.dp
    val OnboardingSkipRightMargin: Dp = 24.dp
    val OnboardingTitleSize: TextUnit = 30.sp
    val OnboardingBodySize: TextUnit = 13.sp
    val OnboardingDockHeight: Dp = 72.dp
    val OnboardingDockMargin: Dp = 16.dp
    val OnboardingDockRadius: Dp = 28.dp
    val OnboardingDashWidth: Dp = 18.dp
    val OnboardingDashHeight: Dp = 4.dp
    val OnboardingDotSize: Dp = 4.dp

    // Section 3.4: Scan QR screen
    // "Camera flip button: 52dp rounded-square glass button (radius 16dp, 20dp from right)"
    val FlipButtonSize: Dp = 52.dp
    val FlipButtonRadius: Dp = 16.dp
    val FlipButtonRightMargin: Dp = 20.dp

    // "Scan frame: centered horizontally, about 232 x 232dp, rounded 20dp corners, 4 corner brackets 29dp long, 4dp stroke"
    val ScanFrameSize: Dp = 232.dp
    val ScanFrameRadius: Dp = 20.dp
    val ScanBracketLength: Dp = 29.dp
    val ScanBracketStroke: Dp = 4.dp
    val ScanBracketInnerRadius: Dp = 8.dp
    val ScanLineThickness: Dp = 10.dp

    // "Extra controls: centered row of three round 48dp glass buttons (Torch, Gallery, Zoom)"
    val ScanExtraButtonSize: Dp = 48.dp

    // Section 3.5: Create QR screen
    // "Destination list: glass cards 20dp from each edge, about 76dp tall, radius 24dp, 16dp apart"
    val DestinationCardHeight: Dp = 76.dp
    val DestinationCardRadius: Dp = 24.dp
    val DestinationCardGap: Dp = 16.dp
    val DestinationTypeTileSize: Dp = 52.dp
    val DestinationTypeTileRadius: Dp = 16.dp
    val DestinationTypeNameSize: TextUnit = 16.sp

    // Section 3.6: Create form
    val FormCardRadius: Dp = 16.dp
    val FormInputMinHeight: Dp = 140.dp
    val FormLabelSize: TextUnit = 13.sp

    // Section 3.7: Result modal
    // "Modal card: about 340dp wide, radius 28dp"
    val ResultModalWidth: Dp = 340.dp
    val ResultModalRadius: Dp = 28.dp
    val ResultCodeTileSize: Dp = 200.dp
    val ResultActionButtonSize: Dp = 48.dp

    // Section 3.8: Recent screen
    // "List rows: glass cards 16dp from edges, about 69dp tall, radius 16dp, 11dp apart"
    val RecentRowHeight: Dp = 69.dp
    val RecentRowRadius: Dp = 16.dp
    val RecentRowSideMargin: Dp = 16.dp
    val RecentRowGap: Dp = 11.dp
    val RecentTypeTileSize: Dp = 28.dp
    val RecentTypeTileRadius: Dp = 8.dp
    val RecentDeleteButtonSize: Dp = 44.dp
    val RecentDeleteButtonRadius: Dp = 12.dp

    // Section 3.9: Scan result bottom sheet
    val BottomSheetRadius: Dp = 32.dp
}

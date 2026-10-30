package com.salim.qr.ui.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.qr.R
import com.salim.qr.ui.components.SalimBackground
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimBodyGray
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimPrimaryGradient
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    SalimBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Skip Button at top right: 24dp from right, about 75dp from top
            Text(
                text = stringResource(R.string.onboarding_skip),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = SalimNavy,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = DesignSpec.OnboardingSkipRightMargin)
                    .clickable { onFinish() }
                    .testTag("onboarding_skip_button")
            )

            // Horizontal Pager for 3 pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> OnboardingPageOne()
                    1 -> OnboardingPageTwo()
                    2 -> OnboardingPageThree()
                }
            }

            // Bottom Dock Button (all pages): indigo-gradient rounded bar, 72dp tall, 16dp from sides and bottom
            val dockLabel = when (pagerState.currentPage) {
                0 -> stringResource(R.string.onboarding_dock_1)
                1 -> stringResource(R.string.onboarding_dock_2)
                else -> stringResource(R.string.onboarding_dock_3)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = DesignSpec.OnboardingDockMargin,
                        end = DesignSpec.OnboardingDockMargin,
                        bottom = 24.dp
                    )
                    .fillMaxWidth()
                    .height(DesignSpec.OnboardingDockHeight)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(DesignSpec.OnboardingDockRadius),
                        ambientColor = SalimIndigo.copy(alpha = 0.35f),
                        spotColor = SalimIndigo.copy(alpha = 0.45f)
                    )
                    .clip(RoundedCornerShape(DesignSpec.OnboardingDockRadius))
                    .background(SalimPrimaryGradient)
                    .clickable {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish()
                        }
                    }
                    .testTag("onboarding_dock_button")
                    .padding(horizontal = 24.dp)
            ) {
                // Left progress indicator: current page is white elongated dash (18x4dp), others are 4dp dots
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 0 until 3) {
                        val isSelected = pagerState.currentPage == i
                        val width by animateDpAsState(
                            targetValue = if (isSelected) DesignSpec.OnboardingDashWidth else DesignSpec.OnboardingDotSize,
                            label = "indicator_width"
                        )
                        Box(
                            modifier = Modifier
                                .height(DesignSpec.OnboardingDashHeight)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                        )
                    }
                }

                // Right label and arrow
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = dockLabel,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageOne() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = DesignSpec.PageMarginOnboarding)
                .padding(top = 90.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_title_1),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = DesignSpec.OnboardingTitleSize,
                lineHeight = 38.sp,
                color = SalimNavy
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.onboarding_body_1),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = DesignSpec.OnboardingBodySize,
                lineHeight = 20.sp,
                color = SalimBodyGray,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }

        // 3D Avatar character illustration in lower right
        Image(
            painter = painterResource(id = R.drawable.img_onboarding_avatar),
            contentDescription = "Onboarding Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(270.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 24.dp, y = (-76).dp)
                .clip(RoundedCornerShape(32.dp))
        )
    }
}

@Composable
private fun OnboardingPageTwo() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Large pale-blue QR graphic in top left crossed by scan band
        Canvas(
            modifier = Modifier
                .size(240.dp)
                .offset(x = (-30).dp, y = 30.dp)
        ) {
            val color = Color(0xFFC5D4F6)
            val strokeWidth = 6f
            val module = size.width / 8f

            drawRoundRect(
                color = color,
                topLeft = Offset(40f, 40f),
                size = Size(module * 3f, module * 3f),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = strokeWidth)
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(40f + module * 0.75f, 40f + module * 0.75f),
                size = Size(module * 1.5f, module * 1.5f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(module * 4.5f, 50f),
                size = Size(module * 2.5f, module * 2f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(60f, module * 4.5f),
                size = Size(module * 2.5f, module * 2.5f),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Translucent purple-indigo scan band crossing through
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0x006B73F0), Color(0xCC7C6CF5), Color(0x006B73F0))
                ),
                topLeft = Offset(0f, size.height * 0.45f),
                size = Size(size.width, 24f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = DesignSpec.PageMarginOnboarding)
                .align(Alignment.CenterStart)
                .offset(y = 60.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_title_2),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = DesignSpec.OnboardingTitleSize,
                lineHeight = 38.sp,
                color = SalimNavy
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.onboarding_body_2),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = DesignSpec.OnboardingBodySize,
                lineHeight = 20.sp,
                color = SalimBodyGray,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}

@Composable
private fun OnboardingPageThree() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = DesignSpec.PageMarginOnboarding)
                .padding(top = 90.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_title_3),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = DesignSpec.OnboardingTitleSize,
                lineHeight = 38.sp,
                color = SalimNavy
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.onboarding_body_3),
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = DesignSpec.OnboardingBodySize,
                lineHeight = 20.sp,
                color = SalimBodyGray,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }

        // 3D Stack of folders in lower right
        Image(
            painter = painterResource(id = R.drawable.img_onboarding_folders),
            contentDescription = "Onboarding Folders",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = (-76).dp)
                .clip(RoundedCornerShape(32.dp))
        )
    }
}

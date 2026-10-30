package com.salim.qr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.salim.qr.ui.theme.SalimBgGlowBottomRight
import com.salim.qr.ui.theme.SalimBgGlowTopLeft
import com.salim.qr.ui.theme.SalimBgMiddle
import com.salim.qr.ui.theme.SalimDarkBgBottom
import com.salim.qr.ui.theme.SalimDarkBgTop

@Composable
fun SalimBackground(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val brush = if (darkTheme) {
        Brush.linearGradient(
            colors = listOf(SalimDarkBgTop, SalimDarkBgBottom),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(SalimBgGlowTopLeft, SalimBgMiddle, SalimBgGlowBottomRight),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    ) {
        content()
    }
}

package com.salim.qr.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.salim.qr.ui.theme.SalimDarkGlass
import com.salim.qr.ui.theme.SalimDarkGlassBorder
import com.salim.qr.ui.theme.SalimGlassBorder
import com.salim.qr.ui.theme.SalimGlassShadow
import com.salim.qr.ui.theme.SalimGlassWhite

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 6.dp,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable BoxScope.() -> Unit
) {
    val shape: Shape = RoundedCornerShape(cornerRadius)
    val bgColor = if (darkTheme) SalimDarkGlass else SalimGlassWhite
    val borderColor = if (darkTheme) SalimDarkGlassBorder else SalimGlassBorder

    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = SalimGlassShadow,
                spotColor = SalimGlassShadow
            ),
        shape = shape,
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box {
            content()
        }
    }
}

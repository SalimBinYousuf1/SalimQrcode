package com.salim.qr.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.SalimDarkGlass
import com.salim.qr.ui.theme.SalimDarkGlassBorder
import com.salim.qr.ui.theme.SalimGlassBorder
import com.salim.qr.ui.theme.SalimGlassShadow
import com.salim.qr.ui.theme.SalimGlassWhite
import com.salim.qr.ui.theme.SalimIndigo

@Composable
fun GlassBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val shape = RoundedCornerShape(DesignSpec.BackButtonRadius)
    val bgColor = if (darkTheme) SalimDarkGlass else SalimGlassWhite
    val borderColor = if (darkTheme) SalimDarkGlassBorder else SalimGlassBorder

    Surface(
        modifier = modifier
            .size(DesignSpec.BackButtonSize)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = SalimGlassShadow,
                spotColor = SalimGlassShadow
            )
            .testTag("glass_back_button"),
        shape = shape,
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .size(DesignSpec.BackButtonSize)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = SalimIndigo,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

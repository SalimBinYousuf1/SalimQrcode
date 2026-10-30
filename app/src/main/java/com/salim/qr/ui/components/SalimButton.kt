package com.salim.qr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimPrimaryGradient

@Composable
fun SalimButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "salim_primary_button"
) {
    val shape = RoundedCornerShape(DesignSpec.PrimaryButtonRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(DesignSpec.PrimaryButtonHeight)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = shape,
                ambientColor = SalimIndigo.copy(alpha = 0.35f),
                spotColor = SalimIndigo.copy(alpha = 0.45f)
            )
            .clip(shape)
            .background(
                if (enabled) SalimPrimaryGradient else androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(Color(0xFFB9BDE0), Color(0xFFC7CAEC))
                )
            )
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = DesignSpec.PrimaryButtonTextSize,
            color = Color.White
        )
    }
}

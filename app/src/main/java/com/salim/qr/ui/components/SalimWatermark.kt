package com.salim.qr.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.SalimWatermarkColor

@Composable
fun SalimWatermark(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(DesignSpec.WatermarkWidth, DesignSpec.WatermarkHeight)
            .offset(x = 24.dp, y = (-20).dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Canvas(modifier = Modifier.size(DesignSpec.WatermarkWidth, DesignSpec.WatermarkHeight)) {
            val color = SalimWatermarkColor.copy(alpha = DesignSpec.WatermarkAlpha)
            val strokeWidth = 5f
            val moduleSize = size.width / 9f

            // Decorative top-right QR pattern elements
            // Outer finder pattern top-right
            val finderSize = moduleSize * 3.5f
            drawRoundRect(
                color = color,
                topLeft = Offset(size.width - finderSize - 10f, 10f),
                size = Size(finderSize, finderSize),
                cornerRadius = CornerRadius(14f, 14f),
                style = Stroke(width = strokeWidth)
            )
            // Inner eye
            drawRoundRect(
                color = color,
                topLeft = Offset(size.width - finderSize - 10f + moduleSize * 0.9f, 10f + moduleSize * 0.9f),
                size = Size(finderSize * 0.45f, finderSize * 0.45f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Outer finder pattern bottom-right
            drawRoundRect(
                color = color,
                topLeft = Offset(size.width - finderSize - 10f, size.height - finderSize - 10f),
                size = Size(finderSize, finderSize),
                cornerRadius = CornerRadius(14f, 14f),
                style = Stroke(width = strokeWidth)
            )

            // Accent blocks
            val blockRadius = CornerRadius(4f, 4f)
            drawRoundRect(
                color = color,
                topLeft = Offset(moduleSize * 1.5f, moduleSize * 1.5f),
                size = Size(moduleSize, moduleSize),
                cornerRadius = blockRadius
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(moduleSize * 3f, moduleSize * 2f),
                size = Size(moduleSize * 1.5f, moduleSize),
                cornerRadius = blockRadius
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(moduleSize * 1f, moduleSize * 4f),
                size = Size(moduleSize * 2f, moduleSize),
                cornerRadius = blockRadius
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(moduleSize * 3.5f, moduleSize * 4.5f),
                size = Size(moduleSize, moduleSize * 2f),
                cornerRadius = blockRadius
            )
        }
    }
}

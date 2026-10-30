package com.salim.qr.ui.screens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.qr.R
import com.salim.qr.ui.components.GlassCard
import com.salim.qr.ui.components.SalimBackground
import com.salim.qr.ui.components.SalimWatermark
import com.salim.qr.ui.components.SalimWordmark
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimInactiveTab
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy

@Composable
fun CreateDestinationScreen(
    onSelectDestination: (DestinationType) -> Unit,
    modifier: Modifier = Modifier
) {
    SalimBackground {
        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Decorative QR Watermark in top right corner
            SalimWatermark(
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Wordmark: 24dp from left, under status bar
                SalimWordmark(
                    modifier = Modifier
                        .padding(start = DesignSpec.WordmarkLeftMargin, top = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Headline: "Choose" in indigo, "your Destination!" in navy, 30sp bold
                Column(
                    modifier = Modifier.padding(horizontal = DesignSpec.PageMarginOnboarding)
                ) {
                    Text(
                        text = stringResource(R.string.create_title_choose),
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = SalimIndigo,
                        lineHeight = 36.sp
                    )
                    Text(
                        text = stringResource(R.string.create_title_destination),
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = SalimNavy,
                        lineHeight = 36.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Destination list: glass cards, 76dp tall, radius 24dp, 16dp apart
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("destination_list"),
                    contentPadding = PaddingValues(
                        start = DesignSpec.PageMarginContent,
                        end = DesignSpec.PageMarginContent,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(DesignSpec.DestinationCardGap)
                ) {
                    items(DestinationType.values()) { item ->
                        DestinationCardItem(
                            destination = item,
                            onClick = { onSelectDestination(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationCardItem(
    destination: DestinationType,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(DesignSpec.DestinationCardHeight)
            .clickable(onClick = onClick)
            .testTag("destination_card_${destination.name.lowercase()}"),
        cornerRadius = DesignSpec.DestinationCardRadius,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 52dp glossy rounded type tile at left with vertical gradient
            Surface(
                modifier = Modifier
                    .size(DesignSpec.DestinationTypeTileSize),
                shape = RoundedCornerShape(DesignSpec.DestinationTypeTileRadius),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(destination.gradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Type Name: 16sp medium navy
            Text(
                text = destination.title,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = DesignSpec.DestinationTypeNameSize,
                color = SalimNavy,
                modifier = Modifier.weight(1f)
            )

            // Thin chevron at right
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SalimInactiveTab,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(24.dp)
            )
        }
    }
}

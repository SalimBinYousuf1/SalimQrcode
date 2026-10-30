package com.salim.qr.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salim.qr.R
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimInactiveTab
import com.salim.qr.ui.theme.SalimIndigo

enum class MainTab {
    CREATE, SCAN, RECENT, SETTINGS
}

@Composable
fun SalimBottomBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .height(DesignSpec.BottomBarHeight)
            .padding(horizontal = DesignSpec.BottomBarHorizontalMargin),
        cornerRadius = DesignSpec.BottomBarCornerRadius,
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(
                label = stringResource(R.string.tab_create),
                icon = Icons.Outlined.Edit,
                isSelected = currentTab == MainTab.CREATE,
                onClick = { onTabSelected(MainTab.CREATE) },
                testTag = "tab_create_qr"
            )
            TabItem(
                label = stringResource(R.string.tab_scan),
                icon = Icons.Outlined.QrCodeScanner,
                isSelected = currentTab == MainTab.SCAN,
                onClick = { onTabSelected(MainTab.SCAN) },
                testTag = "tab_scan_qr"
            )
            TabItem(
                label = stringResource(R.string.tab_recent),
                icon = Icons.Outlined.History,
                isSelected = currentTab == MainTab.RECENT,
                onClick = { onTabSelected(MainTab.RECENT) },
                testTag = "tab_recent"
            )
            TabItem(
                label = stringResource(R.string.tab_settings),
                icon = Icons.Outlined.Settings,
                isSelected = currentTab == MainTab.SETTINGS,
                onClick = { onTabSelected(MainTab.SETTINGS) },
                testTag = "tab_settings"
            )
        }
    }
}

@Composable
private fun TabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val color = if (isSelected) SalimIndigo else SalimInactiveTab
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .size(width = 72.dp, height = DesignSpec.BottomBarHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(DesignSpec.TabIconSize)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontFamily = PoppinsFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = DesignSpec.TabLabelSize,
            color = color,
            maxLines = 1
        )
    }
}

package com.salim.qr.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.salim.qr.R
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy

@Composable
fun SalimWordmark(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("salim_wordmark"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.brand_salim),
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = DesignSpec.WordmarkSize,
            color = SalimNavy
        )
        Text(
            text = " " + stringResource(R.string.brand_qr),
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = DesignSpec.WordmarkSize,
            color = SalimIndigo
        )
    }
}

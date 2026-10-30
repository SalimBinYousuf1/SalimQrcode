package com.salim.qr

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.salim.qr.data.datastore.SalimSettings
import com.salim.qr.data.datastore.SettingsPreferences
import com.salim.qr.ui.components.MainTab
import com.salim.qr.ui.components.SalimBackground
import com.salim.qr.ui.components.SalimBottomBar
import com.salim.qr.ui.components.SalimButton
import com.salim.qr.ui.screens.create.CreateDestinationScreen
import com.salim.qr.ui.screens.create.CreateFormScreen
import com.salim.qr.ui.screens.create.DestinationType
import com.salim.qr.ui.screens.onboarding.OnboardingScreen
import com.salim.qr.ui.screens.recent.RecentScreen
import com.salim.qr.ui.screens.scan.ScanScreen
import com.salim.qr.ui.screens.settings.SettingsScreen
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimSubtitleGray
import com.salim.qr.ui.theme.SalimTheme
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = SettingsPreferences(applicationContext)

        // Check if shared text intent was received
        val sharedText = if (intent?.action == Intent.ACTION_SEND && intent?.type?.startsWith("text/") == true) {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else null

        setContent {
            val settings by preferences.settingsFlow.collectAsState(initial = SalimSettings())
            val scope = rememberCoroutineScope()
            var isAppUnlocked by remember { mutableStateOf(!settings.appLockEnabled) }

            SalimTheme(
                darkTheme = when (settings.themeMode) {
                    "dark" -> true
                    "light" -> false
                    else -> isSystemInDarkTheme()
                }
            ) {
                if (!settings.onboardingCompleted) {
                    OnboardingScreen(
                        onFinish = {
                            scope.launch {
                                preferences.setOnboardingCompleted(true)
                            }
                        }
                    )
                } else if (settings.appLockEnabled && !isAppUnlocked) {
                    AppLockScreen(
                        onAuthenticate = {
                            showBiometricPrompt { success ->
                                if (success) isAppUnlocked = true
                            }
                        }
                    )
                } else {
                    SalimMainApp(
                        settings = settings,
                        preferences = preferences,
                        sharedText = sharedText
                    )
                }
            }
        }
    }

    private fun showBiometricPrompt(onResult: (Boolean) -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onResult(false)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onResult(false)
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Salim")
            .setSubtitle("Confirm your identity to access saved QR codes")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(promptInfo)
    }
}

@Composable
fun AppLockScreen(onAuthenticate: () -> Unit) {
    LaunchedEffect(Unit) {
        onAuthenticate()
    }

    SalimBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SalimIndigo.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Locked",
                        tint = SalimIndigo,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Salim is Locked",
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = SalimNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Authenticate with biometric or screen lock PIN to proceed.",
                    fontFamily = PoppinsFontFamily,
                    fontSize = 13.sp,
                    color = SalimSubtitleGray
                )
                Spacer(modifier = Modifier.height(28.dp))
                SalimButton(
                    text = "Unlock",
                    onClick = onAuthenticate,
                    modifier = Modifier.height(50.dp)
                )
            }
        }
    }
}

@Composable
fun SalimMainApp(
    settings: SalimSettings,
    preferences: SettingsPreferences,
    sharedText: String? = null
) {
    var currentTab by remember { mutableStateOf(if (sharedText != null) MainTab.CREATE else MainTab.SCAN) }
    var selectedCreateDestination by remember {
        mutableStateOf<DestinationType?>(if (sharedText != null) DestinationType.WEBSITE else null)
    }

    // Predictive Back handling
    BackHandler(enabled = selectedCreateDestination != null || currentTab != MainTab.SCAN) {
        if (selectedCreateDestination != null) {
            selectedCreateDestination = null
        } else if (currentTab != MainTab.SCAN) {
            currentTab = MainTab.SCAN
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Active Screen
        when (currentTab) {
            MainTab.SCAN -> {
                ScanScreen(
                    settings = settings,
                    modifier = Modifier.fillMaxSize()
                )
            }
            MainTab.CREATE -> {
                if (selectedCreateDestination == null) {
                    CreateDestinationScreen(
                        onSelectDestination = { selectedCreateDestination = it },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CreateFormScreen(
                        type = selectedCreateDestination!!,
                        settings = settings,
                        onBack = { selectedCreateDestination = null }
                    )
                }
            }
            MainTab.RECENT -> {
                RecentScreen(modifier = Modifier.fillMaxSize())
            }
            MainTab.SETTINGS -> {
                SettingsScreen(
                    settings = settings,
                    preferences = preferences,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating Glass Bottom Bar (shown on all top-level destinations)
        if (selectedCreateDestination == null) {
            SalimBottomBar(
                currentTab = currentTab,
                onTabSelected = { newTab ->
                    selectedCreateDestination = null
                    currentTab = newTab
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = DesignSpec.BottomBarBottomMargin)
            )
        }
    }
}

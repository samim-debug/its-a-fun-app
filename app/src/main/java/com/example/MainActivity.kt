package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonLime
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.CardSlate
import com.example.ui.theme.MetallicSlate
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val viewModel: AppViewModel = viewModel()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val toastMsg by viewModel.toastMessage.collectAsState()
    val paymentResult by viewModel.paymentResult.collectAsState()
    val context = LocalContext.current

    // Observe and display central toasts
    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = ObsidianBackground,
        bottomBar = {
            // Display bottom bar ONLY inside main active states
            val showBottomNav = currentScreen != AppScreen.ONBOARDING && 
                               currentScreen != AppScreen.UPI_PAY && 
                               paymentResult == null
            if (showBottomNav) {
                GlassBottomNavigationBar(
                    selectedScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (currentScreen != AppScreen.ONBOARDING && currentScreen != AppScreen.UPI_PAY && paymentResult == null) 80.dp else 0.dp)
        ) {
            // Master Screen state router
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "MainScreenNavigation"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.ONBOARDING -> OnboardingScreen(viewModel)
                    AppScreen.HOME -> HomeScreen(viewModel)
                    AppScreen.UPI_PAY -> {
                        // Check if payment is verified or currently checking PIN input
                        val activePayment by viewModel.activePayment.collectAsState()
                        if (activePayment != null) {
                            TransactionConfirmationScreen(viewModel)
                        } else {
                            ScanPayScreen(viewModel)
                        }
                    }
                    AppScreen.CHAT_AI -> AIAssistantScreen(viewModel)
                    AppScreen.BILL_SPLIT -> BillSplitScreen(viewModel)
                    AppScreen.ADMIN_DASHBOARD -> AdminScreen(viewModel)
                    else -> HomeScreen(viewModel)
                }
            }

            // Celebrity Success Overlay covers everything when a transfer resolves
            paymentResult?.let {
                Box(modifier = Modifier.fillMaxSize()) {
                    PaymentSuccessScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun GlassBottomNavigationBar(
    selectedScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CardSlate.copy(alpha = 0.85f))
                .border(1.dp, MetallicSlate.copy(alpha = 0.40f), RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isActive = selectedScreen == AppScreen.HOME,
                onClick = { onNavigate(AppScreen.HOME) }
            )

            BottomNavItem(
                icon = Icons.Default.Group,
                label = "Split",
                isActive = selectedScreen == AppScreen.BILL_SPLIT,
                onClick = { onNavigate(AppScreen.BILL_SPLIT) }
            )

            // Centre Floating Scan Button (FamPay highlight action)
            Box(
                modifier = Modifier
                    .offset(y = (-18).dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(NeonLime)
                    .border(4.dp, ObsidianBackground, CircleShape)
                    .clickable { onNavigate(AppScreen.UPI_PAY) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan any QR code",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            BottomNavItem(
                icon = Icons.Default.ChatBubble,
                label = "AI Help",
                isActive = selectedScreen == AppScreen.CHAT_AI,
                onClick = { onNavigate(AppScreen.CHAT_AI) }
            )

            BottomNavItem(
                icon = Icons.Default.Security,
                label = "Secure",
                isActive = selectedScreen == AppScreen.ADMIN_DASHBOARD,
                onClick = { onNavigate(AppScreen.ADMIN_DASHBOARD) }
            )
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isActive) NeonLime else Color.LightGray.copy(alpha = 0.7f)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isActive) FontWeight.Black else FontWeight.Normal,
            color = tint
        )
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionConfirmationScreen(viewModel: AppViewModel) {
    val activePayment by viewModel.activePayment.collectAsState()
    val pinEntered by viewModel.pinEntered.collectAsState()

    Scaffold(
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBackground),
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back", tint = Color.White)
                    }
                },
                title = {
                    Text("Secure Handwriting Handclasp", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Payment Summary
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = "SENDING SECURELY TO",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = activePayment?.name ?: "Unknown Partner",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = activePayment?.upiId ?: "vpa@handle",
                    fontSize = 13.sp,
                    color = NeonLime
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "₹${String.format("%,.2f", activePayment?.amount ?: 0.0)}",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif
                )
                if (activePayment?.note?.isNotEmpty() == true) {
                    Text(
                        text = "“${activePayment?.note}”",
                        fontSize = 14.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // PIN Dots Entry Alerters
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val active = i < pinEntered.length
                        val size by animateDpAsState(if (active) 16.dp else 12.dp, label = "dotSize")
                        val color by animateColorAsState(if (active) NeonLime else MetallicSlate, label = "dotColor")
                        
                        Box(
                            modifier = Modifier
                                .size(size)
                                .clip(CircleShape)
                                .background(color)
                                .border(1.dp, if (active) Color.Transparent else Color(0x33FFFFFF), CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔒", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("128-bit RBI Dual handshakes tunneling active", color = TextMuted, fontSize = 11.sp)
                }
            }

            // Pin Pad Key grid
            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                val keys = listOf(
                    listOf('1', '2', '3'),
                    listOf('4', '5', '6'),
                    listOf('7', '8', '9'),
                    listOf('F', '0', 'D') // F = Biometric fingerprint, D = Backspace delete
                )

                keys.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            PinKeyItem(
                                key = key,
                                onClick = {
                                    when (key) {
                                        'D' -> viewModel.deletePinKey()
                                        'F' -> {
                                            // Simulated Fingerprint Scan Bypass for testing
                                            viewModel.showToast("Biometric Signature linked successfully!")
                                            // Set master PIN
                                            viewModel.appendPinKey('1')
                                            viewModel.appendPinKey('2')
                                            viewModel.appendPinKey('3')
                                            viewModel.appendPinKey('4')
                                        }
                                        else -> viewModel.appendPinKey(key)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinKeyItem(key: Char, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(if (key == 'D' || key == 'F') Color.Transparent else CardSlate)
            .border(1.dp, if (key == 'D' || key == 'F') Color.Transparent else MetallicSlate, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            'D' -> Icon(Icons.Default.Backspace, contentDescription = "Delete key", tint = Color.LightGray, modifier = Modifier.size(24.dp))
            'F' -> Icon(Icons.Default.Fingerprint, contentDescription = "Face Unlock bypass", tint = NeonLime, modifier = Modifier.size(34.dp))
            else -> Text(
                text = key.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

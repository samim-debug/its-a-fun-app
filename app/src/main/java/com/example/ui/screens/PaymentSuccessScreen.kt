package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfettiOverlay
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentSuccessScreen(viewModel: AppViewModel) {
    val txResult by viewModel.paymentResult.collectAsState()
    val isSuccess = txResult?.status == "SUCCESS"

    // Scratch card states
    var isScratched by remember { mutableStateOf(false) }
    var coinsWon by remember { mutableStateOf(0) }

    // Bounce Animations for primary check mark
    val scale = remember { Animatable(0f) }
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            // Roll random win amount for rewards scratch card
            coinsWon = (25..150).random()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        // Overlay Particle Canvas
        if (isSuccess) {
            ConfettiOverlay()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Success Header Animation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(if (isSuccess) NeonLime else NeonCoral)
                        .scale(scale.value),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Check, // Fallback check or close icon
                        contentDescription = "Success tick",
                        tint = Color.Black,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isSuccess) "UPI TRUST HANDSHAKE SUCCESSFUL" else "TRANSACTION FAILED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSuccess) NeonLime else NeonCoral,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = if (isSuccess) "Sent securely via dual tunnels" else "Security gateway declined payment",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            // Receipt Box Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .border(1.dp, if (isSuccess) NeonLime.copy(alpha = 0.20f) else NeonCoral.copy(alpha = 0.20f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = "TRANSACTION RECEIPT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ReceiptRow(label = "Recipient Payee", value = txResult?.peerName ?: "Sneha Mehta")
                    ReceiptRow(label = "Recipient UPI ID", value = txResult?.peerUpi ?: "sneha@inrpay")
                    ReceiptRow(label = "Amount Transferred", value = "₹${String.format("%,.2f", txResult?.amount ?: 0.0)}")
                    ReceiptRow(label = "Payment Note", value = txResult?.note?.ifEmpty { "General transfer note" } ?: "General transfer note")
                    ReceiptRow(label = "Transaction VPA Code", value = "INRPAY${txResult?.id ?: 1002934}X${(10..99).random()}")
                    ReceiptRow(
                        label = "Timestamp Hour", 
                        value = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(txResult?.timestamp ?: System.currentTimeMillis()))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Divider(color = MetallicSlate, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { viewModel.showToast("Receipt saved to Photos successfully!") }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Download, contentDescription = "", tint = NeonLime)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save PDF", color = NeonLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(onClick = { viewModel.showToast("Receipt link copied to clipboard!") }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Share, contentDescription = "", tint = CyberCyan)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Interactive Scratch Card Area
            if (isSuccess) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Text(
                        text = "YOU UNLOCKED A REWARD SCRATCH CARD!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CyberCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .size(240.dp, 120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isScratched) CardSlateElevated else CardSlate)
                            .border(1.dp, if (isScratched) NeonLime.copy(alpha = 0.40f) else CyberPurple.copy(alpha = 0.40f), RoundedCornerShape(16.dp))
                            .clickable {
                                if (!isScratched) {
                                    isScratched = true
                                    viewModel.claimCoinsScratchCard()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isScratched) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎁", fontSize = 34.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("TAP TO SCRATCH SCREEN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Score up to 150 FamCoins", color = TextMuted, fontSize = 11.sp)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥 W BONUS", color = NeonLime, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("+$coinsWon Coins", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
                                Text("Credited to profile balance!", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Dismiss Button
            Button(
                onClick = { viewModel.dismissSuccessScreen() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Done, Back to Home Board",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

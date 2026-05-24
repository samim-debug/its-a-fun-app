package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPayScreen(viewModel: AppViewModel) {
    var upiAddress by remember { mutableStateOf("") }
    var payeeName by remember { mutableStateOf("") }
    var payAmount by remember { mutableStateOf("") }
    var payNote by remember { mutableStateOf("") }

    val recentContacts = listOf(
        Pair("Sneha Mehta", "sneha@inrpay"),
        Pair("Kabir Kapoor", "kabir@inrpay"),
        Pair("Mom ❤️", "mom@vpa"),
        Pair("Rohan Verma", "rohan@okhdfc"),
        Pair("Burger Corner", "mccorner@upi")
    )

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
                    Text("Pay or Scan QR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simulated Camera QR scanner screen with neon bounds
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF13151D))
                    .border(1.dp, MetallicSlate, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Neon corners scanning canvas
                Canvas(modifier = Modifier.size(130.dp)) {
                    val strokeWidth = 5f
                    val len = 35f
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                    // Top Left Corner
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(len, 0f), strokeWidth = strokeWidth)
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(0f, len), strokeWidth = strokeWidth)

                    // Top Right Corner
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(size.width, 0f), end = androidx.compose.ui.geometry.Offset(size.width - len, 0f), strokeWidth = strokeWidth)
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(size.width, 0f), end = androidx.compose.ui.geometry.Offset(size.width, len), strokeWidth = strokeWidth)

                    // Bottom Left Corner
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(0f, size.height), end = androidx.compose.ui.geometry.Offset(len, size.height), strokeWidth = strokeWidth)
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(0f, size.height), end = androidx.compose.ui.geometry.Offset(0f, size.height - len), strokeWidth = strokeWidth)

                    // Bottom Right Corner
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(size.width, size.height), end = androidx.compose.ui.geometry.Offset(size.width - len, size.height), strokeWidth = strokeWidth)
                    drawLine(color = NeonLime, start = androidx.compose.ui.geometry.Offset(size.width, size.height), end = androidx.compose.ui.geometry.Offset(size.width, size.height - len), strokeWidth = strokeWidth)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "", tint = NeonLime, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Viewfinder active", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Alerters check safe QR ports", color = TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Contact Selector
            Text(
                text = "PAY TO RECENT FRIEND",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                items(recentContacts) { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (upiAddress == contact.second) NeonLime.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable {
                                upiAddress = contact.second
                                payeeName = contact.first
                            }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CardSlateElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(contact.first.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(contact.first, fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Manual UPI details Inputs Form
            Text(
                text = "MANUAL UPI TRANSFER DETAILS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = upiAddress,
                onValueChange = { upiAddress = it },
                label = { Text("Receiver's UPI ID / Phone", color = TextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardSlate,
                    unfocusedContainerColor = CardSlate,
                    focusedIndicatorColor = NeonLime,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = payeeName,
                onValueChange = { payeeName = it },
                label = { Text("Receiver's Public Name (Optional)", color = TextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardSlate,
                    unfocusedContainerColor = CardSlate,
                    focusedIndicatorColor = NeonLime,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = payAmount,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '.' }) payAmount = it
                },
                prefix = { Text("₹ ", color = NeonLime, fontWeight = FontWeight.Bold) },
                label = { Text("Enter Amount (INR)", color = TextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardSlate,
                    unfocusedContainerColor = CardSlate,
                    focusedIndicatorColor = NeonLime,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = payNote,
                onValueChange = { payNote = it },
                label = { Text("Add Personal Note (e.g. food, games)", color = TextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardSlate,
                    unfocusedContainerColor = CardSlate,
                    focusedIndicatorColor = NeonLime,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(30.dp))

            val keyboardController = LocalSoftwareKeyboardController.current
            Button(
                onClick = {
                    keyboardController?.hide()
                    val amt = payAmount.toDoubleOrNull() ?: 0.0
                    val cleanName = payeeName.ifBlank { upiAddress.substringBefore("@") }
                    if (upiAddress.isBlank() || amt <= 0) {
                        viewModel.showToast("UPI parameters incorrect!")
                    } else {
                        viewModel.initiatePayment(cleanName, upiAddress, amt, payNote)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Proceed to Dual-Encrypt PIN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

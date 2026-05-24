package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel
import com.example.data.SecurityLog
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AppViewModel) {
    val logs by viewModel.securityLogs.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val wallet by viewModel.wallet.collectAsState()

    var showResetDbConfirm by remember { mutableStateOf(false) }

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
                    Text("Admin Security Audits", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // High-fidelity analytics cards row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsCard(
                    label = "LEDGER COUNT",
                    value = transactions.size.toString(),
                    color = NeonLime,
                    modifier = Modifier.weight(1f)
                )

                AnalyticsCard(
                    label = "SECURITY LOGS",
                    value = logs.size.toString(),
                    color = CyberPurple,
                    modifier = Modifier.weight(1f)
                )

                AnalyticsCard(
                    label = "AVAILABLE BAL",
                    value = "₹${wallet?.balance?.toInt() ?: 1000}",
                    color = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            // Realtime Security Logs Feed
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE HANDSHAKE & FRAUD AUDITS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Reset DB",
                    fontSize = 12.sp,
                    color = NeonCoral,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showResetDbConfirm = true }
                )
            }

            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events verified yet.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(logs) { log ->
                        LogAuditRow(log)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action: Clear Logs histories
            OutlinedButton(
                onClick = { viewModel.showToast("Wiped activity history. System re-initialized.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MetallicSlate),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recycle Debug Logs Cash")
            }
        }
    }

    // SQLite Wipe Confirmation Dialog
    if (showResetDbConfirm) {
        AlertDialog(
            onDismissRequest = { showResetDbConfirm = false },
            containerColor = CardSlate,
            icon = { Icon(Icons.Default.Security, contentDescription = "", tint = NeonCoral) },
            title = { Text("Recycle Database Cache?", color = Color.White) },
            text = {
                Text("Warning: This wipes all user profiles, wallet ledger entries and security reports. Resets local SQLite entities to newly-seeded defaults.", fontSize = 13.sp, color = TextMuted)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetEntireDatabase()
                        showResetDbConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCoral)
                ) {
                    Text("Confirm Wipe", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDbConfirm = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun AnalyticsCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .border(1.dp, MetallicSlate, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun LogAuditRow(log: SecurityLog) {
    val color = when (log.severity) {
        "CRITICAL" -> NeonCoral
        "WARNING" -> Color(0xFFFFB300)
        else -> NeonLime
    }
    val background = when (log.severity) {
        "CRITICAL" -> NeonCoral.copy(alpha = 0.12f)
        "WARNING" -> Color(0x1FFFB300)
        else -> NeonLime.copy(alpha = 0.12f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MetallicSlate, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (log.tag == "FRAUD_ALERT") "🚨" else "⚙️",
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.tag,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = color
                    )

                    Text(
                        text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(java.util.Date(log.timestamp)),
                        fontSize = 8.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.message,
                    fontSize = 11.sp,
                    color = TextWhite,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InteractiveVirtualCard
import com.example.ui.components.TapPulsateButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel
import com.example.data.Wallet
import com.example.data.Transaction
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()

    var showTopUpSheet by remember { mutableStateOf(false) }
    var topUpAmountStr by remember { mutableStateOf("") }
    var selectedSource by remember { mutableStateOf("SBI Debit Card") }
    
    // New hallmark interactive modules
    var showProfileModal by remember { mutableStateOf(false) }
    var showNearbyRadarModal by remember { mutableStateOf(false) }
    var showTapToPayModal by remember { mutableStateOf(false) }
    var showMyQrModal by remember { mutableStateOf(false) }
    
    // Simulate interactive collect request trigger
    var showCollectMockDialog by remember { mutableStateOf(false) }
    var collectMockAmount by remember { mutableStateOf("500") }
    var collectMockName by remember { mutableStateOf("Sneha") }

    Scaffold(
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 14.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBackground),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showProfileModal = true }
                            .semantics { contentDescription = "User profile bar" }
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(NeonLime, CyberPurple)
                                    ),
                                    shape = CircleShape
                                )
                                .padding(2.dp),
                            color = CardSlateElevated
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = profile?.avatarEmoji ?: "⚡",
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VERIFIED TEEN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonLime,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Hi, ${profile?.name?.substringBefore(" ") ?: "Aarav"}!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = profile?.upiId ?: "vpa@inrpay",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                },
                actions = {
                    // Flame Streak Counter (Fampay hallmark!)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardSlate)
                            .border(1.dp, Color(0x33FFB300), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .clickable {
                                viewModel.showToast("Daily checkout streak: 5 Days! Keep saving 🥂")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile?.streakDays ?: 5} D",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFB300)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.clip(CircleShape).background(CardSlate)
                    ) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = "Change Theme Palette",
                            tint = NeonLime
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // AI Insight Bullet Banner
            AIInsightBanner(viewModel)

            Spacer(modifier = Modifier.height(14.dp))

            // Wallet Balance + Physical Card Block
            WalletBalanceSection(
                balance = wallet?.balance ?: 4850.50,
                onTopUpClick = { showTopUpSheet = true }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Virtual Custom Interactive Teen Card
            InteractiveVirtualCardSection(viewModel, wallet)

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Rapid Actions Row
            PrimaryActionsRow(viewModel)

            Spacer(modifier = Modifier.height(28.dp))

            ProFeaturesSection(
                onNearbyClick = { showNearbyRadarModal = true },
                onTapPayClick = { showTapToPayModal = true },
                onMyQrClick = { showMyQrModal = true }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Recent Transactions Horizontal Feed
            RecentTransactionsSection(viewModel, transactions)

            Spacer(modifier = Modifier.height(28.dp))

            // Fast Utility Bill payment channels
            UtilityRechargesSection(viewModel)

            Spacer(modifier = Modifier.height(28.dp))

            // Testing / Simulation Sandboxes panel
            QuickSandboxPanel(
                onShowCollect = { showCollectMockDialog = true },
                onNavigateAdmin = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Top up sheet modal drawer
    if (showTopUpSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showTopUpSheet = false },
            sheetState = sheetState,
            containerColor = CardSlate
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fuel Wallet Cash",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add digital pocket money to your wallet instantly from any linked bank account.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = topUpAmountStr,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) topUpAmountStr = it
                    },
                    prefix = { Text("₹", color = NeonLime, fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                    placeholder = { Text("500", color = TextMuted, fontSize = 20.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ObsidianBackground,
                        unfocusedContainerColor = ObsidianBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = NeonLime,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(color = Color.White)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Fast Choice Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("100", "500", "1000", "2000").forEach { preset ->
                        OutlinedButton(
                            onClick = { topUpAmountStr = preset },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (topUpAmountStr == preset) NeonLime else MetallicSlate),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("₹$preset", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Select Funding Option",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                )

                val sources = listOf(
                    "SBI Debit Card" to "🏦",
                    "HDFC Netbanking" to "💳",
                    "Google Pay Link" to "⚡",
                    "PhonePe Simulator" to "🟣"
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sources.forEach { (name, emoji) ->
                        val isSelected = selectedSource == name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonLime.copy(alpha = 0.15f) else CardSlateElevated)
                                .border(1.dp, if (isSelected) NeonLime else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { selectedSource = name }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedSource = name },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonLime, unselectedColor = MetallicSlate)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val amt = topUpAmountStr.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.topUpWallet(amt, selectedSource)
                            showTopUpSheet = false
                            topUpAmountStr = ""
                        } else {
                            viewModel.showToast("Enter a valid deposit volume")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Load Wallet from Option ⚡", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }

    // Mock incoming Request Collect Dialog
    if (showCollectMockDialog) {
        AlertDialog(
            onDismissRequest = { showCollectMockDialog = false },
            containerColor = CardSlate,
            title = {
                Text("Peer Money Sandbox", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Simulate an incoming peer collect request to test instant reactive wallet flow on lower end sandbox.", fontSize = 13.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = collectMockName,
                        onValueChange = { collectMockName = it },
                        label = { Text("Friend Name") },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = collectMockAmount,
                        onValueChange = { collectMockAmount = it },
                        label = { Text("Amount INR") },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = collectMockAmount.toDoubleOrNull() ?: 500.0
                        viewModel.requestTestCollectRequest(amt, collectMockName)
                        showCollectMockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime)
                ) {
                    Text("Simulate", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCollectMockDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    // Coroutine Scope for local async jobs
    val coroutineScope = rememberCoroutineScope()

    // 1. Configure Profile modal
    if (showProfileModal) {
        var profileName by remember { mutableStateOf(profile?.name ?: "Aarav") }
        var profilePhone by remember { mutableStateOf(profile?.phone ?: "9876543210") }
        var profileUpi by remember { mutableStateOf(profile?.upiId ?: "aarav@inrpay") }
        var selectedAvatar by remember { mutableStateOf(profile?.avatarEmoji ?: "⚡") }

        AlertDialog(
            onDismissRequest = { showProfileModal = false },
            containerColor = CardSlate,
            title = {
                Text(
                    text = "Configure Profile Credentials",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Select a custom teen profile avatar:", color = TextMuted, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("⚡", "🚀", "👑", "😎", "👾", "🦊").forEach { emoji ->
                            val isSelected = selectedAvatar == emoji
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) NeonLime else CardSlateElevated)
                                    .clickable { selectedAvatar = emoji }
                                    .border(1.5.dp, if (isSelected) NeonLime else Color.Transparent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = profileName,
                        onValueChange = { profileName = it },
                        label = { Text("Display Name", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ObsidianBackground,
                            unfocusedContainerColor = ObsidianBackground,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = NeonLime
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = profilePhone,
                        onValueChange = { profilePhone = it },
                        label = { Text("Register Phone / Email", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ObsidianBackground,
                            unfocusedContainerColor = ObsidianBackground,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = NeonLime
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = profileUpi,
                        onValueChange = { profileUpi = it },
                        label = { Text("UPI Address ID", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ObsidianBackground,
                            unfocusedContainerColor = ObsidianBackground,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = NeonLime
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        profile?.let { original ->
                            val updated = original.copy(
                                name = profileName,
                                phone = profilePhone,
                                upiId = profileUpi,
                                avatarEmoji = selectedAvatar
                            )
                            viewModel.updateUserProfile(updated)
                        } ?: run {
                            viewModel.showToast("No active profile to update!")
                        }
                        showProfileModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileModal = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    // 2. Locate Nearby Sonar Radar modal
    if (showNearbyRadarModal) {
        var scanStage by remember { mutableStateOf(1) } // 1 = Scanning, 2 = List found
        var selectedUserToPay by remember { mutableStateOf<Pair<String, String>?>(null) }
        var inputAmountStr by remember { mutableStateOf("") }
        var inputNoteStr by remember { mutableStateOf("") }

        // Scanning timing
        LaunchedEffect(showNearbyRadarModal) {
            kotlinx.coroutines.delay(2200)
            scanStage = 2
        }

        AlertDialog(
            onDismissRequest = { showNearbyRadarModal = false },
            containerColor = CardSlate,
            title = {
                Text(
                    text = if (selectedUserToPay != null) "Paying Nearby User" else "Locate Nearby INR PAY Users",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedUserToPay != null) {
                        val peer = selectedUserToPay!!
                        Text("You are paying ${peer.first} (${peer.second})", color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = inputAmountStr,
                            onValueChange = { if (it.all { char -> char.isDigit() }) inputAmountStr = it },
                            placeholder = { Text("Amount in ₹") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBackground,
                                unfocusedContainerColor = ObsidianBackground,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputNoteStr,
                            onValueChange = { inputNoteStr = it },
                            placeholder = { Text("What is it for?") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBackground,
                                unfocusedContainerColor = ObsidianBackground,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (scanStage == 1) {
                        Spacer(modifier = Modifier.height(14.dp))
                        val infiniteTransition = rememberInfiniteTransition()
                        val radarPulse by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1.6f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size((60 * radarPulse).dp)
                                    .border(1.5.dp, NeonLime.copy(alpha = (2f - radarPulse).coerceIn(0f, 1f)), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NeonLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📡", fontSize = 24.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text("Activating sonar radar...", color = NeonLime, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Searching for offline peer device signatures.", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center)
                    } else {
                        Text("Device signatures located near Bangalore:", color = TextMuted, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(12.dp))
                        val nearbyUsers = listOf(
                            "Sneha Mehta" to "sneha@inrpay",
                            "Kabir Kapoor" to "kabir@inrpay",
                            "Siddharth Dev" to "sid@inrpay"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            nearbyUsers.forEach { (name, upiId) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardSlateElevated)
                                        .clickable { selectedUserToPay = name to upiId }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x1A8B5CF6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🦊", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(upiId, color = TextMuted, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("Pay ⚡", color = NeonLime, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (selectedUserToPay != null) {
                    Button(
                        onClick = {
                            val amt = inputAmountStr.toDoubleOrNull() ?: 0.0
                            val peer = selectedUserToPay!!
                            if (amt > 0) {
                                viewModel.initiatePayment(peer.first, peer.second, amt, inputNoteStr.ifEmpty { "Nearby Radar Swap" })
                                showNearbyRadarModal = false
                            } else {
                                viewModel.showToast("Please enter a valid amount")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime)
                    ) {
                        Text("Pay Now", color = Color.Black)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (selectedUserToPay != null) {
                        selectedUserToPay = null
                    } else {
                        showNearbyRadarModal = false
                    }
                }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    // 3. Tap to Pay Terminal Mock modal
    if (showTapToPayModal) {
        var animateTapProgress by remember { mutableStateOf(0) } // 0 = Idle, 1 = Tapping, 2 = Success
        val infiniteTransition = rememberInfiniteTransition()
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        LaunchedEffect(animateTapProgress) {
            if (animateTapProgress == 1) {
                kotlinx.coroutines.delay(2500)
                animateTapProgress = 2
                
                coroutineScope.launch {
                    val walletState = viewModel.wallet.value
                    if (walletState != null && walletState.balance >= 150.0) {
                        com.example.data.AppDatabase.getDatabase(viewModel.getApplication()).appDao.insertWallet(
                            walletState.copy(balance = walletState.balance - 150.0)
                        )
                        com.example.data.AppDatabase.getDatabase(viewModel.getApplication()).appDao.insertTransaction(
                            com.example.data.Transaction(
                                type = "SENT",
                                amount = 150.0,
                                peerName = "Starbucks Contactless Tap",
                                peerUpi = "starbucks@merchant",
                                note = "Tap to Pay Contactless NFC ☕",
                                category = "Food"
                            )
                        )
                    }
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showTapToPayModal = false },
            containerColor = CardSlate,
            title = {
                Text("Contactless Tap to Pay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    if (animateTapProgress == 0) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0x1200E5FF))
                                .border(1.dp, CyberCyan.copy(alpha = 0.40f), CircleShape)
                                .clickable { animateTapProgress = 1 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📶", fontSize = 42.sp, modifier = Modifier.graphicsLayer { scaleX = pulseScale; scaleY = pulseScale })
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text("Hold device near terminal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("We simulate a fast NFC reader handshake. Pay up to ₹150 instantly without entering a PIN.", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { animateTapProgress = 1 },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Simulate Tap on POS Terminal", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else if (animateTapProgress == 1) {
                        CircularProgressIndicator(color = CyberCyan, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(18.dp))
                        Text("Exchanging secure cellular tokens...", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Validating dual token certificate handshake with merchant register terminal.", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center)
                    } else {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Contactless Payment Successful!", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("Starbucks Coffee debited ₹150.00 from your active teen card securely.", color = TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showTapToPayModal = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    // 4. Share My QR Code and statement to Email modal
    if (showMyQrModal) {
        var targetEmailStr by remember { mutableStateOf("") }
        var isEmailSending by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showMyQrModal = false },
            containerColor = CardSlate,
            title = {
                Text("Receive Money - My QR Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            for (row in 0..4) {
                                Row {
                                    for (col in 0..4) {
                                        val fill = (row + col) % 2 == 0 || (row == 0 && col == 0) || (row == 4 && col == 4) || (row == 0 && col == 4) || (row == 4 && col == 0)
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .padding(2.dp)
                                                .background(if (fill) Color.Black else Color.White)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile?.name?.uppercase() ?: "AARAV SHARMA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Text(
                        text = profile?.upiId ?: "aarav@inrpay",
                        color = NeonLime,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MetallicSlate))
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Send Statement or QR to Email:",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = targetEmailStr,
                        onValueChange = { targetEmailStr = it },
                        placeholder = { Text("enter.your.email@gmail.com", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ObsidianBackground,
                            unfocusedContainerColor = ObsidianBackground,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEmailSending) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = NeonLime, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Despatching payload...", color = NeonLime, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (targetEmailStr.contains("@") && targetEmailStr.contains(".")) {
                                    isEmailSending = true
                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(1600)
                                        isEmailSending = false
                                        viewModel.showToast("QR and statement sent successfully to $targetEmailStr! 📩")
                                        targetEmailStr = ""
                                    }
                                } else {
                                    viewModel.showToast("Please enter a valid email address!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Send PDF Statement on Email 📧", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMyQrModal = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun AIInsightBanner(viewModel: AppViewModel) {
    val wallet by viewModel.wallet.collectAsState()
    val isFrozen = wallet?.isCardFrozen ?: false

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonLime.copy(alpha = 0.20f),
                        CyberPurple.copy(alpha = 0.20f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(14.dp)
            .clickable {
                viewModel.navigateTo(AppScreen.CHAT_AI)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonLime),
                contentAlignment = Alignment.Center
            ) {
                Text("🧠", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isFrozen) "AI SECURE SHIELD ACTIVE" else "INR PAY SMART ADVISOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonLime,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (isFrozen) "Card locked. AI Shield deactivated online ports to block malware handshakes." else "Spent ₹850 today. Claim scratch cards to score up to 250 Coins!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Chat with AI",
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun WalletBalanceSection(balance: Double, onTopUpClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "POCKET BALANCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₹${String.format("%,.2f", balance)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        TapPulsateButton(
            onClick = onTopUpClick,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(NeonLime)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Load Balance", tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Top Up",
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InteractiveVirtualCardSection(viewModel: AppViewModel, wallet: Wallet?) {
    val isFlipped by viewModel.isCardFlipped.collectAsState()
    val isRevealed by viewModel.isCardDetailsRevealed.collectAsState()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "PREPAID VIRTUAL CARD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Text(
                text = if (isFlipped) "Tap inside to Flip Front ↺" else "Tap inside to Flip CVV ↻",
                fontSize = 11.sp,
                color = NeonLime,
                modifier = Modifier.clickable { viewModel.flipCard() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        InteractiveVirtualCard(
            isFlipped = isFlipped,
            onFlip = { viewModel.flipCard() },
            frontContent = {
                // FRONT SIDE DESIGN
                Box(modifier = Modifier.fillMaxSize()) {
                    // Futuristic Neon Rings Canvas Background (Elegant Dark Glows)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonLime.copy(alpha = 0.15f), Color.Transparent)
                            ),
                            radius = size.width / 2.2f,
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, size.height * 0.05f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(CyberPurple.copy(alpha = 0.15f), Color.Transparent)
                            ),
                            radius = size.width / 2.2f,
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.05f, size.height * 0.95f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INR PAY",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x33CEFF00))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "PREPAID",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonLime
                                )
                            }
                        }

                        // Spark chip
                        Box(
                            modifier = Modifier
                                .size(34.dp, 24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE2E8F0).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        )

                        Column {
                            // Holographic stylized fake number
                            Text(
                                text = if (isRevealed) (wallet?.cardNum ?: "4321 8765 2468 1357") else "•••• •••• •••• 1357",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("CARD HOLDER", fontSize = 8.sp, color = TextMuted)
                                    Text(
                                        text = wallet?.cardHolder ?: "TEEN INVESTOR",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("EXPIRY", fontSize = 8.sp, color = TextMuted)
                                    Text(
                                        text = wallet?.cardExpiry ?: "09/30",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Frozen Overlay
                    if (wallet?.isCardFrozen == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xCC000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("❄️", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "CARD FROZEN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Safeguarding payments",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            },
            backContent = {
                // BACK SIDE DESIGN
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Magnetic stripe
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .background(Color.Black)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Signature area
                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(30.dp)
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = wallet?.cardHolder ?: "JOHN DOE",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Cursive,
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            // CVV
                            Column(modifier = Modifier.weight(0.5f)) {
                                Text("CVV", fontSize = 8.sp, color = TextMuted)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE53E3E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isRevealed) (wallet?.cardCvv ?: "715") else "•••",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Compliance info
                        Text(
                            text = "Powered by standard UPI networks. In compliance with RBI credit/debit mandates. Under bank tier handclasp system.",
                            fontSize = 8.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 22.dp)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card management switches
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { viewModel.revealCardDetails() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MetallicSlate),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isRevealed) "Hide Info" else "Tap Reveal", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { viewModel.freezeUnfreezeCard() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (wallet?.isCardFrozen == true) NeonLime else Color(0x33FF3E6C),
                    contentColor = if (wallet?.isCardFrozen == true) Color.Black else NeonCoral
                )
            ) {
                Icon(
                    if (wallet?.isCardFrozen == true) Icons.Default.LockOpen else Icons.Default.Weekend,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (wallet?.isCardFrozen == true) "Activate Card" else "Freeze Card",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PrimaryActionsRow(viewModel: AppViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionButtonItem(
            text = "Scan QR",
            icon = "📷",
            containerColor = NeonLime.copy(alpha = 0.12f),
            borderColor = NeonLime,
            onClick = { viewModel.navigateTo(AppScreen.UPI_PAY) }
        )

        ActionButtonItem(
            text = "Pay Friend",
            icon = "📲",
            containerColor = Color(0x1A8B5CF6),
            borderColor = CyberPurple,
            onClick = {
                // Shortcut manually triggering payment flow modal
                viewModel.initiatePayment("Sneha Mehta", "sneha@inrpay", 200.0, "For pizza 🍔")
            }
        )

        ActionButtonItem(
            text = "Split Bill",
            icon = "👥",
            containerColor = Color(0x1A00E5FF),
            borderColor = CyberCyan,
            onClick = { viewModel.navigateTo(AppScreen.BILL_SPLIT) }
        )

        ActionButtonItem(
            text = "AI Advisor",
            icon = "🧠",
            containerColor = Color(0x19F3F4F6),
            borderColor = TextMuted,
            onClick = { viewModel.navigateTo(AppScreen.CHAT_AI) }
        )
    }
}

@Composable
fun ActionButtonItem(
    text: String,
    icon: String,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(containerColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun RecentTransactionsSection(viewModel: AppViewModel, transactions: List<Transaction>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT LEDGER PASS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Text(
                text = "See All",
                fontSize = 12.sp,
                color = NeonLime,
                modifier = Modifier.clickable {
                    viewModel.showToast("See detail log at Admin Dashboard tab!")
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSlate),
                contentAlignment = Alignment.Center
            ) {
                Text("No transfers made. Get started! 💸", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 20.dp)
            ) {
                items(transactions) { tx ->
                    TransactionItem(tx)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    val isDecline = tx.status == "FAILED"
    val isCredit = tx.type == "RECEIVED" || tx.type == "TOP_UP"

    Card(
        modifier = Modifier
            .width(170.dp)
            .border(
                1.dp,
                if (isDecline) NeonCoral.copy(alpha = 0.20f) else if (isCredit) NeonLime.copy(alpha = 0.20f) else MetallicSlate,
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDecline) NeonCoral.copy(alpha = 0.20f) else if (isCredit) NeonLime.copy(alpha = 0.20f) else CyberPurple.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (tx.emojiReaction.isNotEmpty()) tx.emojiReaction else if (isCredit) "➕" else "➖",
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = if (isDecline) "Declined" else if (isCredit) "Credit" else "Debit",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDecline) NeonCoral else if (isCredit) NeonLime else CyberPurple,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isDecline) NeonCoral.copy(alpha = 0.12f) else if (isCredit) NeonLime.copy(alpha = 0.12f) else CyberPurple.copy(alpha = 0.12f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column {
                Text(
                    text = tx.peerName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tx.note.ifEmpty { "UPI wire pay" },
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = (if (isCredit) "+" else "-") + "₹${String.format("%.0f", tx.amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = if (isDecline) NeonCoral else if (isCredit) Color.White else Color.White
            )
        }
    }
}

@Composable
fun UtilityRechargesSection(viewModel: AppViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(CardSlate)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QUICK BILLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonLime,
                modifier = Modifier.clickable {
                    viewModel.showToast("All customized options are fully unlocked!")
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            UtilityItem(label = "Mobile", icon = "📱", onClick = {
                viewModel.initiatePayment("Jio Telecom", "jio@upi", 299.0, "Mobile Recharge 📱")
            })
            UtilityItem(label = "Power", icon = "⚡", onClick = {
                viewModel.initiatePayment("BESCOM Bangalore", "electricity@upi", 1250.0, "Home Bill 💡")
            })
            UtilityItem(label = "FASTag", icon = "🚗", onClick = {
                viewModel.initiatePayment("NHAI Fastag", "fastag@upi", 500.0, "Fastag Fuel 🚗")
            })
            UtilityItem(label = "Wi-Fi", icon = "📶", onClick = {
                viewModel.initiatePayment("Airtel Broadband", "broadband@upi", 1250.0, "Wi-Fi Bill 📶")
            })
        }
    }
}

@Composable
fun UtilityItem(label: String, icon: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardSlateElevated)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun QuickSandboxPanel(onShowCollect: () -> Unit, onNavigateAdmin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSlate)
            .border(1.dp, MetallicSlate, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("FINTECH TESTING CENTER", fontSize = 10.sp, fontWeight = FontWeight.Black, color = NeonLime)
            Text("Simulate live money drops or inspect the dual anti-fraud AI logs in the logs dashboard.", fontSize = 11.sp, color = TextMuted)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row {
            IconButton(
                onClick = onShowCollect,
                modifier = Modifier.clip(CircleShape).background(MetallicSlate)
            ) {
                Icon(Icons.Default.Download, contentDescription = "Receive funds", tint = NeonLime)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onNavigateAdmin,
                modifier = Modifier.clip(CircleShape).background(MetallicSlate)
            ) {
                Icon(Icons.Default.Dashboard, contentDescription = "Developer Settings", tint = CyberCyan)
            }
        }
    }
}

@Composable
fun ProFeaturesSection(
    onNearbyClick: () -> Unit,
    onTapPayClick: () -> Unit,
    onMyQrClick: () -> Unit
) {
    Text(
        text = "PRO ACTIONS & ADVANCED CHANNELS",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = NeonLime,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Option 1: Locate Nearby
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onNearbyClick() }
                .border(1.dp, Color(0x228B5CF6), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x198B5CF6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📡", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Nearby",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "Radar scan",
                    fontSize = 9.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Option 2: Tap to Pay
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onTapPayClick() }
                .border(1.dp, Color(0x2200E5FF), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x1900E5FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📶", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap & Pay",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "Contactless NFC",
                    fontSize = 9.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Option 3: Own QR
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onMyQrClick() }
                .border(1.dp, Color(0x22FFB300), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x19FFB300)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔳", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "My QR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "Share & Email",
                    fontSize = 9.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


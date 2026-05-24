package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel
import com.example.data.SplitBill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSplitScreen(viewModel: AppViewModel) {
    val splitBills by viewModel.splitBills.collectAsState()
    
    var showAddSplitDialog by remember { mutableStateOf(false) }
    var splitTitle by remember { mutableStateOf("") }
    var splitAmount by remember { mutableStateOf("") }
    var splitCount by remember { mutableStateOf("2") }
    var splitPayers by remember { mutableStateOf("Sneha, Kabir, You") }

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
                    Text("Split with Friends", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                actions = {
                    IconButton(
                        onClick = { showAddSplitDialog = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NeonLime.copy(alpha = 0.20f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Split", tint = NeonLime)
                    }
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
            // Explanatory Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NeonLime.copy(alpha = 0.08f))
                    .border(1.dp, NeonLime.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👥", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Active Group Debts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Settle up split restaurant, gaming, DTH or event tickets. Debited instantly.", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bills Section list
            Text(
                text = "ACTIVE SPLIT BILLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (splitBills.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🤝", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No splits created yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Tap + in the top bar to create one!", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(splitBills) { bill ->
                        SplitBillRow(bill, 
                            onSettle = { viewModel.settleSplitBill(bill) },
                            onDelete = { viewModel.eraseSplitBill(bill.id) }
                        )
                    }
                }
            }
        }
    }

    // Add split sheet / dialog
    if (showAddSplitDialog) {
        AlertDialog(
            onDismissRequest = { showAddSplitDialog = false },
            containerColor = CardSlate,
            title = {
                Text("Log Splitted Expense", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = splitTitle,
                        onValueChange = { splitTitle = it },
                        label = { Text("Event Name (e.g. Pizza hut)", color = TextMuted) },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = splitAmount,
                        onValueChange = { splitAmount = it },
                        label = { Text("Total Bill (INR)", color = TextMuted) },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = splitCount,
                        onValueChange = { splitCount = it },
                        label = { Text("Number of Friends (Excl. You)", color = TextMuted) },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = splitPayers,
                        onValueChange = { splitPayers = it },
                        label = { Text("Friend Names (comma separated)", color = TextMuted) },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = splitAmount.toDoubleOrNull() ?: 0.0
                        val cnt = splitCount.toIntOrNull() ?: 2
                        if (splitTitle.isEmpty() || amt <= 0 || cnt <= 0) {
                            viewModel.showToast("Log criteria incorrect")
                        } else {
                            viewModel.triggerSplitBill(splitTitle, amt, cnt, splitPayers)
                            showAddSplitDialog = false
                            splitTitle = ""
                            splitAmount = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime)
                ) {
                    Text("Divide Bill ⚡", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSplitDialog = false }) {
                    Text("Discard", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun SplitBillRow(bill: SplitBill, onSettle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (bill.isSettled) MetallicSlate else NeonLime.copy(alpha = 0.20f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(bill.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Text("Divided split among: ${bill.payers}", fontSize = 10.sp, color = TextMuted)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (bill.isSettled) Color(0x1F22C55E) else Color(0x1FCEFF00))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (bill.isSettled) "Settled" else "Pending",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bill.isSettled) Color(0xFF22C55E) else NeonLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("YOUR ALLOCATED DEBT", fontSize = 8.sp, color = TextMuted)
                    Text(
                        "₹${String.format("%,.2f", bill.dividedAmount)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    Text("of total Group Bill ₹${bill.totalAmount}", fontSize = 9.sp, color = TextMuted)
                }

                if (!bill.isSettled) {
                    Button(
                        onClick = onSettle,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "", tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pay Share", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    IconButton(onClick = onDelete) {
                        Text("🗑️", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
import com.example.ui.viewmodel.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(viewModel: AppViewModel) {
    val chatHistory by viewModel.aiChatHistory.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    var typedQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Key suggestion bubbles for teenagers
    val suggestPrompts = listOf(
        "Am I spending too much on Food? 🍔",
        "How can I lock/unfreeze my card? ❄️",
        "Tips to earn more rewards coins 🪙",
        "Is INR PAY secure against hacks? 🔒"
    )

    // Scroll chat list dynamically when a new message flows
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(CyberPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧠", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("INR PAY Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Smart Pocket AI Advisor • Online", color = NeonLime, fontSize = 10.sp)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Main Chat Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
            ) {
                items(chatHistory) { msg ->
                    ChatBubbleItem(msg)
                }

                if (isAiLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = CyberPurple,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "AI is analyzing budget flows...",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Suggestion chips row
            if (!isAiLoading) {
                Column {
                    Text(
                        "POPULAR QUESTIONS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(suggestPrompts) { prompt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardSlate)
                                    .border(1.dp, MetallicSlate, RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.sendChatMessage(prompt)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(prompt, color = TextWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Text Entry Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = typedQuery,
                    onValueChange = { typedQuery = it },
                    placeholder = { Text("Ask anything... e.g. limit safety", color = TextMuted) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CardSlate,
                        unfocusedContainerColor = CardSlate,
                        focusedIndicatorColor = CyberPurple,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                FloatingActionButton(
                    onClick = {
                        if (typedQuery.trim().isNotEmpty()) {
                            viewModel.sendChatMessage(typedQuery)
                            typedQuery = ""
                        }
                    },
                    containerColor = CyberPurple,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send message")
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(msg: ChatMessage) {
    val isUser = msg.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgBrush = if (isUser) {
        Brush.horizontalGradient(colors = listOf(CyberPurple, Color(0xFF6366F1)))
    } else {
        Brush.horizontalGradient(colors = listOf(CardSlate, CardSlateElevated))
    }
    val contentColor = Color.White
    val roundCorners = if (isUser) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .clip(roundCorners)
                .background(brush = bgBrush)
                .border(1.dp, if (isUser) Color.Transparent else MetallicSlate, roundCorners)
                .padding(14.dp)
        ) {
            Text(
                text = msg.text,
                color = contentColor,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isUser) "You" else "INR PAY AI",
            fontSize = 9.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}

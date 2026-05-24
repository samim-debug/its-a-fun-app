package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.components.IndianFlagInrLogo

@Composable
fun OnboardingScreen(viewModel: AppViewModel) {
    var step by remember { mutableStateOf(1) }
    
    val name by viewModel.onboardingName.collectAsState()
    val phone by viewModel.onboardingPhone.collectAsState()
    val upi by viewModel.onboardingUpi.collectAsState()
    val pin by viewModel.onboardingPIN.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Atmosphere Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonLime.copy(alpha = 0.12f), // Orange subtle aura
                            CyberPurple.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branded Logo Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                IndianFlagInrLogo(
                    size = 50.dp,
                    symbolSize = 34.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "INR PAY",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif
                )
            }

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "OnboardingStepAnimation"
            ) { currentStep ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GradientBorder(), RoundedCornerShape(28.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (currentStep) {
                            1 -> WelcomeStep(
                                onGoogleLogin = {
                                    viewModel.onboardingName.value = "John Doe"
                                    viewModel.onboardingPhone.value = "9900887766"
                                    viewModel.onboardingUpi.value = "johndoe@inrpay"
                                    viewModel.showToast("Google Account Linked successfully!")
                                    step = 2
                                },
                                onManualContinue = { step = 2 }
                            )
                            2 -> DetailStep(
                                name = name,
                                onNameChange = { viewModel.onboardingName.value = it },
                                phone = phone,
                                onPhoneChange = { viewModel.onboardingPhone.value = it },
                                onNext = {
                                    if (name.isBlank() || phone.isBlank()) {
                                        viewModel.showToast("Please provide details to register")
                                    } else {
                                        // Auto suggest custom UPI Id
                                        if (upi.isBlank()) {
                                            viewModel.onboardingUpi.value = "${name.lowercase().replace(" ", "")}@inrpay"
                                        }
                                        step = 3
                                    }
                                }
                            )
                            3 -> UpiStep(
                                upi = upi,
                                onUpiChange = { viewModel.onboardingUpi.value = it },
                                onNext = {
                                    if (upi.isBlank()) {
                                        viewModel.showToast("UPI address cannot be blank")
                                    } else {
                                        step = 4
                                    }
                                },
                                onBack = { step = 2 }
                            )
                            4 -> PinStep(
                                pin = pin,
                                onPinChange = {
                                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                        viewModel.onboardingPIN.value = it
                                    }
                                },
                                onSubmit = {
                                    if (pin.length != 4) {
                                        viewModel.showToast("Security PIN must be exactly 4 digits")
                                    } else {
                                        viewModel.submitOnboarding()
                                    }
                                },
                                onBack = { step = 3 }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeStep(onGoogleLogin: () -> Unit, onManualContinue: () -> Unit) {
    Text(
        text = "Say hello to secure spending.",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "The ultimate teen prepay UPI experience, inspired by modern design.",
        fontSize = 14.sp,
        color = TextMuted,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Simulated Google Link Button
    Button(
        onClick = onGoogleLogin,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "G ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFEA4335)
            )
            Text(
                text = "Sign up with Google",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedButton(
        onClick = onManualContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonLime),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, NeonLime)
    ) {
        Text(
            text = "Enter Details Manually",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DetailStep(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Text(
        text = "Who's on-board?",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Spacer(modifier = Modifier.height(20.dp))

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("What do friends call you?", color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon", tint = NeonLime) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = NeonLime,
            unfocusedIndicatorColor = MetallicSlate
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text("Mobile Phone (WhatsApp OTP)", color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Icon", tint = NeonLime) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = NeonLime,
            unfocusedIndicatorColor = MetallicSlate
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text("Continue", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.Black)
    }
}

@Composable
fun UpiStep(
    upi: String,
    onUpiChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Text(
        text = "Your custom UPI ID",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Create your stylish, simple INR PAY VPA handle. Share this to receive payments.",
        fontSize = 12.sp,
        color = TextMuted,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = upi,
        onValueChange = onUpiChange,
        placeholder = { Text("aarav@inrpay", color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Security, contentDescription = "UPI Safety Icon", tint = NeonLime) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = NeonLime,
            unfocusedIndicatorColor = MetallicSlate
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(28.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MetallicSlate)
        ) {
            Text("Back")
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onNext,
            modifier = Modifier
                .weight(1.5f)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Unlock PIN", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PinStep(
    pin: String,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Text(
        text = "Setup secure PIN",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Create a 4-digit security code. This locks your payments, card reveal and wallets.",
        fontSize = 12.sp,
        color = TextMuted,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = pin,
        onValueChange = onPinChange,
        label = { Text("4-Digit Security Code", color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PIN Lock", tint = NeonLime) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = NeonLime,
            unfocusedIndicatorColor = MetallicSlate
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(28.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MetallicSlate)
        ) {
            Text("Back")
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .weight(1.5f)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Go Live 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GradientBorder() = Brush.linearGradient(
    colors = listOf(
        MetallicSlate,
        Color(0x228B5CF6),
        MetallicSlate
    )
)

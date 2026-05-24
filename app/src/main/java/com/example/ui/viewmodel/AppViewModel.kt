package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen {
    ONBOARDING,
    HOME,
    UPI_PAY,
    CARD_VIEW,
    REWARDS_VIEW,
    CHAT_AI,
    ADMIN_DASHBOARD,
    BILL_SPLIT
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class PaymentRecipient(
    val name: String,
    val upiId: String,
    val amount: Double,
    val note: String = ""
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.appDao)

    // Routing and Flow state
    private val _currentScreen = MutableStateFlow(AppScreen.ONBOARDING)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Observable Flows from DB
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val wallet: StateFlow<Wallet?> = repository.wallet
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<Transaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityLogs: StateFlow<List<SecurityLog>> = repository.securityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val splitBills: StateFlow<List<SplitBill>> = repository.splitBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI interactive states
    private val _aiChatHistory = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Yo! I'm INR PAY AI, your teenage money therapist. Ask me about your limits, scratch cards, budgeting, or any transactions you make. Let's stack some coins! ⚡🌴", isUser = false)
    ))
    val aiChatHistory: StateFlow<List<ChatMessage>> = _aiChatHistory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _pinEntered = MutableStateFlow("")
    val pinEntered: StateFlow<String> = _pinEntered.asStateFlow()

    private val _activePayment = MutableStateFlow<PaymentRecipient?>(null)
    val activePayment: StateFlow<PaymentRecipient?> = _activePayment.asStateFlow()

    private val _paymentResult = MutableStateFlow<Transaction?>(null)
    val paymentResult: StateFlow<Transaction?> = _paymentResult.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    private val _isCardDetailsRevealed = MutableStateFlow(false)
    val isCardDetailsRevealed: StateFlow<Boolean> = _isCardDetailsRevealed.asStateFlow()

    private val _currentTheme = MutableStateFlow("NEON_BLUE")
    val currentTheme: StateFlow<String> = _currentTheme.asStateFlow()

    // Splash, onboarding data & dialog states
    val onboardingName = MutableStateFlow("")
    val onboardingPhone = MutableStateFlow("")
    val onboardingUpi = MutableStateFlow("")
    val onboardingPIN = MutableStateFlow("")

    private val _isBiometricLocked = MutableStateFlow(false)
    val isBiometricLocked: StateFlow<Boolean> = _isBiometricLocked.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed database with mature data on first launch
            repository.seedInitialDataIfRequired()
            
            // Check if user is already registered & logged in
            val profile = repository.userProfile.firstOrNull() ?: repository.getUserProfileSync()
            if (profile != null) {
                _currentScreen.value = AppScreen.HOME
                _currentTheme.value = profile.currentTheme
            } else {
                _currentScreen.value = AppScreen.ONBOARDING
            }
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            if (_toastMessage.value == msg) _toastMessage.value = null
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        _pinEntered.value = "" // Reset pin entry when switching screens
    }

    fun toggleTheme() {
        val nextTheme = when (_currentTheme.value) {
            "NEON_BLUE" -> "PURPLE_PASSION"
            "PURPLE_PASSION" -> "CORAL_GLOW"
            else -> "NEON_BLUE"
        }
        _currentTheme.value = nextTheme
        viewModelScope.launch {
            val profile = repository.getUserProfileSync()
            if (profile != null) {
                repository.insertUserProfile(profile.copy(currentTheme = nextTheme))
            }
        }
    }

    // Interactive Virtual Card control
    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun revealCardDetails() {
        _isCardDetailsRevealed.value = !_isCardDetailsRevealed.value
    }

    fun freezeUnfreezeCard() {
        viewModelScope.launch {
            val walletState = repository.getWalletSync()
            if (walletState != null) {
                val newState = !walletState.isCardFrozen
                repository.insertWallet(walletState.copy(isCardFrozen = newState))
                val logText = if (newState) "Virtual prepaid card FROZEN by user" else "Virtual prepaid card UN-FROZEN by user"
                repository.insertSecurityLog(
                    SecurityLog(tag = "SECURITY", message = logText, severity = "WARNING")
                )
                showToast(if (newState) "Card Frozen successfully" else "Card Active!")
            }
        }
    }

    // Top up money
    fun topUpWallet(amount: Double, sourceName: String = "Linked Bank Account") {
        if (amount <= 0) return
        viewModelScope.launch {
            val walletState = repository.getWalletSync()
            if (walletState != null) {
                val newBal = walletState.balance + amount
                repository.insertWallet(walletState.copy(balance = newBal))

                // Insert ledger transaction
                val tx = Transaction(
                    type = "TOP_UP",
                    amount = amount,
                    peerName = sourceName,
                    peerUpi = if (sourceName.contains("SBI")) "sbi@bank" else if (sourceName.contains("HDFC")) "hdfc@bank" else "external@inrpay",
                    note = "Wallet Top Up via $sourceName",
                    category = "TopUp"
                )
                repository.insertTransaction(tx)

                // Log system event
                repository.insertSecurityLog(
                    SecurityLog(tag = "SYSTEM", message = "Wallet successfully loaded with ₹${String.format("%.2f", amount)} from $sourceName", severity = "INFO")
                )
                
                // Reward coins for loading money!
                val profile = repository.getUserProfileSync()
                if (profile != null) {
                    val bonusCoins = (amount / 10).toInt().coerceAtMost(100)
                    if (bonusCoins > 0) {
                        repository.insertUserProfile(profile.copy(coins = profile.coins + bonusCoins))
                        showToast("Loaded ₹$amount successfully! Added $bonusCoins FamCoins 🪙")
                    } else {
                        showToast("Loaded ₹$amount successfully from $sourceName.")
                    }
                }
            }
        }
    }

    // Registrations & onboarding flow
    fun submitOnboarding() {
        val name = onboardingName.value.trim()
        val phone = onboardingPhone.value.trim()
        val pin = onboardingPIN.value.trim()
        val upi = if (onboardingUpi.value.trim().isNotEmpty()) onboardingUpi.value.trim() else "${name.lowercase().replace(" ", "")}@inrpay"

        if (name.isEmpty() || phone.isEmpty() || pin.length != 4) {
            showToast("Complete all fields with a robust 4-digit PIN")
            return
        }

        viewModelScope.launch {
            // Seed Profile
            val profile = UserProfile(
                name = name,
                phone = phone,
                upiId = upi,
                pincode = pin,
                coins = 0, // No welcome bonus coins
                isLoggedIn = true,
                currentTheme = _currentTheme.value
            )
            repository.insertUserProfile(profile)

            // Seed fresh wallet
            val newWallet = Wallet(
                balance = 0.0, // Wallet balance starts at ₹0.0 (no signup bonus)
                cardNum = "4321 " + (1000..9999).random() + " " + (1000..9999).random() + " " + (1000..9999).random(),
                cardHolder = name.uppercase(),
                cardExpiry = "10/31",
                cardCvv = (100..999).random().toString()
            )
            repository.insertWallet(newWallet)

            // Introduce initial logs
            repository.insertSecurityLog(
                SecurityLog(tag = "SYSTEM", message = "Fresh account set up completed for teen developer $name", severity = "INFO")
            )

            showToast("Welcome $name!")
            navigateTo(AppScreen.HOME)
        }
    }

    // Log-out or Wipe DB for Admin testing
    fun resetEntireDatabase() {
        viewModelScope.launch {
            repository.clearHistory()
            // Re-seed on next app trigger or delete & recreate
            showToast("Database and cache fully recycled!")
            navigateTo(AppScreen.ONBOARDING)
        }
    }

    // Send money trigger
    fun initiatePayment(name: String, upiId: String, amount: Double, note: String) {
        if (amount <= 0) {
            showToast("Amount must be greater than zero")
            return
        }
        if (upiId.isEmpty()) {
            showToast("Recipient UPI code required")
            return
        }
        _activePayment.value = PaymentRecipient(name, upiId, amount, note)
        _pinEntered.value = ""
        navigateTo(AppScreen.UPI_PAY)
    }

    // Input security Pin values
    fun appendPinKey(char: Char) {
        if (_pinEntered.value.length < 4) {
            _pinEntered.value += char
        }
        if (_pinEntered.value.length == 4) {
            verifyPinAndProcessTransaction()
        }
    }

    fun deletePinKey() {
        if (_pinEntered.value.isNotEmpty()) {
            _pinEntered.value = _pinEntered.value.dropLast(1)
        }
    }

    // Transaction execution & validation engine
    private fun verifyPinAndProcessTransaction() {
        val payment = _activePayment.value
        if (payment == null) {
            showToast("No active checkout found")
            return
        }

        viewModelScope.launch {
            val profile = repository.getUserProfileSync()
            val wall = repository.getWalletSync()

            if (profile == null || wall == null) {
                showToast("Account validation failure. Sync assets again.")
                return@launch
            }

            // PIN Match verify
            if (_pinEntered.value != profile.pincode) {
                // Fraud Warning Alert
                repository.insertSecurityLog(
                    SecurityLog(
                        tag = "FRAUD_ALERT",
                        message = "Warning: 4-digit PIN verification failure for UPI transfer of ₹${payment.amount} to ${payment.upiId}",
                        severity = "WARNING"
                    )
                )
                showToast("Invalid 4-digit security PIN!")
                _pinEntered.value = ""
                return@launch
            }

            // Frozen card checks
            if (wall.isCardFrozen) {
                repository.insertSecurityLog(
                    SecurityLog(
                        tag = "FRAUD_ALERT",
                        message = "Blocked: Payment attempt of ₹${payment.amount} to ${payment.upiId} was initiated while prepaid card is FROZEN",
                        severity = "CRITICAL"
                    )
                )
                showToast("Payment aborted! Card is locked.")
                return@launch
            }

            // Check Limits
            if (payment.amount > wall.dailyLimit) {
                repository.insertSecurityLog(
                    SecurityLog(
                        tag = "FRAUD_ALERT",
                        message = "Blocked: Spend velocity anomaly detected limit: ₹${wall.dailyLimit}, tried: ₹${payment.amount}",
                        severity = "CRITICAL"
                    )
                )
                showToast("Daily Limit of ₹${wall.dailyLimit} exceeded!")
                return@launch
            }

            // Check Balance
            if (wall.balance < payment.amount) {
                val tFailed = Transaction(
                    type = "SENT",
                    amount = payment.amount,
                    peerName = payment.name,
                    peerUpi = payment.upiId,
                    note = "Declined: Insufficient Funds",
                    category = "Failed",
                    status = "FAILED"
                )
                repository.insertTransaction(tFailed)
                _paymentResult.value = tFailed
                showToast("Insufficient Balance")
                return@launch
            }

            // Deduct & Record Ledger
            val updatedBalance = wall.balance - payment.amount
            repository.insertWallet(wall.copy(balance = updatedBalance))

            val tSuccess = Transaction(
                type = "SENT",
                amount = payment.amount,
                peerName = payment.name,
                peerUpi = payment.upiId,
                note = payment.note,
                emojiReaction = getEmojiForCategory(payment.note),
                category = parseCategory(payment.note)
            )
            repository.insertTransaction(tSuccess)
            _paymentResult.value = tSuccess

            // Log security update
            repository.insertSecurityLog(
                SecurityLog(
                    tag = "TRANSACTION",
                    message = "INR PAY dual handshook transfer status successful: ₹${payment.amount} sent to ${payment.upiId}",
                    severity = "INFO"
                )
            )

            // Reward FamCoins for spending!
            val coinsEarned = (payment.amount / 5).toInt().coerceIn(1, 250)
            repository.insertUserProfile(profile.copy(coins = profile.coins + coinsEarned))
            showToast("Transferred successfully! Earned $coinsEarned FamCoins 🪙")
        }
    }

    private fun getEmojiForCategory(note: String): String {
        val n = note.lowercase()
        return when {
            n.contains("burger") || n.contains("pizza") || n.contains("food") -> "🍔"
            n.contains("spotify") || n.contains("sing") || n.contains("music") -> "🎵"
            n.contains("game") || n.contains("fifa") || n.contains("skin") -> "🎮"
            n.contains("rent") || n.contains("bill") -> "💸"
            n.contains("movie") || n.contains("film") || n.contains("inox") -> "🍿"
            else -> "⚡"
        }
    }

    private fun parseCategory(note: String): String {
        val n = note.lowercase()
        return when {
            n.contains("burger") || n.contains("pizza") || n.contains("food") -> "Food"
            n.contains("spotify") || n.contains("sing") || n.contains("music") -> "Subscriptions"
            n.contains("game") || n.contains("fifa") || n.contains("skin") -> "Games"
            n.contains("rent") || n.contains("bill") || n.contains("electricity") -> "Utilities"
            n.contains("movie") || n.contains("film") || n.contains("inox") -> "Lifestyle"
            else -> "General"
        }
    }

    // Dismiss custom modals
    fun dismissSuccessScreen() {
        _paymentResult.value = null
        _activePayment.value = null
        _pinEntered.value = ""
        navigateTo(AppScreen.HOME)
    }

    // Group billing splits
    fun triggerSplitBill(title: String, amount: Double, membersCount: Int, payers: String) {
        if (amount <= 0 || title.isEmpty() || membersCount <= 0) return
        viewModelScope.launch {
            val divided = amount / (membersCount + 1)
            val cleanPayers = if (payers.isEmpty()) "Friends, You" else payers

            val bill = SplitBill(
                title = title,
                totalAmount = amount,
                dividedAmount = divided,
                payers = cleanPayers,
                creatorName = "You"
            )
            repository.insertSplitBill(bill)
            repository.insertSecurityLog(
                SecurityLog(tag = "SYSTEM", message = "Divided bill created: '$title' split by ${membersCount + 1} users", severity = "INFO")
            )
            showToast("Created split: ₹${String.format("%.2f", divided)} per person")
        }
    }

    fun settleSplitBill(bill: SplitBill) {
        viewModelScope.launch {
            val wall = repository.getWalletSync()
            if (wall == null) return@launch
            
            if (wall.balance < bill.dividedAmount) {
                showToast("Insufficient pocket wallet balance to settle")
                return@launch
            }

            // Deduct
            repository.insertWallet(wall.copy(balance = wall.balance - bill.dividedAmount))
            // Update bill
            repository.updateSplitBill(bill.copy(isSettled = true))

            // Record transaction
            repository.insertTransaction(
                Transaction(
                    type = "SENT",
                    amount = bill.dividedAmount,
                    peerName = "Group Split Settle",
                    peerUpi = "split@inrpay",
                    note = "Settled portion for ${bill.title}",
                    category = "Lifestyle"
                )
            )

            // Log secure handshakes
            repository.insertSecurityLog(
                SecurityLog(tag = "TRANSACTION", message = "Settled Split debt portion of ₹${bill.dividedAmount} for bill ID: ${bill.id}", severity = "INFO")
            )

            showToast("Settled successfully!")
        }
    }

    fun eraseSplitBill(id: Int) {
        viewModelScope.launch {
            repository.deleteSplitBill(id)
            showToast("Record scrubbed")
        }
    }

    // AI chat messaging block
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return
        val userMsg = ChatMessage(text, isUser = true)
        _aiChatHistory.value = _aiChatHistory.value + userMsg
        _isAiLoading.value = true

        viewModelScope.launch {
            // Retrieve recent transactions to contextualize AI spending limits advice
            val recentTxs = transactions.value.take(4).joinToString("\n") {
                val sign = if (it.type == "SENT" || it.type == "RECHARGE") "Debited ₹" else "Credited ₹"
                "- $sign${it.amount} on ${SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Date(it.timestamp))} with ${it.peerName} (${it.note})"
            }

            val context = """
                Prepaid Card Limit: ₹${wallet.value?.dailyLimit ?: 5000.0}
                Available Balance: ₹${wallet.value?.balance ?: 1000.0}
                Recent Transaction History:
                $recentTxs
            """.trimIndent()

            val response = GeminiClient.askAssistant(text, context)
            _isAiLoading.value = false
            _aiChatHistory.value = _aiChatHistory.value + ChatMessage(response, isUser = false)
        }
    }

    // Scratch card claiming
    fun claimCoinsScratchCard() {
        viewModelScope.launch {
            val coinsWon = (10..150).random()
            val wall = repository.getWalletSync()
            val profile = repository.getUserProfileSync()
            if (profile != null) {
                repository.insertUserProfile(profile.copy(coins = profile.coins + coinsWon))
                
                // Add a small pocket cash reward if lucky!
                val pocketCash = if (coinsWon > 120 && wall != null) 25.0 else 0.0
                if (pocketCash > 0 && wall != null) {
                    repository.insertWallet(wall.copy(balance = wall.balance + pocketCash))
                    repository.insertTransaction(
                        Transaction(
                            type = "RECEIVED",
                            amount = pocketCash,
                            peerName = "Lucky Scratch Rewards 🎉",
                            peerUpi = "rewards@inrpay",
                            note = "Mystery cashback from FamCoin check-in",
                            category = "Pocket Money"
                        )
                    )
                    showToast("Double W! Won $coinsWon coins & ₹25 cashback!")
                } else {
                    showToast("Lucky! You won $coinsWon FamCoins!")
                }
            }
        }
    }

    // Simulation tool for incoming money (very fun for teen mock scenarios or testing fintech)
    fun requestTestCollectRequest(amount: Double, peerName: String) {
        viewModelScope.launch {
            val walletState = repository.getWalletSync()
            if (walletState != null) {
                repository.insertWallet(walletState.copy(balance = walletState.balance + amount))
                repository.insertTransaction(
                    Transaction(
                        type = "RECEIVED",
                        amount = amount,
                        peerName = peerName,
                        peerUpi = "${peerName.lowercase().replace(" ", "")}@vpa",
                        note = "Incoming peer transfer request",
                        category = "Pocket Money"
                    )
                )

                repository.insertSecurityLog(
                    SecurityLog(tag = "SECURITY", message = "Incoming collect transfer verified through UPI hand-clasp: Received ₹${amount} from $peerName", severity = "INFO")
                )
                showToast("Received ₹$amount from $peerName! 💸")
            }
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.insertUserProfile(profile)
            showToast("Profile credentials updated successfully ⚡")
        }
    }
}

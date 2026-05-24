package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val dao: AppDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val wallet: Flow<Wallet?> = dao.getWallet()
    val transactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val securityLogs: Flow<List<SecurityLog>> = dao.getAllSecurityLogs()
    val splitBills: Flow<List<SplitBill>> = dao.getAllSplitBills()

    suspend fun insertUserProfile(userProfile: UserProfile) {
        dao.insertUserProfile(userProfile)
    }

    suspend fun insertWallet(wallet: Wallet) {
        dao.insertWallet(wallet)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction)
    }

    suspend fun insertSecurityLog(securityLog: SecurityLog) {
        dao.insertSecurityLog(securityLog)
    }

    suspend fun insertSplitBill(splitBill: SplitBill) {
        dao.insertSplitBill(splitBill)
    }

    suspend fun updateSplitBill(splitBill: SplitBill) {
        dao.updateSplitBill(splitBill)
    }

    suspend fun deleteSplitBill(billId: Int) {
        dao.deleteSplitBill(billId)
    }

    suspend fun clearHistory() {
        dao.clearAllTransactions()
        dao.clearAllSecurityLogs()
    }

    suspend fun getUserProfileSync(): UserProfile? = dao.getUserProfileSync()
    suspend fun getWalletSync(): Wallet? = dao.getWalletSync()

    suspend fun seedInitialDataIfRequired() {
        if (dao.getUserProfileSync() == null) {
            val user = UserProfile(
                name = "Aarav Sharma",
                phone = "9876543210",
                upiId = "aarav@inrpay",
                coins = 350,
                avatarEmoji = "⚡",
                isVerified = true,
                pincode = "1234",
                streakDays = 5,
                isLoggedIn = false,
                currentTheme = "NEON_BLUE"
            )
            dao.insertUserProfile(user)

            val initialWallet = Wallet(
                balance = 4850.50,
                cardNum = "4321 8765 2468 1357",
                cardHolder = "AARAV SHARMA",
                cardExpiry = "09/30",
                cardCvv = "715",
                isCardFrozen = false,
                dailyLimit = 15000.0,
                spentToday = 850.0
            )
            dao.insertWallet(initialWallet)

            // Seed initial realistic transactions (social + payments)
            val txs = listOf(
                Transaction(
                    type = "SENT",
                    amount = 120.0,
                    peerName = "Sneha Mehta",
                    peerUpi = "sneha@inrpay",
                    note = "McCorner Burgers 🍔🍿",
                    emojiReaction = "🍔",
                    category = "Food",
                    status = "SUCCESS"
                ),
                Transaction(
                    type = "RECEIVED",
                    amount = 500.0,
                    peerName = "Mom ❤️",
                    peerUpi = "mom@vpa",
                    note = "Weekly pocket money incentive 🎉",
                    emojiReaction = "❤️",
                    category = "Pocket Money",
                    status = "SUCCESS"
                ),
                Transaction(
                    type = "SENT",
                    amount = 350.0,
                    peerName = "Spotify Premium",
                    peerUpi = "spotify@upi",
                    note = "Monthly jam pass 🎵",
                    emojiReaction = "😎",
                    category = "Subscriptions",
                    status = "SUCCESS"
                ),
                Transaction(
                    type = "TOP_UP",
                    amount = 1000.0,
                    peerName = "HDFC Bank",
                    peerUpi = "hdfc@bank",
                    note = "Top-up from linked bank account",
                    category = "TopUp",
                    status = "SUCCESS"
                ),
                Transaction(
                    type = "SENT",
                    amount = 380.0,
                    peerName = "Kabir Kapoor",
                    peerUpi = "kabir@inrpay",
                    note = "FIFA credits custom swap 🎮",
                    emojiReaction = "🔥",
                    category = "Entertainment",
                    status = "SUCCESS"
                )
            )
            txs.forEach { dao.insertTransaction(it) }

            // Seed anti-fraud intelligence and security system logs
            val logs = listOf(
                SecurityLog(
                    tag = "SYSTEM",
                    message = "INR PAY Anti-Hack Engine initialized successfully v1.5",
                    severity = "INFO"
                ),
                SecurityLog(
                    tag = "SECURITY",
                    message = "Google Account and biometric lock verified for current device signature",
                    severity = "INFO"
                ),
                SecurityLog(
                    tag = "SYSTEM",
                    message = "RBI compliant 128-bit dual-tunnel bank handshake completed",
                    severity = "INFO"
                ),
                SecurityLog(
                    tag = "FRAUD_ALERT",
                    message = "AI Guard scanned transaction with Spotify Premium: Safe.",
                    severity = "INFO"
                )
            )
            logs.forEach { dao.insertSecurityLog(it) }

            // Seed split bills
            val splits = listOf(
                SplitBill(
                    title = "Pizza Party Pizza Hut",
                    totalAmount = 900.0,
                    dividedAmount = 300.0,
                    payers = "Sneha, Kabir, You",
                    creatorName = "You",
                    isSettled = false
                ),
                SplitBill(
                    title = "Movie Night IMAX",
                    totalAmount = 1200.0,
                    dividedAmount = 400.0,
                    payers = "Rohan, Kabir, Sneha",
                    creatorName = "Sneha",
                    isSettled = true
                )
            )
            splits.forEach { dao.insertSplitBill(it) }
        }
    }
}

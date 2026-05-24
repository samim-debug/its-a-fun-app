package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val phone: String,
    val upiId: String,
    val coins: Int = 120, // Prepopulate some initial teen reward coins
    val avatarEmoji: String = "⚡",
    val isVerified: Boolean = true,
    val pincode: String = "1234",
    val streakDays: Int = 3,
    val isLoggedIn: Boolean = false,
    val currentTheme: String = "NEON_BLUE"
)

@Entity(tableName = "wallet")
data class Wallet(
    @PrimaryKey val id: Int = 1,
    val balance: Double = 5000.00, // Initial wallet balance
    val cardNum: String = "4321 8765 2468 1357",
    val cardHolder: String = "TEEN INVESTOR",
    val cardExpiry: String = "12/31",
    val cardCvv: String = "101",
    val isCardFrozen: Boolean = false,
    val dailyLimit: Double = 10000.0,
    val spentToday: Double = 0.0
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // SENT, RECEIVED, TOP_UP, RECHARGE, BILL_PAYMENT
    val amount: Double,
    val peerName: String,
    val peerUpi: String,
    val note: String = "",
    val emojiReaction: String = "",
    val category: String = "General", // Food, Entertainment, Bills, Lifestyle, etc.
    val status: String = "SUCCESS" // SUCCESS, FAILED
)

@Entity(tableName = "security_logs")
data class SecurityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String, // SYSTEM, SECURITY, FRAUD_ALERT, MALWARE
    val message: String,
    val severity: String // INFO, WARNING, CRITICAL
)

@Entity(tableName = "split_bills")
data class SplitBill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val totalAmount: Double,
    val dividedAmount: Double,
    val payers: String, // Comma separated list of member labels
    val creatorName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSettled: Boolean = false
)

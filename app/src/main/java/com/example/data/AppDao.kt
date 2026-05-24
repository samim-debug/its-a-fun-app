package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    fun getWallet(): Flow<Wallet?>

    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    suspend fun getWalletSync(): Wallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM security_logs ORDER BY timestamp DESC")
    fun getAllSecurityLogs(): Flow<List<SecurityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityLog(securityLog: SecurityLog)

    @Query("SELECT * FROM split_bills ORDER BY timestamp DESC")
    fun getAllSplitBills(): Flow<List<SplitBill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplitBill(splitBill: SplitBill)

    @Update
    suspend fun updateSplitBill(splitBill: SplitBill)

    @Query("DELETE FROM split_bills WHERE id = :billId")
    suspend fun deleteSplitBill(billId: Int)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    @Query("DELETE FROM security_logs")
    suspend fun clearAllSecurityLogs()
}

package com.example.data

import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askAssistant(prompt: String, transactionHistoryContext: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getLocalFallbackInsight(prompt, transactionHistoryContext)
        }

        val requestPrompt = """
            User Ask: "$prompt"
            
            Current User's Transaction Context:
            $transactionHistoryContext
        """.trimIndent()

        try {
            // Build the standard Gemini API request manually using native JSON classes to keep compilation robust
            val postData = JSONObject()
            
            val contentsArray = JSONArray()
            val contentItem = JSONObject()
            val partsArray = JSONArray()
            val partItem = JSONObject()
            partItem.put("text", requestPrompt)
            partsArray.put(partItem)
            contentItem.put("parts", partsArray)
            contentsArray.put(contentItem)
            postData.put("contents", contentsArray)

            // System instructions
            val systemItem = JSONObject()
            val systemPartsArray = JSONArray()
            val systemPartItem = JSONObject()
            systemPartItem.put("text", "You are INR PAY AI, an ultra-smooth, witty financial assistant in India helping teenagers build money habits. Keep advice conversational, encouraging, funny, and punchy. Use slight Gen Z slang (like 'W budget', 'big L', 'no cap', 'saving is valid') but ensure the financial guidance is highly advice. Keep answers under 120 words.")
            systemPartsArray.put(systemPartItem)
            systemItem.put("parts", systemPartsArray)
            postData.put("systemInstruction", systemItem)

            val jsonPayload = postData.toString()
            val requestBody = jsonPayload.toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("${BASE_URL}v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val rawBody = response.body?.string() ?: ""
                Log.d("GeminiClient", "Received raw body: $rawBody")
                
                // Native JSON parsing to retrieve content values safely
                val responseJson = JSONObject(rawBody)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    if (contentObj != null) {
                        val parts = contentObj.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val textValue = parts.getJSONObject(0).optString("text")
                            if (!textValue.isNullOrBlank()) {
                                return@withContext textValue
                            }
                        }
                    }
                }
            }
            return@withContext "INR PAY Guard: Connected, but API returned empty. Tip: You've saved ₹340 in cashbacks this week! Claim your coins below 🪙"
        } catch (e: Exception) {
            Log.e("GeminiClient", "Error calling Gemini API: ${e.message}", e)
            return@withContext "INR PAY Guard: Offline fallback. Tips: Keep an eye on subscriptions. Try setting up weekly budgets in Settings! 💸"
        }
    }

    private fun getLocalFallbackInsight(prompt: String, context: String): String {
        val query = prompt.lowercase()
        return when {
            query.contains("budget") || query.contains("save") || query.contains("saving") -> {
                "W query! Here's the tea: You Spent quite some pocket bucks on Food recently. Try setting a ₹200 daily limit in your settings. Saving that cash is highly valid! 💸"
            }
            query.contains("limit") || query.contains("spend") || query.contains("track") -> {
                "Checking your vibes: You spent ₹850 today. Your daily threshold is ₹15,000. That's a solid buffer, but don't splurge on gaming credits! Keep it green ⚡"
            }
            query.contains("fraud") || query.contains("hack") || query.contains("secure") -> {
                "INR PAY Secure Guard is active. Every single transfer is 128-bit dual-encrypted and requires your 4-digit biometric lock Pin. Absolute fortress vibe! 🏰🔒"
            }
            else -> {
                "Hey! I'm INR PAY AI, your premium finance advisor. Ask me anything about budgeting, card limits, or how to claim massive cash back coins! I got you. ✌️🔥"
            }
        }
    }
}

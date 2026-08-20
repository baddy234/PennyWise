package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CurrencyRate(
    val code: String,
    val name: String,
    val symbol: String,
    val rateVsUsd: Double
)

object CurrencyRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Default fallback rates vs USD
    val fallbackRates = mapOf(
        "USD" to CurrencyRate("USD", "US Dollar", "$", 1.0),
        "EUR" to CurrencyRate("EUR", "Euro", "€", 0.92),
        "GBP" to CurrencyRate("GBP", "British Pound", "£", 0.79),
        "JPY" to CurrencyRate("JPY", "Japanese Yen", "¥", 155.20),
        "CAD" to CurrencyRate("CAD", "Canadian Dollar", "CA$", 1.38),
        "AUD" to CurrencyRate("AUD", "Australian Dollar", "A$", 1.52),
        "CHF" to CurrencyRate("CHF", "Swiss Franc", "CHF", 0.89),
        "CNY" to CurrencyRate("CNY", "Chinese Yuan", "¥", 7.23),
        "INR" to CurrencyRate("INR", "Indian Rupee", "₹", 83.45),
        "BRL" to CurrencyRate("BRL", "Brazilian Real", "R$", 5.65),
        "SGD" to CurrencyRate("SGD", "Singapore Dollar", "S$", 1.35),
        "MXN" to CurrencyRate("MXN", "Mexican Peso", "MX$", 18.20),
        "NGN" to CurrencyRate("NGN", "Nigerian Naira", "₦", 1580.0),
        "KES" to CurrencyRate("KES", "Kenyan Shilling", "KSh", 129.5),
        "ZAR" to CurrencyRate("ZAR", "South African Rand", "R", 18.10),
        "AED" to CurrencyRate("AED", "UAE Dirham", "AED", 3.67)
    )

    private var cachedRates: Map<String, CurrencyRate> = fallbackRates
    var lastUpdatedTime: Long = System.currentTimeMillis()
        private set

    suspend fun fetchLatestRates(): Result<Map<String, CurrencyRate>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://open.er-api.com/v6/latest/USD")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (body != null) {
                        val json = JSONObject(body)
                        val ratesJson = json.optJSONObject("rates")
                        if (ratesJson != null) {
                            val newRates = mutableMapOf<String, CurrencyRate>()
                            fallbackRates.forEach { (code, baseMeta) ->
                                val rate = ratesJson.optDouble(code, baseMeta.rateVsUsd)
                                newRates[code] = baseMeta.copy(rateVsUsd = rate)
                            }
                            cachedRates = newRates
                            lastUpdatedTime = System.currentTimeMillis()
                            return@withContext Result.success(newRates)
                        }
                    }
                }
            }
            Result.success(cachedRates)
        } catch (e: Exception) {
            // Return cached/fallback rates on network error
            Result.success(cachedRates)
        }
    }

    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val fromRate = cachedRates[fromCurrency]?.rateVsUsd ?: 1.0
        val toRate = cachedRates[toCurrency]?.rateVsUsd ?: 1.0
        val amountInUsd = amount / fromRate
        return amountInUsd * toRate
    }

    fun getRates(): Map<String, CurrencyRate> = cachedRates
}

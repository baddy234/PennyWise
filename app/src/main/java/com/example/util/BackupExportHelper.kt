package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.local.entity.TransactionEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupExportHelper {

    private val fullDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeOnlyFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun generateCsv(
        transactions: List<TransactionEntity>,
        currencySymbol: String = "$"
    ): String {
        val sb = StringBuilder()
        // Header according to standard CSV financial specifications
        sb.append("ID,Date,Time,Type,Category,Title,Amount,Currency,Payment Method,Fund,Notes\n")
        
        for (item in transactions) {
            val dateObj = Date(item.dateMillis)
            val dateStr = dateOnlyFormat.format(dateObj)
            val timeStr = timeOnlyFormat.format(dateObj)
            val escapedTitle = escapeCsvField(item.title)
            val escapedCategory = escapeCsvField(item.category)
            val escapedPaymentMethod = escapeCsvField(item.paymentMethod)
            val escapedFund = escapeCsvField(item.fundName ?: "None")
            val escapedNote = escapeCsvField(item.note)
            val formattedAmount = String.format(Locale.US, "%.2f", item.amount)

            sb.append("${item.id},")
                .append("$dateStr,")
                .append("$timeStr,")
                .append("${item.type},")
                .append("\"$escapedCategory\",")
                .append("\"$escapedTitle\",")
                .append("$formattedAmount,")
                .append("\"$currencySymbol\",")
                .append("\"$escapedPaymentMethod\",")
                .append("\"$escapedFund\",")
                .append("\"$escapedNote\"\n")
        }
        return sb.toString()
    }

    private fun escapeCsvField(value: String): String {
        return value.replace("\"", "\"\"").replace("\n", " ").replace("\r", " ")
    }

    /**
     * Writes CSV content directly to a user-selected SAF Uri.
     */
    fun saveCsvToUri(context: Context, uri: Uri, csvContent: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Saves CSV file directly to the device's public Downloads directory.
     */
    fun saveCsvToDownloads(context: Context, csvContent: String, filename: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/BudgetLens")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
                        outputStream.flush()
                    }
                    true
                } else {
                    false
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val appDir = File(downloadsDir, "BudgetLens")
                if (!appDir.exists()) appDir.mkdirs()
                val targetFile = File(appDir, filename)
                FileOutputStream(targetFile).use { outputStream ->
                    outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Shares CSV as a genuine file attachment using FileProvider.
     */
    fun shareCsvFile(context: Context, csvContent: String, filename: String) {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, filename)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Financial Backup Export ($filename)")
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share or Save CSV Backup").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to sharing as text if file provider fails
            shareTextFile(context, csvContent, filename, "text/csv")
        }
    }

    fun generateJsonBackup(
        transactions: List<TransactionEntity>,
        limits: List<BudgetLimitEntity>
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        root.put("exportedAt", fullDateFormat.format(Date()))

        val txArray = JSONArray()
        for (t in transactions) {
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("amount", t.amount)
            obj.put("category", t.category)
            obj.put("type", t.type)
            obj.put("dateMillis", t.dateMillis)
            obj.put("note", t.note)
            obj.put("paymentMethod", t.paymentMethod)
            if (t.fundId != null) obj.put("fundId", t.fundId)
            if (t.fundName != null) obj.put("fundName", t.fundName)
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        val limitsArray = JSONArray()
        for (l in limits) {
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("periodType", l.periodType)
            obj.put("categoryName", l.categoryName ?: JSONObject.NULL)
            obj.put("limitAmount", l.limitAmount)
            obj.put("isEnabled", l.isEnabled)
            obj.put("notifyThresholdPercent", l.notifyThresholdPercent)
            limitsArray.put(obj)
        }
        root.put("limits", limitsArray)

        return root.toString(2)
    }

    data class ParsedBackup(
        val transactions: List<TransactionEntity>,
        val limits: List<BudgetLimitEntity>
    )

    fun parseJsonBackup(jsonString: String): ParsedBackup {
        val root = JSONObject(jsonString)
        val transactions = mutableListOf<TransactionEntity>()
        val limits = mutableListOf<BudgetLimitEntity>()

        if (root.has("transactions")) {
            val txArray = root.getJSONArray("transactions")
            for (i in 0 until txArray.length()) {
                val obj = txArray.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        id = obj.optLong("id", 0),
                        title = obj.optString("title", "Untitled"),
                        amount = obj.optDouble("amount", 0.0),
                        category = obj.optString("category", "Other"),
                        type = obj.optString("type", "EXPENSE"),
                        dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                        note = obj.optString("note", ""),
                        paymentMethod = obj.optString("paymentMethod", "Card"),
                        fundId = if (obj.has("fundId") && !obj.isNull("fundId")) obj.optLong("fundId") else null,
                        fundName = if (obj.has("fundName") && !obj.isNull("fundName")) obj.optString("fundName") else null
                    )
                )
            }
        }

        if (root.has("limits")) {
            val lArray = root.getJSONArray("limits")
            for (i in 0 until lArray.length()) {
                val obj = lArray.getJSONObject(i)
                val catName = if (obj.isNull("categoryName")) null else obj.optString("categoryName", null)
                limits.add(
                    BudgetLimitEntity(
                        id = obj.optLong("id", 0),
                        periodType = obj.optString("periodType", "MONTHLY"),
                        categoryName = catName,
                        limitAmount = obj.optDouble("limitAmount", 500.0),
                        isEnabled = obj.optBoolean("isEnabled", true),
                        notifyThresholdPercent = obj.optInt("notifyThresholdPercent", 80)
                    )
                )
            }
        }

        return ParsedBackup(transactions, limits)
    }

    fun shareTextFile(context: Context, content: String, filename: String, mimeType: String = "text/plain") {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_SUBJECT, filename)
            putExtra(Intent.EXTRA_TEXT, content)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(shareIntent, "Export $filename").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooser)
    }
}

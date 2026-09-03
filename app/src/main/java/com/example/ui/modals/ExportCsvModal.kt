package com.example.ui.modals

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TimeframeFilter
import com.example.util.BackupExportHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportCsvModal(
    transactions: List<TransactionEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSaveToUri: (Uri, String) -> Unit,
    onSaveToDownloads: (String, String) -> Unit,
    onShare: (String, String) -> Unit
) {
    val context = LocalContext.current
    var selectedTimeframe by remember { mutableStateOf<TimeframeFilter?>(null) } // null = All Time
    var selectedType by remember { mutableStateOf("ALL") } // "ALL", "EXPENSE", "INCOME"
    var showPreview by remember { mutableStateOf(false) }

    val currentDateStr = remember {
        SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
    }
    val defaultFilename = "expenses_backup_$currentDateStr.csv"

    // Calculate filtered list
    val filteredTransactions = remember(transactions, selectedTimeframe, selectedType) {
        var list = transactions
        if (selectedTimeframe != null) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                when (selectedTimeframe) {
                    TimeframeFilter.DAILY -> {}
                    TimeframeFilter.WEEKLY -> set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    TimeframeFilter.MONTHLY -> set(Calendar.DAY_OF_MONTH, 1)
                    TimeframeFilter.YEARLY -> set(Calendar.DAY_OF_YEAR, 1)
                    null -> {}
                }
            }
            val startMillis = cal.timeInMillis
            list = list.filter { it.dateMillis >= startMillis }
        }
        if (selectedType != "ALL") {
            list = list.filter { it.type == selectedType }
        }
        list
    }

    val totalAmount = remember(filteredTransactions) {
        filteredTransactions.sumOf { it.amount }
    }

    val csvString = remember(filteredTransactions, currencySymbol) {
        BackupExportHelper.generateCsv(filteredTransactions, currencySymbol)
    }

    // SAF File Creator Launcher
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            onSaveToUri(uri, csvString)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = glassModalContainerColor(),
        scrimColor = Color.Black.copy(alpha = 0.75f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isAppInDarkTheme()) Slate700 else Color(0xFFCBD5E1))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Export Local CSV Backup",
                            color = textPrimaryColor(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Save financial history to local storage",
                            color = textMutedColor(),
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textMutedColor(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Summary Stats Pill Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selected Records",
                            color = textMutedColor(),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${filteredTransactions.size} transactions",
                            color = textPrimaryColor(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Value",
                            color = textMutedColor(),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%.2f", totalAmount)}",
                            color = AccentEmerald,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeframe Filter
            Text(
                text = "Time Period Filter",
                color = textSecondaryColor(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(glassSurfaceBgColor())
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val timeframeOptions = listOf(
                    null to "All Time",
                    TimeframeFilter.DAILY to "Daily",
                    TimeframeFilter.WEEKLY to "Weekly",
                    TimeframeFilter.MONTHLY to "Monthly",
                    TimeframeFilter.YEARLY to "Yearly"
                )

                timeframeOptions.forEach { (tf, label) ->
                    val isSelected = selectedTimeframe == tf
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.background(Brush.horizontalGradient(listOf(AccentCyan, AccentIndigo)))
                                } else Modifier
                            )
                            .clickable { selectedTimeframe = tf }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else textMutedColor(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Type Filter
            Text(
                text = "Transaction Type",
                color = textSecondaryColor(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Records",
                    "EXPENSE" to "Expenses Only",
                    "INCOME" to "Income Only"
                ).forEach { (typeKey, label) ->
                    val isSelected = selectedType == typeKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentIndigo.copy(alpha = 0.3f) else glassSurfaceBgColor())
                            .border(1.dp, if (isSelected) AccentIndigo else glassBorderColor(), RoundedCornerShape(8.dp))
                            .clickable { selectedType = typeKey }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else textMutedColor(),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPreview = !showPreview }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (showPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showPreview) "Hide CSV Format Preview" else "Preview CSV Output Format",
                        color = AccentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    imageVector = if (showPreview) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = showPreview) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Columns: ID, Date, Time, Type, Category, Title, Amount, Currency, Payment Method, Notes",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = csvString.lines().take(5).joinToString("\n") + if (csvString.lines().size > 5) "\n... (${csvString.lines().size - 5} more lines)" else "",
                            color = Slate200,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action 1: Save to Custom Storage (SAF Folder / Document picker)
            GlassButton(
                text = "Save CSV to Local Storage (Pick Folder)",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = {
                    createDocumentLauncher.launch(defaultFilename)
                },
                gradient = Brush.horizontalGradient(listOf(AccentEmerald, Color(0xFF0D9488))),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_csv_picker_button")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Action 2: Quick Save to Downloads Folder
            GlassButton(
                text = "Quick Save to Downloads / BudgetLens",
                icon = {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = {
                    onSaveToDownloads(csvString, defaultFilename)
                    onDismiss()
                },
                gradient = Brush.horizontalGradient(listOf(AccentIndigo, AccentViolet)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_save_downloads_button")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Action 3: Share / Send CSV file attachment
            GlassButton(
                text = "Share / Open CSV in Other Apps",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Slate200,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = {
                    onShare(csvString, defaultFilename)
                    onDismiss()
                },
                isSecondary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("share_csv_button")
            )
        }
    }
}

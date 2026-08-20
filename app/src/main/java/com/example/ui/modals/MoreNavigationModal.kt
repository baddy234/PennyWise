package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppNavigationTab
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreNavigationModal(
    currentTab: AppNavigationTab,
    onSelectTab: (AppNavigationTab) -> Unit,
    onOpenExportCsv: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Slate900,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Modal Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "APP NAVIGATION",
                        color = Slate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "More Options",
                        color = Slate50,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassSurfaceDark)
                        .border(1.dp, GlassBorderDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = Slate300
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: Main Pages
            Text(
                text = "PAGES & ANALYTICS",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MoreNavItem(
                    title = "Reports & Analytics",
                    subtitle = "Financial breakdowns, graphs, and category trends",
                    icon = Icons.Default.Insights,
                    accentColor = AccentIndigo,
                    isSelected = currentTab == AppNavigationTab.REPORTS,
                    badgeText = "Insights",
                    onClick = {
                        onSelectTab(AppNavigationTab.REPORTS)
                        onDismiss()
                    },
                    testTag = "more_nav_reports"
                )

                MoreNavItem(
                    title = "Spending Ceilings",
                    subtitle = "Set monthly caps and track overspend warnings",
                    icon = Icons.Default.Tune,
                    accentColor = AccentCyan,
                    isSelected = currentTab == AppNavigationTab.LIMITS,
                    badgeText = "Budgets",
                    onClick = {
                        onSelectTab(AppNavigationTab.LIMITS)
                        onDismiss()
                    },
                    testTag = "more_nav_limits"
                )

                MoreNavItem(
                    title = "Settings & Security",
                    subtitle = "Currency, PIN lock, notifications, & preferences",
                    icon = Icons.Default.Settings,
                    accentColor = AccentViolet,
                    isSelected = currentTab == AppNavigationTab.SETTINGS,
                    badgeText = "Config",
                    onClick = {
                        onSelectTab(AppNavigationTab.SETTINGS)
                        onDismiss()
                    },
                    testTag = "more_nav_settings"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Quick Tools & Data
            Text(
                text = "DATA & UTILITIES",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Export CSV Data",
                    subtitle = "Download transactions",
                    icon = Icons.Default.Download,
                    accentColor = AccentEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDismiss()
                        onOpenExportCsv()
                    },
                    testTag = "more_nav_export"
                )

                QuickActionCard(
                    title = "Subscriptions",
                    subtitle = "Manage recurring",
                    icon = Icons.Default.NotificationsActive,
                    accentColor = AccentRose,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDismiss()
                        onOpenSubscriptions()
                    },
                    testTag = "more_nav_subs"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MoreNavItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        borderGlowColor = if (isSelected) accentColor else GlassBorderDark
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) accentColor else accentColor.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color.White else accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = Slate50,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isSelected) {
                        ShadcnBadge(text = "Active", variant = BadgeVariant.CYAN)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Slate400,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassSurfaceDark)
            .border(1.dp, GlassBorderDark, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag)
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = Slate100,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Slate400,
                fontSize = 10.sp
            )
        }
    }
}

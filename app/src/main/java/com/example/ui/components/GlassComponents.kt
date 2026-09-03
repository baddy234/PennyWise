package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Ambient background with glowing gradient orbs for Frosted Glass depth.
 */
@Composable
fun AmbientGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val isDark = bg == DarkBackground || bg == Color(0xFF030712)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .drawBehind {
                if (isDark) {
                    // Top-left indigo glowing orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x4D4F46E5),
                                Color(0x204338CA),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.05f, size.height * 0.05f),
                            radius = size.width * 0.85f
                        )
                    )

                    // Top-right purple ambient glow orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x389333EA),
                                Color(0x187E22CE),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.95f, size.height * 0.22f),
                            radius = size.width * 0.75f
                        )
                    )

                    // Bottom-center blue ambient glow orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x353B82F6),
                                Color(0x121D4ED8),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.45f, size.height * 0.92f),
                            radius = size.width * 0.85f
                        )
                    )
                } else {
                    // Light mode ambient pastel gradient orbs
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x286366F1),
                                Color(0x0F818CF8),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.05f, size.height * 0.05f),
                            radius = size.width * 0.85f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x2038BDF8),
                                Color(0x0AE0F2FE),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.95f, size.height * 0.22f),
                            radius = size.width * 0.75f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x1E10B981),
                                Color(0x0AD1FAE5),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.45f, size.height * 0.92f),
                            radius = size.width * 0.85f
                        )
                    )
                }
            },
        content = content
    )
}

/**
 * Translucent Glass Card with subtle gradient borders & glowing highlight.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderGlowColor: Color? = null,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isAppInDarkTheme()
    val defaultBg = if (isDark) GlassSurfaceDark else Color(0xE6FFFFFF) // Translucent light glass card
    val effectiveBg = backgroundColor ?: defaultBg

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "glass_press"
    )

    val borderBrush = if (borderGlowColor != null) {
        Brush.linearGradient(
            listOf(
                borderGlowColor.copy(alpha = 0.8f),
                borderGlowColor.copy(alpha = 0.25f),
                if (isDark) GlassBorderDark else Color(0x22000000)
            )
        )
    } else {
        if (isDark) {
            Brush.linearGradient(
                listOf(
                    Color(0x35FFFFFF),
                    Color(0x12FFFFFF),
                    Color(0x20818CF8)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    Color(0x80FFFFFF),
                    Color(0x30CBD5E1),
                    Color(0x406366F1)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(effectiveBg)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        content = content
    )
}

/**
 * Shadcn-inspired Badge pill component.
 */
enum class BadgeVariant {
    DEFAULT,
    SECONDARY,
    OUTLINE,
    DESTRUCTIVE,
    SUCCESS,
    WARNING,
    CYAN
}

@Composable
fun ShadcnBadge(
    text: String,
    variant: BadgeVariant = BadgeVariant.DEFAULT,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    val (bg, textColor, borderCol) = if (isDark) {
        when (variant) {
            BadgeVariant.DEFAULT -> Triple(AccentIndigo.copy(alpha = 0.2f), AccentIndigo, AccentIndigo.copy(alpha = 0.4f))
            BadgeVariant.SECONDARY -> Triple(Slate800.copy(alpha = 0.7f), Slate200, Slate700)
            BadgeVariant.OUTLINE -> Triple(Color.Transparent, Slate300, Slate700)
            BadgeVariant.DESTRUCTIVE -> Triple(AccentRose.copy(alpha = 0.2f), AccentRose, AccentRose.copy(alpha = 0.5f))
            BadgeVariant.SUCCESS -> Triple(Color(0x2810B981), Color(0xFF34D399), Color(0x6610B981))
            BadgeVariant.WARNING -> Triple(Color(0x28F59E0B), Color(0xFFFBBF24), Color(0x66F59E0B))
            BadgeVariant.CYAN -> Triple(Color(0x2838BDF8), AccentCyan, Color(0x6638BDF8))
        }
    } else {
        when (variant) {
            BadgeVariant.DEFAULT -> Triple(AccentIndigo.copy(alpha = 0.15f), Color(0xFF3730A3), AccentIndigo.copy(alpha = 0.35f))
            BadgeVariant.SECONDARY -> Triple(Color(0xFFE2E8F0), Slate900, Color(0xFFCBD5E1))
            BadgeVariant.OUTLINE -> Triple(Color.Transparent, Slate800, Color(0xFF94A3B8))
            BadgeVariant.DESTRUCTIVE -> Triple(AccentRose.copy(alpha = 0.15f), Color(0xFFBE123C), AccentRose.copy(alpha = 0.4f))
            BadgeVariant.SUCCESS -> Triple(Color(0x2010B981), Color(0xFF047857), Color(0x6610B981))
            BadgeVariant.WARNING -> Triple(Color(0x20F59E0B), Color(0xFFB45309), Color(0x66F59E0B))
            BadgeVariant.CYAN -> Triple(Color(0x2038BDF8), Color(0xFF0369A1), Color(0x6638BDF8))
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(999.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Modern Glass Button with gradient fill & hover glow spring response.
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = Brush.horizontalGradient(listOf(AccentIndigo, AccentViolet)),
    textColor: Color? = null,
    icon: (@Composable () -> Unit)? = null,
    isSecondary: Boolean = false
) {
    val isDark = isAppInDarkTheme()
    val effectiveTextColor = textColor ?: if (isSecondary && !isDark) Slate900 else Color.White

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "btn_press"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (isSecondary) {
                    Modifier
                        .background(if (isDark) GlassSurfaceDark else Color(0xFFE2E8F0))
                        .border(
                            1.dp,
                            if (isDark) Brush.linearGradient(listOf(Color(0x35FFFFFF), Color(0x10FFFFFF)))
                            else Brush.linearGradient(listOf(Color(0x66CBD5E1), Color(0x33CBD5E1))),
                            RoundedCornerShape(14.dp)
                        )
                } else {
                    Modifier
                        .background(gradient)
                        .border(1.dp, Brush.linearGradient(listOf(Color(0x55FFFFFF), Color.Transparent)), RoundedCornerShape(14.dp))
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                icon()
                Box(modifier = Modifier.padding(end = 8.dp))
            }
            Text(
                text = text,
                color = effectiveTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )
        }
    }
}

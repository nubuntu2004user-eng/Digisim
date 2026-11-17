package com.example.digisim.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digisim.SettingsViewModel

/**
 * Swiss Style Structural Primitives ("Zurich Drafting Table" & "Basel Dark" Tokens).
 * Implements 1px exposed grid hairlines, 2dp industrial corners, and high-contrast typographic hierarchy.
 */
@Composable
fun SwissHorizontalDivider(settings: SettingsViewModel = SettingsViewModel.default) {
    HorizontalDivider(
        thickness = 1.dp,
        color = settings.borderColor
    )
}

@Composable
fun SwissVerticalDivider(
    modifier: Modifier = Modifier.fillMaxHeight(),
    settings: SettingsViewModel = SettingsViewModel.default
) {
    VerticalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = settings.borderColor
    )
}

@Composable
fun SwissSectionHeader(
    indexAndTitle: String,
    settings: SettingsViewModel = SettingsViewModel.default,
    trailingContent: @Composable (RowScope.() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(settings.panelBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 36.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(14.dp)
                        .background(settings.accentTaupe)
                )
                Text(
                    text = indexAndTitle.uppercase(),
                    color = settings.textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
            if (trailingContent != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    content = trailingContent
                )
            }
        }
        SwissHorizontalDivider(settings)
    }
}

@Composable
fun SwissButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false,
    isActive: Boolean = false,
    isDanger: Boolean = false,
    compact: Boolean = false,
    settings: SettingsViewModel = SettingsViewModel.default,
    leadingContent: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(2.dp)
    val bgColor = when {
        !enabled -> settings.panelBackgroundColor.copy(alpha = 0.5f)
        isDanger -> settings.dangerColor
        isPrimary || isActive -> settings.primaryControlFill
        else -> settings.elevatedSurfaceColor
    }
    val contentColor = when {
        !enabled -> settings.secondaryTextColor.copy(alpha = 0.5f)
        isDanger -> Color.White
        isPrimary || isActive -> settings.primaryControlText
        else -> settings.textColor
    }
    val strokeColor = when {
        !enabled -> settings.borderColor.copy(alpha = 0.5f)
        isDanger -> settings.dangerColor
        isActive -> settings.accentTaupe
        isPrimary -> settings.strongBorderColor
        else -> settings.borderColor
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = if (compact) 28.dp else 32.dp),
        shape = shape,
        color = bgColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, strokeColor)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 8.dp else 12.dp,
                vertical = if (compact) 4.dp else 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isActive && !isDanger) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(settings.accentTaupe)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            if (leadingContent != null) {
                leadingContent()
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = if (compact) 11.sp else 12.sp,
                    fontWeight = if (isPrimary || isActive || isDanger) FontWeight.SemiBold else FontWeight.Medium,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

@Composable
fun SwissMonoBadge(
    text: String,
    settings: SettingsViewModel = SettingsViewModel.default,
    highlight: Boolean = false
) {
    val bg = if (highlight) settings.accentTaupe.copy(alpha = 0.2f) else settings.panelBackgroundColor
    val border = if (highlight) settings.accentTaupe else settings.borderColor
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(2.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = settings.textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun SwissTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = true,
    settings: SettingsViewModel = SettingsViewModel.default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        if (!label.isNullOrEmpty()) {
            Text(
                text = label.uppercase(),
                color = settings.secondaryTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = TextStyle(
                color = settings.textColor,
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif
            ),
            placeholder = if (placeholder != null) {
                {
                    Text(
                        text = placeholder,
                        color = settings.secondaryTextColor,
                        fontSize = 12.sp
                    )
                }
            } else null,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = settings.elevatedSurfaceColor,
                unfocusedContainerColor = settings.elevatedSurfaceColor,
                focusedBorderColor = settings.accentTaupeDark,
                unfocusedBorderColor = settings.borderColor,
                focusedTextColor = settings.textColor,
                unfocusedTextColor = settings.textColor,
                cursorColor = settings.textColor
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

package com.orbits.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A horizontal scrollable list of circular/rounded category filter tag highlights.
 * Styled in the Instagram highlights bar aesthetic.
 *
 * @param tags List of distinct tag strings.
 * @param selectedTag Currently filtered tag.
 * @param onTagSelect Callback when a tag chip is clicked.
 */
@Composable
fun TagPills(
    tags: List<String>,
    selectedTag: String?,
    onTagSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false
) {
    val height = if (isCollapsed) 24.dp else 36.dp
    val fontSize = if (isCollapsed) 11.sp else 13.sp
    
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = if (isCollapsed) 4.dp else 8.dp)
    ) {
        item {
            TagPillItem(
                text = "All",
                isSelected = selectedTag == null,
                onClick = { onTagSelect(null) },
                height = height,
                fontSize = fontSize
            )
        }

        items(tags) { tag ->
            TagPillItem(
                text = tag,
                isSelected = tag == selectedTag,
                onClick = { onTagSelect(tag) },
                height = height,
                fontSize = fontSize
            )
        }
    }
}

/**
 * Individual tag item styled like an Instagram highlight circle.
 *
 * @param text The tag name.
 * @param isSelected Check if chip is active.
 * @param onClick Trigger click action.
 */
@Composable
fun TagPillItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    height: androidx.compose.ui.unit.Dp = 36.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 13.sp
) {
    val backgroundColor = if (isSelected) Color(0xFF0095F6) else Color(0xFF1C1C1E)
    val textColor = if (isSelected) Color.White else Color.LightGray
    val borderColor = if (isSelected) Color.Transparent else Color.Gray.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(height / 2))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium
        )
    }
}

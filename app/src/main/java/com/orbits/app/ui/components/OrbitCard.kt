package com.orbits.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.app.domain.model.Orbit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composable representing a compact contact row styled in the Instagram DM list aesthetic.
 *
 * @param orbit Contact entity info.
 * @param index Index in the list for staggered entrance animation.
 * @param onViewClick Callback when "View" button is clicked.
 * @param onMoreClick Callback when "..." options menu is clicked.
 * @param onTogglePin Callback when double-tapped to pin.
 */
@Composable
fun OrbitCard(
    orbit: Orbit,
    index: Int,
    onRowClick: () -> Unit,
    onViewClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = index * 50 // Stagger delay based on list position
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .graphicsLayer {
                alpha = animatedProgress.value
                translationY = (1f - animatedProgress.value) * 30.dp.toPx()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .androidx.compose.foundation.clickable { onRowClick() }
                .background(Color(0xFF121212), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = getInitials(orbit.name)
            
            val ringBrush = when {
                orbit.status == "Pinned" -> Brush.linearGradient(
                    colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFF56040), Color(0xFFFFDC80))
                )
                orbit.tags.any { it.contains("Hire", ignoreCase = true) } -> Brush.linearGradient(
                    colors = listOf(Color(0xFF39FF14), Color(0xFF008080))
                )
                orbit.tags.any { it.contains("Met at Event", ignoreCase = true) } -> Brush.linearGradient(
                    colors = listOf(Color(0xFFFF4500), Color(0xFFFF7F50))
                )
                orbit.tags.any { it.contains("Referred", ignoreCase = true) } -> Brush.linearGradient(
                    colors = listOf(Color(0xFF00008B), Color(0xFF6A5ACD))
                )
                else -> Brush.linearGradient(
                    colors = listOf(Color.LightGray, Color.DarkGray)
                )
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(
                        width = 2.dp,
                        brush = ringBrush,
                        shape = CircleShape
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = orbit.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                orbit.headline?.let {
                    Text(
                        text = it,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                val tagList = orbit.tags.map { "#${it.trim()}" }.take(3)
                if (tagList.isNotEmpty()) {
                    Text(
                        text = tagList.joinToString(" "),
                        color = Color(0xFF0095F6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(72.dp)
                        .background(Color(0xFF0095F6), RoundedCornerShape(8.dp))
                        .androidx.compose.foundation.clickable { onViewClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.LightGray
                    )
                }
            }
        }

    }
}

/**
 * Extracts initials from the contact's name.
 *
 * @param name Full name string.
 * @return Uppercase initials (up to 2 letters).
 */
private fun getInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
        parts.size == 1 -> "${parts[0][0]}".uppercase()
        else -> "?"
    }
}

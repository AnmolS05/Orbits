package com.orbits.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.app.ui.OrbitViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: OrbitViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val activeOrbits = uiState.orbits
    
    val now = System.currentTimeMillis()
    val thirtyDaysMs = TimeUnit.DAYS.toMillis(30)
    
    val activeConnectionsCount = activeOrbits.count { 
        val updatedAt = it.updatedAt ?: it.createdAt
        now - updatedAt <= thirtyDaysMs
    }
    
    val dormantOrbits = activeOrbits.filter { 
        val updatedAt = it.updatedAt ?: it.createdAt
        now - updatedAt > thirtyDaysMs
    }
    
    val tagCounts = mutableMapOf<String, Int>()
    activeOrbits.forEach { orbit ->
        orbit.tags.map { it.trim() }.filter { it.isNotEmpty() }.forEach { tag ->
            tagCounts[tag] = tagCounts.getOrDefault(tag, 0) + 1
        }
    }
    
    val totalTags = tagCounts.values.sum()
    val tagPercentages = tagCounts.mapValues { if (totalTags > 0) it.value.toFloat() / totalTags else 0f }
    val colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFF56040), Color(0xFF39FF14), Color(0xFF0095F6))

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Explore",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Network Breakdown", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (tagPercentages.isEmpty()) {
                    Text("No tags added yet.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF121212), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var colorIndex = 0
                        tagPercentages.toList().sortedByDescending { it.second }.forEach { (tag, pct) ->
                            val color = colors[colorIndex % colors.size]
                            colorIndex++
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(tag, color = Color.White, fontSize = 14.sp)
                                    Text("${(pct * 100).toInt()}%", color = Color.Gray, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Canvas(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                                    drawRoundRect(
                                        color = Color.DarkGray,
                                        size = size,
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                                    )
                                    drawRoundRect(
                                        color = color,
                                        size = size.copy(width = size.width * pct),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("Active Connections", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "$activeConnectionsCount profiles",
                            color = Color(0xFF0095F6),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Modified within the last 30 days.",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                Text("Dormant Orbits Meter", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${dormantOrbits.size} Dormant Profiles",
                            color = Color(0xFFFF4500),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "These contacts haven't been viewed or updated in over 30 days. It might be time for a professional follow-up.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

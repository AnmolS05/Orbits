package com.orbits.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.app.domain.model.Orbit
import com.orbits.app.ui.OrbitViewModel
import com.orbits.app.ui.components.OrbitCard
import com.orbits.app.utils.IntentUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke

/**
 * Main feed screen displaying saved contacts chronologically in an Instagram-DM style list.
 * Supports swipe actions: swipe left to archive, swipe right to delete (with undo option).
 *
 * @param viewModel ViewModel providing states and database operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: OrbitViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog state for three-dot menu options
    var selectedOrbitForOptions by remember { mutableStateOf<Orbit?>(null) }
    
    // Swipe Confirmation State
    var orbitToConfirm by remember { mutableStateOf<Orbit?>(null) }
    var confirmActionType by remember { mutableStateOf<SwipeToDismissBoxValue?>(null) }
    var resetDismissState by remember { mutableStateOf<(() -> Unit)?>(null) }



    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Orbits",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },

        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                if (uiState.orbits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No contacts saved yet.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = uiState.orbits,
                        key = { _, orbit -> orbit.id }
                    ) { index, orbit ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                                    orbitToConfirm = orbit
                                    confirmActionType = value
                                    return@rememberSwipeToDismissBoxState true
                                }
                                false
                            }
                        )
                        
                        if (orbitToConfirm == orbit && resetDismissState == null) {
                            resetDismissState = { scope.launch { dismissState.reset() } }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val direction = dismissState.dismissDirection
                                val color = when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color.Red
                                    SwipeToDismissBoxValue.EndToStart -> Color.Gray
                                    else -> Color.Transparent
                                }
                                val icon = when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                                    SwipeToDismissBoxValue.EndToStart -> Icons.Default.Email // Archive indicator
                                    else -> null
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 24.dp),
                                    contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                                ) {
                                    icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            content = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OrbitCard(
                                        orbit = orbit,
                                        index = index,
                                        onRowClick = {},
                                        onViewClick = {
                                            IntentUtils.openLinkedInProfile(context, orbit.cleanUrl)
                                        },
                                        onMoreClick = { selectedOrbitForOptions = orbit },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    }
    
    // Swipe Confirmation AlertDialog
    if (orbitToConfirm != null && confirmActionType != null) {
        AlertDialog(
            onDismissRequest = {
                resetDismissState?.invoke()
                orbitToConfirm = null
                confirmActionType = null
                resetDismissState = null
            },
            title = { Text(if (confirmActionType == SwipeToDismissBoxValue.StartToEnd) "Delete Profile" else "Archive Profile") },
            text = { Text("Are you sure you want to ${if (confirmActionType == SwipeToDismissBoxValue.StartToEnd) "delete" else "archive"} ${orbitToConfirm?.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        delay(300)
                        if (confirmActionType == SwipeToDismissBoxValue.StartToEnd) {
                            orbitToConfirm?.let { viewModel.deleteOrbit(it) }
                            Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show()
                        } else {
                            orbitToConfirm?.let { viewModel.archiveOrbit(it) }
                            Toast.makeText(context, "Profile archived", Toast.LENGTH_SHORT).show()
                        }
                        orbitToConfirm = null
                        confirmActionType = null
                        resetDismissState = null
                    }
                }) {
                    Text("Confirm", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    resetDismissState?.invoke()
                    orbitToConfirm = null
                    confirmActionType = null
                    resetDismissState = null
                }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1C1C1E),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }

    // Options Modal Dialog (Delete Confirmation Only)
    if (selectedOrbitForOptions != null) {
        AlertDialog(
            onDismissRequest = { selectedOrbitForOptions = null },
            title = { Text("Delete Profile") },
            text = { Text("Are you sure you want to delete ${selectedOrbitForOptions?.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedOrbitForOptions?.let { viewModel.deleteOrbit(it) }
                    Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show()
                    selectedOrbitForOptions = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedOrbitForOptions = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1C1C1E),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }
}

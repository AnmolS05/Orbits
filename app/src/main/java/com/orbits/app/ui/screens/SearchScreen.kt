package com.orbits.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.app.domain.model.Orbit
import com.orbits.app.ui.OrbitViewModel
import com.orbits.app.ui.components.OrbitCard
import com.orbits.app.ui.components.TagPills
import com.orbits.app.utils.IntentUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: OrbitViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val timeFilter by viewModel.timeFilter.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val listState = rememberLazyListState()
    val isCollapsed = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 50
        }
    }

    var selectedOrbitForOptions by remember { mutableStateOf<Orbit?>(null) }
    
    // Swipe Confirmation State
    var orbitToConfirm by remember { mutableStateOf<Orbit?>(null) }
    var confirmActionType by remember { mutableStateOf<SwipeToDismissBoxValue?>(null) }
    var resetDismissState by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Bulk Edit State
    var isBulkEditMode by remember { mutableStateOf(false) }
    val selectedOrbits = remember { mutableStateListOf<Orbit>() }

    val timeFilters = listOf("Added This Week", "Added This Month", "Older than 6 Months")

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0095F6),
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )
                },
                actions = {
                    TextButton(onClick = {
                        isBulkEditMode = !isBulkEditMode
                        if (!isBulkEditMode) selectedOrbits.clear()
                    }) {
                        Text(if (isBulkEditMode) "Cancel" else "Select", color = Color(0xFF0095F6))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {
            if (isBulkEditMode) {
                BottomAppBar(
                    containerColor = Color(0xFF1C1C1E),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "${selectedOrbits.size} Selected",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                selectedOrbits.forEach { viewModel.deleteOrbit(it) }
                                Toast.makeText(context, "Deleted ${selectedOrbits.size} profiles", Toast.LENGTH_SHORT).show()
                                selectedOrbits.clear()
                                isBulkEditMode = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        enabled = selectedOrbits.isNotEmpty()
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            if (uiState.activeTags.isNotEmpty()) {
                TagPills(
                    tags = uiState.activeTags,
                    selectedTag = selectedTag,
                    onTagSelect = { viewModel.selectTag(it) },
                    isCollapsed = isCollapsed.value
                )
            }
            
            TagPills(
                tags = timeFilters,
                selectedTag = timeFilter,
                onTagSelect = { viewModel.selectTimeFilter(it) },
                isCollapsed = isCollapsed.value
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.orbits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matches found.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                    itemsIndexed(
                        items = uiState.orbits,
                        key = { _, orbit -> orbit.id }
                    ) { index, orbit ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (!isBulkEditMode && (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd)) {
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
                            state = if (isBulkEditMode) rememberSwipeToDismissBoxState(confirmValueChange = { false }) else dismissState,
                            backgroundContent = {
                                val direction = dismissState.dismissDirection
                                val color = when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color.Red
                                    SwipeToDismissBoxValue.EndToStart -> Color.Gray
                                    else -> Color.Transparent
                                }
                                val icon = when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                                    SwipeToDismissBoxValue.EndToStart -> Icons.Default.Email
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
                                    AnimatedVisibility(visible = isBulkEditMode) {
                                        Checkbox(
                                            checked = selectedOrbits.contains(orbit),
                                            onCheckedChange = { checked ->
                                                if (checked) selectedOrbits.add(orbit) else selectedOrbits.remove(orbit)
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF0095F6),
                                                uncheckedColor = Color.Gray
                                            )
                                        )
                                    }
                                    
                                    OrbitCard(
                                        orbit = orbit,
                                        index = index,
                                        isBulkEditMode = isBulkEditMode,
                                        onRowClick = {
                                            if (isBulkEditMode) {
                                                if (selectedOrbits.contains(orbit)) selectedOrbits.remove(orbit)
                                                else selectedOrbits.add(orbit)
                                            }
                                        },
                                        onViewClick = {
                                            IntentUtils.openLinkedInProfile(context, orbit.cleanUrl)
                                        },
                                        onMoreClick = {
                                            if (!isBulkEditMode) selectedOrbitForOptions = orbit
                                        },
                                        onTogglePin = {
                                            if (!isBulkEditMode) viewModel.togglePin(orbit)
                                        },
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

    // Options Modal Dialog
    selectedOrbitForOptions?.let { orbit ->
        ModalBottomSheet(
            onDismissRequest = { selectedOrbitForOptions = null },
            containerColor = Color(0xFF1C1C1E),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = orbit.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (orbit.status == "Archived") {
                    Button(
                        onClick = {
                            viewModel.restoreOrbit(orbit)
                            Toast.makeText(context, "${orbit.name} unarchived", Toast.LENGTH_SHORT).show()
                            selectedOrbitForOptions = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0095F6)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        Text("Unarchive Orbit", color = Color.White)
                    }
                }
                if (orbit.isPinned) {
                    Button(
                        onClick = {
                            viewModel.togglePin(orbit)
                            Toast.makeText(context, "${orbit.name} unpinned", Toast.LENGTH_SHORT).show()
                            selectedOrbitForOptions = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Unpin Orbit", color = Color.White)
                    }
                }
                Button(
                    onClick = {
                        viewModel.deleteOrbit(orbit)
                        Toast.makeText(context, "${orbit.name} deleted", Toast.LENGTH_SHORT).show()
                        selectedOrbitForOptions = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Orbit", color = Color.White)
                }
            }
        }
    }
}

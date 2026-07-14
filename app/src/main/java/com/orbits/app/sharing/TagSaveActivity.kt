package com.orbits.app.sharing

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.orbits.app.data.local.OrbitDatabase
import com.orbits.app.data.repository.OrbitRepositoryImpl
import com.orbits.app.domain.model.Orbit
import com.orbits.app.utils.LinkedInParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Translucent activity that opens a dialog-like slide-up overlay to tag and save LinkedIn profiles.
 */
class TagSaveActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedText = if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }

        if (sharedText != null) {
            parseAndSetupUi(sharedText)
        } else {
            finish()
        }
    }

    /**
     * Parses the shared text asynchronously and configures the Compose UI once parsing finishes.
     *
     * @param text Shared content from intent.
     */
    private fun parseAndSetupUi(text: String) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.Default) {
                LinkedInParser.parse(text)
            }

            if (result != null) {
                val (name, cleanUrl) = result
                setContent {
                    MaterialTheme(
                        colorScheme = darkColorScheme(
                            primary = Color(0xFF0095F6),
                            background = Color.Transparent,
                            surface = Color(0xFF1C1C1E),
                            onBackground = Color.White,
                            onSurface = Color.White
                        )
                    ) {
                        TagSaveOverlay(
                            name = name,
                            cleanUrl = cleanUrl,
                            onDismiss = { finish() },
                            onSave = { selectedTags, notes ->
                                saveAndFinish(name, cleanUrl, selectedTags, notes)
                            }
                        )
                    }
                }
            } else {
                Toast.makeText(this@TagSaveActivity, "Invalid LinkedIn profile shared!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Commits the new contact or merges with an existing one in the database, then finishes.
     *
     * @param name Extracted contact name.
     * @param cleanUrl Extracted and cleaned LinkedIn URL.
     * @param selectedTags Set of selected tags.
     * @param notes Custom user notes.
     */
    private fun saveAndFinish(name: String, cleanUrl: String, selectedTags: Set<String>, notes: String) {
        lifecycleScope.launch {
            val db = OrbitDatabase.getDatabase(applicationContext)
            val repository = OrbitRepositoryImpl(db.orbitDao())
            val existing = repository.getByCleanUrl(cleanUrl)

            val newTagsList = selectedTags.toList()
            val finalNotes = notes.trim().ifEmpty { null }

            if (existing != null) {
                val mergedTags = (existing.tags + newTagsList).distinct()
                val mergedNotes = mergeNotes(existing.notes, finalNotes)
                val updated = existing.copy(
                    name = name,
                    tags = mergedTags,
                    notes = mergedNotes,
                    status = "Saved",
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateOrbit(updated)
            } else {
                val newOrbit = Orbit(
                    name = name,
                    cleanUrl = cleanUrl,
                    tags = newTagsList,
                    notes = finalNotes,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertOrbit(newOrbit)
            }
            Toast.makeText(this@TagSaveActivity, "Saved to Orbits!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Merges previously saved notes with new notes, appending them with a line break.
     *
     * @param oldNotes Pre-existing notes.
     * @param newNotes New notes to append.
     * @return Merged notes string, or null if both are null.
     */
    private fun mergeNotes(oldNotes: String?, newNotes: String?): String? {
        return when {
            oldNotes != null && newNotes != null -> "$oldNotes\n$newNotes"
            oldNotes != null -> oldNotes
            newNotes != null -> newNotes
            else -> null
        }
    }
}

/**
 * Slide-up Compose modal representation.
 *
 * @param name Profile name.
 * @param cleanUrl Cleaned profile URL.
 * @param onDismiss Dismiss callback.
 * @param onSave Save callback receiving selected tags and note text.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSaveOverlay(
    name: String,
    cleanUrl: String,
    onDismiss: () -> Unit,
    onSave: (Set<String>, String) -> Unit
) {
    val tags = listOf("Met at Event", "Referred", "Hire", "Follow up")
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    var notesText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false, onClick = {})
                .navigationBarsPadding(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Orbit Contact",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = name,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTags = if (isSelected) {
                                    selectedTags - tag
                                } else {
                                    selectedTags + tag
                                }
                            },
                            label = { Text(tag) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Add note...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onSave(selectedTags, notesText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0095F6)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

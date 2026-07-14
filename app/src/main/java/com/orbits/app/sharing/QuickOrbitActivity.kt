package com.orbits.app.sharing

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.orbits.app.data.local.OrbitDatabase
import com.orbits.app.data.repository.OrbitRepositoryImpl
import com.orbits.app.domain.model.Orbit
import com.orbits.app.utils.LinkedInParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Completely invisible activity that handles the "Orbits: Quick Orbit" share intent.
 *
 * It parses the shared LinkedIn profile text, checks for duplicates in the local
 * Room database, updates or inserts the contact, shows a brief Toast, and finishes.
 */
class QuickOrbitActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedText = if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }

        if (sharedText != null) {
            handleSharedText(sharedText)
        } else {
            finish()
        }
    }

    /**
     * Parses the shared text and performs the database save/merge logic.
     *
     * @param text Raw shared text from LinkedIn.
     */
    private fun handleSharedText(text: String) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.Default) {
                LinkedInParser.parse(text)
            }

            if (result != null) {
                val (name, cleanUrl) = result
                saveProfileToDatabase(name, cleanUrl)
            } else {
                Toast.makeText(this@QuickOrbitActivity, "Invalid LinkedIn profile shared!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Saves or merges the LinkedIn profile into the database.
     *
     * @param name The contact's name.
     * @param cleanUrl The cleaned profile URL.
     */
    private suspend fun saveProfileToDatabase(name: String, cleanUrl: String) {
        val db = OrbitDatabase.getDatabase(applicationContext)
        val repository = OrbitRepositoryImpl(db.orbitDao())
        
        val existing = repository.getByCleanUrl(cleanUrl)

        if (existing != null) {
            val updated = existing.copy(
                status = "Saved",
                updatedAt = System.currentTimeMillis()
            )
            repository.updateOrbit(updated)
        } else {
            val newOrbit = Orbit(
                name = name,
                cleanUrl = cleanUrl,
                createdAt = System.currentTimeMillis()
            )
            repository.insertOrbit(newOrbit)
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(this@QuickOrbitActivity, "Saved to Orbits!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

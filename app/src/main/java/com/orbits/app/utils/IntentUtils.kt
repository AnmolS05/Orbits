package com.orbits.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Utility functions for triggering system intents.
 */
object IntentUtils {

    /**
     * Deep-links and opens the profile URL directly in the LinkedIn native app (or browser).
     *
     * @param context Android context.
     * @param cleanUrl The cleaned profile URL.
     */
    fun openLinkedInProfile(context: Context, cleanUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open LinkedIn profile", Toast.LENGTH_SHORT).show()
        }
    }
}

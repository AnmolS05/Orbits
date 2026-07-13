package com.orbits.app.utils

import java.util.regex.Pattern

/**
 * Utility to parse and clean LinkedIn profile information from shared text intents.
 */
object LinkedInParser {

    private val urlPattern = Pattern.compile(
        "(https?://[a-zA-Z0-9.-]+\\.linkedin\\.com/in/[a-zA-Z0-9-_]+)",
        Pattern.CASE_INSENSITIVE
    )

    private val namePattern = Pattern.compile(
        "(?:Check out\\s+)?(.*?)(?:’s|'s|’|')\\s+profile on LinkedIn",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Parses the shared text to extract the name and cleaned URL of the profile.
     *
     * @param text Shared text content.
     * @return Pair containing extracted Name and Clean URL, or null if parsing fails.
     */
    fun parse(text: String): Pair<String, String>? {
        // 1. Extract URL
        val urlMatcher = urlPattern.matcher(text)
        val url = if (urlMatcher.find()) {
            urlMatcher.group(1)
        } else {
            null
        } ?: return null

        // 2. Clean URL
        val cleanUrl = cleanUrl(url)

        // 3. Extract Name
        val nameMatcher = namePattern.matcher(text)
        val name = if (nameMatcher.find()) {
            nameMatcher.group(1)?.trim() ?: ""
        } else {
            ""
        }

        // Fallback: If name couldn't be extracted, format it from the username path
        val finalName = if (name.isNotEmpty()) {
            name
        } else {
            val username = cleanUrl.substringAfter("/in/").trim('/')
            username.replace("-", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        }

        return Pair(finalName, cleanUrl)
    }

    /**
     * Cleans a LinkedIn URL by removing tracking query parameters.
     *
     * @param url The raw URL.
     * @return Cleaned URL string.
     */
    fun cleanUrl(url: String): String {
        return if (url.contains("?")) {
            url.substringBefore("?")
        } else {
            url
        }
    }
}

package com.orbits.app

import com.orbits.app.utils.LinkedInParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [LinkedInParser].
 */
class LinkedInParserTest {

    /**
     * Test parse standard shared text format.
     */
    @Test
    fun testParseStandard() {
        val text = "Check out John Doe’s profile on LinkedIn: https://www.linkedin.com/in/john-doe?utm_source=share"
        val result = LinkedInParser.parse(text)
        assertNotNull(result)
        assertEquals("John Doe", result?.first)
        assertEquals("https://www.linkedin.com/in/john-doe", result?.second)
    }

    /**
     * Test parse standard shared text format with curly apostrophe.
     */
    @Test
    fun testParseStandardCurlyApostrophe() {
        val text = "Check out Jane Doe’ profile on LinkedIn: https://www.linkedin.com/in/jane-doe?utm_source=share"
        val result = LinkedInParser.parse(text)
        assertNotNull(result)
        assertEquals("Jane Doe", result?.first)
        assertEquals("https://www.linkedin.com/in/jane-doe", result?.second)
    }

    /**
     * Test fallback when only URL is present.
     */
    @Test
    fun testParseFallback() {
        val text = "https://www.linkedin.com/in/tanushka-poojary?miniProfileId=123"
        val result = LinkedInParser.parse(text)
        assertNotNull(result)
        assertEquals("Tanushka Poojary", result?.first)
        assertEquals("https://www.linkedin.com/in/tanushka-poojary", result?.second)
    }

    /**
     * Test invalid string returns null.
     */
    @Test
    fun testParseInvalid() {
        val text = "Checkout my cool website at https://google.com"
        val result = LinkedInParser.parse(text)
        assertNull(result)
    }
}

# Orbits

Orbits is a lightweight, local-first Android app designed to help you instantly bookmark LinkedIn profiles without ever leaving the LinkedIn app. With a clean, high-density aesthetic inspired by the Instagram Direct Messages list, Orbits makes managing your professional network fast and seamless.

## Features

### Seamless Sharing
Orbits integrates directly into the Android system share sheet, offering two distinct ways to save profiles from LinkedIn:
* **Quick Orbit:** A frictionless, one-tap save. Runs entirely in the background and instantly saves the profile with a brief confirmation toast.
* **Tag & Save:** Opens a translucent, slide-up modal over LinkedIn. It allows you to quickly append tags (e.g., `Met at Event`, `Hire`, `Follow up`) and custom notes before saving.

### Smart Data Extraction & Duplicate Handling
* **Regex-Driven Extraction:** Automatically parses LinkedIn share links to extract the person's name and the clean profile URL, stripping out unnecessary tracking parameters.
* **Duplicate Detection:** If a profile is already saved, new tags and notes are seamlessly merged. The profile is then bumped to the top of your recent list.

### Beautiful, Scannable UI
* **Compact Main Feed ("My Orbits"):** Displays 5 to 6 profiles per screen without scrolling.
* **Instagram Story-Style Reminders:** Avatars feature a colorful gradient ring if a follow-up reminder is pending.
* **Quick Actions:** Easily view a saved profile directly in the native LinkedIn app with a single tap, or swipe right/left to archive or delete profiles.

## Technical Stack
* **Target Platform:** Android (Native)
* **UI Framework:** Jetpack Compose for highly responsive and clean layouts
* **Database:** SQLite / Room (Local-only storage)
* **Key Android Features:** Intent Filters (MimeType: `text/plain`), Background Services, System Share Sheet Integration

## Local Database Schema
The app uses a simplified local SQLite database schema to store profile information including names, clean URLs, tags, notes, reminders, and connection status.

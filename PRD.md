This is a concise, non-bloated Product Requirement Document (PRD) designed to be highly readable for **Google Antigravity** agents. It outlines the technical architecture, visual layout, and background logic of the **Orbits** Android app.

---

# Product Requirement Document (PRD): Orbits

## 1. Product Overview
**Orbits** is a lightweight, local-first Android app (APK) that allows users to instantly bookmark LinkedIn profiles using the system share sheet without leaving the LinkedIn app. The in-app user interface is styled after the high-density, clean aesthetic of the Instagram Direct Messages (DM) list.

---

## 2. Core Technical Specifications
*   **Target Platform:** Android (Native)
*   **UI Framework:** Jetpack Compose (highly responsive, clean layouts)
*   **Database:** SQLite / Room (Local-only storage)
*   **Key Android Features:** Intent Filters (MimeType: `text/plain`), Background Services, System Share Sheet Integration.

---

## 3. The Sharing Architecture (Share Targets)
The app must expose **two separate activities** in the Android system share sheet:

### A. Intent Target 1: `Orbits: Quick Orbit`
*   **Trigger:** Click "Share" in LinkedIn ➔ Tap "Orbits: Quick Orbit".
*   **User Experience:** 
    1. Runs instantly as a background task. No UI container opens.
    2. Runs the **Data Cleansing & Extraction Pipeline** (see Section 5).
    3. Saves the profile to the local database.
    4. Displays a brief system toast: *"Saved to Orbits!"*
    5. Terminates the process immediately.

### B. Intent Target 2: `Orbits: Tag & Save`
*   **Trigger:** Click "Share" in LinkedIn ➔ Tap "Orbits: Tag & Save".
*   **User Experience:**
    1. Opens a translucent, slide-up modal overlay over the active LinkedIn app.
    2. Runs the **Data Cleansing & Extraction Pipeline** (see Section 5).
    3. Displays the following UI components in the modal:
        *   **Quick-Tap Tags (Horizontal Row):** `Met at Event`, `Referred`, `Hire`, `Follow up`. Tapping these automatically appends them as tags.
        *   **Custom Note Input:** A text field to write notes.
        *   **Save Button:** Tap to commit data to the database and dismiss the modal overlay.

---

## 4. Visual Layout & Screen Design (Instagram DM Style)
The main app interface must be compact and scannable, displaying **5 to 6 profiles per screen** without scrolling.

### A. Main Feed ("My Orbits")
*   **Top Bar:**
    *   Left: Stylized "Orbits" logo in clean typography.
    *   Right: Clean filter/search icon.
*   **Compact Rows (100dp height limit per row):**
    *   **Left (Avatar):** Initials centered inside a clean circle. 
        *   *If an active follow-up reminder is pending:* The avatar circle is surrounded by a colorful gradient ring (styled like an **Instagram Story ring**).
    *   **Center-Left (Details Stack):**
        *   *Line 1 (Bold):* Extracted Name (size 16sp).
        *   *Line 2 (Muted Gray):* Title / Company (size 12sp).
        *   *Line 3 (Light Blue/Gray):* Horizontal list of tags (e.g., `#Founder` `#Hire`).
    *   **Right (Action buttons):**
        *   A rounded, flat action button labeled **"View"** (styled like Instagram’s blue *Follow* or gray *Message* button). Tapping this deep-links and opens the profile directly in the LinkedIn native app.
        *   A subtle three-dot menu icon (`...`) for editing and deleting.

### B. Navigation Bar (Bottom Tabs)
1.  **Home (House Icon):** Displays the chronological compact feed of "My Orbits".
2.  **Search (Magnifying Glass Icon):** Top search bar with category-filter circles (styled like Instagram Story Highlights) representing active tags.
3.  **Reminders (Heart or Alarm Icon):** A dedicated section displaying only profiles with due follow-up tasks.

---

## 5. Logic & Processing Rules

### A. Data Cleansing & Extraction Pipeline (Regex-driven)
LinkedIn mobile app shares look like this:  
`"Check out [First Name] [Last Name]’s profile on LinkedIn: https://www.linkedin.com/in/[username]?utm_source=..."`

When shared, the app must run a regex pattern to extract:
1.  **Name:** The text between `"Check out "` and `"’s profile on LinkedIn"`.
2.  **URL:** The text starting with `https://www.linkedin.com/in/` up to the first space or line break.
3.  **URL Clean-up:** Strip out all trailing query parameters (e.g., `?utm_source=...`, `?miniProfileId=...`) to ensure the URL saved is clean.

### B. Duplicate Detection
*   Before saving a new record, query the database for the clean URL.
*   **If URL exists:**
    1. Merge the new notes/tags with the existing ones.
    2. Update the timestamp to the current time.
    3. Bring the profile row to the very top of the "Recently Added" list on the Home feed.

### C. Swipe Actions (Main Feed List)
*   **Swipe Left:** Move the profile card to "Archived" status (removes it from the active list but does not delete the data).
*   **Swipe Right:** Permanently delete the profile from the database with a quick undo-banner option.

---

## 6. Local Database Schema (Simplified)
```sql
CREATE TABLE orbits (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    clean_url TEXT NOT NULL UNIQUE,
    headline TEXT,
    tags TEXT, -- Comma-separated list
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    reminder_date TIMESTAMP NULL,
    status TEXT DEFAULT 'Saved' -- 'Saved', 'Connected', 'In Conversation', 'Archived'
);
```
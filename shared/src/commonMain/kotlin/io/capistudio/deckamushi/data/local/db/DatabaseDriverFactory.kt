package io.capistudio.deckamushi.data.local.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Creates the platform-specific SQLDelight driver.
 *
 * This is pure infrastructure plumbing: shared code depends only on the resulting driver, while
 * each platform decides how the underlying SQLite driver is created.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
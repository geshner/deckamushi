package io.capistudio.deckamushi.data.local.db

import io.capistudio.deckamushi.db.AppDatabase

class AppDatabaseProvider(
    driverFactory: DatabaseDriverFactory,
) {
    val db: AppDatabase = AppDatabase(driverFactory.createDriver())
}
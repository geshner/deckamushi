package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import org.koin.dsl.module

actual fun platformModule() = module {
    single<VersionCache> { VersionCacheFactory().create() }
    single { AppDatabaseProvider(DatabaseDriverFactory()) }
}

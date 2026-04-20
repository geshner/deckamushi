package io.capistudio.deckamushi.di

import io.capistudio.deckamushi.data.local.AndroidVersionCache
import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.db.AppDatabaseProvider
import io.capistudio.deckamushi.data.local.db.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<VersionCache> { AndroidVersionCache(get()) }
    single { AppDatabaseProvider(DatabaseDriverFactory(get())) }
}

package io.capistudio.deckamushi.data.remote

import io.capistudio.deckamushi.data.remote.dto.CardDto
import io.capistudio.deckamushi.data.remote.dto.VersionDto

interface CardDataApi {
    suspend fun fetchVersion(etag: String? = null): RemoteResult<VersionDto>
    suspend fun fetchCards(): RemoteResult<List<CardDto>>
}
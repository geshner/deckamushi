package io.capistudio.deckamushi.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.capistudio.deckamushi.data.paging.CardsPagingSource
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.CardSummary
import io.capistudio.deckamushi.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow

class GetCardsPageUseCase(
    private val repository: CardRepository,
) {

    operator fun invoke(query: String? = null): Flow<PagingData<CardSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 60
            ),
            pagingSourceFactory = { CardsPagingSource(repository, query) }
        ).flow
    }
}
package io.capistudio.deckamushi.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.capistudio.deckamushi.domain.model.OwnedCard
import io.capistudio.deckamushi.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow

class GetOwnedCardsUseCase(
    private val repository: CardRepository,
) {

    operator fun invoke(): Flow<PagingData<OwnedCard>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 60
            ),
            pagingSourceFactory = { repository.getOwnedCardsPagingSource() }
        ).flow
    }
}

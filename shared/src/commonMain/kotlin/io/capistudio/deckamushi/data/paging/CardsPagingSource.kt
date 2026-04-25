package io.capistudio.deckamushi.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.model.CardSummary
import io.capistudio.deckamushi.domain.repository.CardRepository

class CardsPagingSource(
    private val repository: CardRepository,
    private val query: String? = null,
) : PagingSource<Int, CardSummary>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CardSummary> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize

            val cards = if (query.isNullOrBlank()) {
                repository.getCardsPage(limit, offset)
            } else {
                repository.searchCardsByName(query, limit, offset)
            }

            LoadResult.Page(
                data = cards,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (cards.isEmpty()) null else offset + cards.size
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CardSummary>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}

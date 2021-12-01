package com.example.marvelcharacters.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.marvelcharacters.data.local.models.CharactersEntity
import com.example.marvelcharacters.utilities.Mappers

class MarvelPagingSource(
    private val service: MarvelService,
) : PagingSource<Int, CharactersEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharactersEntity> {
        return try {
            val response = service.getListCharacters()
            val characters = Mappers.mapperToListEntity(response.data.characters)
            LoadResult.Page(
                data = characters,
                prevKey = null,
                nextKey = null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharactersEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}

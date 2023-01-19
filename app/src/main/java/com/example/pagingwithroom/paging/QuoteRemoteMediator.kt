package com.example.pagingwithroom.paging

import androidx.paging.*
import androidx.room.withTransaction
import com.example.pagingwithroom.db.QuoteDatabase
import com.example.pagingwithroom.models.QuoteRemoteKeys
import com.example.pagingwithroom.retrofit.QuoteAPI
import com.example.pagingwithroom.models.Result

@ExperimentalPagingApi
class QuoteRemoteMediator(
    private val quoteAPI: QuoteAPI, private val quoteDatabase: QuoteDatabase
) : RemoteMediator<Int, Result>() {

    // 1. Fetch Quotes from API
    // 2. Save these Quotes + RemoteKeys Data into DB
    // 3. Logic for States - REFRESH, PREPEND, APPEND

    private val quoteDao = quoteDatabase.quoteDao()
    private val quoteRemoteKeysDao = quoteDatabase.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Result>): MediatorResult {
        return try {

            val currentPage = when(loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    nextPage // todo 1
                }
            }

            // 1. Fetch Quotes from API
            val response = quoteAPI.getQuotes(currentPage)
            val endOfPaginationReached = response.totalPages == currentPage

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            // 2. Save these Quotes + RemoteKeys Data into DB
            quoteDatabase.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    quoteDao.deleteQuotes()
                    quoteRemoteKeysDao.deleteAllRemoteKeys()
                }

                quoteDao.addQuotes(response.results)

                // for RemoteKeys Table Data
                val keys = response.results.map { quote ->
                    QuoteRemoteKeys(
                        id = quote._id, prevPage = prevPage, nextPage = nextPage
                    )
                }
                quoteRemoteKeysDao.allAllRemoteKeys(keys)
            }
            MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Result>): QuoteRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?._id?.let { id->
                quoteRemoteKeysDao.getRemoteKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Result>): QuoteRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { quote ->
            quoteRemoteKeysDao.getRemoteKeys(id = quote._id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Result>): QuoteRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { quote ->
            quoteRemoteKeysDao.getRemoteKeys(id = quote._id)
        }
    }

}
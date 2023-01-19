package com.example.pagingwithroom.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.pagingwithroom.db.QuoteDatabase
import com.example.pagingwithroom.paging.QuotePagingSource
import com.example.pagingwithroom.paging.QuoteRemoteMediator
import com.example.pagingwithroom.retrofit.QuoteAPI
import javax.inject.Inject

@ExperimentalPagingApi
class QuoteRepository @Inject constructor(private val quoteAPI: QuoteAPI, private val quoteDatabase: QuoteDatabase) {

    fun getQuotes() = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100),
        remoteMediator = QuoteRemoteMediator(quoteAPI, quoteDatabase),
        pagingSourceFactory = { quoteDatabase.quoteDao().getQuotes() } //QuotePagingSource(quoteAPI) for only API calling
    ).liveData
}
package com.example.pagingwithroom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pagingwithroom.models.QuoteRemoteKeys
import com.example.pagingwithroom.models.Result

@Database(entities = [Result::class, QuoteRemoteKeys::class], version = 1)
abstract class QuoteDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
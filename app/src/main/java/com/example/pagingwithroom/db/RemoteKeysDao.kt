package com.example.pagingwithroom.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pagingwithroom.models.QuoteRemoteKeys

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM QuoteRemoteKeys WHERE id =:id")
    suspend fun getRemoteKeys(id: String) : QuoteRemoteKeys

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun allAllRemoteKeys(remoteKeys: List<QuoteRemoteKeys>)

    @Query("DELETE FROM QuoteRemoteKeys")
    suspend fun deleteAllRemoteKeys()
}
package com.rodcollab.matriviaapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rodcollab.matriviaapp.data.model.local.RankingLocal

@Dao
interface RankingDao {
    @Insert
    suspend fun insert(ranking: RankingLocal)
    @Query("SELECT * FROM ranking ORDER BY ranking_createdAt DESC LIMIT 10")
    suspend fun getRanking(): List<RankingLocal>
}
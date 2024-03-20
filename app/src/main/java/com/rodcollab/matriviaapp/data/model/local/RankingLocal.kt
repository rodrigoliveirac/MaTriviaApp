package com.rodcollab.matriviaapp.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ranking")
class RankingLocal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo("ranking_correctAnswers") val correctAnswers: Int,
    @ColumnInfo("ranking_createdAt") val createdAt: Long
)
package com.example.diaryapp.data.repository

import com.example.diaryapp.models.Diaries
import kotlinx.coroutines.flow.Flow

interface MongoRepository {
    fun configureMongoDB()
    fun getAllDiaries(): Flow<Diaries>
}
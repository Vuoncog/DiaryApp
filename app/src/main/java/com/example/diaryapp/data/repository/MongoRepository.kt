package com.example.diaryapp.data.repository

import com.example.diaryapp.models.Diaries
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface MongoRepository {
    fun configureMongoDB()
    fun getAllDiaries(): Flow<Diaries>
    fun getSelectedDiary(diaryId: ObjectId): RequestState<Diary>
    suspend fun addNewDiary(diary: Diary): RequestState<Diary>
}
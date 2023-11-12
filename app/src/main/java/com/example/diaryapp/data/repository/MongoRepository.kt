package com.example.diaryapp.data.repository

import com.example.diaryapp.models.Diaries
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime

interface MongoRepository {
    fun configureMongoDB()
    fun getAllDiaries(): Flow<Diaries>
    fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries>
    fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>>
    suspend fun insertDiary(diary: Diary): RequestState<Diary>
    suspend fun updateDiary(diary: Diary): RequestState<Diary>
    suspend fun deleteDiary(id: ObjectId): RequestState<Diary>
    suspend fun deleteAll(): RequestState<Boolean>
}
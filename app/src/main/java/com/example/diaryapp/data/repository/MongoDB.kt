package com.example.diaryapp.data.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.diaryapp.models.Diaries
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.RequestState
import com.example.diaryapp.utility.Constant.APP_ID
import com.example.diaryapp.utility.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.ZoneId

object MongoDB : MongoRepository {
    private val user = App.create(APP_ID).currentUser
    private lateinit var realm: Realm

    init {
        configureMongoDB()
    }

    override fun configureMongoDB() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>(query = "ownerId == $0", user.id),
                        name = "User's Diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun getAllDiaries(): Flow<Diaries> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "ownerId == $0", user.id)
                    .sort("date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            })
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserIsNotAuthenticated())) }
        }
    }

    override fun getSelectedDiary(diaryId: ObjectId): RequestState<Diary> {
        return if (user != null){
            try {
                val diary = realm.query<Diary>(query = "_id == $0", diaryId).find().first()
                RequestState.Success(diary)
            }catch (e: Exception){
                RequestState.Error(e)
            }
        }else{
            RequestState.Error(UserIsNotAuthenticated())
        }
    }

    override suspend fun addNewDiary(diary: Diary): RequestState<Diary> {
        return if (user != null){
            realm.write {
                try {
                    val addedDiary = copyToRealm(diary.apply { ownerId = user.id })
                    RequestState.Success(diary)
                }catch (e: Exception){
                    RequestState.Error(e)
                }
            }
        }else{
            RequestState.Error(UserIsNotAuthenticated())
        }
    }
}

private class UserIsNotAuthenticated: Exception("User is not authenticated")
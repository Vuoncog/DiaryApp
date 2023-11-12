package com.example.diaryapp.data.repository

import com.example.diaryapp.models.Diaries
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.RequestState
import com.example.diaryapp.utility.Constant.APP_ID
import com.example.diaryapp.utility.toInstant
import com.example.diaryapp.utility.toRealmInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.delete
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

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

    override fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries> {
        val lastMidnight = RealmInstant.from(
            LocalDateTime.of(
                zonedDateTime.toLocalDate(),
                LocalTime.MIDNIGHT
            ).toEpochSecond(zonedDateTime.offset), 0
        )

        val thisMidnight = RealmInstant.from(
            LocalDateTime.of(
                zonedDateTime.toLocalDate().plusDays(1),
                LocalTime.MIDNIGHT
            ).toEpochSecond(zonedDateTime.offset), 0
        )
        return if (user != null) {
            try {
                realm.query<Diary>(
                    query = "ownerId == $0 AND date < $1 AND date >= $2",
                    user.id,
                    thisMidnight,
                    lastMidnight
                )
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserIsNotAuthenticated())) }
        }
    }

    override fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "_id == $0", diaryId).asFlow().map {
                    RequestState.Success(it.list.first())
                }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserIsNotAuthenticated())) }
        }
    }

    override suspend fun insertDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                try {
                    val addedDiary = copyToRealm(diary.apply { ownerId = user.id })
                    RequestState.Success(addedDiary)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserIsNotAuthenticated())
        }
    }

    override suspend fun deleteDiary(id: ObjectId): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                try {
                    val queryDiary =
                        query<Diary>(query = "_id == $0 AND ownerId == $1", id, user.id).find()
                            .first()
                    delete(queryDiary)
                    RequestState.Success(queryDiary)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserIsNotAuthenticated())
        }
    }

    override suspend fun deleteAll(): RequestState<Boolean> {
        return if (user != null) {
            realm.write {
                try {
                    val query = this.query<Diary>("ownerId == $0", user.id).find()
                    delete(query)
                    RequestState.Success(true)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserIsNotAuthenticated())
        }
    }

    override suspend fun updateDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                val queryDiary = query<Diary>(query = "_id == $0", diary._id).find().first()
                run {
                    queryDiary.title = diary.title
                    queryDiary.description = diary.description
                    queryDiary.mood = diary.mood
                    queryDiary.date = diary.date
                    queryDiary.images = diary.images
                    RequestState.Success(data = queryDiary)
                }
            }
        } else {
            RequestState.Error(UserIsNotAuthenticated())
        }
    }
}

private class UserIsNotAuthenticated : Exception("User is not authenticated")
package com.example.diaryapp.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.diaryapp.connectivity.ConnectivityObserver
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.connectivity.NetworkConnectivityObserver
import com.example.diaryapp.data.database.ImageToDeleteDao
import com.example.diaryapp.data.database.entity.ImageToDelete
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.models.Diaries
import com.example.diaryapp.models.RequestState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkConnectivityObserver: NetworkConnectivityObserver,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {
    private lateinit var observeAllDiariesJob: Job
    private lateinit var observeFilteredDiariesJob: Job

    val diaries: MutableState<Diaries> = mutableStateOf(RequestState.Loading)
    var network by mutableStateOf(ConnectivityObserver.Status.UNAVAILABLE)

    var isFiltered by mutableStateOf(false)
        private set


    init {
        diaries.value = RequestState.Loading
        getDiaries()
        viewModelScope.launch {
            networkConnectivityObserver.observe().collect {
                network = it
            }
        }
    }

    fun getDiaries(zonedDateTime: ZonedDateTime? = null) {
        isFiltered = zonedDateTime != null
        if (isFiltered && zonedDateTime != null) {
            observeFilteredDiaries(zonedDateTime = zonedDateTime)
        } else {
            observeAllDiaries()
        }
    }

    private fun observeAllDiaries() {
        observeAllDiariesJob = viewModelScope.launch {
            if (::observeFilteredDiariesJob.isInitialized) {
                observeFilteredDiariesJob.cancelAndJoin()
            }
            MongoDB.getAllDiaries().collect {
                diaries.value = it
            }
        }
    }

    private fun observeFilteredDiaries(zonedDateTime: ZonedDateTime) {
        observeFilteredDiariesJob = viewModelScope.launch {
            if (::observeAllDiariesJob.isInitialized) {
                observeAllDiariesJob.cancelAndJoin()
            }
            MongoDB.getFilteredDiaries(zonedDateTime = zonedDateTime).collect {
                diaries.value = it
            }
        }
    }

    fun eraseAllMemories(
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        if (network == ConnectivityObserver.Status.AVAILABLE) {
            val storage = FirebaseStorage.getInstance().reference
            val user = FirebaseAuth.getInstance().currentUser
            val path = "/${user!!.uid}/images"
            storage.child(path).listAll()
                .addOnSuccessListener {
                    it.items.forEach { ref ->
                        val imagePath = path + "/${ref.name}"
                        storage.child(imagePath)
                            .delete()
                            .addOnFailureListener {
                                viewModelScope.launch(Dispatchers.IO) {
                                    imageToDeleteDao.addDeleteImage(
                                        ImageToDelete(remotePath = imagePath)
                                    )
                                }
                            }
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        val result = MongoDB.deleteAll()
                        if (result is RequestState.Success) {
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        } else if (result is RequestState.Error) {
                            withContext(Dispatchers.Main) {
                                onFailed(result.error)
                            }
                        }
                    }
                }
        } else {
            onFailed(Exception("No Internet Connection."))
        }
    }
}



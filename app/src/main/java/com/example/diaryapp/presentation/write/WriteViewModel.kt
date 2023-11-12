package com.example.diaryapp.presentation.write

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.data.database.ImageToDeleteDao
import com.example.diaryapp.data.database.ImageToUploadDao
import com.example.diaryapp.data.database.entity.ImageToDelete
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.GalleryImage
import com.example.diaryapp.models.GalleryState
import com.example.diaryapp.data.database.entity.ImageToUpload
import com.example.diaryapp.models.Mood
import com.example.diaryapp.models.RequestState
import com.example.diaryapp.utility.Constant.WRITE_ARGUMENT
import com.example.diaryapp.utility.fetchImageFromDatabase
import com.example.diaryapp.utility.toInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val imageToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    private val _galleryState = MutableStateFlow<GalleryState>(GalleryState())
    val galleryState = _galleryState

    private val _editScreen = MutableStateFlow<Boolean>(false)
    val editScreen = _editScreen
    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
        fetchSelectedDiaryData()
    }

    fun changeToEditScreen() {
        _editScreen.value = true
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(key = WRITE_ARGUMENT)
        )
    }

    private fun fetchSelectedDiaryData() {
        if (uiState.selectedDiaryId != null) {
            viewModelScope.launch {
                MongoDB.getSelectedDiary(diaryId = ObjectId.invoke(uiState.selectedDiaryId!!))
                    .catch {
                        RequestState.Error(it)
                    }
                    .collect { diaryData ->
                        if (diaryData is RequestState.Success) {
                            setSelectedDiary(diaryData.data)
                            setTitle(diaryData.data.title)
                            setDescription(diaryData.data.description)
                            setMood(Mood.valueOf(diaryData.data.mood))
                            setDate(diaryData.data.date.toInstant())

                            fetchImageFromDatabase(
                                remoteImagePaths = diaryData.data.images.toList(),
                                onImageDownload = { uri ->
                                    Log.d("WriteViewModel", "URL: $uri")
                                    Log.d(
                                        "WriteViewModel",
                                        "Short URI: ${extractImagePath(uri.toString())}"
                                    )
                                    uiState.images.add(extractImagePath(uri.toString()))
                                    _galleryState.value.images.add(
                                        GalleryImage(
                                            images = uri,
                                            remoteImagePath = extractImagePath(uri.toString())
                                        )
                                    )
                                }
                            )
                        }
                    }
            }
        }
    }

    fun UpdateOrInsert(
        diary: Diary,
        onSucces: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            if (uiState.selectedDiaryId != null) {
                uploadImageToFirebase()
                deleteImageFromFirebase(_galleryState.value.imagesForDeleting.map { it.remoteImagePath })
                updateDiary(
                    diary = diary,
                    onSuccess = onSucces,
                    onError = onError
                )
            } else {
                uploadImageToFirebase()
                insertDiary(
                    diary = diary,
                    onSuccess = onSucces,
                    onError = onError
                )
            }
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val added = MongoDB.insertDiary(diary)
        if (added is RequestState.Success) {
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (added is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(added.error.message.toString())
            }
        }
    }

    private suspend fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val added = MongoDB.updateDiary(diary)
        if (added is RequestState.Success) {
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (added is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(added.error.message.toString())
            }
        }
    }

    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val delete = MongoDB.deleteDiary(ObjectId(uiState.selectedDiaryId!!))
            if (delete is RequestState.Success) {
                withContext(Dispatchers.Main) {
                    deleteImageFromFirebase(uiState.images)
                    onSuccess()
                }
            } else if (delete is RequestState.Error) {
                withContext(Dispatchers.Main) {
                    onError(delete.error.message.toString())
                }
            }
        }
    }

    fun addImage(imageUri: Uri, imageType: String) {
        val imageFirebasePath = "/${FirebaseAuth.getInstance().currentUser?.uid}/images/" +
                "${imageUri.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
        _galleryState.value.addImage(
            galleryImage = GalleryImage(
                images = imageUri,
                remoteImagePath = imageFirebasePath
            )
        )
    }

    fun uploadImageToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        _galleryState.value.images.forEach { galleryImage ->
            val path = storage.child(galleryImage.remoteImagePath)
            path.putFile(galleryImage.images)
                .addOnProgressListener {
                    val sessionUri = it.uploadSessionUri
                    if (sessionUri != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToUploadDao.addUploadImage(
                                imageToUpload = ImageToUpload(
                                    imageUri = galleryImage.images.toString(),
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }
        }
    }

    fun deleteImageFromFirebase(images: List<String>) {
        val storage = FirebaseStorage.getInstance().reference
        images.forEach { galleryImage ->
            val path = storage.child(galleryImage)
            path.delete()
                .addOnFailureListener {
                    viewModelScope.launch(Dispatchers.IO) {
                        imageToDeleteDao.addDeleteImage(
                            ImageToDelete(remotePath = galleryImage)
                        )
                    }
                }
        }
    }

    private fun setSelectedDiary(diary: Diary) {
        uiState = uiState.copy(selectedDiary = diary)
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }

    fun setDate(date: Instant) {
        uiState = uiState.copy(date = date)
    }

    private fun extractImagePath(fullImageUrl: String): String {
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "/${Firebase.auth.currentUser?.uid}/images/$imageName"
    }
}

data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary = Diary(),
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Normal,
    val date: Instant = Instant.now(),
    val images: MutableList<String> = mutableListOf()
)
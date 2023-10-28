package com.example.diaryapp.presentation.write

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.models.Diary
import com.example.diaryapp.models.Mood
import com.example.diaryapp.models.RequestState
import com.example.diaryapp.utility.Constant.WRITE_ARGUMENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class WriteViewModel(
    val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _editScreen = MutableStateFlow<Boolean>(false)
    val editScreen = _editScreen
    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
        fetchSelectedDiaryData()
    }

    fun changeToEditScreen(){
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
                val diaryData =
                    MongoDB.getSelectedDiary(
                        diaryId = ObjectId.invoke(uiState.selectedDiaryId!!))
                if (diaryData is RequestState.Success) {
                    setSelectedDiary(diaryData.data)
                    setTitle(diaryData.data.title)
                    setDescription(diaryData.data.description)
                    setMood(Mood.valueOf(diaryData.data.mood))
                }
            }
        }
    }

    fun insertDiary(
        diary: Diary,
        onSucces: () -> Unit,
        onError: (String) -> Unit,
    ){
        viewModelScope.launch(Dispatchers.IO) {
            val added = MongoDB.addNewDiary(diary)
            if (added is RequestState.Success){
                withContext(Dispatchers.Main){
                    onSucces()
                }
            }else if (added is RequestState.Error){
                withContext(Dispatchers.Main){
                    onError(added.error.message.toString())
                }
            }
        }
    }

    fun setSelectedDiary(diary: Diary){
        uiState = uiState.copy(selectedDiary = diary)
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }

}

data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary = Diary(),
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Normal,
)
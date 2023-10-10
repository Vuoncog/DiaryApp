package com.example.diaryapp.presentation.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.utility.Constant.APP_ID
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel : ViewModel() {

    var loadingState = mutableStateOf(false)
        private set
    var isLogged = mutableStateOf(false)
        private set

    fun setLoadingState(loadingState: Boolean) {
        this.loadingState.value = loadingState
    }

    fun signInWithMongoDB(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = App.create(APP_ID).login(
                    Credentials.jwt(
                        jwtToken = tokenId
                    )
                ).loggedIn
                withContext(Dispatchers.Main) {
                    if (result){
                        onSuccess()
                        delay(1000)
                        isLogged.value = true
                    }else{
                        onError(Exception("Invalid user"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
}
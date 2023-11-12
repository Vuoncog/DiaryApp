package com.example.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaryapp.data.database.ImageToDeleteDao
import com.example.diaryapp.data.database.ImageToUploadDao
import com.example.diaryapp.navigation.Screen
import com.example.diaryapp.navigation.SetUpNavigation
import com.example.diaryapp.ui.theme.DiaryAppTheme
import com.example.diaryapp.utility.Constant.APP_ID
import com.example.diaryapp.utility.retryToDeleteImagesFromFirebase
import com.example.diaryapp.utility.retryToUploadImagesToFirebase
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imageToUploadDao: ImageToUploadDao

    @Inject
    lateinit var imageToDeleteDao: ImageToDeleteDao

    private lateinit var navController: NavHostController
    private var keepOnScreenCondition = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { keepOnScreenCondition }
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DiaryAppTheme {
                navController = rememberNavController()
                SetUpNavigation(
                    startDestination = getCurrentUser(),
                    onDataLoad = {
                        keepOnScreenCondition = false
                    },
                    navController = navController
                )
            }
        }
        cleanupCheck(
            scope = lifecycleScope,
            imageToUploadDao = imageToUploadDao,
            imageToDeleteDao = imageToDeleteDao
        )
    }

    private fun cleanupCheck(
        scope: CoroutineScope,
        imageToUploadDao: ImageToUploadDao,
        imageToDeleteDao: ImageToDeleteDao
    ) {
        scope.launch(Dispatchers.IO) {
            val failedUploadImage = imageToUploadDao.getAllUploadImages()
            failedUploadImage.forEach {
                retryToUploadImagesToFirebase(
                    imageToUpload = it,
                    onSuccess = {
                        scope.launch(Dispatchers.IO) {
                            imageToUploadDao.cleanupImage(it.id)
                        }
                    }
                )
            }

            val failedDeleteImage = imageToDeleteDao.getAllDeleteImages()
            failedDeleteImage.forEach {
                retryToDeleteImagesFromFirebase(
                    imageToDelete = it,
                    onSuccess = {
                        scope.launch(Dispatchers.IO) {
                            imageToDeleteDao.cleanupImage(it.id)
                        }
                    }
                )
            }
        }
    }

    private fun getCurrentUser(): String {
        val user = App.create(APP_ID).currentUser
        return if (user != null && user.loggedIn) Screen.Home.route
        else Screen.Authentication.route
    }
}

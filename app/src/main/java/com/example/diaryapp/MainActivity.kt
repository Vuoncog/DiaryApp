package com.example.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diaryapp.navigation.Screen
import com.example.diaryapp.navigation.SetUpNavigation
import com.example.diaryapp.ui.theme.DiaryAppTheme
import com.example.diaryapp.utility.Constant.APP_ID
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    var keepOnScreenCondition = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {keepOnScreenCondition}
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
    }

    private fun getCurrentUser(): String{
        val user = App.create(APP_ID).currentUser
        return if (user != null && user.loggedIn) Screen.Home.route
        else Screen.Authentication.route
    }
}

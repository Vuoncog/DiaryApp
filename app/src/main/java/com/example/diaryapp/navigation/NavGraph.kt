package com.example.diaryapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.models.RequestState
import com.stevdzasan.onetap.rememberOneTapSignInState
import com.example.diaryapp.presentation.authentication.AuthenticationScreen
import com.example.diaryapp.presentation.authentication.AuthenticationViewModel
import com.example.diaryapp.presentation.components.CustomAlertDialog
import com.example.diaryapp.presentation.home.HomeScreen
import com.example.diaryapp.presentation.home.HomeViewModel
import com.example.diaryapp.utility.Constant.APP_ID
import com.example.diaryapp.utility.Constant.WRITE_ARGUMENT
import com.stevdzasan.messagebar.rememberMessageBarState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
fun SetUpNavigation(
    startDestination: String,
    onDataLoad: () -> Unit,
    navController: NavHostController
) {
    LaunchedEffect(key1 = Unit){
        delay(100)
        onDataLoad()
    }

    NavHost(navController = navController, startDestination = startDestination) {
        authenticationRoute(
            navigationToHome = { navController.navigate(Screen.Home.route) }
        )
        homeRoute(
            navigationToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            navigationToWrite = { navController.navigate(Screen.Write.route) }
        )
        writeRoute()
    }
}

private fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(
            name = WRITE_ARGUMENT
        ) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

    }
}

private fun NavGraphBuilder.homeRoute(
    navigationToAuth: () -> Unit,
    navigationToWrite: () -> Unit
) {
    composable(
        route = Screen.Home.route
    ) {
        val homeViewModel: HomeViewModel = viewModel()
        val diaries by homeViewModel.diaries
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var isDialogOpened by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit){
            MongoDB.configureMongoDB()
        }

        HomeScreen(
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            onDateClicked = {},
            onSignOutClicked = {
                isDialogOpened = true
            },
            diaries = diaries
        )

        CustomAlertDialog(
            title = "Sign out",
            message = "Are you sure that you want to Sign out?",
            isDialogOpened = isDialogOpened,
            onCloseDialog = { isDialogOpened = false },
            onConfirmDialog = {
                scope.launch {
                    App.create(APP_ID).currentUser?.logOut()
                }
                navigationToAuth()
            })
    }
}

private fun NavGraphBuilder.authenticationRoute(
    navigationToHome: () -> Unit
) {
    composable(
        route = Screen.Authentication.route
    ) {
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        val viewModel: AuthenticationViewModel = viewModel()
        AuthenticationScreen(
            isLogged = viewModel.isLogged.value,
            loadingState = viewModel.loadingState.value,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onSignInClicked = {
                oneTapState.open()
                viewModel.setLoadingState(true)
            },
            onTokenReceived = { tokenId ->
                viewModel.signInWithMongoDB(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Logged in successfully")
                    },
                    onError = {
                        messageBarState.addError(it)
                    }
                )
                viewModel.setLoadingState(false)
            },
            onDialogDismissed = {
                messageBarState.addError(Exception(it))
                viewModel.setLoadingState(false)
            },
            navigateToHome = navigationToHome
        )
    }
}

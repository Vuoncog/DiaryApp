package com.example.diaryapp.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.models.Mood
import com.stevdzasan.onetap.rememberOneTapSignInState
import com.example.diaryapp.presentation.authentication.AuthenticationScreen
import com.example.diaryapp.presentation.authentication.AuthenticationViewModel
import com.example.diaryapp.presentation.components.CustomAlertDialog
import com.example.diaryapp.presentation.home.HomeScreen
import com.example.diaryapp.presentation.home.HomeViewModel
import com.example.diaryapp.presentation.write.WriteScreen
import com.example.diaryapp.presentation.write.WriteViewModel
import com.example.diaryapp.utility.Constant.APP_ID
import com.example.diaryapp.utility.Constant.WRITE_ARGUMENT
import com.stevdzasan.messagebar.rememberMessageBarState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
fun SetUpNavigation(
    startDestination: String,
    onDataLoad: () -> Unit,
    navController: NavHostController
) {
    LaunchedEffect(key1 = Unit) {
        delay(100)
        onDataLoad()
    }

    NavHost(navController = navController, startDestination = startDestination) {
        authenticationRoute(
            navigationToHome = { navController.navigate(Screen.Home.route) }
        )
        homeRoute(
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            navigateToWrite = { navController.navigate(Screen.Write.route) },
            navigateToWriteWithArgs = { diaryId ->
                navController.navigate(Screen.Write.passDiaryId(diaryId))
            }
        )
        writeRoute(
            navigationToHome = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun NavGraphBuilder.writeRoute(
    navigationToHome: () -> Unit
) {
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
        val pagerState = rememberPagerState {
            Mood.values().size
        }
        val viewModel: WriteViewModel = viewModel()
        val uiState = viewModel.uiState
        val editScreen by viewModel.editScreen.collectAsState()
        WriteScreen(
            pagerState = pagerState,
            navigateToHome = navigationToHome,
            onEditClicked = { viewModel.changeToEditScreen() },
            onTitleChanged = { viewModel.setTitle(it) },
            onDescriptionChanged = { viewModel.setDescription(it) },
            onButtonClicked = {
                viewModel.insertDiary(
                    diary = it.apply { mood = Mood.values()[pagerState.currentPage].name },
                    onSucces = navigationToHome,
                    onError = {}
                )
            },
            uiState = uiState,
            editScreen = editScreen
        )
    }
}

private fun NavGraphBuilder.homeRoute(
    navigateToAuth: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
) {
    composable(
        route = Screen.Home.route
    ) {
        val homeViewModel: HomeViewModel = viewModel()
        val diaries by homeViewModel.diaries
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var isDialogOpened by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit) {
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
            diaries = diaries,
            navigateToWrite = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs
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
                navigateToAuth()
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

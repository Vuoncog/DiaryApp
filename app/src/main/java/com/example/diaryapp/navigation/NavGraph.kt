package com.example.diaryapp.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

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
        val context = LocalContext.current
        val pagerState = rememberPagerState {
            Mood.values().size
        }
        val viewModel: WriteViewModel = hiltViewModel()
        val uiState = viewModel.uiState
        val editScreen by viewModel.editScreen.collectAsState()
        val scope = rememberCoroutineScope()
        var isDialogOpened by remember { mutableStateOf(false) }
        val galleryState by viewModel.galleryState.collectAsState()

        WriteScreen(
            pagerState = pagerState,
            navigateToHome = navigationToHome,
            uiState = uiState,
            editScreen = editScreen,
            galleryState = galleryState,
            onEditClicked = { viewModel.changeToEditScreen() },
            onTitleChanged = { viewModel.setTitle(it) },
            onDescriptionChanged = { viewModel.setDescription(it) },
            onSaveClicked = {
                viewModel.UpdateOrInsert(
                    diary = it.apply { mood = Mood.values()[pagerState.currentPage].name },
                    onSucces = navigationToHome,
                    onError = {}
                )
            },
            onDeleteClicked = {
                isDialogOpened = true
            },
            onDateChanged = {
                viewModel.setDate(it.toInstant())
                Log.d("UiStateDate", uiState.date.toString())
            },
            onAddClicked = {

            },
            onImageSelected = {
                val imageType = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(it, imageType)
            },
            onImageDeleteClicked = {
                galleryState.removeImage(it)
            }
        )

        CustomAlertDialog(
            title = "Delete the diary",
            message = "Do you confirm to delete this diary?",
            isDialogOpened = isDialogOpened,
            onCloseDialog = { isDialogOpened = false },
            onConfirmDialog = {
                scope.launch {
                    viewModel.deleteDiary(
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Delete",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigationToHome()
                        },
                        onError = {
                            Toast.makeText(
                                context,
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
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
        val homeViewModel: HomeViewModel = hiltViewModel()
        val context = LocalContext.current
        val diaries by homeViewModel.diaries
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var isDialogOpened by remember { mutableStateOf(false) }
        var isEraseDialogOpened by remember { mutableStateOf(false) }

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
            onDateClicked = {
                homeViewModel.getDiaries(
                    zonedDateTime = ZonedDateTime.of(
                        it,
                        LocalTime.of(0, 0, 0),
                        ZoneId.systemDefault()
                    )
                )
            },
            onSignOutClicked = {
                isDialogOpened = true
            },
            onEraseClicked = {
                isEraseDialogOpened = true
            },
            onClearClicked = {
                homeViewModel.getDiaries()
            },
            diaries = diaries,
            isFiltered = homeViewModel.isFiltered,
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
            }
        )

        CustomAlertDialog(
            title = "Erase all memories",
            message = "Are you sure that you want to erase your memories permanently?",
            isDialogOpened = isEraseDialogOpened,
            onCloseDialog = { isEraseDialogOpened = false },
            onConfirmDialog = {
                homeViewModel.eraseAllMemories(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "All Diaries Deleted.",
                            Toast.LENGTH_SHORT
                        ).show()
                        scope.launch {
                            drawerState.close()
                        }
                        isEraseDialogOpened = false
                    },
                    onFailed = {
                        Toast.makeText(
                            context,
                            if (it.message == "No Internet Connection.")
                                "We need an Internet Connection for this operation."
                            else it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        scope.launch {
                            drawerState.close()
                        }
                        isEraseDialogOpened = false

                    }
                )
            }
        )
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
            onFirebaseLoginSuccessfully = { tokenId ->
                viewModel.signInWithMongoDB(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Logged in successfully")
                        viewModel.setLoadingState(false)
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoadingState(false)
                    }
                )
            },
            onFirebaseLoginFail = {
                messageBarState.addError(it)
                viewModel.setLoadingState(false)
            },
            onDialogDismissed = {
                messageBarState.addError(Exception(it))
                viewModel.setLoadingState(false)
            },
            navigateToHome = navigationToHome,

            )
    }
}

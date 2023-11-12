package com.example.diaryapp.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.diaryapp.R
import com.example.diaryapp.models.Diaries
import com.example.diaryapp.models.RequestState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    onDateClicked: (LocalDate) -> Unit,
    onSignOutClicked: () -> Unit,
    onEraseClicked: () -> Unit,
    onClearClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    isFiltered: Boolean,
    diaries: Diaries
) {
    var paddingValues by remember {
        mutableStateOf(PaddingValues())
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    NavigationDrawer(
        drawerState = drawerState,
        onSignOutClicked = onSignOutClicked,
        onEraseClicked = onEraseClicked
    ) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
                .navigationBarsPadding()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeTopBar(
                    onMenuClicked = onMenuClicked,
                    onEditClicked = navigateToWrite,
                    onDateClicked = onDateClicked,
                    onClearClicked = onClearClicked,
                    scrollBehavior = scrollBehavior,
                    isFiltered = isFiltered
                )
            },
            content = {
                when (diaries) {
                    is RequestState.Success -> {
                        Log.d("Diary", diaries.data.toString())
                        HomeContent(
                            diaries = diaries.data,
                            paddingValues = it,
                            navigateToWrite = navigateToWrite,
                            navigateToWriteWithArgs = navigateToWriteWithArgs
                        )
                    }

                    is RequestState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is RequestState.Error -> {
                        EmptyScreen(
                            title = diaries.error.toString(),
                            description = diaries.error.message.toString(),
                            onClicked = navigateToWrite
                        )
                    }

                    else -> {}
                }

            }
        )
    }
}

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    onSignOutClicked: () -> Unit,
    onEraseClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        content = content,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.diary_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    NavigationDrawerItem(
                        label = {
                            CustomUINavigationDrawerItem(
                                icon = R.drawable.google,
                                isPainter = true,
                                title = "Sign Out"
                            )
                        },
                        selected = false,
                        onClick = onSignOutClicked
                    )
                    NavigationDrawerItem(
                        label = {
                            CustomUINavigationDrawerItem(
                                icon = R.drawable.trash,
                                isPainter = false,
                                title = "Erase all memories"
                            )
                        },
                        selected = false,
                        onClick = onEraseClicked
                    )
                }
            )
        },
    )
}

@Composable
fun CustomUINavigationDrawerItem(
    @DrawableRes icon: Int = R.drawable.google,
    isPainter: Boolean = true,
    title: String = "Sign out"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isPainter){
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(R.string.google_logo),
                modifier = Modifier.size(18.dp)
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = "Delete all icon",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.01.em,
            lineHeight = 20.sp
        )
    }
}
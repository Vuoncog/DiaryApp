package com.example.diaryapp.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.diaryapp.R
import com.example.diaryapp.models.Diaries
import com.example.diaryapp.models.RequestState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    onDateClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    diaries: Diaries
) {
    var paddingValues by remember {
        mutableStateOf(PaddingValues())
    }

    NavigationDrawer(
        drawerState = drawerState,
        onSignOutClicked = onSignOutClicked
    ) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
                .navigationBarsPadding(),
            topBar = {
                HomeTopBar(
                    onMenuClicked = onMenuClicked,
                    onDateClicked = onDateClicked
                )
            },
            content = {
                when (diaries) {
                    is RequestState.Success -> {
                        HomeContent(
                            diaries = diaries.data,
                            paddingValues = it
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
                            description = diaries.error.message.toString()
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
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        content = content,
        drawerContent =
        {
            ModalDrawerSheet(
                content =
                {
                    Image(
                        painter = painterResource(id = R.drawable.diary_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    NavigationDrawerItem(
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.google),
                                    contentDescription = stringResource(R.string.google_logo),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Sign out",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.01.em,
                                    lineHeight = 20.sp
                                )
                            }
                        },
                        selected = false,
                        onClick = onSignOutClicked
                    )
                }
            )
        },
    )
}
package com.vuoncog.diaryapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vuoncog.diaryapp.presentation.authentication.AuthenticationScreen
import com.vuoncog.diaryapp.utility.Constant.WRITE_ARGUMENT

@Composable
fun SetUpNavigation(
    startDestination: String,
    navController: NavHostController
){
    NavHost(navController = navController, startDestination = startDestination){
        authenticationRoute()
        homeRoute()
        writeRoute()
    }
}

private fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(
            name = WRITE_ARGUMENT
        ){
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ){

    }
}

private fun NavGraphBuilder.homeRoute() {
    composable(
        route = Screen.Home.route
    ){

    }
}

private fun NavGraphBuilder.authenticationRoute() {
    composable(
        route = Screen.Authentication.route
    ){
        AuthenticationScreen()
    }
}

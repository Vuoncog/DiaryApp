package com.vuoncog.diaryapp.navigation

import com.vuoncog.diaryapp.utility.Constant.WRITE_ARGUMENT

sealed class Screen(val route: String) {
    object Authentication: Screen(
        route = "authentication"
    )
    object Home: Screen(
        route = "home"
    )
    object Write: Screen(
        route = "write?$WRITE_ARGUMENT={$WRITE_ARGUMENT}"
    ){
        fun passDiaryId(diaryId: String): String{
            return "write?diaryId=${diaryId}"
        }
    }
}
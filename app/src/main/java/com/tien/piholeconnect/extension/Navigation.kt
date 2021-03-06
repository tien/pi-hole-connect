package com.tien.piholeconnect.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavHostController.currentRouteAsState(): State<String?> {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return mutableStateOf(navBackStackEntry?.arguments?.getString(KEY_ROUTE))
}
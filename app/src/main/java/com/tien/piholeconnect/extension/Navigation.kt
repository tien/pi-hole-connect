package com.tien.piholeconnect.extension

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.screenForRoute

@Composable
fun NavHostController.currentRouteAsState(): State<String?> {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return mutableStateOf(navBackStackEntry?.arguments?.getString(KEY_ROUTE)?.split("?")?.get(0))
}

@Composable
fun NavHostController.currentScreenAsState(): State<Screen?> {
    val currentRoute by this.currentRouteAsState()
    return mutableStateOf(currentRoute?.let { screenForRoute(it) })
}
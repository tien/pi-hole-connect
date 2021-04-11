package com.tien.piholeconnect.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tien.piholeconnect.model.Screen
import com.tien.piholeconnect.model.screenForRoute

@Composable
fun NavHostController.currentRouteAsState(): State<String?> {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return object : State<String?> {
        override val value: String?
            get() = navBackStackEntry?.arguments?.getString(KEY_ROUTE)?.split("?")?.get(0)
    }
}

@Composable
fun NavHostController.currentScreenAsState(): State<Screen?> {
    val currentRoute by this.currentRouteAsState()
    return object : State<Screen?> {
        override val value: Screen? get() = currentRoute?.let { screenForRoute(it) }
    }
}
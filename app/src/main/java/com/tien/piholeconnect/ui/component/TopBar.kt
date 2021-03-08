package com.tien.piholeconnect.ui.component

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TopBar(title: String) {
    TopAppBar(title = { Text(title) })
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(title = "Pi-Hole Connect")
}
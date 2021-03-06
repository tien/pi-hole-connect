package com.tien.piholeconnect.ui.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun TopBar() {
    TopAppBar(title = { Text("Pi-Hole Connect") }, navigationIcon = {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.TwoTone.Menu, contentDescription = "Menu")
        }
    })
}
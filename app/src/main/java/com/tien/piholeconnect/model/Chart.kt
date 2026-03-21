package com.tien.piholeconnect.model

import androidx.compose.ui.graphics.Color

data class LineChartData(
    val label: String,
    val data: Iterable<Coordinate>,
    val color: Color? = null,
)

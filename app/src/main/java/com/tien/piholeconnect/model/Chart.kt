package com.tien.piholeconnect.model

import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.core.entry.ChartEntry

class Entry(
    override val x: Float,
    override val y: Float,
    val xDisplayValue: String? = null,
    val yDisplayValue: String? = null,
    val xLabel: String? = null,
    val yLabel: String? = null,
) : ChartEntry {
    override fun withY(y: Float): ChartEntry = Entry(x, y, xDisplayValue, yDisplayValue, xLabel, yLabel)
}

data class LineChartData(
    val label: String,
    val data: Iterable<Coordinate>,
    val color: Color? = null,
)

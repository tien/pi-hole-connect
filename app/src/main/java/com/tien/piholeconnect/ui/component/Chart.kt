package com.tien.piholeconnect.ui.component

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineDataSet.Mode.CUBIC_BEZIER
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.tien.piholeconnect.model.Coordinate
import com.tien.piholeconnect.ui.theme.toColorInt

data class LineChartData(
    val label: String,
    val data: Iterable<Coordinate>,
    val configure: (LineDataSet.() -> Unit) = {}
)

data class SelectedValue(val label: String, val value: Coordinate?)

@Composable
fun LineChart(
    lineData: LineChartData,
    modifier: Modifier = Modifier,
    onValueSelected: (Iterable<SelectedValue>) -> Unit = {},
    configure: com.github.mikephil.charting.charts.LineChart.() -> Unit = {}
) = LineChart(listOf(lineData), modifier, onValueSelected, configure)

@Composable
fun LineChart(
    lineData: Iterable<LineChartData>,
    modifier: Modifier = Modifier,
    onValueSelected: (Iterable<SelectedValue>) -> Unit = {},
    configure: com.github.mikephil.charting.charts.LineChart.() -> Unit = {}
) {
    val contentColor = LocalContentColor.current.toColorInt()

    val parsedData = remember(lineData) {
        LineData(lineData.map {
            val lineDataSet = LineDataSet(
                it.data.map { pair -> Entry(pair.first, pair.second) },
                it.label
            )

            lineDataSet.configure(contentColor)
            it.configure(lineDataSet)

            lineDataSet
        })
    }

    val listener = remember(lineData) {
        object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                onValueSelected(lineData.map {
                    SelectedValue(
                        it.label,
                        it.data.firstOrNull { value -> value.first == e.x })
                })
            }

            override fun onNothingSelected() {
                onValueSelected(lineData.map { SelectedValue(it.label, null) })
            }
        }
    }

    AndroidView(
        factory = {
            val chart = com.github.mikephil.charting.charts.LineChart(it)

            chart.axisLeft.textColor = contentColor
            chart.axisRight.setDrawLabels(false)
            chart.xAxis.textColor = contentColor
            chart.description.isEnabled = false
            chart.legend.isEnabled = false

            chart.setTouchEnabled(true)
            chart.isDragEnabled = true
            chart.setPinchZoom(false)
            chart.isScaleXEnabled = true
            chart.isScaleYEnabled = false
            chart.animateXY(0, 1000, EaseOutBounce)

            chart.setOnChartValueSelectedListener(listener)

            configure(chart)

            chart.data = parsedData
            chart.invalidate()
            chart
        },
        update = {
            it.data = parsedData
            it.setOnChartValueSelectedListener(listener)
            it.invalidate()
        },
        modifier = modifier
    )
}

private fun LineDataSet.configure(contentColor: Int? = null) {
    contentColor?.let { this.valueTextColor = it }
    this.mode = CUBIC_BEZIER
    this.cubicIntensity = 0.2f
    this.setDrawFilled(true)
    this.setDrawCircles(false)
    this.lineWidth = 1.8f
    this.highLightColor = Color.RED
    this.color = Color.WHITE
    this.fillColor = Color.WHITE
    this.fillAlpha = 100
    this.setDrawHorizontalHighlightIndicator(false)
}

@Preview
@Composable
fun LineChartPreview() {
    LineChart(
        LineChartData(label = "label", data = listOf(Pair(0f, 0f), Pair(3f, 6f))),
        Modifier.fillMaxSize()
    )
}
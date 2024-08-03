package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.tien.piholeconnect.model.Entry
import com.tien.piholeconnect.model.LineChartData
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.success
import java.text.DateFormat
import java.text.DecimalFormat

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: LineChartData,
    xAxisFormatter: ((y: Number) -> String)? = null
) = LineChart(modifier, listOf(data), xAxisFormatter)

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: Iterable<LineChartData>,
    xAxisFormatter: ((y: Number) -> String)? = null
) = ProvideChartStyle(
    m3ChartStyle(entityColors = data.map { it.color ?: MaterialTheme.colorScheme.primary })
) {
    val entries = remember(data) {
        data.map { lineData ->
            lineData.data.mapIndexed { index, coordinate ->
                Entry(
                    if (xAxisFormatter == null) coordinate.first.toFloat() else index.toFloat(),
                    coordinate.second.toFloat(),
                    xDisplayValue = xAxisFormatter?.invoke(coordinate.first),
                    yLabel = lineData.label
                )
            }
        }
    }

    val chartModelProducer = remember { ChartEntryModelProducer(entries) }

    LaunchedEffect(entries) {
        chartModelProducer.setEntries(entries)
    }

    Chart(
        modifier = modifier,
        chart = lineChart(),
        chartModelProducer = chartModelProducer,
        bottomAxis = rememberBottomAxis(
            axis = null,
            guideline = null,
            itemPlacer = remember(data) {
                AxisItemPlacer.Horizontal.default(
                    spacing = maxOf(
                        data.maxOf { it.data.count() / 4 }, 1
                    )
                )
            },
            valueFormatter = { value, chartValues ->
                (chartValues.chartEntryModel.entries.firstOrNull()
                    ?.getOrNull(value.toInt()) as Entry?)?.xDisplayValue ?: value.toString()
            },
        ),
        endAxis = rememberEndAxis(axis = null,
            tick = null,
            guideline = null,
            valueFormatter = { value, _ ->
                if (value == 0f) "" else DecimalFormat("#.##;−#.##").format(value)
            },
            horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
            itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = 3) }),
        chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun LineChartPreview() {
    val formatter = DateFormat.getDateInstance()
    PiHoleConnectTheme {
        LineChart(Modifier.fillMaxSize(), data = listOf(
            LineChartData(
                label = "label",
                data = listOf(1525546500 to 163, 1525547100 to 154, 1525547700 to 164),
                color = MaterialTheme.colorScheme.success
            ), LineChartData(
                label = "label",
                data = listOf(1525546500 to 30, 1525547100 to 64, 1525547700 to 10),
                color = MaterialTheme.colorScheme.error
            )
        ), xAxisFormatter = { formatter.format(it) })
    }
}

package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.tien.piholeconnect.model.LineChartData
import com.tien.piholeconnect.ui.theme.PiHoleConnectTheme
import com.tien.piholeconnect.ui.theme.success
import java.text.DateFormat
import java.text.DecimalFormat

private val XAxisLabelKey = ExtraStore.Key<Map<Double, String>>()
private val BottomAxisSpacingKey = ExtraStore.Key<Int>()

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: LineChartData,
    xAxisFormatter: ((y: Number) -> String)? = null,
) = LineChart(modifier, listOf(data), xAxisFormatter)

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: Iterable<LineChartData>,
    xAxisFormatter: ((y: Number) -> String)? = null,
) {
  ProvideVicoTheme(rememberM3VicoTheme()) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val dataList = remember(data) { data.toList() }

    LaunchedEffect(dataList) {
      val nonEmpty = dataList.filter { it.data.any() }
      if (nonEmpty.isEmpty()) return@LaunchedEffect

      modelProducer.runTransaction {
        extras { extraStore ->
          extraStore[BottomAxisSpacingKey] = maxOf(nonEmpty.maxOf { it.data.count() / 4 }, 1)

          if (xAxisFormatter != null) {
            extraStore[XAxisLabelKey] =
                nonEmpty
                    .first()
                    .data
                    .mapIndexed { index, coordinate ->
                      index.toDouble() to xAxisFormatter(coordinate.first)
                    }
                    .toMap()
          }
        }

        lineSeries {
          nonEmpty.forEach { lineData ->
            if (xAxisFormatter != null) {
              series(
                  x = lineData.data.mapIndexed { index, _ -> index },
                  y = lineData.data.map { it.second },
              )
            } else {
              series(
                  x = lineData.data.map { it.first },
                  y = lineData.data.map { it.second },
              )
            }
          }
        }
      }
    }

    val defaultColor = MaterialTheme.colorScheme.primary
    val lineProvider =
        LineCartesianLayer.LineProvider.series(
            dataList.map { lineData ->
              LineCartesianLayer.rememberLine(
                  fill = LineCartesianLayer.LineFill.single(Fill(lineData.color ?: defaultColor)),
                  areaFill = null,
              )
            })

    val bottomAxisValueFormatter =
        remember(xAxisFormatter) {
          if (xAxisFormatter != null) {
            CartesianValueFormatter { context, value, _ ->
              context.model.extraStore.getOrNull(XAxisLabelKey)?.get(value)
                  ?: value.toInt().toString()
            }
          } else {
            CartesianValueFormatter.decimal()
          }
        }

    val endAxisValueFormatter = remember {
      CartesianValueFormatter { _, value, _ -> DecimalFormat("#.##;\u2212#.##").format(value) }
    }

    val bottomAxisItemPlacer = remember {
      HorizontalAxis.ItemPlacer.aligned(
          spacing = { extraStore -> extraStore.getOrNull(BottomAxisSpacingKey) ?: 1 },
          shiftExtremeLines = false,
          addExtremeLabelPadding = false,
      )
    }

    val markerValueFormatter = remember {
      val defaultFormatter = DefaultCartesianMarker.ValueFormatter.default()
      DefaultCartesianMarker.ValueFormatter { context, targets ->
        val defaultText = defaultFormatter.format(context, targets)
        val x = targets.firstOrNull()?.x ?: return@ValueFormatter defaultText
        val timeLabel =
            context.model.extraStore.getOrNull(XAxisLabelKey)?.get(x)
                ?: return@ValueFormatter defaultText
        buildAnnotatedString {
          append(timeLabel)
          append(" (")
          append(defaultText)
          append(")")
        }
      }
    }

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(lineProvider = lineProvider),
                bottomAxis =
                    HorizontalAxis.rememberBottom(
                        line = null,
                        guideline = null,
                        itemPlacer = bottomAxisItemPlacer,
                        valueFormatter = bottomAxisValueFormatter,
                    ),
                endAxis =
                    VerticalAxis.rememberEnd(
                        line = null,
                        tick = null,
                        guideline = null,
                        valueFormatter = endAxisValueFormatter,
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                        itemPlacer = remember { VerticalAxis.ItemPlacer.count(count = { 3 }) },
                    ),
                marker = rememberMarker(valueFormatter = markerValueFormatter),
            ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
    )
  }
}

@Preview(showBackground = true)
@Composable
fun LineChartPreview() {
  val formatter = DateFormat.getDateInstance()
  PiHoleConnectTheme {
    LineChart(
        Modifier.fillMaxSize(),
        data =
            listOf(
                LineChartData(
                    label = "label",
                    data = listOf(1525546500 to 163, 1525547100 to 154, 1525547700 to 164),
                    color = MaterialTheme.colorScheme.success,
                ),
                LineChartData(
                    label = "label",
                    data = listOf(1525546500 to 30, 1525547100 to 64, 1525547700 to 10),
                    color = MaterialTheme.colorScheme.error,
                ),
            ),
        xAxisFormatter = { formatter.format(it) },
    )
  }
}

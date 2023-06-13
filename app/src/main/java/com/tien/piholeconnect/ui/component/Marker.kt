package com.tien.piholeconnect.ui.component

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.overlayingComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.chart.segment.SegmentProperties
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.extension.appendCompat
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.extension.transformToSpannable
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import com.tien.piholeconnect.model.Entry
import java.text.DecimalFormat

@Composable
fun rememberMarker(): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelBackground = remember(labelBackgroundColor) {
        ShapeComponent(labelBackgroundShape, labelBackgroundColor.toArgb()).setShadow(
            radius = LABEL_BACKGROUND_SHADOW_RADIUS,
            dy = LABEL_BACKGROUND_SHADOW_DY,
            applyElevationOverlay = true,
        )
    }
    val label = textComponent(
        color = MaterialTheme.colorScheme.onSurface,
        background = labelBackground,
        lineCount = LABEL_LINE_COUNT,
        padding = labelPadding,
        typeface = Typeface.MONOSPACE,
    )
    val indicatorInnerComponent =
        shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.surface)
    val indicatorCenterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicatorOuterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicator = overlayingComponent(
        outer = indicatorOuterComponent,
        inner = overlayingComponent(
            outer = indicatorCenterComponent,
            inner = indicatorInnerComponent,
            innerPaddingAll = indicatorInnerAndCenterComponentPaddingValue,
        ),
        innerPaddingAll = indicatorCenterAndOuterComponentPaddingValue,
    )
    val guideline = lineComponent(
        MaterialTheme.colorScheme.onSurface.copy(GUIDELINE_ALPHA),
        guidelineThickness,
        guidelineShape,
    )
    return remember(label, indicator, guideline) {
        object : MarkerComponent(label, indicator, guideline) {
            init {
                indicatorSizeDp = INDICATOR_SIZE_DP
                onApplyEntryColor = { entryColor ->
                    indicatorOuterComponent.color =
                        entryColor.copyColor(INDICATOR_OUTER_COMPONENT_ALPHA)
                    with(indicatorCenterComponent) {
                        color = entryColor
                        setShadow(
                            radius = INDICATOR_CENTER_COMPONENT_SHADOW_RADIUS, color = entryColor
                        )
                    }
                }
            }

            override fun getInsets(
                context: MeasureContext, outInsets: Insets, segmentProperties: SegmentProperties
            ) = with(context) {
                outInsets.top =
                    label.getHeight(context) + labelBackgroundShape.tickSizeDp.pixels + LABEL_BACKGROUND_SHADOW_RADIUS.pixels * SHADOW_RADIUS_MULTIPLIER - LABEL_BACKGROUND_SHADOW_DY.pixels
            }
        }.apply {
            labelFormatter = object : MarkerLabelFormatter {

                private val PATTERN = DecimalFormat("#.##;−#.##")

                override fun getLabel(
                    markedEntries: List<Marker.EntryModel>,
                    chartValues: ChartValues,
                ): CharSequence = markedEntries.transformToSpannable(
                    prefix = when (val entry = markedEntries.firstOrNull()?.entry) {
                        is Entry -> entry.xDisplayValue ?: PATTERN.format(entry.x)
                        null -> ""
                        else -> PATTERN.format(entry.x)
                    } + if (markedEntries.size > 1) " (" else " ",
                    postfix = if (markedEntries.size > 1) ")" else "",
                    separator = "; ",
                ) { model ->
                    appendCompat(
                        when (val entry = model.entry) {
                            is Entry -> PATTERN.format(model.entry.y) + (entry.yLabel?.let { " $it" }
                                ?: "")

                            else -> PATTERN.format(model.entry.y)
                        },
                        ForegroundColorSpan(model.color),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
            }
        }
    }
}

const val LABEL_BACKGROUND_SHADOW_RADIUS = 4f
const val LABEL_BACKGROUND_SHADOW_DY = 2f
const val LABEL_LINE_COUNT = 1
const val GUIDELINE_ALPHA = .2f
const val INDICATOR_SIZE_DP = 36f
const val INDICATOR_OUTER_COMPONENT_ALPHA = 32
const val INDICATOR_CENTER_COMPONENT_SHADOW_RADIUS = 12f
const val GUIDELINE_DASH_LENGTH_DP = 8f
const val GUIDELINE_GAP_LENGTH_DP = 4f
const val SHADOW_RADIUS_MULTIPLIER = 1.3f

val labelBackgroundShape = MarkerCorneredShape(Corner.FullyRounded)
val labelHorizontalPaddingValue = 8.dp
val labelVerticalPaddingValue = 4.dp
val labelPadding = dimensionsOf(labelHorizontalPaddingValue, labelVerticalPaddingValue)
val indicatorInnerAndCenterComponentPaddingValue = 5.dp
val indicatorCenterAndOuterComponentPaddingValue = 10.dp
val guidelineThickness = 2.dp
val guidelineShape =
    DashedShape(Shapes.pillShape, GUIDELINE_DASH_LENGTH_DP, GUIDELINE_GAP_LENGTH_DP)

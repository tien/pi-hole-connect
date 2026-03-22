package com.tien.piholeconnect.ui.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LayeredComponent
import com.patrykandpatrick.vico.compose.common.MarkerCornerBasedShape
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent

@Composable
internal fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter = remember {
      DefaultCartesianMarker.ValueFormatter.default()
    }
): CartesianMarker {
  val labelBackgroundShape = MarkerCornerBasedShape(CircleShape)
  val labelBackground =
      rememberShapeComponent(
          fill = Fill(MaterialTheme.colorScheme.surface),
          shape = labelBackgroundShape,
          strokeFill = Fill(MaterialTheme.colorScheme.outline),
          strokeThickness = 1.dp,
      )
  val label =
      rememberTextComponent(
          style =
              TextStyle(
                  color = MaterialTheme.colorScheme.onSurface,
                  textAlign = TextAlign.Center,
                  fontSize = 12.sp,
              ),
          padding = Insets(8.dp, 4.dp),
          background = labelBackground,
          minWidth = TextComponent.MinWidth.fixed(40.dp),
      )
  val indicatorFrontComponent =
      rememberShapeComponent(Fill(MaterialTheme.colorScheme.surface), CircleShape)
  val guideline = rememberAxisGuidelineComponent()
  return rememberDefaultCartesianMarker(
      label = label,
      valueFormatter = valueFormatter,
      indicator = { color ->
        LayeredComponent(
            back = ShapeComponent(Fill(color.copy(alpha = 0.15f)), CircleShape),
            front =
                LayeredComponent(
                    back = ShapeComponent(fill = Fill(color), shape = CircleShape),
                    front = indicatorFrontComponent,
                    padding = Insets(5.dp),
                ),
            padding = Insets(10.dp),
        )
      },
      indicatorSize = 36.dp,
      guideline = guideline,
  )
}

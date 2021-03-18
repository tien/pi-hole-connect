package com.tien.piholeconnect.ui.theme

import android.graphics.Color.rgb
import android.os.Build
import androidx.compose.ui.graphics.Color

val Red700 = Color(0xFFD32F2F)
val Red700Light = Color(0xFFFF6659)
val Red700Dark = Color(0xFF9A0007)
val Green400 = Color(0xFF66BB6A)
val Green400Light = Color(0xFF98EE99)
val Green400Dark = Color(0xFF338A3E)

val GreenAccent400 = Color(0xFF00E676)
val GreenAccent400Dark = Color(0xFF00B248)
val Blue500 = Color(0xFF2196F3)
val Blue500Dark = Color(0xFF0069C0)
val Amber500 = Color(0xFFFFC107)
val Amber500Dark = Color(0xFFC79100)

fun Color.toColorInt() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    rgb(this.red, this.green, this.blue)
} else {
    rgb((this.red * 255).toInt(), (this.green * 255).toInt(), (this.blue * 255).toInt())
}
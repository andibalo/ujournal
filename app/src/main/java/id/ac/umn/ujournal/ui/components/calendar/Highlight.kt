package id.ac.umn.ujournal.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import id.ac.umn.ujournal.R
import java.time.LocalDate

private class HalfSizeShape(private val clipStart: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val half = size.width / 2f
        val offset = if (layoutDirection == LayoutDirection.Ltr) {
            if (clipStart) Offset(half, 0f) else Offset.Zero
        } else {
            if (clipStart) Offset.Zero else Offset(half, 0f)
        }
        return Outline.Rectangle(Rect(offset, Size(half, size.height)))
    }
}

/**
 * Modern Airbnb highlight style, as seen in the app.
 * See also [backgroundHighlightLegacy].
 */
fun Modifier.backgroundHighlight(
    day: CalendarDay,
    today: LocalDate,
    selectedDate: LocalDate?,
    selectionColor: Color,
    textColor: (Color) -> Unit,
): Modifier = composed {
    val padding = 4.dp
    when (day.position) {
        DayPosition.MonthDate -> {
            when {
                day.date == selectedDate -> {
                    textColor(Color.White)
                    padding(vertical = padding)
                        .padding(horizontal = padding)
                        .background(color = selectionColor, shape = CircleShape)
                }
                day.date == today -> {
                    textColor(colorResource(R.color.example_4_grey))
                    padding(padding)
                        .border(
                            width = 1.dp,
                            shape = CircleShape,
                            color = colorResource(R.color.inactive_text_color),
                        )
                }
                else -> {
                    textColor(colorResource(R.color.example_4_grey))
                    this
                }
            }
        }
        DayPosition.InDate -> {
            this
        }
        DayPosition.OutDate -> {
            this
        }
    }
}


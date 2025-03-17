package id.ac.umn.ujournal.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import id.ac.umn.ujournal.ui.journal.JournalEntry
import id.ac.umn.ujournal.ui.util.displayText

import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

private val primaryColor = Color.Black.copy(alpha = 0.9f)
private val selectionColor = primaryColor
private val continuousSelectionColor = Color.LightGray.copy(alpha = 0.3f)

@Composable
fun Calendar(
    journalEntries: List<JournalEntry> = emptyList(),
    close: () -> Unit = {},
    dateSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit = { _, _ -> },
    adjacentYears: Long = 50
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusYears(adjacentYears) }
    val endMonth = remember {  currentMonth.plusYears(adjacentYears) }
    val today = remember { LocalDate.now() }
    var selectedDate : LocalDate? by remember { mutableStateOf(null) }
    val daysOfWeek = remember { daysOfWeek() }

    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = primaryColor)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Column {
                val state = rememberCalendarState(
                    startMonth = startMonth,
                    endMonth = endMonth,
                    firstVisibleMonth = currentMonth,
                    firstDayOfWeek = daysOfWeek.first(),
                )

                VerticalCalendar(
                    state = state,
                    contentPadding = PaddingValues(bottom = 100.dp),
                    dayContent = { value ->
                        Day(
                            value,
                            today = today,
                            selectedDate = selectedDate,
                        ) { day ->
                                selectedDate = day.date
                        }
                    },
                    monthHeader = { month -> MonthHeader(month) },

                )
            }

        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    selectedDate: LocalDate?,
    onClick: (CalendarDay) -> Unit,
) {
    var textColor = Color.Transparent
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .clickable(
                onClick = { onClick(day) },
            )
            .backgroundHighlight(
                day = day,
                today = today,
                selectedDate = selectedDate,
                selectionColor = selectionColor,
            ) { textColor = it }
        ,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun MonthHeader(calendarMonth: CalendarMonth) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = calendarMonth.yearMonth.displayText(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CalendarTop(
    modifier: Modifier = Modifier,
    year: Year,
    onRightArrowClick: () -> Unit = {},
    onLeftArrowClick: () -> Unit = {},
) {
    Column(modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Left arrow")
                }
                Spacer(Modifier.padding(4.dp))
                Text(
                    text = year.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.padding(4.dp))
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Right arrow")
                }
            }
        }
        HorizontalDivider()
    }
}

@Preview(heightDp = 800)
@Composable
private fun CalendarPreview() {
    Calendar()
}
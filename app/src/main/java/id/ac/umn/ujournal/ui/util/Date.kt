package id.ac.umn.ujournal.ui.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val HourTimeFormatter24: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val ddMMMMyyyyDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

fun LocalDateTime.toLocalMilliseconds(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
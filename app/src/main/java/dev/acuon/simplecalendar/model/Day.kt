package dev.acuon.simplecalendar.model

data class Day(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthDate: Boolean = false
) {
    val date: String = "$year-$month-$day"
}
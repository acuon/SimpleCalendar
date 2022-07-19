package dev.acuon.simplecalendar.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import dev.acuon.simplecalendar.model.Day
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
object Extensions {
    fun Context.toast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    private fun LocalDate.lengthOfPreviousMonth(dayOfWeek: Int): Int {
        var month = this.toString().split("-")[1].toInt() - 1
        if (month == 0) month = 12
        else if (month == 13) month = 1
        var daysInMonth = 0
        if (month == 2) {
            daysInMonth = if (this.isLeapYear) 29 else 28
        } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            daysInMonth = 31
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30
        }
        return daysInMonth - dayOfWeek + 1
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    fun TextView.currentTime() {
        val sdf = SimpleDateFormat("hh:mm aaa")
        val currentTime = sdf.format(Date())
        this.text = currentTime
    }

    fun TextView.setTime(context: Context) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            TimePickerDialog.OnTimeSetListener { timePicker, H, M ->
                cal.set(Calendar.HOUR_OF_DAY, H)
                cal.set(Calendar.MINUTE, M)

                val myFormat = "hh:mm aaa" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                this.text = sdf.format(cal.time)
            }

        TimePickerDialog(
            context,
            dateSetListener,
            cal.get(Calendar.HOUR),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun TextView.setDate(context: Context) {
        val cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                this.text = sdf.format(cal.time)
            }

        DatePickerDialog(
            context,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun LocalDate.monthToArray(): ArrayList<Day> {
        val daysInMonthArray: ArrayList<Day> = ArrayList()
        val yearMonth: YearMonth = YearMonth.from(this)

        val current = this.toString().split("-")
        val daysInMonth: Int = yearMonth.lengthOfMonth()
        val firstOfMonth = this.withDayOfMonth(1)
        var dayOfWeek = firstOfMonth.dayOfWeek.value
        dayOfWeek %= 7
        var daysOfPreviousMonth: Int = this.lengthOfPreviousMonth(dayOfWeek)
        for (i in 1..42) {
            if (i in (dayOfWeek + 1)..(daysInMonth + dayOfWeek)) {
                daysInMonthArray.add(
                    Day(
                        i - dayOfWeek,
                        current[1].toInt(),
                        current[0].toInt(),
                        true
                    )
                )
            } else {
                if (i > (daysInMonth + dayOfWeek)) {
                    daysInMonthArray.add(
                        Day(
                            i - daysInMonth - dayOfWeek,
                            current[1].toInt().let { if (it == 12) 1 else it + 1 },
                            current[0].toInt().let { if (current[1].toInt() == 12) it + 1 else it })
                    )
                } else {
                    daysInMonthArray.add(
                        Day(
                            daysOfPreviousMonth++,
                            current[1].toInt().let { if (it == 1) 12 else it - 1 },
                            current[0].toInt().let { if (current[1].toInt() == 1) it - 1 else it })
                    )
                }
            }
            if (i >= (daysInMonth + dayOfWeek) && i % 7 == 0) break
        }
//        Log.d("day_array", daysInMonthArray.toString())
        return daysInMonthArray
    }

    fun LocalDate.monthYearFromDate(): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return this.format(formatter)
    }
}
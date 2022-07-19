package dev.acuon.simplecalendar.ui

import dev.acuon.simplecalendar.model.Reminder

interface DateClickListener {
    fun onDateClicked(today: String, position: Int)
}

interface ReminderClickListener {
    fun onLongClick(position: Int)
    fun onClick(position: Int)
}

interface CurrentDateSelectedListener {
    fun passSelectedDate(currentSelectedDate: String)
    fun passSelectedReminder(reminder: Reminder)
}
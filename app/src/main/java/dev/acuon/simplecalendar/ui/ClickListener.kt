package dev.acuon.simplecalendar.ui

interface DateClickListener {
    fun onDateClicked(today: String, position: Int)
}

interface ReminderClickListener {
    fun onLongClick(position: Int)
    fun onClick(position: Int)
}

interface CurrentDateSelectedListener {
    fun passSelectedDate(currentSelectedDate: String)
}
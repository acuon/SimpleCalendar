package dev.acuon.simplecalendar

import android.app.Application
import dev.acuon.simplecalendar.data.CalendarDatabase
import dev.acuon.simplecalendar.repo.CalendarRepository

class ApplicationClass : Application() {
    lateinit var repository: CalendarRepository
    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        val database = CalendarDatabase.getDataBaseObject(applicationContext)
        repository = CalendarRepository(database.getDao())
    }
}
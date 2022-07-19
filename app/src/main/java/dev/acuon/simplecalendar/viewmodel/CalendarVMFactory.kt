package dev.acuon.simplecalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.acuon.simplecalendar.repo.CalendarRepository

class CalendarVMFactory(private val repo: CalendarRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalendarVM(repo) as T
    }
}
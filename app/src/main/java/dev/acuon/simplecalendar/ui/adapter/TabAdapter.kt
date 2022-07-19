package dev.acuon.simplecalendar.ui.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.acuon.simplecalendar.ui.fragment.MonthView
import java.time.LocalDate

class TabAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragList: Array<MonthView>
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> return fragList[0]
            1 -> return fragList[1]
            2 -> return fragList[2]
        }
        return fragList[1]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCalendar(currentMonth: LocalDate) {
        val prevMonth = currentMonth.minusMonths(1)
        val nextMonth = currentMonth.plusMonths(1)
        //update all 3 fragments
        fragList[0].updateUI(prevMonth)
        fragList[1].updateUI(currentMonth)
        fragList[2].updateUI(nextMonth)
//        fragList[0] = MonthView(prevMonth)
//        fragList[1] = MonthView(currentMonth)
//        fragList[2] = MonthView(nextMonth)
    }
}
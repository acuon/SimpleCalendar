package dev.acuon.simplecalendar.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.acuon.simplecalendar.ApplicationClass
import dev.acuon.simplecalendar.R
import dev.acuon.simplecalendar.databinding.ActivityMainBinding
import dev.acuon.simplecalendar.databinding.AddReminderLayoutBinding
import dev.acuon.simplecalendar.model.Reminder
import dev.acuon.simplecalendar.ui.adapter.TabAdapter
import dev.acuon.simplecalendar.ui.fragment.MonthView
import dev.acuon.simplecalendar.utils.Extensions.currentTime
import dev.acuon.simplecalendar.utils.Extensions.monthYearFromDate
import dev.acuon.simplecalendar.utils.Extensions.setDate
import dev.acuon.simplecalendar.utils.Extensions.setTime
import dev.acuon.simplecalendar.utils.Extensions.toast
import dev.acuon.simplecalendar.viewmodel.CalendarVM
import dev.acuon.simplecalendar.viewmodel.CalendarVMFactory
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), CurrentDateSelectedListener, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var addTaskLayoutBinding: AddReminderLayoutBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var viewModel: CalendarVM

    private var selectedDate: LocalDate? = LocalDate.now()
    private var fragList: Array<MonthView> =
        arrayOf(MonthView(selectedDate!!), MonthView(selectedDate!!), MonthView(selectedDate!!))

    init {
        selectedDate = LocalDate.now()
    }

    companion object {
        private var focusPage = 1
        private val PAGE_CENTER = 1
        private var selectedDay: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()
        selectedDay = selectedDate.toString()
        initViewModel()
        binding.fabAddReminder.setOnClickListener(this@MainActivity)
    }

    private fun initViewModel() {
        val repository = (application as ApplicationClass).repository
        viewModel = ViewModelProvider(
            this@MainActivity,
            CalendarVMFactory(repository)
        )[CalendarVM::class.java]
    }

    private fun setAdapter() {
        val adapter = TabAdapter(supportFragmentManager, lifecycle, fragList)
        updateTitle(selectedDate!!)
        binding.apply {
            viewPager.adapter = adapter
            viewPager.setCurrentItem(PAGE_CENTER, false)

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    focusPage = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        if (focusPage < PAGE_CENTER) {
                            selectedDate = selectedDate!!.minusMonths(1)
                        } else if (focusPage > PAGE_CENTER) {
                            selectedDate = selectedDate!!.plusMonths(1)
                        }
                        adapter.setCalendar(selectedDate!!)
                        viewPager.setCurrentItem(1, false)
                        updateTitle(selectedDate!!)
                    }
                }
            })
        }
    }

    private fun updateTitle(date: LocalDate) {
        binding.monthYearName.text = date.monthYearFromDate()
    }

    override fun passSelectedDate(currentSelectedDate: String) {
        selectedDay = currentSelectedDate
//        this.toast(selectedDay!!)
    }

    override fun passSelectedReminder(reminder: Reminder) {
        this.toast(reminder.name)
        addUpdateReminderBottomSheet(reminder)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fabAddReminder -> {
                    addUpdateReminderBottomSheet()
                }
            }
        }
    }

    private fun addUpdateReminderBottomSheet(reminder: Reminder? = null) {
        bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        addTaskLayoutBinding = AddReminderLayoutBinding.inflate(layoutInflater)

        if (reminder != null) addTaskLayoutBinding.reminder = reminder

        bottomSheetDialog.apply {
            setContentView(addTaskLayoutBinding.root)
            setCancelable(false)
            show()
        }
        addTaskLayoutBinding.apply {
            if (reminder != null) {
                tvReminderDate.text = reminder.date
                tvReminderTime.text = reminder.time
            } else {
                tvReminderDate.text = selectedDay
                tvReminderTime.currentTime()
            }
            tvReminderDate.setOnClickListener {
                tvReminderDate.setDate(this@MainActivity)
            }
            tvReminderTime.setOnClickListener {
                tvReminderTime.setTime(this@MainActivity)
            }
            ivCloseBottomSheet.setOnClickListener {
                bottomSheetDialog.cancel()
            }
            btnSubmit.setOnClickListener {
                if (reminder != null) {
                    viewModel.deleteFromReminder(reminder)
                }
                viewModel.insertDataInReminder(
                    Reminder(
                        tvReminderTitle.text.toString(),
                        tvReminderDescription.text.toString(),
                        tvReminderDate.text.toString(),
                        tvReminderTime.text.toString(),
                        0,
                    )
                )
                bottomSheetDialog.cancel()
            }
        }
    }
}
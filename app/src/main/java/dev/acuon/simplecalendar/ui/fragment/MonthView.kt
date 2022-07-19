package dev.acuon.simplecalendar.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dev.acuon.simplecalendar.ApplicationClass
import dev.acuon.simplecalendar.databinding.FragmentMonthViewBinding
import dev.acuon.simplecalendar.model.Day
import dev.acuon.simplecalendar.model.Reminder
import dev.acuon.simplecalendar.ui.CurrentDateSelectedListener
import dev.acuon.simplecalendar.ui.DateClickListener
import dev.acuon.simplecalendar.ui.ReminderClickListener
import dev.acuon.simplecalendar.ui.adapter.CalendarDaysAdapter
import dev.acuon.simplecalendar.ui.adapter.ReminderAdapter
import dev.acuon.simplecalendar.utils.Extensions.monthToArray
import dev.acuon.simplecalendar.viewmodel.CalendarVM
import dev.acuon.simplecalendar.viewmodel.CalendarVMFactory
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MonthView(private var currentDate: LocalDate) : Fragment(), DateClickListener,
    ReminderClickListener {

    private lateinit var binding: FragmentMonthViewBinding
    private lateinit var viewModel: CalendarVM
    private lateinit var daysInMonthList: ArrayList<Day>
    private lateinit var calendarAdapter: CalendarDaysAdapter
    private lateinit var remindersList: ArrayList<Reminder>
    private lateinit var reminderAdapter: ReminderAdapter
    private var selectedDateCallBack: CurrentDateSelectedListener? = null

    companion object {
        private var currentDay: String? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            selectedDateCallBack = context as CurrentDateSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                    .toString() + " must implement FragmentToActivity"
            )
        }
    }

    override fun onDetach() {
        selectedDateCallBack = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMonthViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentDay = currentDate.toString()
        daysInMonthList = currentDate.monthToArray()
        initViewModel()
        setRecyclerView()
    }

    private fun initViewModel() {
        val repository = (requireActivity().application as ApplicationClass).repository
        viewModel = ViewModelProvider(
            this,
            CalendarVMFactory(repository)
        )[CalendarVM::class.java]
    }

    fun updateUI(date: LocalDate) {
        val v = view ?: return
        currentDate = date
        currentDay = currentDate.toString()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        daysInMonthList = ArrayList()
        daysInMonthList = currentDate.monthToArray()
        calendarAdapter = CalendarDaysAdapter(
            daysInMonthList,
            this,
            currentDate.toString(),
            viewLifecycleOwner,
            viewModel
        )

        val gridLayoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRecyclerView.apply {
            adapter = calendarAdapter
            layoutManager = gridLayoutManager
        }

        remindersList = ArrayList()
        reminderAdapter = ReminderAdapter(this)
        reminderAdapter.reminderList.submitList(remindersList)
        binding.reminderRecyclerView.apply {
            adapter = reminderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        reminderAdapter.updateReminderList()
    }

    private fun ReminderAdapter.updateReminderList() {
        viewModel.getReminderByDate(currentDay!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            remindersList.clear()
            remindersList.addAll(it)
            this.notifyDataSetChanged()
        })
    }

    private fun Reminder.showDeleteDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Delete this Reminder")
            .setCancelable(false)
            .setPositiveButton("yes") { _, _ ->
                // TODO: Yet to implement
            }
            .setNegativeButton("no") { dialogView, _ ->
                dialogView.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun onDateClicked(today: String, position: Int) {
        selectedDateCallBack!!.passSelectedDate(today)
        currentDay = today
        reminderAdapter.updateReminderList()
    }

    override fun onLongClick(position: Int) {
        remindersList[position].showDeleteDialog()
    }

    override fun onClick(position: Int) {
        selectedDateCallBack!!.passSelectedReminder(remindersList[position])
    }
}
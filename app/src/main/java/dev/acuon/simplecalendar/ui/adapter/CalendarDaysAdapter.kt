package dev.acuon.simplecalendar.ui.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import dev.acuon.simplecalendar.R
import dev.acuon.simplecalendar.databinding.ItemLayoutCalendarBinding
import dev.acuon.simplecalendar.model.Day
import dev.acuon.simplecalendar.ui.DateClickListener
import dev.acuon.simplecalendar.utils.Extensions.visible
import dev.acuon.simplecalendar.viewmodel.CalendarVM
import java.time.LocalDate
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class CalendarDaysAdapter(
    private val daysList: ArrayList<Day>,
    private val clickListener: DateClickListener,
    private val currentDate: String,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: CalendarVM
) : RecyclerView.Adapter<CalendarDaysAdapter.ViewHolder>() {

    val bool = BooleanArray(daysList.size)
    private val itemViewList = ArrayList<ItemLayoutCalendarBinding>()
    private var selectedDate: LocalDate? = null
    private var curDate: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = DataBindingUtil.inflate<ItemLayoutCalendarBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_layout_calendar,
            parent,
            false
        )
        itemViewList.add(viewBinding)
        return ViewHolder(viewBinding, clickListener, lifecycleOwner, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = daysList[position]
        holder.setData(day)
    }

    override fun getItemCount(): Int {
        return daysList.size
    }

    inner class ViewHolder(
        private val viewBinding: ItemLayoutCalendarBinding,
        private val clickListener: DateClickListener,
        private val lifeOwner: LifecycleOwner,
        private val viewModel: CalendarVM
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        internal fun setData(day: Day) {
            viewBinding.apply {
                tvDay.text = day.day.toString()
                if (!day.monthDate) {
                    tvDay.setTextColor(
                        ContextCompat.getColor(
                            viewBinding.root.context,
                            R.color.grey
                        )
                    );
                }
                //yyyy-MM-dd
                var today = currentDate.substring(0, currentDate.length - 2)
                today += day.day.toString()

                selectedDate = LocalDate.now()
                curDate = selectedDate.toString().split("-")

                viewModel.getReminderByDate(today).observe(lifeOwner, androidx.lifecycle.Observer {
                    if (day.monthDate && it.isNotEmpty()) {
                        eventsCardView.visible()
                    }
                })

                val ar = currentDate.split("-")
                if (curDate!![2] == daysList[adapterPosition].day.toString() && curDate!![0] == ar[0] && curDate!![1] == ar[1]) {
                    bool[adapterPosition] = true
                    viewBinding.rlDate.setBackgroundResource(R.drawable.current_date_background)
                    viewBinding.tvDay.setTextColor(Color.parseColor("#FFFFFFFF"))
                }

                if (day.monthDate) {
                    rlDate.setOnClickListener {
                        if (bool[adapterPosition]) {
                            if (day.day.toString() == curDate!![2]) {
                                viewBinding.rlDate.setBackgroundResource(R.drawable.current_date_background)
                                viewBinding.tvDay.setTextColor(Color.parseColor("#FFFFFFFF"))
                            }
                        } else {
                            boolCheck()
                            if (day.day.toString() == curDate!![2]) {
                                viewBinding.rlDate.setBackgroundResource(R.drawable.current_date_background)
                                viewBinding.tvDay.setTextColor(Color.parseColor("#FFFFFFFF"))
                            } else {
                                rlDate.setBackgroundResource(R.drawable.selected_date_background)
                            }
                            bool[adapterPosition] = true
                        }
                        clickListener.onDateClicked(today, adapterPosition)
                    }
                }
            }
        }

        private fun boolCheck() {
            for (i in 0 until daysList.size) {
                if (bool[i] && i != adapterPosition) {
                    if (itemViewList[i].tvDay.text.toString() == curDate!![2]) {
//                        itemViewList[i].tvDay.setTextColor(Color.parseColor("#527FF3"))
//                        itemViewList[i].rlDate.setBackgroundResource(0)
                    } else {
                        itemViewList[i].rlDate.setBackgroundResource(0)
                    }
                    bool[i] = false
                }
            }

        }

    }
}
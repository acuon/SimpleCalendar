package dev.acuon.simplecalendar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.acuon.simplecalendar.R
import dev.acuon.simplecalendar.databinding.ItemLayoutReminderBinding
import dev.acuon.simplecalendar.model.Reminder
import dev.acuon.simplecalendar.ui.ReminderClickListener

class ReminderAdapter(private val clickListener: ReminderClickListener) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val reminderList = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = DataBindingUtil.inflate<ItemLayoutReminderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_layout_reminder,
            parent,
            false
        )
        return ViewHolder(viewBinding, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(reminderList.currentList[position])
    }

    override fun getItemCount(): Int {
        return reminderList.currentList.size
    }

    inner class ViewHolder(
        private val viewBinding: ItemLayoutReminderBinding,
        private val clickListener: ReminderClickListener
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        internal fun setData(reminder: Reminder) {
            viewBinding.reminder = reminder
            viewBinding.root.setOnLongClickListener {
                clickListener.onLongClick(adapterPosition)
                true
            }
            viewBinding.root.setOnClickListener {
                clickListener.onClick(adapterPosition)
            }
        }
    }
}
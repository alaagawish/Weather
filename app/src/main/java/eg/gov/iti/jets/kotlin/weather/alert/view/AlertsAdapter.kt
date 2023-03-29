package eg.gov.iti.jets.kotlin.weather.alert.view

import eg.gov.iti.jets.kotlin.weather.favourite.view.PlaceOnClickListener


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eg.gov.iti.jets.kotlin.weather.databinding.AlertItemBinding
import eg.gov.iti.jets.kotlin.weather.model.AlertsDB
import eg.gov.iti.jets.kotlin.weather.model.Daily
import java.text.SimpleDateFormat
import java.util.*


class AlertsAdapter(var listener: AlertOnClickListener) :
    ListAdapter<AlertsDB, AlertsAdapter.ViewHolder>(DayDiffUtil()) {
    lateinit var binding: AlertItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.deleteAlertIconImageView.setOnClickListener {
            listener.deleteAlert(item)
        }
        holder.binding.startDateAlertTextView.text =
            "${getDateFormat("hh:mm aa", item.start)}\n${getDateFormat("mm-dd", item.start)}"
        holder.binding.endDateAlertTextView.text =
            "${getDateFormat("hh:mm aa", item.end)}\n${getDateFormat("mm-dd", item.end)}"

    }

    private fun getDateFormat(pattern: String, date: Long) =
        SimpleDateFormat(pattern).format(Date(date))

    class ViewHolder(var binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class DayDiffUtil : DiffUtil.ItemCallback<AlertsDB>() {
    override fun areItemsTheSame(oldItem: AlertsDB, newItem: AlertsDB): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: AlertsDB, newItem: AlertsDB): Boolean {
        return oldItem == newItem
    }


}
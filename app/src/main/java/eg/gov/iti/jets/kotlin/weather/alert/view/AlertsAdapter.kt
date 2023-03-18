package eg.gov.iti.jets.kotlin.weather.alert.view

import eg.gov.iti.jets.kotlin.weather.favourite.view.PlaceOnClickListener


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eg.gov.iti.jets.kotlin.weather.databinding.AlertItemBinding
import eg.gov.iti.jets.kotlin.weather.model.Daily


class AlertsAdapter(var listener: AlertOnClickListener) :
    ListAdapter<Daily, AlertsAdapter.ViewHolder>(DayDiffUtil()) {
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


    }

    class ViewHolder(var binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class DayDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}
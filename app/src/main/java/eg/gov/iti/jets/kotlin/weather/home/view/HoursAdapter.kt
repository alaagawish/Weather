package eg.gov.iti.jets.kotlin.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.databinding.HourItemBinding
import eg.gov.iti.jets.kotlin.weather.model.Daily

class HoursAdapter() :
    ListAdapter<Daily, HoursAdapter.ViewHolder>(HourDiffUtil()) {
    lateinit var binding: HourItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Picasso
            .get()
            .load(item.weather.get(0).icon)
            .into(holder.binding.weatherDescriptionImageView);
        holder.binding.hourlyTemperatureTextView.text = item.temp.day.toString()
        //hour
        holder.binding.hourTextView.text = item.weather.get(0).description


    }

    class ViewHolder(var binding: HourItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class HourDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}
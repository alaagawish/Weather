package eg.gov.iti.jets.kotlin.weather.home.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.databinding.HourItemBinding
import eg.gov.iti.jets.kotlin.weather.model.Hourly
import java.text.SimpleDateFormat
import java.util.*

class HoursAdapter() :
    ListAdapter<Hourly, HoursAdapter.ViewHolder>(HourDiffUtil()) {
    lateinit var binding: HourItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
//        Log.d("TAG", "onBindViewHolder: ${SimpleDateFormat("EEEE").format( Date(1679594400000))}")
        Picasso
            .get()
            .load("https://openweathermap.org/img/wn/${item.weather.get(0).icon}@2x.png")
            .into(holder.binding.weatherDescriptionImageView)
        holder.binding.hourlyTemperatureTextView.text = item.temp.toString()

        holder.binding.hourTextView.text = "${
            SimpleDateFormat("HH").format(
                Date(item.dt*1000)
            )
        }"


    }

    class ViewHolder(var binding: HourItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class HourDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}
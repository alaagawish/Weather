package eg.gov.iti.jets.kotlin.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.databinding.HourItemBinding
import eg.gov.iti.jets.kotlin.weather.model.Hourly
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import java.lang.Math.ceil
import java.text.SimpleDateFormat
import java.util.*

class HoursAdapter(val context: Context) :
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
        val units = when (sharedPreferences.getString(UNIT, "metric")) {
            "metric" -> Triple("℃", context?.getString(R.string.m_sec), context?.getString(R.string.kilo_meter))
            "imperial" -> Triple("℉", context?.getString(R.string.m_hour), context?.getString(R.string.yard))
            else -> Triple("K", context?.getString(R.string.m_sec),  context?.getString(R.string.kilo_meter))
        }
        Picasso
            .get()
            .load("https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png")
            .into(holder.binding.weatherDescriptionImageView)
        holder.binding.hourlyTemperatureTextView.text = "${ceil(item.temp).toInt()}${units.first}"

        holder.binding.hourTextView.text = "${
            SimpleDateFormat("HH:mm aa").format(
                Date(item.dt * 1000)
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
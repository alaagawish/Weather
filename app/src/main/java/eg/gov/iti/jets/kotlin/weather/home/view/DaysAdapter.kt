package eg.gov.iti.jets.kotlin.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.utils.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.databinding.DayItemBinding
import eg.gov.iti.jets.kotlin.weather.model.Daily
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class DaysAdapter(val context: Context) :
    ListAdapter<Daily, DaysAdapter.ViewHolder>(DayDiffUtil()) {
    lateinit var binding: DayItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DayItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val units = when (sharedPreferences.getString(UNIT, "metric")) {
            "metric" -> Triple(
                "℃",
                context?.getString(R.string.m_sec),
                context?.getString(R.string.kilo_meter)
            )
            "imperial" -> Triple(
                "℉",
                context?.getString(R.string.m_hour),
                context?.getString(R.string.yard)
            )
            else -> Triple(
                "K",
                context?.getString(R.string.m_sec),
                context?.getString(R.string.kilo_meter)
            )
        }
        Picasso
            .get()
            .load("https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png")
            .into(holder.binding.dayWeatherIconImageView);
        holder.binding.dayHighLowTempTextView.text =
            "L: ${ceil(item.temp.min).toInt()}${units.first}\nH: ${ceil(item.temp.max).toInt()}${units.first}"
        holder.binding.dayDescriptionTextView.text = item.weather[0].description


        //Day name
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(
            Date(item.dt * 1000)
        )

        holder.binding.dayNameTextView.text =
            if (position > 0)
                dayName
            else
                context.getString(R.string.Today)

    }

    class ViewHolder(var binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class DayDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}
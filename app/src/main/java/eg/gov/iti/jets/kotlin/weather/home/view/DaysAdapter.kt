package eg.gov.iti.jets.kotlin.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.databinding.DayItemBinding
import eg.gov.iti.jets.kotlin.weather.model.Daily

class DaysAdapter() :
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
        Picasso
            .get()
            .load(item.weather.get(0).icon)
            .into(holder.binding.dayWeatherIconImageView);
        holder.binding.dayHighLowTempTextView.text = "H:${item.temp.max} L:${item.temp.min}"
        holder.binding.dayDescriptionTextView.text = item.weather.get(0).description
        //Day name
        holder.binding.dayNameTextView.text = item.weather.get(0).description


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
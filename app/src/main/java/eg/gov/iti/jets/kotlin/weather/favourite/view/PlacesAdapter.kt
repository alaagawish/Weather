package eg.gov.iti.jets.kotlin.weather.favourite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.databinding.PlaceItemBinding
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import java.text.SimpleDateFormat
import java.util.*

class PlacesAdapter(var listener: PlaceOnClickListener) :
    ListAdapter<FavouritePlace, PlacesAdapter.ViewHolder>(DayDiffUtil()) {
    lateinit var binding: PlaceItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PlaceItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.countryNameTextView.text = item.timezone
        holder.binding.countryCardView.setOnClickListener {
            listener.displayPlace(item)
        }
        holder.binding.deleteCountryIconImageView.setOnClickListener {
            listener.deletePlace(item)
        }
        Picasso
            .get()
            .load("https://openweathermap.org/img/wn/${item.icon}@2x.png")
            .into(holder.binding.countryIconImageView);
        holder.binding.timeCountryTextView.text =
            SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(
                Date(item.dt * 1000)
            )
        holder.binding.weatherCountryTextView.text = item.main
        holder.binding.tempCountryTextView.text = "${item.temp}\u00B0K"

    }

    class ViewHolder(var binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class DayDiffUtil : DiffUtil.ItemCallback<FavouritePlace>() {
    override fun areItemsTheSame(oldItem: FavouritePlace, newItem: FavouritePlace): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: FavouritePlace, newItem: FavouritePlace): Boolean {
        return oldItem == newItem
    }
}
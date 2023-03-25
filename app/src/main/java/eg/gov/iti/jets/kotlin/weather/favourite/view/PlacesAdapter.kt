package eg.gov.iti.jets.kotlin.weather.favourite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.kotlin.weather.databinding.DayItemBinding
import eg.gov.iti.jets.kotlin.weather.databinding.PlaceItemBinding
import eg.gov.iti.jets.kotlin.weather.model.Daily
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace


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
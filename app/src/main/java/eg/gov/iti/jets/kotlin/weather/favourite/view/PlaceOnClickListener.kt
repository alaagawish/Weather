package eg.gov.iti.jets.kotlin.weather.favourite.view

import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace

interface PlaceOnClickListener {
    fun displayPlace(favouritePlace: FavouritePlace)
    fun deletePlace(favouritePlace: FavouritePlace)

}
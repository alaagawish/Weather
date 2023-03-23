package eg.gov.iti.jets.kotlin.weather.db

import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import eg.gov.iti.jets.kotlin.weather.model.HourlyDBModel
import kotlinx.coroutines.flow.Flow

interface LocalSourceInterface {
    suspend fun addDay(day: DayDBModel)
    suspend fun deleteAll()
    val getDay: Flow<DayDBModel>

    suspend fun addDayHours(hour: HourlyDBModel)
    suspend fun deleteAllHours()
    val getDayHours: Flow<List<HourlyDBModel>>

    suspend fun addComingDay(day: DailyDBModel)
    suspend fun deleteAllComingDays()
    val getNextDays: Flow<List<DailyDBModel>>
    val getAllFavPlaces: Flow<List<FavouritePlace>>
    suspend fun addPlaceToFav(favouritePlace: FavouritePlace)

    suspend fun deletePlaceFromFav(favouritePlace: FavouritePlace)

}

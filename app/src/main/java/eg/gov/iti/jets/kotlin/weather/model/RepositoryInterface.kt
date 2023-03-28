package eg.gov.iti.jets.kotlin.weather.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getOneCallRemote(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Flow<OneCall>

    suspend fun addDay(day: DayDBModel)
    suspend fun deleteAll()
    val getDay: Flow<DayDBModel>

    suspend fun addDayHours(hour: HourlyDBModel)
    suspend fun deleteAllHours()
    val getDayHours: Flow<List<HourlyDBModel>>

    suspend fun addComingDay(day: DailyDBModel)
    suspend fun deleteAllComingDays()
    val getNextDays: Flow<List<DailyDBModel>>

    suspend fun addPlaceToFav(favouritePlace: FavouritePlace)
    suspend fun deleteFavPlace(favouritePlace: FavouritePlace)
    val getAllFav: Flow<List<FavouritePlace>>

}
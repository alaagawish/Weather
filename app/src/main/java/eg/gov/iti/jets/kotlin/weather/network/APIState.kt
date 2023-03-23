package eg.gov.iti.jets.kotlin.weather.network

import eg.gov.iti.jets.kotlin.weather.model.*

sealed class APIState {
    class Success(val oneCall: OneCall) : APIState()
    class SuccessRoomDay(val day: DayDBModel) : APIState()
    class SuccessFavPlaces(val list: List<FavouritePlace>) : APIState()
    class SuccessRoomHours(val list: List<HourlyDBModel>) : APIState()
    class SuccessRoomDaily(val list: List<DailyDBModel>) : APIState()
    class Failure(val e: Throwable) : APIState()
    object Waiting : APIState()

}
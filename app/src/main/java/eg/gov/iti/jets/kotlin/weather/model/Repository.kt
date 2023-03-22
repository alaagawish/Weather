package eg.gov.iti.jets.kotlin.weather.model

import eg.gov.iti.jets.kotlin.weather.db.LocalSourceInterface
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf

const val TAG = "TAG"

class Repository private constructor(
    var remoteSource: RemoteSource,
    var localSourceInterface: LocalSourceInterface
) : RepositoryInterface {

    companion object {
        private var INSTANCE: Repository? = null
        fun getInstance(
            remoteSource: RemoteSource, localSourceInterface: LocalSourceInterface
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val i = Repository(remoteSource, localSourceInterface)
                INSTANCE = i
                i
            }
        }
    }

    override suspend fun getOneCallRemote(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ) = flowOf(remoteSource.getOneCallByNetwork(lat, lon, unit, lang))

    override suspend fun addDay(day: DayDBModel) {
        localSourceInterface.addDay(day)
    }

    override suspend fun deleteAll() {
        localSourceInterface.deleteAll()
    }

    override val getDay = localSourceInterface.getDay

    override suspend fun addDayHours(hour: HourlyDBModel) {
        localSourceInterface.addDayHours(hour)
    }

    override suspend fun deleteAllHours() {
        localSourceInterface.deleteAllHours()
    }

    override val getDayHours = localSourceInterface.getDayHours

    override suspend fun addComingDay(day: DailyDBModel) {
        localSourceInterface.addComingDay(day)
    }

    override suspend fun deleteAllComingDays() {
        localSourceInterface.deleteAllComingDays()
    }

    override val getNextDays = localSourceInterface.getNextDays


}
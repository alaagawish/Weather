package eg.gov.iti.jets.kotlin.weather.model

import eg.gov.iti.jets.kotlin.weather.db.LocalSourceInterface
import eg.gov.iti.jets.kotlin.weather.network.RemoteSource
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(
    var remoteSource: RemoteSource,
    var localSourceInterface: LocalSourceInterface
) : RepositoryInterface {
    private val TAG = "Repository"

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

    override suspend fun getForecastRemote(lat: Double, lon: Double) =
        flowOf(remoteSource.getDayForecastByNetwork(lat, lon))

    override suspend fun getOneCallRemote(lat: Double, lon: Double) =
        flowOf(remoteSource.getOneCallByNetwork(lat, lon))


}
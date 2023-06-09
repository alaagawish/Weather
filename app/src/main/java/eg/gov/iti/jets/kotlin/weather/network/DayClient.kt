package eg.gov.iti.jets.kotlin.weather.network

class DayClient private constructor() : RemoteSource {
    private val dayService: DayService by lazy {
        Retrofit.getInstance().create(DayService::class.java)
    }

    companion object {
        private var instance: DayClient? = null
        fun getInstance(): DayClient {
            return instance ?: synchronized(this) {
                val t = DayClient()
                instance = t
                t
            }
        }
    }

    override suspend fun getOneCallByNetwork(lat: Double, lon: Double, unit: String, lang: String) =
        dayService.getOneCall(lat, lon, unit, lang)
}
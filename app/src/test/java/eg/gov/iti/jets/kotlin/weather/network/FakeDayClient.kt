package eg.gov.iti.jets.kotlin.weather.network

import eg.gov.iti.jets.kotlin.weather.model.*

class FakeDayClient(
    private val oneCall: OneCall = OneCall(
        11.3, 122.4, "zone",
        Current(
            1680467727, 1680432022, 1680477788, 281.63, 279.49, 1016, 27, 1.21, 0, 10000, 3.6,
            mutableListOf(WeatherObject("Clear", "clear sky", "01d"))
        ), mutableListOf(
            Hourly(1680465600, 281.52, mutableListOf(WeatherObject("Clear", "clear sky", "01d"))),
            Hourly(1680469200, 281.63, mutableListOf(WeatherObject("Clear", "clear sky", "01d"))),
            Hourly(1680490800, 273.59, mutableListOf(WeatherObject("Clouds", "few clouds", "02n"))),
            Hourly(1680505200, 272.93, mutableListOf(WeatherObject("Clear", "clear sky", "01n"))),
            Hourly(1680523200, 275.47, mutableListOf(WeatherObject("Clouds", "few clouds", "02d"))),
            Hourly(
                1680555600,
                288.31,
                mutableListOf(WeatherObject("Clouds", "scattered clouds", "03d"))
            ),
            Hourly(
                1680584400,
                280.7,
                mutableListOf(WeatherObject("Clouds", "overcast clouds", "04n"))
            )
        ),
        mutableListOf(
            Daily(
                1680634800,
                Temp(292.73, 300.0),
                mutableListOf(WeatherObject("Clouds", "scattered clouds", "03d"))
            ),
            Daily(
                1680541200,
                Temp(272.81, 288.31),
                mutableListOf(WeatherObject("Clouds", "few clouds", "02d"))
            ),
            Daily(
                1680627600,
                Temp(279.72, 292.98),
                mutableListOf(WeatherObject("Clouds", "broken clouds", "04d"))
            ),
            Daily(
                1680714000,
                Temp(284.27, 289.64),
                mutableListOf(WeatherObject("Rain", "light rain", "10d"))
            )
        ),
        mutableListOf()
    )
) : RemoteSource {
    override suspend fun getOneCallByNetwork(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): OneCall {
        return oneCall
    }


}


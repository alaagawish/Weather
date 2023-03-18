package eg.gov.iti.jets.kotlin.weather.model


data class Forecast(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: Listt,
    val city: City
)
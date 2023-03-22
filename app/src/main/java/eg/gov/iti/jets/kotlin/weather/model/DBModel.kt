package eg.gov.iti.jets.kotlin.weather.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "day")
data class DayDBModel(
    @PrimaryKey
    val dt: Long,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val clouds: Int,
    val visibility: Long,
    val wind_speed: Double,
    val main: String,
    val description: String,
    val icon: String
)

@Entity(tableName = "hour")

data class HourlyDBModel(
    @PrimaryKey
    val dt: Long,
    val temp: Double,
    val main: String,
    val description: String,
    val icon: String
)

@Entity(tableName = "daily")

data class DailyDBModel(
    @PrimaryKey
    val dt: Long,
    val min: Double,
    val max: Double,
    val main: String,
    val description: String,
    val icon: String
)
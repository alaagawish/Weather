package eg.gov.iti.jets.kotlin.weather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
import eg.gov.iti.jets.kotlin.weather.model.HourlyDBModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HourDao {

    @get:Query("SELECT * FROM hour")
    val getDayHours: Flow<List<HourlyDBModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDayHours(hourlyDBModel: HourlyDBModel)

    @Query("DELETE FROM hour")
    suspend fun deleteAllHours()
}
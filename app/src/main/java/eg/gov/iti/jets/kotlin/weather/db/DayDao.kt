package eg.gov.iti.jets.kotlin.weather.db

import androidx.room.*
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {
    @get:Query("SELECT * FROM day")
    val getDay: Flow<DayDBModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDay(day: DayDBModel)

    @Query("DELETE FROM day")
    suspend fun deleteAll(): Int
}
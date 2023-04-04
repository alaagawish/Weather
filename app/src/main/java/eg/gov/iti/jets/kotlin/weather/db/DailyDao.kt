package eg.gov.iti.jets.kotlin.weather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyDao {
    @get:Query("SELECT * FROM daily")
    val getNextDays: Flow<List<DailyDBModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addComingDay(dailyDBModel: DailyDBModel)

    @Query("DELETE FROM daily")
    suspend fun deleteAllComingDays()
}
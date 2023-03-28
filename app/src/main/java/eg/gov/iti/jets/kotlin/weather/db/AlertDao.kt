package eg.gov.iti.jets.kotlin.weather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eg.gov.iti.jets.kotlin.weather.model.AlertsDB
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @get:Query("SELECT * FROM alert")
    val getAllAlerts: Flow<List<AlertsDB>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlert(alert: AlertsDB)

    @Query("DELETE FROM alert")
    suspend fun deleteAllAlerts()
}
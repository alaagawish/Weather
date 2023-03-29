package eg.gov.iti.jets.kotlin.weather.db

import androidx.room.*
import eg.gov.iti.jets.kotlin.weather.model.AlertsDB
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @get:Query("SELECT * FROM alert")
    val getAllAlerts: Flow<List<AlertsDB>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlert(alert: AlertsDB)

    @Delete
    suspend fun deleteAlert(alert: AlertsDB)
}
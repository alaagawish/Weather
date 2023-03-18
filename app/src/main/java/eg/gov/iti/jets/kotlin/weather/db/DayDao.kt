package eg.gov.iti.jets.kotlin.weather.db

import androidx.room.*
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel

@Dao
interface DayDao {

//    @Query("SELECT * FROM day")
//    suspend fun getDay(): DayDBModel

//    @Update
//    suspend fun updateDay(day: DayDBModel): Boolean

//    @Delete
//    suspend fun deleteDay(day: DayDBModel): Int

}
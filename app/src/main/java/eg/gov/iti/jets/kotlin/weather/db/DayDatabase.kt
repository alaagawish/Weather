package eg.gov.iti.jets.kotlin.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import eg.gov.iti.jets.kotlin.weather.model.HourlyDBModel


@Database(entities = [DayDBModel::class, HourlyDBModel::class, DailyDBModel::class,FavouritePlace::class], version = 1)

abstract class DayDatabase : RoomDatabase() {

    abstract fun getDayDao(): DayDao
    abstract fun getDailyDao(): DailyDao
    abstract fun getHourDao(): HourDao
    abstract fun getFavDao(): FavDao

    companion object {
        @Volatile
        private var INSTANCE: DayDatabase? = null
        fun getInstance(context: Context): DayDatabase {
            return INSTANCE ?: synchronized(this) {
                val i =
                    Room.databaseBuilder(
                        context.applicationContext,
                        DayDatabase::class.java,
                        "my_database"
                    )
                        .build()
                INSTANCE = i
                i
            }
        }
    }
}

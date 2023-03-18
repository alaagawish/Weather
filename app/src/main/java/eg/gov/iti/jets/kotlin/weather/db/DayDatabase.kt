package eg.gov.iti.jets.kotlin.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel


@Database(entities = arrayOf(DayDBModel::class), version = 1)

abstract class DayDatabase : RoomDatabase() {

    abstract fun getDayDao(): DayDao

    companion object {
        @Volatile
        private var INSTANCE: DayDatabase? = null
        fun getInstance(context: Context): DayDatabase {
            return INSTANCE ?: synchronized(this) {
                val i =
                    Room.databaseBuilder(
                        context.applicationContext,
                        DayDatabase::class.java,
                        "day"
                    )
                        .build()
                INSTANCE = i
                i
            }
        }
    }
}

package eg.gov.iti.jets.kotlin.weather.db

import androidx.room.*
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDao {
    @get:Query("SELECT * FROM fav")
    val getAllFavPlaces: Flow<List<FavouritePlace>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavPlace(favouritePlace: FavouritePlace)

    @Delete
    suspend fun deletePlaceFromFav(favouritePlace: FavouritePlace)
}
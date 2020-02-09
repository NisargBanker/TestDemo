package bankernisarg.app.com.locationdemo.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bankernisarg.app.com.locationdemo.data.db.entities.TripData

@Dao
interface TripDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllTripData(quotes: List<TripData>)

    @Query("SELECT * FROM TripData")
    fun getAllTrips(): LiveData<List<TripData>>

}
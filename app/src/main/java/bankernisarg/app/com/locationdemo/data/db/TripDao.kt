package bankernisarg.app.com.locationdemo.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bankernisarg.app.com.locationdemo.data.db.entities.Trip

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrip(trip: Trip) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllQuotes(quotes : List<Trip>)

    @Query("SELECT * FROM Trip")
    fun getAllTrips() : LiveData<List<Trip>>

}
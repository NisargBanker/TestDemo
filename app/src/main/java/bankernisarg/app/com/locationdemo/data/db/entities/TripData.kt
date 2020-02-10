package bankernisarg.app.com.locationdemo.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TripData(
    var trip_id: Int = 0,
    val lat: Double,
    val lng: Double,
    val time: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
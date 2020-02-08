package bankernisarg.app.com.locationdemo.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Trip(

    val trip_name: String,
    val trip_source_address: String,
    val trip_destination_address: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
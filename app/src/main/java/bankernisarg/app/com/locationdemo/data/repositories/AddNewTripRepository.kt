package bankernisarg.app.com.locationdemo.data.repositories

import bankernisarg.app.com.locationdemo.data.db.AppDatabase
import bankernisarg.app.com.locationdemo.data.db.entities.Trip

class AddNewTripRepository(
    private val db: AppDatabase
) {
    suspend fun addTrip(trip: Trip) = db.getTripDao().addTrip(trip)
}
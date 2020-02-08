package bankernisarg.app.com.locationdemo.data.repositories

import bankernisarg.app.com.locationdemo.data.db.AppDatabase

class AllTripRepository(
    private val db: AppDatabase
) {
    fun getAllTrip() = db.getTripDao().getAllTrips()
}
package bankernisarg.app.com.locationdemo.ui.add_new_trip

import android.view.View
import androidx.lifecycle.ViewModel
import bankernisarg.app.com.locationdemo.data.callback.AddTripListener
import bankernisarg.app.com.locationdemo.data.db.entities.Trip
import bankernisarg.app.com.locationdemo.data.repositories.AddNewTripRepository
import bankernisarg.app.com.locationdemo.util.Coroutines
import java.lang.Exception

class AddNewTripViewModel(private val repository: AddNewTripRepository) : ViewModel() {

    var trip_name: String? = null
    var trip_start_destination: String? = null
    var trip_end_destination: String? = null

    var addTripListener: AddTripListener? = null

    fun addNewTrip(view: View) {
        addTripListener?.onStarted()

        if (trip_name.isNullOrEmpty()) {
            addTripListener?.onFailure("Trip name is required")
            return
        }

        if (trip_start_destination.isNullOrEmpty()) {
            addTripListener?.onFailure("Start destination is required")
            return
        }

        if (trip_end_destination.isNullOrEmpty()) {
            addTripListener?.onFailure("End destination is required")
            return
        }


        Coroutines.main {
            try {
                repository.addTrip(
                    Trip(
                        trip_name!!, trip_start_destination!!,
                        trip_end_destination!!
                    )
                )
                addTripListener?.onSuccess()
                return@main
            } catch (e: Exception) {
                addTripListener?.onFailure(e.message!!)
            }
        }
    }

}

package bankernisarg.app.com.locationdemo.ui.trip

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import bankernisarg.app.com.locationdemo.data.repositories.AllTripRepository
import bankernisarg.app.com.locationdemo.util.lazyDeferred

class AllTripViewModel(private val repository: AllTripRepository) : ViewModel() {

    fun onAddTrip(view: View){
        Navigation.findNavController(view).navigate(AllTripFragmentDirections.actionAllTripFragmentToAddNewTripFragment())
    }

    val trips by lazyDeferred {
        repository.getAllTrip()
    }

    fun getLoggedInUser() = repository.getAllTrip()

}

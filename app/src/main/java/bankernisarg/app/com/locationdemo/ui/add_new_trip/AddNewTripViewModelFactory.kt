package bankernisarg.app.com.locationdemo.ui.add_new_trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bankernisarg.app.com.locationdemo.data.repositories.AddNewTripRepository
import bankernisarg.app.com.locationdemo.data.repositories.AllTripRepository

@Suppress("UNCHECKED_CAST")
class AddNewTripViewModelFactory(
    private val repository: AddNewTripRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddNewTripViewModel(repository) as T
    }
}
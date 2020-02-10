package bankernisarg.app.com.locationdemo.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bankernisarg.app.com.locationdemo.data.repositories.AllTripRepository

@Suppress("UNCHECKED_CAST")
class AllTripViewModelFactory(
    private val repository: AllTripRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllTripViewModel(repository) as T
    }
}
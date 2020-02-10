package bankernisarg.app.com.locationdemo.ui.add_new_trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import bankernisarg.app.com.locationdemo.R
import bankernisarg.app.com.locationdemo.data.callback.AddTripListener
import bankernisarg.app.com.locationdemo.databinding.FragmentAddNewTripBinding
import kotlinx.android.synthetic.main.fragment_add_new_trip.*
import bankernisarg.app.com.locationdemo.util.hide
import bankernisarg.app.com.locationdemo.util.show
import bankernisarg.app.com.locationdemo.util.snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * A simple [Fragment] subclass.
 */
class AddNewTripFragment : Fragment(), AddTripListener, KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: AddNewTripViewModel
    private val factory: AddNewTripViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentAddNewTripBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_trip, container, false)
        viewModel = ViewModelProvider(this, factory).get(AddNewTripViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.addTripListener = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStarted() {
        progress_bar.show()
    }

    override fun onSuccess() {
        progress_bar.hide()
        activity?.onBackPressed()
        //Navigation.findNavController(this.requireView())

    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        root_layout.snackbar(message)
    }


}

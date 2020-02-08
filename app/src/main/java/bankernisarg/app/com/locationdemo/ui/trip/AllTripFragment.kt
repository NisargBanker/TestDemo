package bankernisarg.app.com.locationdemo.ui.trip

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import bankernisarg.app.com.locationdemo.R
import bankernisarg.app.com.locationdemo.data.callback.RecyclerViewClickListener
import bankernisarg.app.com.locationdemo.data.db.entities.Trip
import bankernisarg.app.com.locationdemo.databinding.AllTripFragmentBinding
import kotlinx.android.synthetic.main.all_trip_fragment.*
import bankernisarg.app.com.locationdemo.util.Coroutines
import bankernisarg.app.com.locationdemo.util.hide
import bankernisarg.app.com.locationdemo.util.show
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class AllTripFragment : Fragment(), KodeinAware, RecyclerViewClickListener {

    override val kodein by kodein()

    private lateinit var viewModel: AllTripViewModel
    private val factory: AllTripViewModelFactory by instance()
    lateinit var binding: AllTripFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.all_trip_fragment, container, false)
        viewModel = ViewModelProvider(this, factory).get(AllTripViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() = Coroutines.main {
        progress_bar.show()
        viewModel.trips.await().observe(this, Observer { it ->
            progress_bar.hide()
            val trips = it
            binding.recTrip.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = AllTripAdapter(trips, this)
            }
        })
    }

    override fun onRecyclerViewItemClick(view: View, trip: Trip) {
    }

}

package bankernisarg.app.com.locationdemo.ui.trip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import bankernisarg.app.com.locationdemo.R
import bankernisarg.app.com.locationdemo.data.callback.RecyclerViewClickListener
import bankernisarg.app.com.locationdemo.data.db.entities.Trip
import bankernisarg.app.com.locationdemo.databinding.ItemTripBinding

class AllTripAdapter(
    private val trips: List<Trip>,
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<AllTripAdapter.MoviesViewHolder>() {

    override fun getItemCount() = trips.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MoviesViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_trip,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.itemTripBinding.trip = trips[position]
        holder.itemTripBinding.btnStartTrip.setOnClickListener {
            listener.onRecyclerViewItemClick(it, trips[position])
        }
        holder.itemTripBinding.btnEndTrip.setOnClickListener {
            listener.onRecyclerViewItemClick(it, trips[position])
        }
    }


    inner class MoviesViewHolder(
        val itemTripBinding: ItemTripBinding
    ) : RecyclerView.ViewHolder(itemTripBinding.root)
}
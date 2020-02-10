package bankernisarg.app.com.locationdemo.ui.map_route


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import bankernisarg.app.com.locationdemo.R
import bankernisarg.app.com.locationdemo.data.db.AppDatabase
import bankernisarg.app.com.locationdemo.data.db.entities.Trip
import bankernisarg.app.com.locationdemo.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class MapsFragment : Fragment(), OnMapReadyCallback, KodeinAware {

    override val kodein by kodein()

    lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap
    val db: AppDatabase by instance()
    var mList: ArrayList<LatLng> = ArrayList()
    var id: Int? = null
    lateinit var mTrip: Trip

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        binding.lifecycleOwner = this
        return return binding.root
    }

    private fun getIntentData() {
        val args =
            MapsFragmentArgs.fromBundle(
                arguments!!
            )
        id = args.tripId
        mTrip = args.tripData
        binding.trip = mTrip
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getIntentData()
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Draw polyline on map
    fun drawPolyLineOnMap(list: List<LatLng>) {

        Handler().postDelayed({
            val polyOptions = PolylineOptions()
            polyOptions.color(Color.RED)
            polyOptions.width(10f)
            polyOptions.addAll(list)

            mMap.clear()
            mMap.addPolyline(polyOptions)

            val builder = LatLngBounds.Builder()
            for (latLng in list) {
                builder.include(latLng)
            }

            val bounds = builder.build()

            //BOUND_PADDING is an int to specify padding of bound.. try 100.
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 10)
            mMap.animateCamera(cu)

            mMap.addMarker(MarkerOptions().position(list[0]))
            mMap.addMarker(MarkerOptions().position(list[list.size-1]))
        }, 500)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        db.getTripDataDao().getAllTrips(id!!).observeForever {

            mList.clear()


            //setMarker(LatLng(it[0].lat, it[0].lng))

            for (trip in it.indices) {
                mList.add(LatLng(it[trip].lat, it[trip].lng))
            }
            drawPolyLineOnMap(mList)
        }


    }

    private fun setMarker(latLng: LatLng) {
        val marker =
            MarkerOptions().position(latLng).title("Hello Maps")
        mMap.addMarker(marker)
    }

}

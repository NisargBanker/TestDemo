package bankernisarg.app.com.locationdemo.ui.map_route

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import bankernisarg.app.com.locationdemo.R
import bankernisarg.app.com.locationdemo.data.db.AppDatabase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, KodeinAware {

    override val kodein by kodein()

    private lateinit var mMap: GoogleMap
    val db: AppDatabase by instance()
    var mList: ArrayList<LatLng> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


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
        },500)

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        db.getTripDataDao().getAllTrips().observeForever {

            mList.clear()

            for (trip in it.indices) {
                mList.add(LatLng(it[trip].lat, it[trip].lng))
            }
            drawPolyLineOnMap(mList)
        }


    }
}

package bankernisarg.app.com.locationdemo.ui.trip

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import bankernisarg.app.com.locationdemo.R
import bankernisarg.app.com.locationdemo.data.callback.RecyclerViewClickListener
import bankernisarg.app.com.locationdemo.data.db.AppDatabase
import bankernisarg.app.com.locationdemo.data.db.entities.Trip
import bankernisarg.app.com.locationdemo.data.db.entities.TripData
import bankernisarg.app.com.locationdemo.data.location.LocationUpdatesBroadcastReceiver
import bankernisarg.app.com.locationdemo.data.location.Utils
import bankernisarg.app.com.locationdemo.databinding.AllTripFragmentBinding
import bankernisarg.app.com.locationdemo.ui.map_route.MapsActivity
import bankernisarg.app.com.locationdemo.util.Coroutines
import bankernisarg.app.com.locationdemo.util.hide
import bankernisarg.app.com.locationdemo.util.show
import bankernisarg.app.com.locationdemo.util.toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.all_trip_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class AllTripFragment : Fragment(), KodeinAware, RecyclerViewClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    override val kodein by kodein()

    private lateinit var viewModel: AllTripViewModel
    private val factory: AllTripViewModelFactory by instance()
    val db: AppDatabase by instance()
    lateinit var binding: AllTripFragmentBinding
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val TAG = "test"
    private val REQUEST_CHECK_SETTINGS = 0x1
    private val UPDATE_INTERVAL: Long = 10000
    private val FASTEST_UPDATE_INTERVAL: Long = 10000

    private val MAX_WAIT_TIME = UPDATE_INTERVAL
    private var mRequestingLocationUpdates: Boolean? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null

    private var mTripData: ArrayList<TripData>? = null
    private var mSelectedTripId: Int? = null

    private val pendingIntent: PendingIntent
        get() {

            val intent = Intent(this.activity, LocationUpdatesBroadcastReceiver::class.java)
            intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(
                this.activity,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

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

        mRequestingLocationUpdates = false
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.requireContext())
        mSettingsClient = LocationServices.getSettingsClient(this.requireContext())
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
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
                it.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.list_layout_controller)
            }
        })
    }

    override fun onRecyclerViewItemClick(view: View, trip: Trip) {
        when (view.id) {
            R.id.btnStartTrip -> {
                mSelectedTripId = trip.id
                startUpdatesButtonHandler(view)
            }
            R.id.btnEndTrip -> {
                Coroutines.main {
                    removeLocationUpdates(view)
                }
            }
            R.id.root_card -> {
                Intent(this.requireContext() , MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(this.requireContext())
            .registerOnSharedPreferenceChangeListener(this)
    }

  /*  override fun onResume() {
        super.onResume()
        if (mRequestingLocationUpdates!! && checkPermissions()) {
            //startLocationUpdates()
        }
        Utils.getRequestingLocationUpdates(this.requireContext())
    }*/

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(this.requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.maxWaitTime = MAX_WAIT_TIME
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (s == Utils.KEY_LOCATION_UPDATES_RESULT) {
            //db.getTripDataDao().saveAllTripData()

            mTripData!!.add(
                TripData(
                    mSelectedTripId!!,
                    Utils.locations!![0].latitude,
                    Utils.locations!![0].longitude,
                    DateFormat.getDateTimeInstance().format(Date())
                )
            )
            activity?.toast(Utils.getLocationUpdatesResult(this.requireContext()))
        } else if (s == Utils.KEY_LOCATION_UPDATES_REQUESTED) {
            //updateButtonsState(Utils.getRequestingLocationUpdates(this))
        }
    }

    fun requestLocationUpdates(view: View?) {
        try {
            Log.i(TAG, "Starting location updates")
            Utils.setRequestingLocationUpdates(this.requireContext(), true)
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, pendingIntent)
        } catch (e: SecurityException) {
            Utils.setRequestingLocationUpdates(this.requireContext(), false)
            e.printStackTrace()
        }

    }

    suspend fun removeLocationUpdates(view: View) {
        withContext(Dispatchers.IO) {
            db.getTripDataDao().saveAllTripData(mTripData!!)
        }
        Utils.setRequestingLocationUpdates(this.requireContext(), false)
        mFusedLocationClient!!.removeLocationUpdates(pendingIntent)
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
            }
        }
    }

    fun startUpdatesButtonHandler(view: View) {
        if (!mRequestingLocationUpdates!!) {
            mRequestingLocationUpdates = true
            startLocationUpdates()
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(
                this.requireActivity(),
                OnSuccessListener<LocationSettingsResponse> {
                    Log.i(TAG, "All location settings are satisfied.")
                    mTripData = ArrayList()
                    requestLocationUpdates(view)
                })
            .addOnFailureListener(this.requireActivity(), OnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            TAG,
                            "Location settings are not satisfied. Attempting to upgrade " + "location settings "
                        )
                        try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                this.requireActivity(),
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                        mRequestingLocationUpdates = false
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> requestLocationUpdates(view)

                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "User chose not to make required location settings changes.")
                    mRequestingLocationUpdates = false
                }
            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }

}

package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.tbruyelle.rxpermissions2.RxPermissions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


private const val KEY_ZOOM = "camera_zoom"
private const val KEY_LOCATION = "map_location"
private const val DEFAULT_ZOOM = 17f

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        val TAG = SelectLocationFragment::class.java.simpleName
        val DEFAULT_LOCATION_LATLNG = LatLng(27.2038, 77.5011)
    }

    private var geoFenceLimits: Circle? = null
    private var lastKnownLocation: LatLng? = null
    private var lastKnownLocationName: String? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var lastKnownZoom: Float = DEFAULT_ZOOM

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private var map: GoogleMap? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            lastKnownZoom = savedInstanceState.getFloat(KEY_ZOOM)
        }
        binding =
                DataBindingUtil.inflate(
                        inflater,
                        R.layout.fragment_select_location,
                        container,
                        false
                )

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        binding.btnDone.setOnClickListener {
            onLocationSelected()
        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)

        readyMap()

//        DONE: add the map setup implementation
//        DONE: zoom to the user location after taking his permission
//        DONE: add style to the map
//        DONE: put a marker to location that the user selected
//        Done: call this function after the user confirms on the selected location
    }

    private fun readyMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun onLocationSelected() {
        //        DONE: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence

        lastKnownLocation?.let {
            _viewModel.latitude.value = it.latitude
            _viewModel.longitude.value = it.longitude
            _viewModel.reminderSelectedLocationStr.value = lastKnownLocationName
            _viewModel.navigationCommand.postValue(NavigationCommand.Back)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // DONE: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(gooleMap: GoogleMap?) {
        map = gooleMap

        if (lastKnownLocation != null) {
            addMarker(lastKnownLocation!!, lastKnownZoom)
        } else {
            requestPermission()
        }

        setMapStyle()
        //setMapLongClick()
        setPoiClick()
    }


    private fun addMarker(latLng: LatLng, zoomLevel: Float = DEFAULT_ZOOM) {
        val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
        )

        this.map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        this.map?.addMarker(
                MarkerOptions().position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet)
        )

        lastKnownLocation = latLng

        //drawGeoFence(latLng)
    }

    private fun addPoiMarker(
            poi: PointOfInterest,
            zoomLevel: Float = DEFAULT_ZOOM
    ) {
        this.map?.animateCamera(CameraUpdateFactory.newLatLngZoom(poi.latLng, zoomLevel))
        this.map?.addMarker(
                MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name)
        )?.showInfoWindow()

        lastKnownLocation = poi.latLng
        lastKnownLocationName = poi.name

        drawGeoFence(poi.latLng)
    }


    private fun setMapLongClick() {
        this.map?.setOnMapLongClickListener { latLng ->
            addMarker(latLng)
        }
    }

    private fun setPoiClick() {
        map?.setOnPoiClickListener { poi ->
            addPoiMarker(poi)
        }
    }

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RxPermissions(this)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { permission ->
                    // will emit 1 Permission object
                    when {
                        permission.granted -> {
                            // All permissions are granted !
                            getCurrentLocation()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // At least one denied permission without ask never again
                            Toast.makeText(
                                    activity,
                                    getString(R.string.permissions_request),
                                    Toast.LENGTH_SHORT
                            )
                                    .show()
                        }
                        else -> {
                            // At least one denied permission with ask never again Need to go to the settings
                            Toast.makeText(
                                    activity,
                                    getString(R.string.permissions_request_settings),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
    }

    private fun getCurrentLocation() {
        map?.isMyLocationEnabled = true

        try {
            val locationResult = fusedLocationProviderClient?.lastLocation

            locationResult?.addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    lastKnownLocation = LatLng(location?.latitude!!, location.longitude)
                    addMarker(lastKnownLocation!!)
                } else {
                    Log.d(
                            TAG,
                            "Current location is null. Using defaults."
                    )
                    Log.e(TAG, "Exception: %s", task.exception)
                    addMarker(DEFAULT_LOCATION_LATLNG)
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }


    private fun setMapStyle() {
        try {
            val success = map?.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            activity,
                            R.raw.map_style
                    )
            ) ?: false

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun drawGeoFence(latLng: LatLng) {
        val circleOptions = CircleOptions()
                .center(latLng)
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(SaveReminderFragment.DEFAULT_GEOFENCE_RADIUS.toDouble())

        geoFenceLimits?.remove()
        geoFenceLimits = map!!.addCircle(circleOptions)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getCurrentLocation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
                KEY_LOCATION,
                LatLng(
                        map?.cameraPosition?.target!!.latitude,
                        map?.cameraPosition?.target!!.longitude
                )
        )
        outState.putFloat(KEY_ZOOM, map?.cameraPosition!!.zoom);
        super.onSaveInstanceState(outState);
    }
}

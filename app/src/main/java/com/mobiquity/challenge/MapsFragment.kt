package com.mobiquity.challenge

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var recentLatLng: LatLng? = null
    private var googleMap: GoogleMap? = null
    private var showBookmarkMenu = true
    private val list: ArrayList<LatLng> = arrayListOf(LatLng(-34.2, 151.2))
    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        drawMarkers(googleMap)
        resetMarkerClickListener()
    }

    private fun resetMarkerClickListener() {

        googleMap?.setOnMarkerClickListener {
            Toast.makeText(activity, "Marker clicked", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun drawMarkers(googleMap: GoogleMap) {

        googleMap.clear()
        list.forEach { googleMap.addMarker(MarkerOptions().position(it)) }

        val sydney = LatLng(-34.0, 151.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_add_bookmark).isVisible = showBookmarkMenu
        menu.findItem(R.id.action_remove_bookmark).isVisible = showBookmarkMenu
        menu.findItem(R.id.action_done).isVisible = showBookmarkMenu.not()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_add_bookmark ->
                activity?.let {
                    AlertDialog.Builder(it)
                        .setTitle("Tap on map to add bookmark")
                        .setPositiveButton("Okay") { _, _ ->

                            showBookmarkMenu = false
                            activity?.invalidateOptionsMenu()
                            googleMap?.clear()
                            googleMap?.setOnMarkerClickListener(null)

                            googleMap?.setOnMapClickListener { latLng ->
                                googleMap?.clear()
                                googleMap?.addMarker(MarkerOptions().position(latLng))
                                recentLatLng = latLng
                                activity?.invalidateOptionsMenu()
                            }
                        }
                        .setNegativeButton("Cancel", null).show()
                }

            R.id.action_remove_bookmark ->
                activity?.let {
                    AlertDialog.Builder(it)
                        .setTitle("Tap on pin to remove bookmark")
                        .setPositiveButton("Okay") { _, _ ->

                            showBookmarkMenu = false
                            activity?.invalidateOptionsMenu()

                            googleMap?.setOnMarkerClickListener { marker ->
                                list.remove(marker.position)
                                drawMarkers(googleMap!!)
                                showBookmarkMenu = true
                                activity?.invalidateOptionsMenu()
                                resetMarkerClickListener()
                                Toast.makeText(activity, "Bookmark removed", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            }
                        }
                        .setNegativeButton("Cancel", null).show()
                }

            R.id.action_done -> {
                if (recentLatLng == null)
                    Toast.makeText(activity, "No place is selected", Toast.LENGTH_SHORT).show()
                else {
                    list.add(recentLatLng!!)
                    Toast.makeText(activity, "Selected place is bookmarked", Toast.LENGTH_SHORT)
                        .show()
                }

                recentLatLng = null
                drawMarkers(googleMap!!)
                showBookmarkMenu = true
                activity?.invalidateOptionsMenu()
                googleMap?.setOnMapClickListener(null)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
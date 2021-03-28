package com.mobiquity.challenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney 1"))
        mMap.addMarker(MarkerOptions().position(LatLng(-34.2, 151.2)).title("Marker in Sydney 2"))
        mMap.addMarker(MarkerOptions().position(LatLng(-35.0, 150.2)).title("Marker in Sydney 3"))
        mMap.addMarker(MarkerOptions().position(LatLng(-27.4, 133.2)).title("Marker in marla 4"))

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 8f)
//        mMap.animateCamera(cameraUpdate)
    }
}
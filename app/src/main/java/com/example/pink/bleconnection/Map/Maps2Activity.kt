package com.example.pink.bleconnection.Map

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pink.bleconnection.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds



class Maps2Activity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isIndoorEnabled = true
        mMap.isTrafficEnabled = false

        val cameraTraget = LatLngBounds(
                LatLng(13.6, 100.494201), LatLng(13.7, 100.494201))
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(13.650051, 100.494201)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in KMUTT"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20f))
        mMap.setLatLngBoundsForCameraTarget(cameraTraget);


    }
}

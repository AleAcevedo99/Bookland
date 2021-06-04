package com.example.bookland.Constants

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

class UserLocation(val context: Context, val googleApiClient: GoogleApiClient?) :
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener {

    private  var location:Location?= null
    private var locationRequest: LocationRequest?= null

    //Cliente para determinar la ubicacion
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationManager: LocationManager

    private var locationCallback: LocationCallback = object  : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            onLocationChanged(p0?.lastLocation)
        }
    }



    override fun onConnected(p0: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(p0: Location?) {
        TODO("Not yet implemented")
    }

}
package com.example.bookland

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bookland.Constants.ApplicationPermissions
import com.example.bookland.Constants.Constants
import com.example.bookland.databinding.ActivityAboutUsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener


class AboutUsActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener  {
    private lateinit var binding: ActivityAboutUsBinding
    private val permission = ApplicationPermissions(this)
    val destinationLatitude = 19.291342800380818
    val destinationLongitude = -99.13927255055891

    /*
        === variables para UBICACION =======
     */
    private  var location:Location?= null
    private var locationRequest: LocationRequest?= null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationManager: LocationManager

    private var locationCallback: LocationCallback = object  : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            onLocationChanged(p0?.lastLocation)
        }
    }

    private var googleApiClient:GoogleApiClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.txt_title_about_us)

        binding.btnKnowOurSucursal.setOnClickListener {
            if(!permission.hasPermissions(Constants.ACCESS_FINE_LOCATION)){
                permission.acceptPermission(Constants.ACCESS_FINE_LOCATION, 1)
            }else{
                startLocation()
            }
        }

        googleApiClient = GoogleApiClient.Builder(this).apply {
            addConnectionCallbacks(this@AboutUsActivity)
            addOnConnectionFailedListener(this@AboutUsActivity)
            addApi(LocationServices.API)
        }.build()


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    loadMapWitoutUserLocation()
                }else{
                    startLocation()
                }
            }
        }
    }

    fun loadMapUserLocation(latitude:Double?, longitude: Double?){
        if(latitude != null && longitude != null){
            var intent = Intent(Intent.ACTION_VIEW).apply { data= Uri.parse( "https://www.google.com/maps/dir/?api=1&origin=$latitude,$longitude&destination=$destinationLatitude,$destinationLongitude") }
            if(intent.resolveActivity(packageManager) != null){
                startActivity(intent)
                stopLocation()
            }
            else{
                actionDialog(getString(R.string.txt_no_app)).show()
            }
        }else{
            loadMapWitoutUserLocation()
        }


    }

    fun actionDialog(message:String): AlertDialog {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.app_name)
        alert.setMessage(message)

        alert.setPositiveButton(R.string.txt_btn_ok){ _, _ ->

        }
        return  alert.create()
    }

    fun loadMapWitoutUserLocation(){
        var intent = Intent(Intent.ACTION_VIEW).apply { data= Uri.parse( "geo:0,0?q=$destinationLatitude,$destinationLongitude(Bookland)") }

        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }
        else{
            actionDialog(getString(R.string.txt_no_app)).show()
        }
    }

    override fun onConnected(p0: Bundle?) {
        startLocation()
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient?.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        //Mostrar un error
    }

    override fun onLocationChanged(p0: Location?) {
        //cambio de ubicacion
        loadMapUserLocation(p0?.latitude, p0?.longitude)

    }

    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    private fun startLocation(){
        if(isLocationEnabled){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            location = getLastLocation()
            if(location == null){
                Log.d(Constants.LOG_TAG, "location null")
                initLocation()
            }else{
                Log.d(Constants.LOG_TAG, "location not null null")
                loadMapUserLocation(location?.latitude, location?.longitude)

            }
        }else{
            loadMapWitoutUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation():Location?{
        var lastLocation: Location? = null
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            OnSuccessListener<Location>{
                if(it != null){
                    lastLocation = it
                }
            }
        }
        return  lastLocation
    }

    @SuppressLint("MissingPermission")
    private fun initLocation(){
        locationRequest = LocationRequest.create().apply {
            //precisión de la ubicación
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            //milisegundos, cada cuanto recibe actualización deubicación
            interval = (3*1000).toLong()
            //pasivamente si el dispositivo ya tiene las ubicaciones las adquiere
            fastestInterval = (2*1000).toLong()
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocation(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
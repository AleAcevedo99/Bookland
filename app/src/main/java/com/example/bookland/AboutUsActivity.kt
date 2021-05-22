package com.example.bookland

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bookland.Constants.ApplicationPermissions
import com.example.bookland.Constants.Constants
import com.example.bookland.databinding.ActivityAboutUsBinding


class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding
    private val permission = ApplicationPermissions(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.txt_title_about_us)

        binding.btnKnowOurSucursal.setOnClickListener {
            if(!permission.hasPermissions(Constants.ACCESS_FINE_LOCATION)){
                permission.acceptPermission(Constants.ACCESS_FINE_LOCATION, 1)
            }else{
                loadMap()
            }
        }



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    actionDialog(getString(R.string.txt_need_permission)).show()
                }else{
                    loadMap()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun loadMap(){
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val longitude: Double = location?.getLongitude() ?: 0.0
        val latitude: Double = location?.getLatitude() ?: 0.0
        var intent = Intent(Intent.ACTION_VIEW).apply { data= Uri.parse( "geo:${latitude}, ${longitude}?z=18 &q${latitude}, ${longitude} &q=19.3126883,-99.1723803") }

        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }
        else{
            actionDialog(getString(R.string.txt_no_app)).show()
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
}
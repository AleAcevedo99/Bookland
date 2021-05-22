package com.example.bookland.Constants

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkConnectionState(val context: Context) {
    fun checkNetwork():Boolean{
        var answer = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork

            if(network != null){
                connectivityManager.getNetworkCapabilities(network)?.run {
                    answer = when{
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->{
                            //datos
                            true
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            //wifi
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }else{
                answer = false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                answer = isConnected
            }
        }
        return answer
    }
}
package com.example.bookland

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Adapters.MyShelfsAdapter
import com.example.bookland.Adapters.RecommendationsAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityGender
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.databinding.ActivityHomeBinding
import com.example.bookland.databinding.ActivityRecommendationsBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.util.*

class RecommendationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationsBinding
    private var idUser: Long = -1
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "FavoriteGendersPerUser/"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRecommendationsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.txt_recommendations)

        queue = Volley.newRequestQueue(this)
        idUser= intent.getLongExtra(Constants.ID_USER, -1)
        loadMyGendersList()
    }

    override fun onRestart() {
        super.onRestart()
        if(idUser > -1){
            loadMyGendersList()
        }else{
            Toast.makeText(this@RecommendationsActivity, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun loadMyGendersList(){
        val list = arrayListOf<EntityGender>()
        Log.d(Constants.LOG_TAG, "$url/$idUser")
        val stringRequest = StringRequest(Request.Method.GET, "$url/$idUser",
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("favorites")
                        for(i in 0 until array.length()){
                            val gender = EntityGender()
                            gender.id = array.getJSONObject(i).getLong("idGender")
                            gender.idUser = array.getJSONObject(i).getLong("idUser")
                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE) {
                                gender.genderName = array.getJSONObject(i).getString("genderName")
                            }else{
                                gender.genderName = array.getJSONObject(i).getString("genderNameEn")
                            }

                            list.add(gender)
                        }
                    }else{
                        actionDialog().show()
                    }

                    val adapter = RecommendationsAdapter(list, this@RecommendationsActivity)

                    val linearLayout = LinearLayoutManager(this@RecommendationsActivity, LinearLayoutManager.VERTICAL,
                            false)
                    binding.rvGenders.layoutManager = linearLayout
                    binding.rvGenders.adapter = adapter
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)

    }

    fun actionDialog(): AlertDialog {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.app_name)
        alert.setMessage(R.string.txt_configure_fav_genders)

        alert.setPositiveButton(R.string.txt_yes){_,_ ->

        }
        return  alert.create()
    }
}
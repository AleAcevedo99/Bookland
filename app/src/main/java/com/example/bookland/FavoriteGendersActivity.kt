package com.example.bookland

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Adapters.CheckAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityCheck
import com.example.bookland.databinding.ActivityFavoriteGendersBinding
import org.json.JSONObject
import java.util.*

class FavoriteGendersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteGendersBinding
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "Genders"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFavoriteGendersBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val idUser= intent.getLongExtra(Constants.ID_USER, -1)
        supportActionBar?.setTitle(R.string.txt_my_genders)

        queue= Volley.newRequestQueue(this)
        loadGenders(idUser)

        binding.btnOk.setOnClickListener {

            val intent = Intent(this@FavoriteGendersActivity, HomeActivity::class.java).apply{
                putExtra(Constants.ID_USER, idUser)
            }
            startActivity(intent)
            finish()
        }
    }

    fun loadGenders(idUser: Long) {
        val list = arrayListOf<EntityCheck>()
        val stringRequest = StringRequest(
                Request.Method.GET, url + "/" + idUser,
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("favorites")
                        for(i in 0 until array.length()){
                            val element = EntityCheck()
                            element.id = array.getJSONObject(i).getLong("id")
                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE){
                                element.name = array.getJSONObject(i).getString("genderName")
                            }else{
                                element.name = array.getJSONObject(i).getString("genderNameEn")
                            }

                            element.idFavorite = array.getJSONObject(i).getLong("isFavorite")
                            list.add(element)
                        }
                        val adapter = CheckAdapter(list, this, 2, idUser, queue, 0)
                        val linearLayout = LinearLayoutManager(
                                this, LinearLayoutManager.VERTICAL,
                                false
                        )
                        binding.rwsGenders.layoutManager = linearLayout
                        binding.rwsGenders.adapter = adapter
                    }


                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)

    }


}
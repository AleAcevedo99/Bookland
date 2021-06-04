package com.example.bookland

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.example.bookland.databinding.ActivityFavoriteAuthorsBinding
import com.example.bookland.databinding.ActivityFavoriteGendersBinding
import com.example.bookland.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class FavoriteAuthorsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteAuthorsBinding
    private lateinit var queue: RequestQueue
    private var idUser: Long = -1
    private val url= Constants.URL_API + "Authors"


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFavoriteAuthorsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        idUser= intent.getLongExtra(Constants.ID_USER, -1)

        supportActionBar?.setTitle(R.string.txt_my_authors)
        queue= Volley.newRequestQueue(this)

        binding.btnOk.setOnClickListener {
            val intent = Intent(this@FavoriteAuthorsActivity, FavoriteGendersActivity::class.java).apply{
                putExtra(Constants.ID_USER, idUser)
            }
            startActivity(intent)
            finish()
        }

        binding.btnSearch.setOnClickListener {
            if(binding.edtSearchAuthor.text.trim().isNotEmpty()){
                loadAuthors(idUser)
            }else{
                Toast.makeText(this, R.string.txt_wirte_an_autor, Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun loadAuthors(idUser: Long) {
        val list = arrayListOf<EntityCheck>()
        val stringRequest = StringRequest(
            Request.Method.GET, url + "/" + binding.edtSearchAuthor.text  + "/" + idUser,
            Response.Listener<String> { response ->
                val jsonObject = JSONObject(response)
                if(jsonObject["code"] == 1) {
                    val array = jsonObject.getJSONArray("authors")
                    for(i in 0 until array.length()){
                        val element = EntityCheck();
                        element.id = array.getJSONObject(i).getLong("id")
                        element.name = array.getJSONObject(i).getString("fullName")
                        element.idFavorite = array.getJSONObject(i).getLong("isFavorite")
                        list.add(element)
                    }
                }else{
                    Toast.makeText(this, R.string.txt_no_coincidences, Toast.LENGTH_SHORT).show()
                }
                val adapter = CheckAdapter(list, this, 1, idUser, queue, 0)
                val linearLayout = LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL,
                        false
                )
                binding.rwsAuthors.layoutManager = linearLayout
                binding.rwsAuthors.adapter = adapter


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)

    }

    override fun onRestart() {
        super.onRestart()
        queue= Volley.newRequestQueue(this)
        loadAuthors(idUser)
    }
}
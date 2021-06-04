package com.example.bookland

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
import com.example.bookland.Adapters.NameAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityCheck
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.databinding.ActivityBookShelfsBinding
import com.example.bookland.databinding.ActivityHomeBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject

class BookShelfsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookShelfsBinding
    private var idUser: Long = -1
    private var idBook: Long = -1
    private lateinit var queue: RequestQueue
    private val urlBooks= Constants.URL_API + "Books/getOne"
    private val urlBookShelfs= Constants.URL_API + "BookShelfs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookShelfsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.txt_book_shelfs)

        queue = Volley.newRequestQueue(this)
        idUser= intent.getLongExtra(Constants.ID_USER, -1)
        idBook = intent.getLongExtra(Constants.ID_BOOK, -1)
        loadBookShelfs(idBook)
        binding.txvInstructions.text = getString(R.string.txt_shelfs_instructions)
    }

    fun loadBookShelfs(idBook:Long){
        if(idBook > 0){
            val list = arrayListOf<EntityCheck>()
            Log.d(Constants.LOG_TAG, "$urlBooks/$idUser/$idBook")
            val stringRequest = StringRequest(Request.Method.GET, "$urlBooks/$idUser/$idBook",
                    Response.Listener<String> { response ->
                        val jsonObject = JSONObject(response)
                        if(jsonObject["code"] == 1) {
                            val array = jsonObject.getJSONArray("books")
                            binding.txtTitle.setText("${array.getJSONObject(0).getString("title")} - ${array.getJSONObject(0).getString("authorName")}")

                            val arrayShelfs = jsonObject.getJSONArray("shelfs")
                            if(arrayShelfs.length() > 0){
                                for(i in 0 until arrayShelfs.length()){
                                    val shelf = EntityCheck()
                                    shelf.id = arrayShelfs.getJSONObject(i).getLong("id")
                                    shelf.name = arrayShelfs.getJSONObject(i).getString("shelfName")
                                    shelf.idFavorite = arrayShelfs.getJSONObject(i).getLong("idBookShelf")
                                    if(arrayShelfs.getJSONObject(i).getInt("isDefaultShelf") == 0){
                                        list.add(shelf)
                                    }
                                }
                            }

                            if(list.size == 0){
                                binding.txvInstructions.text = getString(R.string.txt_add_shelfs_to_see)
                            }



                            val adapter = CheckAdapter(list, this, 3, idUser, queue, idBook)

                            val linearLayout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                                    false)
                            binding.rvShelfs.layoutManager = linearLayout
                            binding.rvShelfs.adapter = adapter

                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                        finish()
                    })
            queue.add(stringRequest)
        }else{
            Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onRestart() {
        super.onRestart()
        loadBookShelfs(idBook)
    }
}
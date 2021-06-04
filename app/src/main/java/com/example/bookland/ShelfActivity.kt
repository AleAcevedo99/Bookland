package com.example.bookland

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Adapters.BooksAdapter
import com.example.bookland.Adapters.MyShelfsAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityBooks
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.databinding.ActivityHomeBinding
import com.example.bookland.databinding.ActivityShelfBinding
import org.json.JSONObject
import java.io.Console
import java.util.*

class ShelfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShelfBinding
    private var idShelf: Long = -1
    private var idGender: Long = -1
    private var idSaga: Long = -1
    private var type: Int = -1
    private var idUser: Long = -1
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "BookShelfs"
    private val urlRecs = Constants.URL_API + "Books/recommendations"
    private val urlSaga = Constants.URL_API + "Books/getAllBySaga"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityShelfBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(this)

        type = intent.getIntExtra(Constants.TYPE_BOOK_LIST, -1)
        idUser = intent.getLongExtra(Constants.ID_USER, -1)
        if(type == 0){
            //myshelf
            idShelf = intent.getLongExtra(Constants.ID_SHELF, -1)
            if(idShelf > 0){
                loadMyBooksShelfList()
            }else{
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                finish()
            }

        }else if(type == 1){
            //recommendations
            idGender = intent.getLongExtra(Constants.ID_GENDER, -1)
            if(idGender > 0){
                loadMyRecommendationsList()
            }else{
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                finish()
            }

        }else if(type == 2){
            //busqueda
            loadBooksSearch()
        }else if(type == 3){
            //saga
            idSaga = intent.getLongExtra(Constants.ID_SAGA, -1)
            if(idSaga > 0){
                loadBooksSaga()
            }else{
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }

    fun loadMyBooksShelfList(){
        val list = arrayListOf<EntityBooks>() //BookShelfs_GetAll
        val stringRequest = StringRequest(Request.Method.GET, "$url/$idShelf",
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("books")
                        for(i in 0 until array.length()){
                            val book = EntityBooks()
                            book.id = array.getJSONObject(i).getLong("id")
                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE) {
                                book.title = array.getJSONObject(i).getString("title")
                            }else{
                                book.title = array.getJSONObject(i).getString("titleEn")
                            }
                            book.authorName = array.getJSONObject(i).getString("authorName")
                            book.publicationYear = array.getJSONObject(i).getInt("publicationYear")
                            book.avgRating = array.getJSONObject(i).getDouble("avgRating")
                            book.imageURL = array.getJSONObject(i).getString("imageURL")
                            book.rating = array.getJSONObject(i).getDouble("rating")
                            book.idShelf = array.getJSONObject(i).getLong("idShelf")
                            book.shelfName = array.getJSONObject(i).getString("shelfName")
                            list.add(book)
                        }

                    }else{
                        actionDialog(getString(R.string.txt_no_books_in_shelf)).show()
                    }
                    val adapter = BooksAdapter(list, this@ShelfActivity, idUser)

                    val linearLayout = LinearLayoutManager(this@ShelfActivity, LinearLayoutManager.VERTICAL,
                            false)
                    binding.rvMyBooksShelf.layoutManager = linearLayout
                    binding.rvMyBooksShelf.adapter = adapter
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)
    }

    fun loadBooksSearch(){
        val list = arrayListOf<EntityBooks>() //BookGetAllbyCoincidence
        val adapter = BooksAdapter(list, this@ShelfActivity, idUser)

        val linearLayout = LinearLayoutManager(this@ShelfActivity, LinearLayoutManager.VERTICAL,
            false)
        binding.rvMyBooksShelf.layoutManager = linearLayout
        binding.rvMyBooksShelf.adapter = adapter
    }

    fun loadMyRecommendationsList(){
        val list = arrayListOf<EntityBooks>()
        Log.d(Constants.LOG_TAG, "$urlRecs/$idGender/$idUser")
        val stringRequest = StringRequest(Request.Method.GET, "$urlRecs/$idGender/$idUser",
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("books")
                        for(i in 0 until array.length()){
                            val book = EntityBooks()
                            book.id = array.getJSONObject(i).getLong("id")
                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE) {
                                book.title = array.getJSONObject(i).getString("title")
                            }else{
                                book.title = array.getJSONObject(i).getString("titleEn")
                            }
                            book.authorName = array.getJSONObject(i).getString("authorName")
                            book.publicationYear = array.getJSONObject(i).getInt("publicationYear")
                            book.avgRating = array.getJSONObject(i).getDouble("avgRating")
                            book.imageURL = array.getJSONObject(i).getString("imageURL")
                            book.rating = array.getJSONObject(i).getDouble("rating")
                            book.idShelf = array.getJSONObject(i).getLong("idShelf")
                            book.shelfName = array.getJSONObject(i).getString("shelfName")
                            list.add(book)
                        }

                        val adapter = BooksAdapter(list, this@ShelfActivity, idUser)

                        val linearLayout = LinearLayoutManager(this@ShelfActivity, LinearLayoutManager.VERTICAL,
                                false)
                        binding.rvMyBooksShelf.layoutManager = linearLayout
                        binding.rvMyBooksShelf.adapter = adapter

                    }else{
                        actionDialog(getString(R.string.txt_no_recommendations)).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)
    }

    fun loadBooksSaga(){
        val list = arrayListOf<EntityBooks>() //BookShelfs_GetAll
        Log.d(Constants.LOG_TAG, "SagaURL $urlSaga/$idSaga/$idUser")
        val stringRequest = StringRequest(Request.Method.GET, "$urlSaga/$idSaga/$idUser",
            Response.Listener<String> { response ->
                val jsonObject = JSONObject(response)
                if(jsonObject["code"] == 1) {
                    val array = jsonObject.getJSONArray("books")
                    for(i in 0 until array.length()){
                        val book = EntityBooks()
                        book.id = array.getJSONObject(i).getLong("id")
                        if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE) {
                            book.title = array.getJSONObject(i).getString("title")
                        }else{
                            book.title = array.getJSONObject(i).getString("titleEn")
                        }
                        book.authorName = array.getJSONObject(i).getString("authorName")
                        book.publicationYear = array.getJSONObject(i).getInt("publicationYear")
                        book.avgRating = array.getJSONObject(i).getDouble("avgRating")
                        book.imageURL = array.getJSONObject(i).getString("imageURL")
                        book.rating = array.getJSONObject(i).getDouble("rating")
                        book.idShelf = array.getJSONObject(i).getLong("idShelf")
                        book.shelfName = array.getJSONObject(i).getString("shelfName")
                        list.add(book)
                    }

                    val adapter = BooksAdapter(list, this@ShelfActivity, idUser)

                    val linearLayout = LinearLayoutManager(this@ShelfActivity, LinearLayoutManager.VERTICAL,
                        false)
                    binding.rvMyBooksShelf.layoutManager = linearLayout
                    binding.rvMyBooksShelf.adapter = adapter
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                finish()
            })
        queue.add(stringRequest)
    }

    override fun onRestart() {
        super.onRestart()
        if(type == 0){
            //myshelf
            idShelf = intent.getLongExtra(Constants.ID_SHELF, -1)
            if(idShelf > 0){
                loadMyBooksShelfList()
            }else{
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                finish()
            }

        }else if(type == 1){
            //recommendations
            idGender = intent.getLongExtra(Constants.ID_GENDER, -1)
            if(idGender > 0){
                loadMyRecommendationsList()
            }else{
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                finish()
            }

        }else if(type == 2){
            //busqueda
            loadBooksSearch()
        }else if(type == 3){
            //saga
            idSaga = intent.getLongExtra(Constants.ID_SAGA, -1)
            if(idSaga > 0){
                loadBooksSaga()
            }else{
                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }

    fun actionDialog(message:String): AlertDialog {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.app_name)
        alert.setMessage(message)

        alert.setPositiveButton(R.string.txt_yes){_,_ ->
            finish()
        }
        return  alert.create()
    }
}
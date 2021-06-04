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
import com.example.bookland.Adapters.BooksAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityBooks
import com.example.bookland.Entity.EntityFilter
import com.example.bookland.Entity.ListFilters
import com.example.bookland.databinding.ActivityAdvancedSearchResultsBinding
import org.json.JSONObject
import java.util.*

class AdvancedSearchResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdvancedSearchResultsBinding
    private lateinit var queue: RequestQueue
    private var idUser: Long = -1
    private val url= Constants.URL_API + "Books/search"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAdvancedSearchResultsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        idUser= intent.getLongExtra(Constants.ID_USER, -1)
        if(idUser > 0){
            queue = Volley.newRequestQueue(this)

            loadListBooks()

            binding.btnFilters.setOnClickListener {
                super.onBackPressed()
            }
        }else{
            Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
        }

    }

    fun loadListBooks(){
        val filterSettings = ListFilters().getFilterSetting()
        val gender = if (filterSettings.gender > 0) filterSettings.gender.toString() else ""
        val subGender = if (filterSettings.subgender > 0) filterSettings.subgender.toString() else ""
        val minPageNumber = filterSettings.minPage?.toString() ?: ""
        val maxPageNumber = filterSettings.maxPage?.toString() ?: ""
        val tags = getTagsSearch(filterSettings)
        val tropesSearch = ""
        val pov = if (filterSettings.pov > 0) filterSettings.pov.toString() else ""
        val person = if (filterSettings.narrating > 0) filterSettings.narrating.toString() else ""
        val targetAge = if (filterSettings.ageRange > 0) filterSettings.ageRange.toString() else ""
        val myURL = url + "?idGender=${gender}&idSubender=${subGender}&tagsSearch=${tags}" +
                "&tropesSearch=${tropesSearch}&multiplePOV=${pov}&person=${person}&maxPageNumber=${maxPageNumber}" +
                "&minPageNumber=${minPageNumber}&targetAge=${targetAge}&idUser=${idUser}"
        val list = arrayListOf<EntityBooks>()
        Log.d(Constants.LOG_TAG, "BA: $myURL")
        val stringRequest = StringRequest(Request.Method.GET, myURL,
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("books")
                        for(i in 0 until array.length()){
                            val book = EntityBooks()
                            book.id = array.getJSONObject(i).getLong("id")
                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE){
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
                        Toast.makeText(this, R.string.txt_no_coincidences,
                                Toast.LENGTH_LONG).show()
                        finish()
                    }
                    val adapter = BooksAdapter(list, this, idUser)

                    val linearLayout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                            false)
                    binding.rvBooksSearch.layoutManager = linearLayout
                    binding.rvBooksSearch.adapter = adapter
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)
    }

    fun getTagsSearch(filterSettings: EntityFilter): String{
        var search = ""
        for((index, trope) in filterSettings.tropesSearch.withIndex()){
            if(index > 0){
                search = search + ",${trope}"
            }else{
                search = search + trope
            }
        }
        return  search
    }

    override fun onRestart() {
        super.onRestart()
        loadListBooks()
    }
}
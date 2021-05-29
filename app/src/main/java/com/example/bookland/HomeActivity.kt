package com.example.bookland

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import org.json.JSONObject
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var idUser: Long = -1
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "Shelfs/GetPerUser"
    private val urlSearch= Constants.URL_API + "Books/getCoincidence"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.txt_home)

        queue = Volley.newRequestQueue(this)
        idUser= intent.getLongExtra(Constants.ID_USER, -1)
        loadMyShelfsList()

    }

    fun loadBooksSearch(search:String){
        val list = arrayListOf<EntityBooks>()
        Log.d(Constants.LOG_TAG, "search: $urlSearch/$search/$idUser")
        val stringRequest = StringRequest(Request.Method.GET, "$urlSearch/$search/$idUser",
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

                        val adapter = BooksAdapter(list, this@HomeActivity, idUser)

                        val linearLayout = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL,
                                false)
                        binding.rvMyShelfs.layoutManager = linearLayout
                        binding.rvMyShelfs.adapter = adapter
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, R.string.txt_error_request, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)
    }

    override fun onRestart() {
        super.onRestart()
        if(idUser > -1){
            loadMyShelfsList()
        }else{
            Toast.makeText(this@HomeActivity, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun loadMyShelfsList(){
        val list = arrayListOf<EntityShelf>()
        val stringRequest = StringRequest(Request.Method.GET, "$url/$idUser",
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("shelfs")
                        for(i in 0 until array.length()){
                            val myNew = EntityShelf()
                            myNew.id = array.getJSONObject(i).getLong("id")
                            myNew.shelfName = array.getJSONObject(i).getString("shelfName")
                            myNew.idUser = array.getJSONObject(i).getLong("idUser")
                            myNew.count = array.getJSONObject(i).getInt("shelfCount")
                            myNew.isDefault = array.getJSONObject(i).getInt("isDefaultShelf")
                            if(myNew.isDefault == 1){
                                myNew.shelfName = getString(R.string.txt_default_shelf)
                            }
                            list.add(myNew)
                        }

                        val adapter = MyShelfsAdapter(list, this@HomeActivity)

                        val linearLayout = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL,
                                false)
                        binding.rvMyShelfs.layoutManager = linearLayout
                        binding.rvMyShelfs.adapter = adapter
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bookland, menu)
        val searchItem = menu?.findItem(R.id.itmSimpleSearch)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(!query.isNullOrEmpty()){
                        loadBooksSearch(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            searchView.setOnCloseListener(object : SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    searchView.onActionViewCollapsed()
                    loadMyShelfsList()
                    return true
                }

            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itmShelf -> {
                val intent = Intent(this, CatalogShelfsActivity::class.java).apply{
                    putExtra(Constants.ID_USER, idUser)
                }
                startActivity(intent)
            }
            R.id.itmEditFavAuthors -> {
                val intent = Intent(this, FavoriteAuthorsActivity::class.java).apply{
                    putExtra(Constants.ID_USER, idUser)
                }
                startActivity(intent)
            }
            R.id.itmAboutUs -> {
                val intent = Intent(this, AboutUsActivity::class.java)
                startActivity(intent)
            }
            R.id.itmExit -> {
                val intent = Intent(this, LogInActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
            R.id.itmNewBookRequest -> {
                val emails = arrayOf("ale.by.3008@gmail.com")
                var intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, emails)
                    putExtra(Intent.EXTRA_SUBJECT, "Solicitar agregar un nuevo libro")
                    putExtra(Intent.EXTRA_TEXT, "Hola, quisiera que agregaran a la aplicación el libro ...")
                }

                if(intent.resolveActivity(packageManager) != null){
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "No tienes una app para abrir esta opción",
                        Toast.LENGTH_LONG).show()
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
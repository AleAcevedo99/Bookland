package com.example.bookland

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Adapters.NameAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.databinding.ActivityBookDetailBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.*

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var queue: RequestQueue
    private var idSaga: Long = 0
    private val url= Constants.URL_API + "Books/getOne"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(this)

        val idBook = intent.getLongExtra(Constants.ID_BOOK, -1)
        val idUser = intent.getLongExtra(Constants.ID_USER, -1)
        Log.d(Constants.LOG_TAG, "Detail: " + "$url/$idUser/$idBook")

        if(idBook > 0){
            Log.d(Constants.LOG_TAG, "$url/$idUser/$idBook")
            val stringRequest = StringRequest(Request.Method.GET, "$url/$idUser/$idBook",
                    Response.Listener<String> { response ->
                        val jsonObject = JSONObject(response)
                        if(jsonObject["code"] == 1) {
                            val array = jsonObject.getJSONArray("books")
                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE){
                                binding.txtTitle.setText(array.getJSONObject(0).getString("title"))
                                binding.txtGender.setText(array.getJSONObject(0).getString("genderName"))
                                binding.txtDescription.setText(array.getJSONObject(0).getString("description"))
                            }else{
                                binding.txtTitle.setText(array.getJSONObject(0).getString("titleEn"))
                                binding.txtGender.setText(array.getJSONObject(0).getString("genderNameEn"))
                                binding.txtDescription.setText(array.getJSONObject(0).getString("descriptionEn"))

                            }
                            if(array.getJSONObject(0).getInt("narrating") == 1){
                                binding.txtNarrating.setText("${getString(R.string.txt_narrating)} ${getString(R.string.txt_first_person)}")
                            }else{
                                binding.txtNarrating.setText("${getString(R.string.txt_narrating)} ${getString(R.string.txt_third_person)}")
                            }

                            binding.txtAuthor.setText(array.getJSONObject(0).getString("authorName"))
                            binding.txtPublishDate.setText("${array.getJSONObject(0).getInt("publicationYear")}")
                            binding.txtAvg.setText("${getString(R.string.txt_avg)} ${array.getJSONObject(0).getDouble("avgRating")}")
                            binding.ratingBar.rating = array.getJSONObject(0).getDouble("rating").toFloat()
                            //binding.spnShelfs.setSelection(array.getJSONObject(0).getLong("idShelf"))
                            binding.txtPageNumber.setText("${getString(R.string.txt_page_number)}  ${array.getJSONObject(0).getInt("pageNumber")}")

                            binding.btnSeeSaga.isVisible =
                                array.getJSONObject(0).getLong("idSaga") > 0
                            idSaga = array.getJSONObject(0).getLong("idSaga")

                            Picasso.get().load(array.getJSONObject(0).getString("imageURL")).fit()
                                    .placeholder(R.drawable.imgplaceholder)
                                    .error(R.drawable.imgplaceholder)
                                    .into(binding.imgCover)

                            val list = arrayListOf<String>()
                            if(array.getJSONObject(0).getInt("ageClassification") == 1){
                                list.add(getString(R.string.txt_adult))
                            }else if(array.getJSONObject(0).getInt("ageClassification") == 2){
                                list.add(getString(R.string.txt_ya))
                            }else if(array.getJSONObject(0).getInt("ageClassification") == 3){
                                list.add(getString(R.string.txt_na))
                            }else{
                                list.add(getString(R.string.txt_children))
                            }
                            if(array.getJSONObject(0).getInt("pov") == 1){
                                list.add("${getString(R.string.txt_pov)} ${getString(R.string.txt_no)}")
                            }else{
                                list.add("${getString(R.string.txt_pov)} ${getString(R.string.txt_yes_string)}")
                            }

                            val arrayTags = jsonObject.getJSONArray("tags")
                            for(i in 0 until arrayTags.length()){
                                if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE){
                                    list.add("${arrayTags.getJSONObject(i).getString("tagName")}")
                                }else{
                                    list.add("${arrayTags.getJSONObject(i).getString("tagNameEn")}")
                                }

                            }

                            val arrayTropes = jsonObject.getJSONArray("tropes")
                            for(i in 0 until arrayTropes.length()){
                                if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE){
                                    list.add("${arrayTropes.getJSONObject(i).getString("tropeName")}")
                                }else{
                                    list.add("${arrayTropes.getJSONObject(i).getString("tropeNameEn")}")
                                }

                            }

                            val adapter = NameAdapter(list, this)

                            val linearLayout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                                    false)
                            binding.rvTropes.layoutManager = linearLayout
                            binding.rvTropes.adapter = adapter

                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                        finish()
                    })
            queue.add(stringRequest)

            binding.ratingBar.setOnRatingBarChangeListener {ratingBar, rating, fromUser ->
                if(fromUser){
                    val jsonBody = JSONObject()
                    jsonBody.put("idUser", idUser)
                    jsonBody.put("idBook", idBook)
                    jsonBody.put("rating", rating)
                    val jsonObjectRequest = JsonObjectRequest(
                            Request.Method.POST, Constants.URL_API + "UserBooks",jsonBody,
                            Response.Listener { response ->
                                if(response["code"].toString().toInt() >= 1){
                                    Toast.makeText(this, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                }
                            },
                            Response.ErrorListener { error ->
                                Toast.makeText(this, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                            })
                    queue.add(jsonObjectRequest)
                }
            }

            binding.btnShelfs.setOnClickListener {
                val intent = Intent(this, BookShelfsActivity::class.java).apply{
                    putExtra(Constants.ID_BOOK, idBook)
                    putExtra(Constants.ID_USER, idUser)
                }
                startActivity(intent)
            }

            binding.btnSeeSaga.setOnClickListener {
                val intent = Intent(this, ShelfActivity::class.java).apply{
                    putExtra(Constants.ID_SAGA, idSaga)
                    putExtra(Constants.ID_USER, idUser)
                    putExtra(Constants.TYPE_BOOK_LIST, 3)
                }
                startActivity(intent)
            }
        }else{
            Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
            finish()
        }







    }
}
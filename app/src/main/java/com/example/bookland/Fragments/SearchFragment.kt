package com.example.bookland.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.bookland.R
import com.example.bookland.databinding.FragmentHomeBinding
import com.example.bookland.databinding.FragmentSearchBinding
import org.json.JSONObject
import java.util.*

private const val ID_USER = Constants.ID_USER
private const val SEARCH = Constants.SEARCH


class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var idUser: Long = -1
    private var search: String = ""
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var queue: RequestQueue
    private val urlSearch= Constants.URL_API + "Books/getCoincidence"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUser = it.getLong(ID_USER, -1)
            search = it.getString(SEARCH, "")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        queue = Volley.newRequestQueue(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(search != ""){
            loadBooksSearch(search)
        }else{
            Toast.makeText(context, R.string.txt_error_request, Toast.LENGTH_SHORT).show()
        }


    }

    fun loadBooksSearch(search:String){
        val list = arrayListOf<EntityBooks>()
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

                        val adapter = BooksAdapter(list, context!!, idUser)

                        val linearLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                                false)
                        binding.rvBooksSearch.layoutManager = linearLayout
                        binding.rvBooksSearch.adapter = adapter
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, R.string.txt_error_request, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance(idUser: Long, search: String) =
                SearchFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ID_USER, idUser)
                        putString(SEARCH, search)
                    }
                }
    }
}
package com.example.bookland.Fragments

import android.os.Bundle
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
import com.example.bookland.Adapters.MyShelfsAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.R
import com.example.bookland.databinding.FragmentHomeBinding
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ID_USER = Constants.ID_USER


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var idUser: Long? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "Shelfs/GetPerUser"
    private val urlSearch= Constants.URL_API + "Books/getCoincidence"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUser = it.getLong(ID_USER, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        queue = Volley.newRequestQueue(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMyShelfsList()
    }

    fun loadMyShelfsList(){
        val list = arrayListOf<EntityShelf>()
        val stringRequest = StringRequest(
            Request.Method.GET, "$url/$idUser",
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
                    val adapter = MyShelfsAdapter(list, context!!)

                    val linearLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                        false)
                    binding.rvMyShelfs.layoutManager = linearLayout
                    binding.rvMyShelfs.adapter = adapter


                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance(idUser: Long) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putLong(ID_USER, idUser)
                }
            }
    }
}
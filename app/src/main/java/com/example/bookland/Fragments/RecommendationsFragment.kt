package com.example.bookland.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Adapters.RecommendationsAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityGender
import com.example.bookland.MainActivity
import com.example.bookland.R
import com.example.bookland.databinding.FragmentHomeBinding
import com.example.bookland.databinding.FragmentRecommendationsBinding
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ID_USER = Constants.ID_USER
private lateinit var queue: RequestQueue
private val url= Constants.URL_API + "FavoriteGendersPerUser/"

class RecommendationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var idUser: Long? = null
    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        queue = Volley.newRequestQueue(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        loadMyGendersList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadMyGendersList()
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

                    val adapter = RecommendationsAdapter(list, context!!)

                    val linearLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                            false)
                    binding.rvGenders.layoutManager = linearLayout
                    binding.rvGenders.adapter = adapter
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)

    }

    fun actionDialog(): AlertDialog {
        val alert = AlertDialog.Builder(context!!)
        alert.setTitle(R.string.app_name)
        alert.setMessage(R.string.txt_configure_fav_genders)

        alert.setPositiveButton(R.string.txt_yes){ _, _ ->
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        return  alert.create()
    }



    companion object {
        @JvmStatic
        fun newInstance(idUser: Long) =
            RecommendationsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ID_USER, idUser)
                }
            }
    }
}
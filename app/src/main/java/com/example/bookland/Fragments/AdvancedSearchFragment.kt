package com.example.bookland.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Adapters.CheckAdapter
import com.example.bookland.AdvancedSearchResultsActivity
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityCheck
import com.example.bookland.Entity.ListFilters
import com.example.bookland.R
import com.example.bookland.databinding.FragmentAdvancedSearchBinding
import com.example.bookland.databinding.FragmentRecommendationsBinding
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ID_USER = Constants.ID_USER

class AdvancedSearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var idUser: Long = -1
    private var _binding: FragmentAdvancedSearchBinding? = null
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "Tags"
    private val urlGenders = Constants.URL_API + "Genders"
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
        _binding = FragmentAdvancedSearchBinding.inflate(inflater, container, false)
        queue = Volley.newRequestQueue(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFilterSettings()
        loadTags(idUser)
        loadGenders(idUser)

        binding.btnAdvacnedSearch.setOnClickListener {
            val filterSetting = ListFilters().getFilterSetting()
            val listGenders = ListFilters().getListGenders()
            if(binding.spnGender.selectedItemPosition > 0){
                filterSetting.gender = listGenders[binding.spnGender.selectedItemPosition].id.toInt()
                filterSetting.genderPos = binding.spnGender.selectedItemPosition
            }else{
                filterSetting.gender = 0
                filterSetting.genderPos = 0
            }
            if(binding.spnSubGender.selectedItemPosition > 0){
                filterSetting.subgender = listGenders[binding.spnSubGender.selectedItemPosition].id.toInt()
                filterSetting.subgenderPos = binding.spnSubGender.selectedItemPosition
            }else{
                filterSetting.subgender = 0
                filterSetting.subgenderPos = 0
            }

            filterSetting.pov = if(binding.swPOV.isChecked) 2 else 0
            filterSetting.narrating = binding.spnPerson.selectedItemPosition
            filterSetting.minPage = if(binding.edtMin.text.isNotEmpty()) binding.edtMin.text.toString().toInt() else null
            filterSetting.maxPage = if(binding.edtMax.text.isNotEmpty()) binding.edtMax.text.toString().toInt() else null
            filterSetting.ageRange = binding.spnAgeType.selectedItemPosition
            val intent = Intent(context, AdvancedSearchResultsActivity::class.java).apply{
                putExtra(Constants.ID_USER, idUser)
            }
            startActivity(intent)
        }

        binding.btnClean.setOnClickListener {
            binding.spnGender.setSelection(0)
            binding.spnSubGender.setSelection(0)
            val filterSetting = ListFilters().getFilterSetting()
            filterSetting.tropes = arrayListOf<String>()
            filterSetting.tropesSearch = arrayListOf<String>()
            val listGenders = ListFilters().getListGenders()
            listGenders.clear()
            loadTags(idUser)
            binding.swPOV.isChecked = false
            binding.spnPerson.setSelection(0)
            binding.edtMin.setText("")
            binding.edtMax.setText("")
            binding.spnAgeType.setSelection(0)

        }
    }

    fun loadGenders(idUser: Long){
        val gendersNames = arrayListOf<String>()
        val listGenders = ListFilters().getListGenders()
        val filterSetting = ListFilters().getFilterSetting()
        var genderPos: Int = 0
        var subGenderPos: Int = 0
        listGenders.add(EntityCheck(0, 0, getString(R.string.txt_select)))
        gendersNames.add(getString(R.string.txt_select))
        val stringRequest = StringRequest(Request.Method.GET, "$urlGenders/$idUser",
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("favorites")
                        for(i in 0 until array.length()){
                            val element = EntityCheck()
                            element.id = array.getJSONObject(i).getLong("id")

                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE) {
                                element.name = array.getJSONObject(i).getString("genderName")

                            }else{
                                element.name = array.getJSONObject(i).getString("genderNameEn")
                            }
                            listGenders.add(element)
                            gendersNames.add(element.name)
                            if(filterSetting.gender.toLong() == element.id){
                                genderPos = i + 1
                            }
                            if(filterSetting.subgender.toLong() == element.id){
                                subGenderPos = i + 1
                            }
                        }
                        val gendersSpinner = binding.spnGender
                        gendersSpinner.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item,
                                gendersNames)
                        gendersSpinner.setSelection(genderPos)
                        val subGendersSpinner = binding.spnSubGender
                        subGendersSpinner.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item,
                                gendersNames)
                        subGendersSpinner.setSelection(subGenderPos)
                    }

                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)

    }

    fun loadTags(idUser:Long){
        val list = arrayListOf<EntityCheck>()
        val stringRequest = StringRequest(Request.Method.GET, "$url",
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        val array = jsonObject.getJSONArray("tags")
                        for(i in 0 until array.length()){
                            val element = EntityCheck()
                            val filterSetting = ListFilters().getFilterSetting()
                            element.id = array.getJSONObject(i).getLong("id")
                            if(Locale.getDefault().getDisplayLanguage() == Constants.LANGUAGE) {
                                element.name = array.getJSONObject(i).getString("tagName")
                            }else{
                                element.name = array.getJSONObject(i).getString("tagNameEn")
                            }

                            if(filterSetting.tropes.contains(element.name)){
                                element.idFavorite = 1
                            }else{
                                element.idFavorite = 0
                            }
                            list.add(element)
                        }
                    }

                    val adapter = CheckAdapter(list, context!!, 4, idUser, queue, 0)
                    val linearLayout = LinearLayoutManager(
                            context, LinearLayoutManager.VERTICAL,
                            false
                    )
                    binding.rvTags.layoutManager = linearLayout
                    binding.rvTags.adapter = adapter
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                })
        queue.add(stringRequest)
    }

    fun setFilterSettings(){
        val filterSetting = ListFilters().getFilterSetting()
        binding.spnGender.setSelection(filterSetting.genderPos)
        binding.spnSubGender.setSelection(filterSetting.subgenderPos)
        binding.swPOV.isChecked = filterSetting.pov == 2
        binding.spnPerson.setSelection(filterSetting.narrating)
        binding.edtMin.setText(filterSetting.minPage?.toString() ?: "")
        binding.edtMax.setText(filterSetting.maxPage?.toString() ?: "")
        binding.spnAgeType.setSelection(filterSetting.ageRange)
    }

    companion object {
        @JvmStatic
        fun newInstance(idUser: Long) =
            AdvancedSearchFragment().apply {
                arguments = Bundle().apply {
                    putLong(ID_USER, idUser)
                }
            }
    }
}
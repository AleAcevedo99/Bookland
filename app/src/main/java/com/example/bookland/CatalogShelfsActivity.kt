package com.example.bookland

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Adapters.CatalogShelfsAdapter
import com.example.bookland.Adapters.MyShelfsAdapter
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.databinding.ActivityCatalogShelfsBinding
import com.example.bookland.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class CatalogShelfsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCatalogShelfsBinding
    private var idUser: Long = -1
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "Shelfs/GetPerUser"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCatalogShelfsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.txt_shelfs)

        queue = Volley.newRequestQueue(this)
        idUser= intent.getLongExtra(Constants.ID_USER, -1)
        loadMyShelfsList()

        binding.btnNewShelf.setOnClickListener {
            //ShelfsAdd
            actionDialog().show();
        }

    }

    override fun onRestart() {
        super.onRestart()
        if(idUser > -1){
            loadMyShelfsList()
        }else{
            Toast.makeText(this@CatalogShelfsActivity, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
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
                            val shelf = EntityShelf()
                            shelf.id = array.getJSONObject(i).getLong("id")
                            shelf.shelfName = array.getJSONObject(i).getString("shelfName")
                            shelf.idUser = array.getJSONObject(i).getLong("idUser")
                            shelf.count = array.getJSONObject(i).getInt("shelfCount")
                            shelf.isDefault = array.getJSONObject(i).getInt("isDefaultShelf")
                            if(shelf.isDefault == 0){
                                list.add(shelf)
                            }
                        }
                        val adapter = CatalogShelfsAdapter(list, this@CatalogShelfsActivity, queue)

                        val linearLayout = LinearLayoutManager(this@CatalogShelfsActivity, LinearLayoutManager.VERTICAL,
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

    fun actionDialog(): AlertDialog {
        val alert = AlertDialog.Builder(this@CatalogShelfsActivity)
        val editText = EditText(this@CatalogShelfsActivity)
        alert.setTitle(R.string.app_name)
        alert.setMessage(R.string.txt_new_shelf)

        alert.setView(editText)

        alert.setPositiveButton(R.string.txt_ok) { _, _ ->
            val jsonBody = JSONObject()
            jsonBody.put("idUser", idUser)
            jsonBody.put("shelfName", editText.text.toString())
            val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, Constants.URL_API + "Shelfs",jsonBody,
                    Response.Listener { response ->
                        if(response["code"].toString().toInt() >= 1){
                            Toast.makeText(this, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                            loadMyShelfsList()
                        }else{
                            Toast.makeText(this, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                        }

                    },
                    Response.ErrorListener { error ->
                        if(error.networkResponse.statusCode == 406){
                            Toast.makeText(this, R.string.txt_name_cannot_be_repeated, Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                        }
                    })
            queue.add(jsonObjectRequest)


        }

        alert.setNegativeButton(R.string.txt_cancel) { _, _ ->

        }

        return  alert.create()
    }
}